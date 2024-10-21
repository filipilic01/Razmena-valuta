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
import api.dtos.CryptoWalletDto;

@FeignClient("crypto-wallet")
public interface CryptoWalletProxy {

	@DeleteMapping("/crypto-wallet/wallet/{email}")
    void deleteUserWallet(@PathVariable("email") String email);
    
    @PostMapping("/crypto-wallet/wallet")
    ResponseEntity<?> createUserWallet(@RequestBody String email);  
    
    @GetMapping("/crypto-wallet/wallet")
    double GetUserWallet(@RequestParam("email") String email, @RequestParam("from") String from,  @RequestParam("to") String to);
    
    @PutMapping("crypto-wallet/wallet")
    ResponseEntity<CryptoWalletDto> updateUserWallet
    (@RequestParam("from") String from,
    @RequestParam("to") String to,
    @RequestParam("quantity") double quantity, 
    @RequestParam("email") String email, 
    @RequestParam("result") double result); 
    
    @PutMapping("crypto-wallet/walletTrade")
    ResponseEntity<CryptoWalletDto> updateUserWalletAfterTradeFiatToCrypto
    (
    @RequestParam("to") String to,
    @RequestParam("email") String email, 
    @RequestParam("result") double result); 
    
    @PutMapping("crypto-wallet/walletTradeCrypto")
    ResponseEntity<CryptoWalletDto> updateUserWalletAfterTradeCryptoToFiat
    (
    @RequestParam("from") String from,
    @RequestParam("email") String email, 
    @RequestParam("result") double result); 
}
