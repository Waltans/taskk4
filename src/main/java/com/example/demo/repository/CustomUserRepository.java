package com.example.demo.repository;

import com.example.demo.entity.User;
import java.util.List;

public interface CustomUserRepository {
	List<User> findByNameAndAddress_Street(String name, String street);
	List<User> findByAddressStreet(String street);
}