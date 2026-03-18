package com.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
@Tag(name = "Home Controller", description = "Endpoints for home, signin, register, about pages")
public class HomeController {

	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

	@GetMapping("/")
	@Operation(summary = "Get home page - Welcome message")
	public ResponseEntity<String> home() {
		logger.info("GET / - Home page accessed");
		return ResponseEntity.ok("Welcome to Voting Application");
	}

	@GetMapping("/signin")
	@Operation(summary = "Get signin page")
	public ResponseEntity<String> login() {
		logger.info("GET /signin - Sign-in page accessed");
		return ResponseEntity.ok("Signin endpoint");
	}

	@GetMapping("/register")
	@Operation(summary = "Get register page")
	public ResponseEntity<String> register() {
		logger.info("GET /register - Register page accessed");
		return ResponseEntity.ok("Register endpoint - use POST /register with User data");
	}

	@GetMapping("/about")
	@Operation(summary = "Get about page")
	public ResponseEntity<String> about() {
		logger.info("GET /about - About page accessed");
		return ResponseEntity.ok("Voting Application - A complete voting system with user registration, authentication, and voting capabilities");
	}
}



