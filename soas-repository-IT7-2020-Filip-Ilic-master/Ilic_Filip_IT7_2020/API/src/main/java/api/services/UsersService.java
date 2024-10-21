package api.services;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import api.dtos.UserDto;

public interface UsersService {

	@GetMapping("/users")
	List<UserDto> getUsers();
	
	@PostMapping("/users/newAdmin")
	ResponseEntity<?> createAdmin(@RequestBody UserDto dto);
	
	@PostMapping("/users/newUser")
	ResponseEntity<?> createUser(@RequestBody UserDto dto);
	
	@DeleteMapping("/users/{userId}")
	ResponseEntity<?> deleteUser(@PathVariable Integer userId);
	
	@PutMapping("/users/editUser")
	ResponseEntity<?> updateUser(@RequestBody UserDto dto);
	
	@PutMapping("/users/editAdmin")
	ResponseEntity<?> updateAdmin(@RequestBody UserDto dto);
	
	
}
