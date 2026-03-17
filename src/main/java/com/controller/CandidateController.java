package com.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.model.Candidate;
import com.model.User;
import com.service.CandidateService;
import com.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
@Tag(name = "Candidate Controller", description = "Endpoints for voting on candidates")
public class CandidateController {
	
	@Autowired
	private CandidateService canServ;
	
	@Autowired
	private UserService userServ;
	
	@GetMapping("/candidates")
	@Operation(summary = "Get all candidates")
	public ResponseEntity<List<Candidate>> getAllCandidates()
	{
		List<Candidate> candidates = canServ.getAllCandidates();
		return ResponseEntity.ok(candidates);
	}
	
	@PostMapping("/addcandidate") // vote
	@Operation(summary = "Vote for a candidate")
	public ResponseEntity<String> addCandidate(@RequestParam("candidate") String candidate,
			Principal p)
	{
		// Validation
		if(candidate == null || candidate.trim().isEmpty()) {
			return ResponseEntity.badRequest().body("Candidate name is required");
		}
		
		if(p == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
		}
		
		String email = p.getName();
		User user = userServ.getUserByEmail(email);
		
		if(user == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
		}
	
		
		if(user.getStatus() == null)
		{
			try {
				// Check if candidate exists
				Candidate selectedCan = canServ.getCandidateByCandidate(candidate);
				if(selectedCan == null) {
					return ResponseEntity.badRequest().body("Candidate does not exist");
				}
				
				// add a vote to the selectedCandidate
				selectedCan.setVotes(selectedCan.getVotes() + 1);
				canServ.addCandidate(selectedCan); // update candidate
				
				user.setStatus("Voted");
				userServ.addUser(user); // update user
				
				return ResponseEntity.ok("Successfully Voted...");
			}
			catch(Exception e)
			{
				e.printStackTrace();
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong...");
			}
			
			
		}
		else
		{
			return ResponseEntity.badRequest().body("Already voted");
		}

	}

}
