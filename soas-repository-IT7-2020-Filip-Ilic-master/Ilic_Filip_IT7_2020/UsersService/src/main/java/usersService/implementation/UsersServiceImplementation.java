package usersService.implementation;

import java.util.ArrayList;
import org.bouncycastle.util.encoders.Base64;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import api.dtos.UserDto;
import api.feignProxies.BankAccountProxy;
import api.feignProxies.CryptoWalletProxy;
import api.feignProxies.UsersServiceProxy;
import api.services.UsersService;
import feign.FeignException;
import usersService.model.UserModel;
import usersService.repository.UsersServiceRepository;
import util.exceptions.NoDataFoundException;
import util.exceptions.ServiceUnavailableException;

@RestController
public class UsersServiceImplementation implements UsersService, UsersServiceProxy {

	@Autowired
	private UsersServiceRepository repo;
	
	@Autowired
	private BankAccountProxy proxy;
	
	@Autowired
	private CryptoWalletProxy proxyCrypto;
	
	@Override
	public List<UserDto> getUsers() {
		List<UserModel> listOfModels = repo.findAll();
		ArrayList<UserDto> listOfDtos = new ArrayList<UserDto>();
		for(UserModel model: listOfModels) {
			listOfDtos.add(convertModelToDto(model));
		}
		return listOfDtos;
	}
	


	@Override
	public ResponseEntity<?> createAdmin(UserDto dto) {
		if(repo.findByEmail(dto.getEmail()) == null) {
			dto.setRole("ADMIN");
			UserModel model = convertDtoToModel(dto);
			
			return ResponseEntity.status(201).body(repo.save(model));
		}
		return ResponseEntity.status(409).body("Admin with forwarded email already exists");
	}

	@Override
	public ResponseEntity<?> createUser(UserDto dto) {
		try {
			if(repo.findByEmail(dto.getEmail()) == null) {
				dto.setRole("USER");
				UserModel model = convertDtoToModel(dto);
				proxy.createUserAccount(dto.getEmail());
				proxyCrypto.createUserWallet(dto.getEmail());
				
				return ResponseEntity.status(201).body(repo.save(model));
			}
			return ResponseEntity.status(409).body("User with forwarded email already exists");
		}catch (FeignException e) {
            if (e.status() != 404) {
                throw handleFeignException(e);
            }
            throw new NoDataFoundException(e.getMessage());
        }
		
	}

	@Override
	public ResponseEntity<?> updateUser(UserDto dto) {
		if(repo.findByEmail(dto.getEmail()) != null) {
			dto.setRole("USER");
			repo.updateUser(dto.getEmail(), dto.getPassword(), dto.getRole());
			return ResponseEntity.status(200).body(dto);
		}
		return ResponseEntity.status(404).body("User with forwarded email does not exist");
	}
	
	@Override
	public ResponseEntity<?> updateAdmin(UserDto dto) {
		if(repo.findByEmail(dto.getEmail()) != null) {
			dto.setRole("ADMIN");
			repo.updateUser(dto.getEmail(), dto.getPassword(), dto.getRole());
			return ResponseEntity.status(200).body(dto);
		}
		return ResponseEntity.status(404).body("Admin with forwarded email does not exist");
		
	}
	
	public UserModel convertDtoToModel(UserDto dto) {
		return new UserModel(dto.getEmail(), dto.getPassword(), dto.getRole());
	}
	
	public UserDto convertModelToDto(UserModel model) {
		return new UserDto(model.getEmail(), model.getPassword(), model.getRole());
	}

	@Override
	public ResponseEntity<?> deleteUser(Integer userId) {
		try {
			if(repo.existsById(userId)) {
				UserModel user =  repo.findById(userId).orElse(null);
				if(user.getRole().equals("OWNER")) {
					return ResponseEntity.status(403).body("You don't have permission to delete this user.");
				}
				repo.deleteById(userId);
				proxy.deleteUserAccount(user.getEmail());
				proxyCrypto.deleteUserWallet(user.getEmail());
				return ResponseEntity.status(200).body("User successfully deleted");
			}
			return ResponseEntity.status(404).body("User with forwarded id does not exist");
		}
		catch (FeignException e) {
            if (e.status() != 404) {
                throw handleFeignException(e);
            }
            throw new NoDataFoundException(e.getMessage());
        }
		
	}

	@Override
	public Boolean findUserByEmail(String email) {
		if(repo.findByEmail(email) != null) {
			return true;
		}
		return false;
	}

	@Override
	public String getCurrentUserEmail(String authorizationHeader) {
		String email = extractEmail(authorizationHeader);
		if(repo.findByEmail(email) != null)
		return email;
		else return null;
	}
	
	public String extractEmail(String authorizationHeader) {
		String encodedCredentials = authorizationHeader.replaceFirst("Basic ", "");
		byte[] decodedBytes = Base64.decode(encodedCredentials.getBytes());
		String decodedCredentials = new String(decodedBytes);
		String[] credentials = decodedCredentials.split(":");
		String email = credentials[0]; 
		System.out.println(email);
		return email;
	}

	private RuntimeException handleFeignException(FeignException e) {
        String serviceName = e.contentUTF8();
        if (serviceName.contains("BankAccountProxy")) {
            return new ServiceUnavailableException("Bank account service is unavailable");
        } else if (serviceName.contains("CryptoWalletProxy")) {
            return new ServiceUnavailableException("Crypto wallet service is unavailable");
        }
        else {
            return new ServiceUnavailableException("Service is unavailable");
        }
    }



	@Override
	public List<UserDto> getAllUsers() {
		List<UserModel> listOfModels = repo.findAll();
		ArrayList<UserDto> listOfDtos = new ArrayList<UserDto>();
		for(UserModel model: listOfModels) {
			listOfDtos.add(convertModelToDto(model));
		}
		return listOfDtos;
	}


}
