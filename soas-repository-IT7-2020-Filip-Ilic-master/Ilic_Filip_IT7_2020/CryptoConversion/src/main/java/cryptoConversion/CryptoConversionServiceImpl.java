package cryptoConversion;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import api.dtos.BankAccountDto;
import api.dtos.CryptoConversionDto;
import api.dtos.CryptoExchangeDto;
import api.dtos.CryptoWalletDto;
import api.dtos.CurrencyConversionDto;
import api.dtos.CurrencyExchangeDto;
import api.feignProxies.CryptoExchangeProxy;
import api.feignProxies.CryptoWalletProxy;
import api.feignProxies.CurrencyExchangeProxy;
import api.feignProxies.UsersServiceProxy;
import api.services.CryptoConversionService;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import util.exceptions.NoDataFoundException;
import util.exceptions.ServiceUnavailableException;

@RestController
public class CryptoConversionServiceImpl implements CryptoConversionService{

	private static final Logger log = LoggerFactory.getLogger(CryptoConversionServiceImpl.class);

//private RestTemplate template = new RestTemplate();
	
	@Autowired
	private CryptoExchangeProxy proxy;
	
	@Autowired
	private CryptoWalletProxy proxyWallet;
	
	@Autowired
	private UsersServiceProxy proxyUser;
	
	CryptoExchangeDto response;
	
	
	Retry retry;
	
	public CryptoConversionServiceImpl(RetryRegistry registry) {
		this.retry = registry.retry("default");
	}
	
	@Override
	@CircuitBreaker(name = "cb", fallbackMethod = "fallback")
	public ResponseEntity<?> getConversion(String from, String to, double quantity, String authorizationHeader) {
		try {
			String email = proxyUser.getCurrentUserEmail(authorizationHeader);
			
			double userAmount = proxyWallet.GetUserWallet(email, from, to);
			if(userAmount == -1) {
				return ResponseEntity.status(400).body("Please, enter valid currency!");
			}
			if(userAmount >= quantity) {
				
				CryptoExchangeDto responseDto = retry.executeSupplier( () -> response = acquireExchange(from, to));			
				CryptoConversionDto conv = exchangeToConversion(responseDto, quantity);
			    System.out.println(4565);
				ResponseEntity<CryptoWalletDto> walletDto = proxyWallet.updateUserWallet(from, to, quantity, email, conv.getConversionResult().getResult());
				
				String message = "Conversion was successfull " + from + ": " + quantity + " for " + to + ": " + conv.getConversionResult().getResult();
				
				return ResponseEntity.ok().body(new Object() {
					public Object getBody() {
						return walletDto.getBody();
					}
					public String getMessage() {
						return message;
					}
				});
				}
				else {
					return ResponseEntity.status(400).body("You don't have enough crypto money to exchange!");
				}
			
			
		} catch (FeignException e) {
			if(e.status() != 404) {
				throw new ServiceUnavailableException("Crypto exchange service is unavailable");
			}
			throw new NoDataFoundException(e.getMessage());
		}
		
		
	}
	
	public CryptoExchangeDto acquireExchange(String from, String to) {
		return proxy.getExchange(from, to).getBody();
	
	}
	
	public ResponseEntity<?> fallback(CallNotPermittedException ex){
		return ResponseEntity.status(503).body("Crypto conversion service is unavailable");
	}

	public CryptoConversionDto exchangeToConversion(CryptoExchangeDto exchange, double quantity) {
		double exchangeValue = exchange.getExchangeValue();
		double totalCalculatedAmount = quantity * exchangeValue;
		return new CryptoConversionDto(exchange, quantity, exchange.getTo(), totalCalculatedAmount);
	}

}
