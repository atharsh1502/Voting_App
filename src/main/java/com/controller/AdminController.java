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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.model.Candidate;
import com.model.User;
import com.service.CandidateService;
import com.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
@Tag(name = "Admin Controller", description = "Endpoints for admin dashboard and management")
public class AdminController {

	private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

	@Autowired
	private CandidateService canServ;

	@Autowired
	private UserService userServ;

	@GetMapping("/admin")
	@Operation(summary = "Get admin dashboard with vote statistics")
	public ResponseEntity<Map<String, Object>> dashboard(Principal p) {
		logger.info("GET /admin - Admin dashboard requested by: {}", p != null ? p.getName() : "unknown");

		List<Candidate> allCandidates = canServ.getAllCandidates();

		Map<String, Object> response = new HashMap<>();
		Map<String, Integer> votes = new HashMap<>();
		int totalVotes = 0;

		for (Candidate candidate : allCandidates) {
			votes.put(candidate.getCandidate(), candidate.getVotes());
			totalVotes += candidate.getVotes();
			logger.info("GET /admin - Candidate '{}': {} vote(s)", candidate.getCandidate(), candidate.getVotes());
		}

		response.put("candidates", votes);
		response.put("totalVotes", totalVotes);

		logger.info("GET /admin - Dashboard: {} candidate(s), {} total vote(s)", allCandidates.size(), totalVotes);
		return ResponseEntity.ok(response);
	}

	@PostMapping("/admin/candidate")
	@Operation(summary = "Create a new candidate")
	public ResponseEntity<String> createCandidate(@RequestBody Candidate candidate) {
		logger.info("POST /admin/candidate - Request to create candidate: '{}'", candidate.getCandidate());

		if (candidate.getCandidate() == null || candidate.getCandidate().trim().isEmpty()) {
			logger.warn("POST /admin/candidate - Rejected: candidate name is blank");
			return ResponseEntity.badRequest().body("Candidate name is required");
		}

		if (canServ.getCandidateByCandidate(candidate.getCandidate()) != null) {
			logger.warn("POST /admin/candidate - Rejected: candidate '{}' already exists", candidate.getCandidate());
			return ResponseEntity.badRequest().body("Candidate already exists");
		}

		candidate.setVotes(0);
		canServ.addCandidate(candidate);
		logger.info("POST /admin/candidate - Candidate '{}' created successfully", candidate.getCandidate());
		return ResponseEntity.status(HttpStatus.CREATED).body("Candidate created successfully");
	}

	@PutMapping("/admin/candidate/{id}")
	@Operation(summary = "Update a candidate name")
	public ResponseEntity<String> updateCandidate(@PathVariable int id, @RequestBody Candidate candidate) {
		logger.info("PUT /admin/candidate/{} - Update request with new name: '{}'", id, candidate.getCandidate());

		if (candidate.getCandidate() == null || candidate.getCandidate().trim().isEmpty()) {
			logger.warn("PUT /admin/candidate/{} - Rejected: candidate name is blank", id);
			return ResponseEntity.badRequest().body("Candidate name is required");
		}

		Candidate existingCandidate = canServ.getCandidateById(id);

		if (existingCandidate == null) {
			logger.warn("PUT /admin/candidate/{} - Candidate not found", id);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Candidate not found");
		}

		String oldName = existingCandidate.getCandidate();
		existingCandidate.setCandidate(candidate.getCandidate());
		// Votes are never changed directly — only through the /vote endpoint
		canServ.addCandidate(existingCandidate);
		logger.info("PUT /admin/candidate/{} - Candidate renamed from '{}' to '{}'", id, oldName, candidate.getCandidate());
		return ResponseEntity.ok("Candidate updated successfully");
	}

	@GetMapping("/admin/candidates")
	@Operation(summary = "Get all candidates (Admin view)")
	public ResponseEntity<List<Candidate>> getAllCandidates() {
		logger.info("GET /admin/candidates - Fetching all candidates");
		List<Candidate> candidates = canServ.getAllCandidates();
		logger.info("GET /admin/candidates - Returning {} candidate(s)", candidates.size());
		return ResponseEntity.ok(candidates);
	}

	@DeleteMapping("/admin/candidate/{id}")
	@Operation(summary = "Delete a candidate")
	public ResponseEntity<String> deleteCandidate(@PathVariable int id) {
		logger.info("DELETE /admin/candidate/{} - Delete request", id);
		Candidate candidate = canServ.getCandidateById(id);

		if (candidate == null) {
			logger.warn("DELETE /admin/candidate/{} - Candidate not found", id);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Candidate not found");
		}

		canServ.delelteCandidate(id);
		logger.info("DELETE /admin/candidate/{} - Candidate '{}' deleted successfully", id, candidate.getCandidate());
		return ResponseEntity.ok("Candidate deleted successfully");
	}

	@PostMapping("/admin/reset-votes")
	@Transactional
	@Operation(summary = "Reset all votes (Admin only) - clears all candidate vote counts and user vote status")
	public ResponseEntity<String> resetVotes() {
		logger.info("POST /admin/reset-votes - Resetting all votes");

		List<Candidate> allCandidates = canServ.getAllCandidates();
		for (Candidate candidate : allCandidates) {
			logger.info("POST /admin/reset-votes - Resetting votes for candidate '{}'", candidate.getCandidate());
			candidate.setVotes(0);
			canServ.addCandidate(candidate);
		}

		List<User> allUsers = userServ.getAllUsers();
		for (User user : allUsers) {
			if ("Voted".equals(user.getStatus())) {
				logger.info("POST /admin/reset-votes - Clearing vote status for user '{}'", user.getEmail());
				user.setStatus(null);
				user.setVotedFor(null);
				userServ.saveUser(user);
			}
		}

		logger.info("POST /admin/reset-votes - All votes reset. {} candidate(s) cleared, {} user(s) cleared",
				allCandidates.size(), allUsers.stream().filter(u -> "Voted".equals(u.getStatus())).count());
		return ResponseEntity.ok("All votes have been reset successfully");
	}
}

