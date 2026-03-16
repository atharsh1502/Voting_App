package com.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
@Tag(name = "Candidate Controller", description = "Endpoints for voting on candidates")
public class CandidateController {
	
	@Autowired
	private CandidateService canServ;
	
	@Autowired
	private UserService userServ;
	
	@PostMapping("/addcandidate") // vote
	@Operation(summary = "Vote for a candidate")
	public ResponseEntity<String> addCandidate(@RequestParam("candidate") String candidate,
			Principal p)
	{
		String email = p.getName();
		User user = userServ.getUserByEmail(email);
	
		
		if(user.getStatus() == null)
		{
			try {
				// add a vote to the selectedCandidate
				Candidate selectedCan = canServ.getCandidateByCandidate(candidate);
				selectedCan.setVotes(selectedCan.getVotes() + 1);
				canServ.addCandidate(selectedCan); // update candidate
				
				user.setStatus("Voted");
				userServ.addUser(user); // update user
				
				return ResponseEntity.ok("Successfully Voted...");
			}
			catch(Exception e)
			{
				e.printStackTrace();
				return ResponseEntity.badRequest().body("Something went wrong...");
			}
			
			
		}
		else
		{
			return ResponseEntity.badRequest().body("Already voted");
		}

	}

}
