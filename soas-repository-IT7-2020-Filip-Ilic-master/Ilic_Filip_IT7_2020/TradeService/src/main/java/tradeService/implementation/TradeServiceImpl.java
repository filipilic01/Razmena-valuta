package tradeService.implementation;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import api.dtos.BankAccountDto;
import api.dtos.CurrencyConversionDto;
import api.dtos.CurrencyExchangeDto;
import api.dtos.CryptoWalletDto;
import api.feignProxies.BankAccountProxy;
import api.feignProxies.CryptoWalletProxy;
import api.feignProxies.CurrencyExchangeProxy;
import api.feignProxies.UsersServiceProxy;
import api.services.TradeService;
import feign.FeignException;
import tradeService.model.TradeModel;
import tradeService.repository.TradeRepository;
import util.exceptions.NoDataFoundException;
import util.exceptions.ServiceUnavailableException;


@RestController
public class TradeServiceImpl implements TradeService{
	
	@Autowired
	private TradeRepository repo;
	
	@Autowired
	private CurrencyExchangeProxy proxy;
	
	@Autowired
	private BankAccountProxy proxyBank;
	
	@Autowired
	private CryptoWalletProxy proxyWallet;
	
	@Autowired
	private UsersServiceProxy proxyUser;
	
	public ResponseEntity<?> getConversion(String from, String to, double quantity, String authorizationHeader) {
	    try {
	    	String email = proxyUser.getCurrentUserEmail(authorizationHeader);

		    if ((from.equals("ETH") || from.equals("BNB") || from.equals("BTC")) && (to.equals("EUR") || to.equals("USD"))) {
		        return handleCryptoToFiatConversion(from, to, quantity, email);
		    } else if ((from.equals("EUR") || from.equals("USD")) && (to.equals("ETH") || to.equals("BNB") || to.equals("BTC"))) {
		        return handleFiatToCryptoConversion(from, to, quantity, email);
		    } else if ((from.equals("ETH") || from.equals("BNB") || from.equals("BTC")) && (!to.equals("EUR") && !to.equals("USD"))) {
		        return handleCryptoToOtherFiatConversion(from, to, quantity, email);
		    } else if ((!from.equals("EUR") && !from.equals("USD")) && (to.equals("ETH") || to.equals("BNB") || to.equals("BTC"))) {
		        return handleOtherFiatToCryptoConversion(from, to, quantity, email);
		    } else {
		        return ResponseEntity.status(404).body("Requested exchange pair [" + from + " into " + to + "] could not be found");
		    }
	    } catch (FeignException e) {
            if (e.status() != 404) {
                throw handleFeignException(e);
            }
            throw new NoDataFoundException(e.getMessage());
        }
		
	}

	private ResponseEntity<?> handleCryptoToFiatConversion(String from, String to, double quantity, String email) {
	    try {
	    	double userAmount = proxyWallet.GetUserWallet(email, from, to);
	    	if (userAmount >= quantity) {
	    		TradeModel model = repo.findByFromAndTo(from, to);
	    		ResponseEntity<CryptoWalletDto> dtoWallet = proxyWallet.updateUserWalletAfterTradeCryptoToFiat(from, email, quantity);
	    		ResponseEntity<BankAccountDto> dto = proxyBank.updateUserAccountAfterTradeCryptoToFiat(to, quantity * model.getExchangeValue(), email);

	    		String message = "Conversion was successful " + from + ": " + quantity + " for " + to + ": " + quantity * model.getExchangeValue();
	    		return ResponseEntity.ok().body(new ConversionResponse(dto.getBody(), message));
	    	} else {
	    		return ResponseEntity.status(400).body("You don't have enough crypto money to exchange!");
	    	}
	    }catch (FeignException e) {
            if (e.status() != 404) {
                throw handleFeignException(e);
            }
            throw new NoDataFoundException(e.getMessage());
        }
		
	}

	private ResponseEntity<?> handleFiatToCryptoConversion(String from, String to, double quantity, String email) {
	    try {
	    	double userAmount = proxyBank.GetUserAccountAmount(email, from, to);
		    if (userAmount >= quantity) {
		        TradeModel model = repo.findByFromAndTo(from, to);
		        ResponseEntity<BankAccountDto> dto = proxyBank.updateUserAccountAfterTradeFiatToCrypto(from, quantity, email);
		        ResponseEntity<CryptoWalletDto> dtoWallet = proxyWallet.updateUserWalletAfterTradeFiatToCrypto(to, email, quantity * model.getExchangeValue());

		        String message = "Conversion was successful " + from + ": " + quantity + " for " + to + ": " + quantity * model.getExchangeValue();
		        return ResponseEntity.ok().body(new ConversionResponse(dtoWallet.getBody(), message));
		    } else {
		        return ResponseEntity.status(400).body("You don't have enough money to exchange!");
		    }
	    }catch (FeignException e) {
            if (e.status() != 404) {
                throw handleFeignException(e);
            }
            throw new NoDataFoundException(e.getMessage());
        }
		
	}

	private ResponseEntity<?> handleCryptoToOtherFiatConversion(String from, String to, double quantity, String email) {
	    try {
	    	double userAmount = proxyWallet.GetUserWallet(email, from, to);
		    if (userAmount >= quantity) {
		        TradeModel model = repo.findByFromAndTo(from, "USD");
		        ResponseEntity<CryptoWalletDto> dtoWallet = proxyWallet.updateUserWalletAfterTradeCryptoToFiat(from, email, quantity);
		        ResponseEntity<BankAccountDto> dto = proxyBank.updateUserAccountAfterTradeCryptoToFiat("USD", quantity * model.getExchangeValue(), email);

		        CurrencyExchangeDto responseDto = acquireExchange("USD", to);
		        CurrencyConversionDto conv = exchangeToConversion(responseDto, quantity * model.getExchangeValue());
		        ResponseEntity<BankAccountDto> accountDto = proxyBank.updateUserAccount("USD", to, quantity * model.getExchangeValue(), email, conv.getConversionResult().getResult());

		        String message = "Conversion was successful " + from + ": " + quantity + " for " + to + ": " + conv.getConversionResult().getResult();
		        return ResponseEntity.ok().body(new ConversionResponse(accountDto.getBody(), message));
		    } else {
		        return ResponseEntity.status(400).body("You don't have enough crypto money to exchange!");
		    }
	    }catch (FeignException e) {
            if (e.status() != 404) {
                throw handleFeignException(e);
            }
            throw new NoDataFoundException(e.getMessage());
        }
		
	}

	private ResponseEntity<?> handleOtherFiatToCryptoConversion(String from, String to, double quantity, String email) {
	   try {
		   double userAmount = proxyBank.GetUserAccountAmount(email, from, to);
		    if (userAmount >= quantity) {
		        CurrencyExchangeDto responseDto = acquireExchange(from, "USD");
		        CurrencyConversionDto conv = exchangeToConversion(responseDto, quantity);
		        ResponseEntity<BankAccountDto> accountDto = proxyBank.updateUserAccount(from, "USD", quantity, email, conv.getConversionResult().getResult());

		        TradeModel model = repo.findByFromAndTo("USD", to);
		        ResponseEntity<BankAccountDto> dto = proxyBank.updateUserAccountAfterTradeFiatToCrypto("USD", conv.getConversionResult().getResult(), email);
		        ResponseEntity<CryptoWalletDto> dtoWallet = proxyWallet.updateUserWalletAfterTradeFiatToCrypto(to, email, conv.getConversionResult().getResult() * model.getExchangeValue());

		        String message = "Conversion was successful " + from + ": " + quantity + " for " + to + ": " + conv.getConversionResult().getResult() * model.getExchangeValue();
		        return ResponseEntity.ok().body(new ConversionResponse(dtoWallet.getBody(), message));
		    } else {
		        return ResponseEntity.status(400).body("You don't have enough money to exchange!");
		    }
	   }catch (FeignException e) {
           if (e.status() != 404) {
               throw handleFeignException(e);
           }
           throw new NoDataFoundException(e.getMessage());
       }
	   
		
	}

	
	public CurrencyExchangeDto acquireExchange(String from, String to) {
		return proxy.getExchange(from, to).getBody();
	}
	
	public CurrencyConversionDto exchangeToConversion(CurrencyExchangeDto exchange, double quantity) {
		double exchangeValue = exchange.getExchangeValue();
		double totalCalculatedAmount = quantity * exchangeValue;
		return new CurrencyConversionDto(exchange, quantity, exchange.getTo(), totalCalculatedAmount);
	}
	
	private RuntimeException handleFeignException(FeignException e) {
	        String serviceName = e.contentUTF8();
	        System.out.println(serviceName);
	        if (serviceName.contains("BankAccountProxy")) {
	            return new ServiceUnavailableException("Bank account service is unavailable");
	        } else if (serviceName.contains("CryptoWalletProxy")) {
	            return new ServiceUnavailableException("Crypto wallet service is unavailable");
	        } else if (serviceName.contains("CurrencyExchangeProxy")) {
	            return new ServiceUnavailableException("Currency exchange service is unavailable");
	        }else if (serviceName.contains("UsersServiceProxy")) {
	            return new ServiceUnavailableException("Users service is unavailable");
	        }
	        else {
	            return new ServiceUnavailableException("Service is unavailable");
	        }
	    }
	
	private static class ConversionResponse {
	    private final Object body;
	    private final String message;

	    public ConversionResponse(Object body, String message) {
	        this.body = body;
	        this.message = message;
	    }

	    public Object getBody() {
	        return body;
	    }

	    public String getMessage() {
	        return message;
	    }
	}
	

}
