package bankAccount.implementation;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import api.dtos.BankAccountDto;
import api.dtos.UserDto;
import api.feignProxies.BankAccountProxy;
import api.feignProxies.UsersServiceProxy;
import api.services.BankAccountService;
import bankAccount.model.BankAccountModel;
import bankAccount.repository.BankAccountRepository;
import feign.FeignException;
import util.exceptions.NoDataFoundException;
import util.exceptions.ServiceUnavailableException;


@RestController
public class BankAccountServiceImpl implements BankAccountService, BankAccountProxy {

	@Autowired
	private BankAccountRepository repo;
	
	@Autowired
	private UsersServiceProxy proxy;
	
	@Override
	public List<BankAccountDto> getBankAccounts() {
		List<BankAccountModel> listOfModels = repo.findAll();
		ArrayList<BankAccountDto> listOfDtos = new ArrayList<BankAccountDto>();
		for(BankAccountModel model: listOfModels) {
			listOfDtos.add(convertModelToDto(model));
		}
		return listOfDtos;
	}

	@Override
	public ResponseEntity<?> createBankAccount(BankAccountDto dto) {
		try {
			if(proxy.findUserByEmail(dto.getEmail())) {
			
				if(repo.findByEmail(dto.getEmail()) == null) {
					
					BankAccountModel model = convertDtoToModel(dto);
					
					
					return ResponseEntity.status(201).body(repo.save(model));
				}
				return ResponseEntity.status(404).body("User with forwarded email already has account!");
			}
			return ResponseEntity.status(400).body("User with forwarded email doesn't exists!");
		}
		catch (FeignException e) {
			if(e.status() != 404) {
				throw new ServiceUnavailableException("Users service is unavailable");
			}
			throw new NoDataFoundException(e.getMessage());
		}
		
		
	}
	
	@Override
	public ResponseEntity<?> getBankAccountById(Integer bankAccountId) {
		if(repo.existsById(bankAccountId)) {
			Optional<BankAccountModel> model = repo.findById(bankAccountId);
			if (model.isPresent()) {
		        BankAccountDto dto = convertModelToDto(model.get());
		        return ResponseEntity.status(200).body(dto);
		    } else {
		        return ResponseEntity.status(404).body("Bank account with forwarded id doesn't exist!");
		    }
		}
		
		return ResponseEntity.status(404).body("Bank account with forwarded id doesn't exists!");
	}
	

	@Override
	public ResponseEntity<?> deleteBankAccount(Integer bankAccountId) {
		if(repo.existsById(bankAccountId)) {
			repo.deleteById(bankAccountId);
			return ResponseEntity.status(200).body("Bank account successfully deleted");
		}
		return ResponseEntity.status(404).body("Bank account with forwarded id does not exist");
	}

	@Override
	public ResponseEntity<?> updateBankAccount(BankAccountDto dto) {
		if(repo.findByEmail(dto.getEmail()) != null) {
			repo.updateBankAccount(dto.getEmail(), dto.getRsd(),dto.getGbp(), dto.getEur(),dto.getUsd(), dto.getChf());
			return ResponseEntity.status(200).body(dto);
		}
		return ResponseEntity.status(404).body("Bank account with forwarded email does not exist");
	}
	
	public BankAccountModel convertDtoToModel(BankAccountDto dto) {
		return new BankAccountModel(dto.getRsd(), dto.getGbp(),dto.getEur(),dto.getUsd(), dto.getChf(), dto.getEmail());
	}
	
	public BankAccountDto convertModelToDto(BankAccountModel model) {
		return new BankAccountDto(model.getRsd(), model.getGbp(),model.getEur(),model.getUsd(), model.getChf(), model.getEmail());
	}

	@Override
	public void deleteUserAccount(String email) {
		BankAccountModel account = repo.findByEmail(email);
		if (account != null) {
			repo.delete(account);
		}
		
		
	}

	@Override
	public ResponseEntity<?> createUserAccount(String email) {
		
		BankAccountModel model = new BankAccountModel(0, 0, 0, 0,0,email);
		return ResponseEntity.status(201).body(repo.save(model));
	}

	@Override
	public double GetUserAccountAmount(String email, String from, String to) {
		BankAccountModel account = repo.findByEmail(email);
		
		String currFrom = from.toLowerCase();
		
		String currTo = to.toLowerCase();
		
		
			if(currFrom.equals("rsd")) {
				double amount =  account.getRsd();
				return amount;
			}else if (currFrom.equals("gbp")) {
				double amount = account.getGbp();
				return amount;
			}else if (currFrom.equals("eur")) {
				 double amount= account.getEur();
				 return amount;
			}else if (currFrom.equals("usd")) {
				double amount=account.getUsd();
				return amount;
			}else if (currFrom.equals("chf")) {
				double amount = account.getChf();
				return amount;
			}
			else {
				return -1;
			}
		
		
		
	}

	@Override
	public ResponseEntity<BankAccountDto> updateUserAccount(String from, String to, double quantity, String email, double result) {
		BankAccountModel existingAccount = repo.findByEmail(email);
		if(existingAccount != null) {
			from = from.toLowerCase();
			to = to.toLowerCase();
			 
            switch (from) {
                case "rsd":
                    existingAccount.setRsd(existingAccount.getRsd() - quantity);
                    break;
                case "usd":
                    existingAccount.setUsd(existingAccount.getUsd() - quantity);
                    break;
                case "gbp":
                    existingAccount.setGbp(existingAccount.getGbp() - quantity);
                    break;
                case "chf":
                    existingAccount.setChf(existingAccount.getChf() - quantity);
                    break;
                case "eur":
                    existingAccount.setEur(existingAccount.getEur() - quantity);
                    break;
                default:
                    return ResponseEntity.status(400).body(null); 
            }
            
            switch (to) {
                case "rsd":
                    existingAccount.setRsd(existingAccount.getRsd() + result);
                    break;
                case "usd":
                    existingAccount.setUsd(existingAccount.getUsd() + result);
                    break;
                case "gbp":
                    existingAccount.setGbp(existingAccount.getGbp() + result);
                    break;
                case "chf":
                    existingAccount.setChf(existingAccount.getChf() + result);
                    break;
                case "eur":
                    existingAccount.setEur(existingAccount.getEur() + result);
                    break;
                default:
                    return ResponseEntity.status(400).body(null); 
			
		}
            
            repo.save(existingAccount);
           
            BankAccountDto dto =convertModelToDto(existingAccount);
            return ResponseEntity.ok(dto);
		
	}else {
		return null;
	}

}

	@Override
	public ResponseEntity<BankAccountDto> updateUserAccountAfterTradeFiatToCrypto(String from, double quantity, String email) {
		BankAccountModel existingAccount = repo.findByEmail(email);
		from = from.toLowerCase();
		if(existingAccount != null) {
			switch (from) {
            case "usd":
                existingAccount.setUsd(existingAccount.getUsd() - quantity);
                break;
            case "eur":
                existingAccount.setEur(existingAccount.getEur() - quantity);
                break;
            default:
                return ResponseEntity.status(400).body(null); 
		
	}

            repo.save(existingAccount);
           
            BankAccountDto dto =convertModelToDto(existingAccount);
            return ResponseEntity.ok(dto);
		
		}else {
			return null;
		}
	}

	@Override
	public ResponseEntity<BankAccountDto> updateUserAccountAfterTradeCryptoToFiat(String to, double quantity,
			String email) {
		BankAccountModel existingAccount = repo.findByEmail(email);
		to = to.toLowerCase();
		if(existingAccount != null) {
			switch (to) {
            case "usd":
                existingAccount.setUsd(existingAccount.getUsd() + quantity);
                break;
            case "eur":
                existingAccount.setEur(existingAccount.getEur() + quantity);
                break;
            default:
                return ResponseEntity.status(400).body(null); 
		
	}

            repo.save(existingAccount);
           
            BankAccountDto dto =convertModelToDto(existingAccount);
            return ResponseEntity.ok(dto);
		
		}else {
			return null;
		}
	}
}
