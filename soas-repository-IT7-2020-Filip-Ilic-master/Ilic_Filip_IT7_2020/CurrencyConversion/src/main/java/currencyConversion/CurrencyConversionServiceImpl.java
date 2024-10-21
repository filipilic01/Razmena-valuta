package currencyConversion;


import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import api.dtos.CurrencyConversionDto;
import api.dtos.CurrencyExchangeDto;
import api.dtos.BankAccountDto;
import api.feignProxies.BankAccountProxy;
import api.feignProxies.CurrencyExchangeProxy;
import api.feignProxies.UsersServiceProxy;
import api.services.CurrencyConversionService;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import util.exceptions.NoDataFoundException;
import util.exceptions.ServiceUnavailableException;

@RestController
public class CurrencyConversionServiceImpl implements CurrencyConversionService {
	
	private RestTemplate template = new RestTemplate();
	
	@Autowired
	private CurrencyExchangeProxy proxy;
	
	@Autowired
	private BankAccountProxy proxyBank;

	@Autowired
	private UsersServiceProxy proxyUser;
	
	CurrencyExchangeDto response;
	Retry retry;
	
	public CurrencyConversionServiceImpl(RetryRegistry registry) {
		this.retry = registry.retry("default");
	}

	
	@Override
	@CircuitBreaker(name = "cb", fallbackMethod = "fallback")
	public ResponseEntity<?> getConversion(String from, String to, double quantity, String authorizationHeader) {
		try {
			String email = proxyUser.getCurrentUserEmail(authorizationHeader);
			
			double userAmount = proxyBank.GetUserAccountAmount(email, from, to);
			if(userAmount == -1) {
				return ResponseEntity.status(400).body("Please, enter valid currency!");
			}
			if(userAmount >= quantity) {
				
			CurrencyExchangeDto responseDto = retry.executeSupplier( () -> response = acquireExchange(from, to));
			
			CurrencyConversionDto conv = exchangeToConversion(responseDto, quantity);
			
			ResponseEntity<BankAccountDto> bankDto = proxyBank.updateUserAccount(from, to, quantity, email, conv.getConversionResult().getResult());
			
			String message = "Conversion was successfull " + from + ": " + quantity + " for " + to + ": " + conv.getConversionResult().getResult();
			
			return ResponseEntity.ok().body(new Object() {
				public Object getBody() {
					return bankDto.getBody();
				}
				public String getMessage() {
					return message;
				}
			});
			}
			else {
				return ResponseEntity.status(400).body("You don't have enough money to exchange!");
			}
		} catch (FeignException e) {
			if(e.status() != 404) {
				throw new ServiceUnavailableException("Currency exchange service is unavailable");
			}
			throw new NoDataFoundException(e.getMessage());
		}
		
		 
	}
	
	public CurrencyExchangeDto acquireExchange(String from, String to) {
		return proxy.getExchange(from, to).getBody();
	}
	
	public ResponseEntity<?> fallback(CallNotPermittedException ex){
		return ResponseEntity.status(503).body("Currency conversion service is unavailable");
	}
	
	public CurrencyConversionDto exchangeToConversion(CurrencyExchangeDto exchange, double quantity) {
		double exchangeValue = exchange.getExchangeValue();
		double totalCalculatedAmount = quantity * exchangeValue;
		return new CurrencyConversionDto(exchange, quantity, exchange.getTo(), totalCalculatedAmount);
	}


}
