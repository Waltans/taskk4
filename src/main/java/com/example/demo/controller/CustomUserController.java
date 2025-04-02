package com.example.demo.controller;

import com.example.demo.entity.Address;
import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/custom/users")
public class CustomUserController {
	
	private final UserService userService;
	
	public CustomUserController(UserService userService) {
		this.userService = userService;
	}
	
	@GetMapping("/by-name-and-street")
	public ResponseEntity<List<User>> getUsersByNameAndStreet(
		@RequestParam String name,
		@RequestParam String street) {
		return ResponseEntity.ok(userService.findByNameAndStreet(name, street));
	}
	
	@GetMapping("/by-street/{street}")
	public ResponseEntity<List<User>> getUsersByStreet(@PathVariable String street) {
		return ResponseEntity.ok(userService.findByStreet(street));
	}
	
	@PostMapping
	public ResponseEntity<User> createUserWithAddress(
		@RequestBody User user,
		@RequestParam String street) {
		Address address = new Address();
		address.setStreet(street);
		return ResponseEntity.ok(userService.createUserWithAddress(user, address));
	}
}