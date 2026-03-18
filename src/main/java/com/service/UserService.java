package com.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.model.User;
import com.repository.UserRepository;

@Service
public class UserService {

	private static final Logger logger = LoggerFactory.getLogger(UserService.class);

	@Autowired
	private UserRepository userRepo;

	/** Register a brand-new user – always assigns ROLE_NORMAL */
	public User addUser(User user) {
		logger.info("Registering new user with email: {}", user.getEmail());
		user.setRole("ROLE_NORMAL");
		User saved = this.userRepo.save(user);
		logger.info("User registered successfully with id: {}", saved.getId());
		return saved;
	}

	/** Persist an existing user exactly as-is (role is NOT overridden) */
	public User saveUser(User user) {
		logger.info("Saving user with id: {} and email: {}", user.getId(), user.getEmail());
		User saved = this.userRepo.save(user);
		logger.info("User saved successfully with id: {}", saved.getId());
		return saved;
	}

	public List<User> getAllUsers() {
		logger.info("Fetching all users");
		List<User> users = this.userRepo.findAll();
		logger.info("Total users found: {}", users.size());
		return users;
	}

	public User getUserById(int id) {
		logger.info("Fetching user by id: {}", id);
		Optional<User> user = this.userRepo.findById(id);
		if (user.isPresent()) {
			logger.info("User found with id: {}", id);
			return user.get();
		}
		logger.warn("User not found with id: {}", id);
		return null;
	}

	public void deleteUser(int id) {
		logger.info("Deleting user with id: {}", id);
		this.userRepo.deleteById(id);
		logger.info("User deleted successfully with id: {}", id);
	}

	public User getUserByEmail(String email) {
		logger.info("Fetching user by email: {}", email);
		User user = this.userRepo.getUserByEmail(email);
		if (user != null) {
			logger.info("User found with email: {}", email);
		} else {
			logger.warn("No user found with email: {}", email);
		}
		return user;
	}
}

