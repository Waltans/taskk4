package com.example.demo.service;

import com.example.demo.entity.Address;
import com.example.demo.entity.User;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.AddressRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {
	
	private final UserRepository userRepository;
	private final AddressRepository addressRepository;
	
	public UserService(UserRepository userRepository, AddressRepository addressRepository) {
		this.userRepository = userRepository;
		this.addressRepository = addressRepository;
	}
	
	@Transactional
	public User createUserWithAddress(User user, Address address) {
		Address savedAddress = addressRepository.save(address);
		user.setAddress(savedAddress);
		return userRepository.save(user);
	}
	
	public List<User> findByNameAndStreet(String name, String street) {
		return userRepository.findByNameAndAddressStreet(name, street);
	}
	
	public List<User> findByStreet(String street) {
		return userRepository.findByAddressStreet(street);
	}
	
	public User findById(Long id) {
		return userRepository.findById(id)
			.orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
	}
	
	public List<User> findAll() {
		return (List<User>) userRepository.findAll();
	}
}