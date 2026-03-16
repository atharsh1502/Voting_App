package com.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
@Tag(name = "User Controller", description = "Endpoints for user registration and dashboard")
public class UserController {
	
	@Autowired
	private UserService userServ;
	
	@PostMapping("/createuser")
	@Operation(summary = "Create a new user")
	public ResponseEntity<String> createUser(@RequestBody User user)
	{
		String email = user.getEmail();
		
		if(userServ.getUserByEmail(email) != null)
		{
			return ResponseEntity.badRequest().body("Registration Failed, Please try different Email Id");
		}
		else{
			 
			User newUser = new User();
			newUser.setEmail(user.getEmail());
			newUser.setName(user.getName());
			newUser.setPassword(user.getPassword());
			newUser.setPhone(user.getPhone());
			newUser.setRole("NORMAL");
			newUser.setStatus(null);

			userServ.addUser(newUser);
			return ResponseEntity.ok("Registration successful");
		}
		
	}
	
	@GetMapping("/user")
	@Operation(summary = "Get user dashboard")
	public ResponseEntity<User> dashboard(Principal p)
	{
		String email = p.getName(); // 
		
		User user  = userServ.getUserByEmail(email);
		
		return ResponseEntity.ok(user);

	}
	
	@PutMapping("/user/{id}")
	@Operation(summary = "Update an existing user")
	public ResponseEntity<String> updateUser(@PathVariable int id, @RequestBody User user) {
		User existingUser = userServ.getUserById(id);

		if (existingUser == null) {
			return ResponseEntity.notFound().build();
		}

		// Update fields from request body, but keep the id from path
		existingUser.setName(user.getName());
		existingUser.setEmail(user.getEmail());
		existingUser.setPhone(user.getPhone());
		existingUser.setPassword(user.getPassword());
		existingUser.setRole(user.getRole());
		existingUser.setStatus(user.getStatus());

		userServ.addUser(existingUser);
		return ResponseEntity.ok("User updated successfully");
	}

	@DeleteMapping("/user/{id}")
	@Operation(summary = "Delete a user")
	public ResponseEntity<String> deleteUser(@PathVariable int id) {
		User user = userServ.getUserById(id);

		if (user == null) {
			return ResponseEntity.notFound().build();
		}

		userServ.deleteUser(id);
		return ResponseEntity.ok("User deleted successfully");
	}



}
