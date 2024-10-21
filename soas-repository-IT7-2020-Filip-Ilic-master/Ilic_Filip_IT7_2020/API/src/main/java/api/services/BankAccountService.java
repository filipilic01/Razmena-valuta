package api.services;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

import api.dtos.BankAccountDto;

public interface BankAccountService {

    @GetMapping("/bank-account")
    List<BankAccountDto> getBankAccounts();
    
    @PostMapping("/bank-account")
    ResponseEntity<?> createBankAccount(@RequestBody BankAccountDto dto);
    
    @GetMapping("/bank-account/{bankAccountId}")
    ResponseEntity<?> getBankAccountById(@PathVariable Integer bankAccountId);
    
    @DeleteMapping("/bank-account/{bankAccountId}")
    ResponseEntity<?> deleteBankAccount(@PathVariable Integer bankAccountId);
    
    @PutMapping("/bank-account")
    ResponseEntity<?> updateBankAccount(@RequestBody BankAccountDto dto);
}
