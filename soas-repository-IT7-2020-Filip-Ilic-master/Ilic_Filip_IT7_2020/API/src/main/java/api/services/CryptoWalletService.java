package api.services;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import api.dtos.BankAccountDto;
import api.dtos.CryptoWalletDto;

public interface CryptoWalletService {

	  @GetMapping("/crypto-wallet")
	    List<CryptoWalletDto> getCryptoWallets();
	    
	    @PostMapping("/crypto-wallet")
	    ResponseEntity<?> createCryptoWallet(@RequestBody CryptoWalletDto dto);
	    
	    @GetMapping("/crypto-wallet/{cryptoWalletId}")
	    ResponseEntity<?> getCryptoWalletById(@PathVariable Integer cryptoWalletId);
	    
	    @DeleteMapping("/crypto-wallet/{cryptoWalletId}")
	    ResponseEntity<?> deleteCryptoWallet(@PathVariable Integer cryptoWalletId);
	    
	    @PutMapping("/crypto-wallet")
	    ResponseEntity<?> updateCryptoWallet(@RequestBody CryptoWalletDto dto);
}
