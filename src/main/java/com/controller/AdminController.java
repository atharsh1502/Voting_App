package com.controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.service.CandidateService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Admin Controller", description = "Endpoints for admin dashboard")
public class AdminController {
	
	@Autowired
	private CandidateService canServ;
	
	@GetMapping("/admin")
	@Operation(summary = "Get admin dashboard")
	public ResponseEntity<Map<String, Integer>> dashboard(Principal p)
	{
		
		// total votes
		int c1 = canServ.getNumOfVotes("candidate1");
		int c2 = canServ.getNumOfVotes("candidate2");
		int c3 = canServ.getNumOfVotes("candidate3");
		int c4 = canServ.getNumOfVotes("candidate4");
		
		Map<String, Integer> votes = new HashMap<>();
		votes.put("candidate1", c1);
		votes.put("candidate2", c2);
		votes.put("candidate3", c3);
		votes.put("candidate4", c4);

		return ResponseEntity.ok(votes);

	}

	@PutMapping("/admin/candidate/{id}")
	@Operation(summary = "Update a candidate")
	public ResponseEntity<String> updateCandidate(@PathVariable int id, @RequestBody com.model.Candidate candidate) {
		com.model.Candidate existingCandidate = canServ.getCandidateById(id);

		if (existingCandidate == null) {
			return ResponseEntity.notFound().build();
		}

		existingCandidate.setCandidate(candidate.getCandidate());
		existingCandidate.setVotes(candidate.getVotes());

		canServ.addCandidate(existingCandidate);
		return ResponseEntity.ok("Candidate updated successfully");
	}

	@DeleteMapping("/admin/candidate/{id}")
	@Operation(summary = "Delete a candidate")
	public ResponseEntity<String> deleteCandidate(@PathVariable int id) {
		com.model.Candidate candidate = canServ.getCandidateById(id);

		if (candidate == null) {
			return ResponseEntity.notFound().build();
		}

		canServ.delelteCandidate(id);
		return ResponseEntity.ok("Candidate deleted successfully");
	}

}
