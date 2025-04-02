package com.example.demo.repository;

import com.example.demo.entity.User;
import java.util.List;

public interface CustomUserRepository {
	List<User> findUsersByNameAndStreet(String name, String street);
	List<User> findUsersByAddressStreet(String street);
}