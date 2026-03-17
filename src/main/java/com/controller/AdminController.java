package com.controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.model.Candidate;
import com.service.CandidateService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
@Tag(name = "Admin Controller", description = "Endpoints for admin dashboard and management")
public class AdminController {
	
	@Autowired
	private CandidateService canServ;
	
	@GetMapping("/admin")
	@Operation(summary = "Get admin dashboard with vote statistics")
	public ResponseEntity<Map<String, Object>> dashboard(Principal p)
	{
		
		// total votes
		List<Candidate> allCandidates = canServ.getAllCandidates();
		
		Map<String, Object> response = new HashMap<>();
		Map<String, Integer> votes = new HashMap<>();
		int totalVotes = 0;
		
		for(Candidate candidate : allCandidates) {
			votes.put(candidate.getCandidate(), candidate.getVotes());
			totalVotes += candidate.getVotes();
		}
		
		response.put("candidates", votes);
		response.put("totalVotes", totalVotes);

		return ResponseEntity.ok(response);

	}

	@PostMapping("/admin/candidate")
	@Operation(summary = "Create a new candidate")
	public ResponseEntity<String> createCandidate(@RequestBody Candidate candidate) {
		
		// Validation
		if(candidate.getCandidate() == null || candidate.getCandidate().trim().isEmpty()) {
			return ResponseEntity.badRequest().body("Candidate name is required");
		}
		
		// Check if candidate already exists
		if(canServ.getCandidateByCandidate(candidate.getCandidate()) != null) {
			return ResponseEntity.badRequest().body("Candidate already exists");
		}
		
		candidate.setVotes(0);
		canServ.addCandidate(candidate);
		return ResponseEntity.status(HttpStatus.CREATED).body("Candidate created successfully");
	}

	@PutMapping("/admin/candidate/{id}")
	@Operation(summary = "Update a candidate")
	public ResponseEntity<String> updateCandidate(@PathVariable int id, @RequestBody Candidate candidate) {
		
		if(candidate.getCandidate() == null || candidate.getCandidate().trim().isEmpty()) {
			return ResponseEntity.badRequest().body("Candidate name is required");
		}
		
		Candidate existingCandidate = canServ.getCandidateById(id);

		if (existingCandidate == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Candidate not found");
		}

		existingCandidate.setCandidate(candidate.getCandidate());
		// Don't update votes directly - votes should only change through voting mechanism

		canServ.addCandidate(existingCandidate);
		return ResponseEntity.ok("Candidate updated successfully");
	}

	@GetMapping("/admin/candidates")
	@Operation(summary = "Get all candidates (Admin view)")
	public ResponseEntity<List<Candidate>> getAllCandidates() {
		List<Candidate> candidates = canServ.getAllCandidates();
		return ResponseEntity.ok(candidates);
	}

	@DeleteMapping("/admin/candidate/{id}")
	@Operation(summary = "Delete a candidate")
	public ResponseEntity<String> deleteCandidate(@PathVariable int id) {
		Candidate candidate = canServ.getCandidateById(id);

		if (candidate == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Candidate not found");
		}

		canServ.delelteCandidate(id);
		return ResponseEntity.ok("Candidate deleted successfully");
	}

}
