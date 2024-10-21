package api.feignProxies;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import api.dtos.UserDto;

import org.springframework.http.ResponseEntity;


@FeignClient("users-service")
public interface UsersServiceProxy {

	@GetMapping("/users/findEmail/{email}")
	Boolean findUserByEmail(@PathVariable("email") String email);
	
	@GetMapping("/users/email")
	String getCurrentUserEmail(@RequestHeader("Authorization") String authorizationHeader);
	
	@GetMapping("/users/users")
	List<UserDto> getAllUsers();
}
