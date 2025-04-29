package com.example.demo.service;

import com.example.demo.entity.Address;
import com.example.demo.entity.User;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.AddressRepository;
import com.example.demo.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
	
	@Mock
	private UserRepository userRepository;
	
	@Mock
	private AddressRepository addressRepository;
	
	@InjectMocks
	private UserService userService;
	
	@Test
	void createUserWithAddress_Success() {
		// Given
		Address address = new Address();
		address.setStreet("123 Main St");
		
		User user = new User();
		user.setName("john_doe");
		user.setPassword("encodedPassword");
		user.setRole("ROLE_USER");
		
		Address savedAddress = new Address();
		savedAddress.setId(1L);
		savedAddress.setStreet("123 Main St");
		
		User savedUser = new User();
		savedUser.setId(1L);
		savedUser.setName("john_doe");
		savedUser.setPassword("encodedPassword");
		savedUser.setRole("ROLE_USER");
		savedUser.setAddress(savedAddress);
		
		when(addressRepository.save(any(Address.class))).thenReturn(savedAddress);
		when(userRepository.save(any(User.class))).thenReturn(savedUser);
		
		// When
		User result = userService.createUserWithAddress(user, address);
		
		// Then
		assertNotNull(result);
		assertEquals(1L, result.getId());
		assertEquals("john_doe", result.getUsername());
		assertEquals("encodedPassword", result.getPassword());
		assertEquals("ROLE_USER", result.getRole());
		assertNotNull(result.getAddress());
		assertEquals(1L, result.getAddress()
			.getId()
		);
		assertEquals("123 Main St", result.getAddress()
			.getStreet()
		);
		assertEquals(1, result.getAuthorities()
			.size()
		);
		assertTrue(result.getAuthorities()
			.contains(new SimpleGrantedAuthority("ROLE_USER")));
		
		verify(addressRepository, times(1)).save(address);
		verify(userRepository, times(1)).save(user);
	}
	
	@Test
	void findById_Success() {
		// Given
		User user = new User();
		user.setId(1L);
		user.setName("john_doe");
		user.setRole("ROLE_USER");
		user.setPassword("password");
		
		Address address = new Address();
		address.setId(1L);
		address.setStreet("123 Main St");
		user.setAddress(address);
		
		when(userRepository.findById(1L)).thenReturn(Optional.of(user));
		
		// When
		User result = userService.findById(1L);
		
		// Then
		assertNotNull(result);
		assertEquals(1L, result.getId());
		assertEquals("john_doe", result.getUsername());
		assertEquals("ROLE_USER", result.getRole());
		assertNotNull(result.getAddress());
		assertEquals("123 Main St", result.getAddress()
			.getStreet()
		);
		assertTrue(result.isAccountNonExpired());
		assertTrue(result.isAccountNonLocked());
		assertTrue(result.isCredentialsNonExpired());
		assertTrue(result.isEnabled());
	}
	
	@Test
	void findById_NotFound() {
		// Given
		when(userRepository.findById(999L)).thenReturn(Optional.empty());
		
		// When & Then
		ResourceNotFoundException exception = assertThrows(
			ResourceNotFoundException.class,
			() -> userService.findById(999L)
		);
		
		assertEquals("User not found with id: 999", exception.getMessage());
		verify(userRepository, times(1)).findById(999L);
	}
	
	@Test
	void findAll_Success() {
		// Given
		User user1 = new User();
		user1.setId(1L);
		user1.setName("user1");
		user1.setRole("ROLE_USER");
		
		User user2 = new User();
		user2.setId(2L);
		user2.setName("user2");
		user2.setRole("ROLE_ADMIN");
		
		when(userRepository.findAll()).thenReturn(List.of(user1, user2));
		
		// When
		List<User> result = userService.findAll();
		
		// Then
		assertNotNull(result);
		assertEquals(2, result.size());
		assertEquals("user1", result.get(0)
			.getUsername()
		);
		assertEquals("ROLE_USER", result.get(0)
			.getRole()
		);
		assertEquals("user2", result.get(1)
			.getUsername()
		);
		assertEquals("ROLE_ADMIN", result.get(1)
			.getRole()
		);
	}
	
	@Test
	void findByNameAndStreet_Success() {
		// Given
		User user = new User();
		user.setId(1L);
		user.setName("john_doe");
		
		Address address = new Address();
		address.setStreet("123 Main St");
		user.setAddress(address);
		
		when(userRepository.findByNameAndAddress_Street("john_doe", "123 Main St"))
			.thenReturn(List.of(user));
		
		// When
		List<User> result = userService.findByNameAndStreet("john_doe", "123 Main St");
		
		// Then
		assertNotNull(result);
		assertEquals(1, result.size());
		assertEquals("john_doe", result.get(0)
			.getUsername()
		);
		assertEquals("123 Main St", result.get(0)
			.getAddress()
			.getStreet()
		);
	}
	
	@Test
	void findByStreet_Success() {
		// Given
		User user1 = new User();
		user1.setId(1L);
		Address address1 = new Address();
		address1.setStreet("123 Main St");
		user1.setAddress(address1);
		
		User user2 = new User();
		user2.setId(2L);
		Address address2 = new Address();
		address2.setStreet("123 Main St");
		user2.setAddress(address2);
		
		when(userRepository.findByAddressStreet("123 Main St"))
			.thenReturn(List.of(user1, user2));
		
		// When
		List<User> result = userService.findByStreet("123 Main St");
		
		// Then
		assertNotNull(result);
		assertEquals(2, result.size());
		assertTrue(result.stream()
			.allMatch(u ->
				"123 Main St".equals(u.getAddress()
					.getStreet())
			));
	}
}