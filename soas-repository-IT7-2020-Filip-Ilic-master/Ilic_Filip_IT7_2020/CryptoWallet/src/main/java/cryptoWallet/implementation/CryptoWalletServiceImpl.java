package cryptoWallet.implementation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import api.dtos.BankAccountDto;
import api.dtos.CryptoWalletDto;
import api.feignProxies.CryptoWalletProxy;
import api.feignProxies.UsersServiceProxy;
import api.services.CryptoWalletService;

import cryptoWallet.model.CryptoWalletModel;
import cryptoWallet.repository.CryptoWalletRepository;
import feign.FeignException;
import util.exceptions.NoDataFoundException;
import util.exceptions.ServiceUnavailableException;


@RestController
public class CryptoWalletServiceImpl implements CryptoWalletService, CryptoWalletProxy{

	@Autowired
	private CryptoWalletRepository repo;
	
	@Autowired
	private UsersServiceProxy proxy;
	
	
	
	@Override
	public List<CryptoWalletDto> getCryptoWallets() {
		List<CryptoWalletModel> listOfModels = repo.findAll();
		ArrayList<CryptoWalletDto> listOfDtos = new ArrayList<CryptoWalletDto>();
		for(CryptoWalletModel model: listOfModels) {
			listOfDtos.add(convertModelToDto(model));
		}
		return listOfDtos;
	}

	@Override
	public ResponseEntity<?> createCryptoWallet(CryptoWalletDto dto) {
		try {
			if(proxy.findUserByEmail(dto.getEmail())) {
				if(repo.findByEmail(dto.getEmail()) == null) {
					
					CryptoWalletModel model = convertDtoToModel(dto);
					
					return ResponseEntity.status(201).body(repo.save(model));
				}
				return ResponseEntity.status(404).body("User with forwarded email already has crypto wallet!");
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
	public ResponseEntity<?> getCryptoWalletById(Integer cryptoWalletId) {
		if(repo.existsById(cryptoWalletId)) {
			Optional<CryptoWalletModel> model = repo.findById(cryptoWalletId);
			if (model.isPresent()) {
				CryptoWalletDto dto = convertModelToDto(model.get());
		        return ResponseEntity.status(200).body(dto);
		    } else {
		        return ResponseEntity.status(404).body("Crypto wallet with forwarded id doesn't exist!");
		    }
		}
		
		return ResponseEntity.status(404).body("Crypto wallet with forwarded id doesn't exists!");
	}

	@Override
	public ResponseEntity<?> deleteCryptoWallet(Integer cryptoWalletId) {
		if(repo.existsById(cryptoWalletId)) {
			repo.deleteById(cryptoWalletId);
			return ResponseEntity.status(200).body("Crypto wallet successfully deleted");
		}
		return ResponseEntity.status(404).body("Crypto wallet with forwarded id does not exist");
	}

	@Override
	public ResponseEntity<?> updateCryptoWallet(CryptoWalletDto dto) {
		if(repo.findByEmail(dto.getEmail()) != null) {
			repo.updateCryptoWallet(dto.getEmail(), dto.getEth(),dto.getBnb(), dto.getBtc());
			return ResponseEntity.status(200).body(dto);
		}
		return ResponseEntity.status(404).body("Crypto wallet with forwarded email does not exist");
	}
	
	public CryptoWalletModel convertDtoToModel(CryptoWalletDto dto) {
		return new CryptoWalletModel(dto.getEth(), dto.getBnb(),dto.getBtc(), dto.getEmail());
	}
	
	public CryptoWalletDto convertModelToDto(CryptoWalletModel model) {
		return new CryptoWalletDto(model.getEth(), model.getBnb(),model.getBtc(), model.getEmail());
	}
	

	@Override
	public void deleteUserWallet(String email) {
		CryptoWalletModel account = repo.findByEmail(email);
		if (account != null) {
			repo.delete(account);
		}
		
		
	}

	@Override
	public ResponseEntity<?> createUserWallet(String email) {
		
		CryptoWalletModel model = new CryptoWalletModel(0, 0,0,email);
		return ResponseEntity.status(201).body(repo.save(model));
	}

	@Override
	public double GetUserWallet(String email, String from, String to) {
		CryptoWalletModel account = repo.findByEmail(email);
		String currFrom = from.toLowerCase();
		String currTo= to.toLowerCase();
		
		
			if(currFrom.equals("eth")) {
				return account.getEth();
			}else if (currFrom.equals("bnb")) {
				return account.getBnb();
			}else if (currFrom.equals("btc")) {
				return account.getBtc();
			}
			else {
				return -1;
			}
		
		
		
		
		
	}

	@Override
	public ResponseEntity<CryptoWalletDto> updateUserWallet(String from, String to, double quantity, String email,
			double result) {
		CryptoWalletModel existingWallet = repo.findByEmail(email);
		if(existingWallet != null) {
			from = from.toLowerCase();
			to = to.toLowerCase();
			 
            switch (from) {
                case "btc":
                	existingWallet.setBtc(existingWallet.getBtc() - quantity);
                    break;
                case "eth":
                	existingWallet.setEth(existingWallet.getEth() - quantity);
                    break;
                case "bnb":
                	existingWallet.setBnb(existingWallet.getBnb() - quantity);
                    break;
                default:
                    return ResponseEntity.status(400).body(null); 
            }
            
            switch (to) {
                case "btc":
                	existingWallet.setBtc(existingWallet.getBtc() + result);
                    break;
                case "eth":
                	existingWallet.setEth(existingWallet.getEth() + result);
                    break;
                case "bnb":
                	existingWallet.setBnb(existingWallet.getBnb() + result);
                    break;
                default:
                    return ResponseEntity.status(400).body(null); 
			
		}
            
            repo.save(existingWallet);
           
            CryptoWalletDto dto =convertModelToDto(existingWallet);
            return ResponseEntity.ok(dto);
		
	}else {
		return null;
	}
	}

	@Override
	public ResponseEntity<CryptoWalletDto> updateUserWalletAfterTradeFiatToCrypto(String to, String email,
			double result) {
		CryptoWalletModel existingWallet = repo.findByEmail(email);
		if(existingWallet != null) {
			
			to = to.toLowerCase();
			 
            
            switch (to) {
                case "btc":
                	existingWallet.setBtc(existingWallet.getBtc() + result);
                    break;
                case "eth":
                	existingWallet.setEth(existingWallet.getEth() + result);
                    break;
                case "bnb":
                	existingWallet.setBnb(existingWallet.getBnb() + result);
                    break;
                default:
                    return ResponseEntity.status(400).body(null); 
			
		}
            
            repo.save(existingWallet);
           
            CryptoWalletDto dto =convertModelToDto(existingWallet);
            return ResponseEntity.ok(dto);
		
	}else {
		return null;
	}
	}

	@Override
	public ResponseEntity<CryptoWalletDto> updateUserWalletAfterTradeCryptoToFiat(String from, String email,
			double result) {
		CryptoWalletModel existingWallet = repo.findByEmail(email);
		if(existingWallet != null) {
			
			from = from.toLowerCase();
			 
            
            switch (from) {
                case "btc":
                	existingWallet.setBtc(existingWallet.getBtc() - result);
                    break;
                case "eth":
                	existingWallet.setEth(existingWallet.getEth() - result);
                    break;
                case "bnb":
                	existingWallet.setBnb(existingWallet.getBnb() - result);
                    break;
                default:
                    return ResponseEntity.status(400).body(null); 
			
		}
            
            repo.save(existingWallet);
           
            CryptoWalletDto dto =convertModelToDto(existingWallet);
            return ResponseEntity.ok(dto);
		
	}else {
		return null;
	}
	}

}
