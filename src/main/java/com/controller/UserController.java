package com.controller;

import java.security.Principal;
import java.util.List;

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
	
	@Autowired
	private UserService userServ;
	
	@PostMapping("/register")
	@Operation(summary = "Create a new user")
	public ResponseEntity<String> createUser(@RequestBody User user)
	{
		// Validation
		if(user.getEmail() == null || user.getEmail().trim().isEmpty()) {
			return ResponseEntity.badRequest().body("Email is required");
		}
		if(user.getName() == null || user.getName().trim().isEmpty()) {
			return ResponseEntity.badRequest().body("Name is required");
		}
		if(user.getPassword() == null || user.getPassword().trim().isEmpty()) {
			return ResponseEntity.badRequest().body("Password is required");
		}
		if(user.getPassword().length() < 4) {
			return ResponseEntity.badRequest().body("Password must be at least 4 characters");
		}
		
		String email = user.getEmail();
		
		if(userServ.getUserByEmail(email) != null)
		{
			return ResponseEntity.badRequest().body("Registration Failed, Email already exists");
		}
		else{
			 
			User newUser = new User();
			newUser.setEmail(user.getEmail());
			newUser.setName(user.getName());
			newUser.setPassword(user.getPassword());
			newUser.setPhone(user.getPhone());
			newUser.setRole("ROLE_NORMAL");
			newUser.setStatus(null);

			userServ.addUser(newUser);
			return ResponseEntity.status(HttpStatus.CREATED).body("Registration successful");
		}
		
	}
	
	@GetMapping("/user")
	@Operation(summary = "Get user dashboard")
	public ResponseEntity<User> dashboard(Principal p)
	{
		if(p == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		
		String email = p.getName();
		User user  = userServ.getUserByEmail(email);
		
		if(user == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		
		return ResponseEntity.ok(user);

	}
	
	@GetMapping("/admin/users")
	@Operation(summary = "Get all users (Admin only)")
	public ResponseEntity<List<User>> getAllUsers()
	{
		List<User> users = userServ.getAllUsers();
		return ResponseEntity.ok(users);
	}
	
	@GetMapping("/user/{id}")
	@Operation(summary = "Get user by ID")
	public ResponseEntity<User> getUserById(@PathVariable int id) {
		User user = userServ.getUserById(id);
		
		if(user == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		
		return ResponseEntity.ok(user);
	}
	
	@PutMapping("/user/{id}")
	@Operation(summary = "Update an existing user")
	public ResponseEntity<String> updateUser(@PathVariable int id, @RequestBody User user) {
		User existingUser = userServ.getUserById(id);

		if (existingUser == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
		}

		// Update fields from request body
		if(user.getName() != null && !user.getName().isEmpty()) {
			existingUser.setName(user.getName());
		}
		if(user.getEmail() != null && !user.getEmail().isEmpty()) {
			existingUser.setEmail(user.getEmail());
		}
		if(user.getPhone() != 0) {
			existingUser.setPhone(user.getPhone());
		}
		if(user.getPassword() != null && !user.getPassword().isEmpty()) {
			existingUser.setPassword(user.getPassword());
		}

		userServ.addUser(existingUser);
		return ResponseEntity.ok("User updated successfully");
	}

	@DeleteMapping("/user/{id}")
	@Operation(summary = "Delete a user account")
	public ResponseEntity<String> deleteUser(@PathVariable int id) {
		User user = userServ.getUserById(id);

		if (user == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
		}

		userServ.deleteUser(id);
		return ResponseEntity.ok("User deleted successfully");
	}

}
