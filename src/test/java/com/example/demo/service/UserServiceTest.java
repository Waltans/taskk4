package com.example.demo.service;

import com.example.demo.entity.Address;
import com.example.demo.entity.User;
import com.example.demo.repository.AddressRepository;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class UserServiceTest {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private AddressRepository addressRepository;
	
	@Test
	public void testCreateUserWithAddress() {
		Address address = new Address();
		address.setStreet("Test Street");
		
		User user = new User();
		user.setName("Test User");
		
		User savedUser = userService.createUserWithAddress(user, address);
		
		assertNotNull(savedUser.getId());
		assertNotNull(savedUser.getAddress().getId());
		assertEquals("Test Street", savedUser.getAddress().getStreet());
	}
}