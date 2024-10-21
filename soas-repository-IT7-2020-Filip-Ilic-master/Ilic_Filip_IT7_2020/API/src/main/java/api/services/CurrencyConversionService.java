package api.services;



import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

public interface CurrencyConversionService {

	/*@GetMapping("/currency-conversion")
	ResponseEntity<?> getConversion(@RequestParam String from, 
			@RequestParam String to,
			@RequestParam double quantity,
			@RequestHeader("Authorization") String authorizationHeader);*/
	
	@GetMapping("/currency-conversion")
	ResponseEntity<?> getConversion(@RequestParam String from, 
			@RequestParam String to,
			@RequestParam double quantity,
			@RequestHeader("Authorization") String authorizationHeader);
}
