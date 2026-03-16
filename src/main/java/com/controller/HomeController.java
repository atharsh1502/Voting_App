package com.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Home Controller", description = "Endpoints for home, signin, register, about pages")
public class HomeController {




	@GetMapping("/")
	@Operation(summary = "Get home page")
	public ResponseEntity<String> home()
	{
		return ResponseEntity.ok("Welcome to Voting Application");
	}

	@GetMapping("/signin")
	@Operation(summary = "Get signin page")
	public ResponseEntity<String> login()
	{
		return ResponseEntity.ok("Signin endpoint");
	}


	@GetMapping("/register")
	@Operation(summary = "Get register page")
	public ResponseEntity<String> register()
	{
		return ResponseEntity.ok("Register endpoint");
	}


	@GetMapping("/about")
	@Operation(summary = "Get about page")
	public ResponseEntity<String> about()
	{
		return ResponseEntity.ok("About Voting Application");
	}

}
