package api.feignProxies;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import api.dtos.BankAccountDto;

@FeignClient(name = "bank-account")
public interface BankAccountProxy {

    @DeleteMapping("/bank-account/account/{email}")
    void deleteUserAccount(@PathVariable("email") String email);
    
    @PostMapping("/bank-account/account")
    ResponseEntity<?> createUserAccount(@RequestBody String email);  
    
    @GetMapping("/bank-account/account")
    double GetUserAccountAmount(@RequestParam("email") String email, @RequestParam("from") String from, @RequestParam("to") String to);
    
    @PutMapping("bank-account/account")
    ResponseEntity<BankAccountDto> updateUserAccount
    (@RequestParam("from") String from,
    @RequestParam("to") String to,
    @RequestParam("quantity") double quantity, 
    @RequestParam("email") String email, 
    @RequestParam("result") double result); 
    
    @PutMapping("bank-account/accountTrade")
    ResponseEntity<BankAccountDto> updateUserAccountAfterTradeFiatToCrypto
    (
    @RequestParam("from") String from,
    @RequestParam("quantity") double quantity, 
    @RequestParam("email") String email); 
    
    @PutMapping("bank-account/accountTradeCurrency")
    ResponseEntity<BankAccountDto> updateUserAccountAfterTradeCryptoToFiat
    (
    @RequestParam("to") String to,
    @RequestParam("quantity") double quantity, 
    @RequestParam("email") String email); 
    
}
