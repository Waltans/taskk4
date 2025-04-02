package com.example.demo.service;

import com.example.demo.entity.Address;
import com.example.demo.entity.User;
import com.example.demo.repository.CustomUserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CustomUserRepositoryImpl implements CustomUserRepository {
	
	@PersistenceContext
	private EntityManager entityManager;
	
	@Override
	public List<User> findUsersByNameAndStreet(String name, String street) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<User> query = cb.createQuery(User.class);
		Root<User> user = query.from(User.class);
		
		Join<User, Address> address = user.join("address");
		
		Predicate namePredicate = cb.equal(user.get("name"), name);
		Predicate streetPredicate = cb.equal(address.get("street"), street);
		
		query.where(cb.and(namePredicate, streetPredicate));
		
		return entityManager.createQuery(query).getResultList();
	}
	
	@Override
	public List<User> findUsersByAddressStreet(String street) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<User> query = cb.createQuery(User.class);
		Root<User> user = query.from(User.class);
		
		Join<User, Address> address = user.join("address");
		query.where(cb.equal(address.get("street"), street));
		
		return entityManager.createQuery(query).getResultList();
	}
}