package com.example.demo.service;

import com.example.demo.entity.Address;
import com.example.demo.entity.User;
import com.example.demo.repository.AddressRepository;
import com.example.demo.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class UserService {
	
	private final UserRepository userRepository;
	private final AddressRepository addressRepository;
	
	public UserService(UserRepository userRepository, AddressRepository addressRepository) {
		this.userRepository = userRepository;
		this.addressRepository = addressRepository;
	}
	
	public Optional<User> findById(Long id) {
		return userRepository.findById(id);
	}
	
	@Transactional
	public User createUserWithAddress(User user, Address address) {
		Address savedAddress = addressRepository.save(address);
		user.setAddress(savedAddress);
		return userRepository.save(user);
	}
	
	@Transactional
	public User updateUserWithAddress(Long userId, User userDetails, Address addressDetails) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new RuntimeException("User not found"));
		
		Address address = user.getAddress();
		if (address == null) {
			address = new Address();
		}
		
		address.setStreet(addressDetails.getStreet());
		Address savedAddress = addressRepository.save(address);
		
		user.setName(userDetails.getName());
		user.setAddress(savedAddress);
		
		return userRepository.save(user);
	}
}