package com.example.demo.repository;

import com.example.demo.entity.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "users")
public interface UserRepository extends JpaRepository<User, Long>, CustomUserRepository {
	
	List<User> findByNameAndAddress_Street(String name, String street);
	
	@Query("SELECT u FROM User u WHERE u.address.street = :street")
	List<User> findByAddressStreet(@Param("street") String street);
	
	User findByName(String username);
}