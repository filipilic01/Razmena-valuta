package api.feignProxies;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import api.dtos.CurrencyExchangeDto;
import api.dtos.UserDto;

@FeignClient("currency-exchange")
public interface CurrencyExchangeProxy {

	@GetMapping("/currency-exchange")
	ResponseEntity<CurrencyExchangeDto> getExchange
			(@RequestParam(value = "from") String from, 
					@RequestParam(value = "to") String to);

}
