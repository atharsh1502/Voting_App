package com.controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

import com.model.User;
import com.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
@Tag(name = "User Controller", description = "Endpoints for user registration and dashboard")
public class UserController {

	private static final Logger logger = LoggerFactory.getLogger(UserController.class);

	@Autowired
	private UserService userServ;

	@PostMapping("/register")
	@Operation(summary = "Create a new user")
	public ResponseEntity<String> createUser(@RequestBody User user) {
		logger.info("POST /register - Registration attempt for email: {}", user.getEmail());

		if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
			logger.warn("POST /register - Rejected: email is blank");
			return ResponseEntity.badRequest().body("Email is required");
		}
		if (user.getName() == null || user.getName().trim().isEmpty()) {
			logger.warn("POST /register - Rejected: name is blank for email: {}", user.getEmail());
			return ResponseEntity.badRequest().body("Name is required");
		}
		if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
			logger.warn("POST /register - Rejected: password is blank for email: {}", user.getEmail());
			return ResponseEntity.badRequest().body("Password is required");
		}
		if (user.getPassword().length() < 4) {
			logger.warn("POST /register - Rejected: password too short for email: {}", user.getEmail());
			return ResponseEntity.badRequest().body("Password must be at least 4 characters");
		}

		String email = user.getEmail();

		if (userServ.getUserByEmail(email) != null) {
			logger.warn("POST /register - Rejected: email already exists: {}", email);
			return ResponseEntity.badRequest().body("Registration Failed, Email already exists");
		}

		User newUser = new User();
		newUser.setEmail(user.getEmail());
		newUser.setName(user.getName());
		newUser.setPassword(user.getPassword());
		newUser.setPhone(user.getPhone());
		newUser.setRole("ROLE_NORMAL");
		newUser.setStatus(null);

		userServ.addUser(newUser);
		logger.info("POST /register - User registered successfully with email: {}", email);
		return ResponseEntity.status(HttpStatus.CREATED).body("Registration successful");
	}

	@GetMapping("/user")
	@Operation(summary = "Get user dashboard")
	public ResponseEntity<User> dashboard(Principal p) {
		logger.info("GET /user - Dashboard request");

		if (p == null) {
			logger.warn("GET /user - Rejected: no authenticated principal");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		String email = p.getName();
		logger.info("GET /user - Fetching dashboard for email: {}", email);
		User user = userServ.getUserByEmail(email);

		if (user == null) {
			logger.error("GET /user - User not found in DB for email: {}", email);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}

		logger.info("GET /user - Returning dashboard for user: {}, voted: {}", email, user.getStatus());
		return ResponseEntity.ok(user);
	}

	@GetMapping("/user/vote-status")
	@Operation(summary = "Check whether the logged-in user has already voted")
	public ResponseEntity<Map<String, Object>> getVoteStatus(Principal p) {
		logger.info("GET /user/vote-status - Vote-status request");

		if (p == null) {
			logger.warn("GET /user/vote-status - Rejected: no authenticated principal");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		String email = p.getName();
		logger.info("GET /user/vote-status - Checking vote status for email: {}", email);
		User user = userServ.getUserByEmail(email);

		if (user == null) {
			logger.error("GET /user/vote-status - User not found for email: {}", email);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}

		boolean hasVoted = "Voted".equals(user.getStatus());
		Map<String, Object> response = new HashMap<>();
		response.put("hasVoted", hasVoted);
		response.put("votedFor", hasVoted ? user.getVotedFor() : null);

		logger.info("GET /user/vote-status - User '{}' hasVoted={}, votedFor='{}'",
				email, hasVoted, user.getVotedFor());
		return ResponseEntity.ok(response);
	}

	@GetMapping("/admin/users")
	@Operation(summary = "Get all users (Admin only)")
	public ResponseEntity<List<User>> getAllUsers() {
		logger.info("GET /admin/users - Fetching all users");
		List<User> users = userServ.getAllUsers();
		logger.info("GET /admin/users - Returning {} user(s)", users.size());
		return ResponseEntity.ok(users);
	}

	@GetMapping("/user/{id}")
	@Operation(summary = "Get user by ID")
	public ResponseEntity<User> getUserById(@PathVariable int id) {
		logger.info("GET /user/{} - Fetching user by id", id);
		User user = userServ.getUserById(id);

		if (user == null) {
			logger.warn("GET /user/{} - User not found", id);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}

		logger.info("GET /user/{} - User found: {}", id, user.getEmail());
		return ResponseEntity.ok(user);
	}

	@PutMapping("/user/{id}")
	@Operation(summary = "Update an existing user")
	public ResponseEntity<String> updateUser(@PathVariable int id, @RequestBody User user) {
		logger.info("PUT /user/{} - Update request", id);
		User existingUser = userServ.getUserById(id);

		if (existingUser == null) {
			logger.warn("PUT /user/{} - User not found", id);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
		}

		if (user.getName() != null && !user.getName().isEmpty()) {
			logger.info("PUT /user/{} - Updating name", id);
			existingUser.setName(user.getName());
		}
		if (user.getEmail() != null && !user.getEmail().isEmpty()) {
			logger.info("PUT /user/{} - Updating email to: {}", id, user.getEmail());
			existingUser.setEmail(user.getEmail());
		}
		if (user.getPhone() != 0) {
			existingUser.setPhone(user.getPhone());
		}
		if (user.getPassword() != null && !user.getPassword().isEmpty()) {
			existingUser.setPassword(user.getPassword());
		}

		userServ.saveUser(existingUser); // preserves role and vote status
		logger.info("PUT /user/{} - User updated successfully", id);
		return ResponseEntity.ok("User updated successfully");
	}

	@DeleteMapping("/user/{id}")
	@Operation(summary = "Delete a user account")
	public ResponseEntity<String> deleteUser(@PathVariable int id) {
		logger.info("DELETE /user/{} - Delete request", id);
		User user = userServ.getUserById(id);

		if (user == null) {
			logger.warn("DELETE /user/{} - User not found", id);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
		}

		userServ.deleteUser(id);
		logger.info("DELETE /user/{} - User deleted successfully", id);
		return ResponseEntity.ok("User deleted successfully");
	}

}
