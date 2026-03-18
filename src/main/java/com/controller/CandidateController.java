package com.controller;

import java.security.Principal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
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

	private static final Logger logger = LoggerFactory.getLogger(CandidateController.class);

	@Autowired
	private CandidateService canServ;

	@Autowired
	private UserService userServ;

	@GetMapping("/candidates")
	@Operation(summary = "Get all candidates")
	public ResponseEntity<List<Candidate>> getAllCandidates() {
		logger.info("GET /candidates - Fetching list of all candidates");
		List<Candidate> candidates = canServ.getAllCandidates();
		logger.info("GET /candidates - Returning {} candidate(s)", candidates.size());
		return ResponseEntity.ok(candidates);
	}

	@PostMapping("/vote")
	@Transactional
	@Operation(summary = "Cast a vote for a candidate (one vote per user)")
	public ResponseEntity<String> castVote(@RequestParam("candidate") String candidate,
			Principal p) {

		logger.info("POST /vote - Vote request received for candidate: '{}'", candidate);

		// Validation: candidate name
		if (candidate == null || candidate.trim().isEmpty()) {
			logger.warn("POST /vote - Rejected: candidate name is blank");
			return ResponseEntity.badRequest().body("Candidate name is required");
		}

		// Validation: authentication
		if (p == null) {
			logger.warn("POST /vote - Rejected: unauthenticated request for candidate '{}'", candidate);
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
		}

		String email = p.getName();
		logger.info("POST /vote - Authenticated user: '{}'", email);

		User user = userServ.getUserByEmail(email);

		if (user == null) {
			logger.error("POST /vote - User not found in DB for email: '{}'", email);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
		}

		// Check if user has already voted
		if (user.getStatus() != null && user.getStatus().equals("Voted")) {
			logger.warn("POST /vote - User '{}' has already voted for '{}'", email, user.getVotedFor());
			return ResponseEntity.badRequest().body("Already voted - you have already cast your vote for: " + user.getVotedFor());
		}

		// Fetch candidate from DB
		Candidate selectedCan = canServ.getCandidateByCandidate(candidate);
		if (selectedCan == null) {
			logger.warn("POST /vote - Candidate '{}' does not exist", candidate);
			return ResponseEntity.badRequest().body("Candidate does not exist: " + candidate);
		}

		try {
			int previousVotes = selectedCan.getVotes();

			// Increment vote count on candidate
			selectedCan.setVotes(previousVotes + 1);
			canServ.addCandidate(selectedCan);
			logger.info("POST /vote - Candidate '{}' vote count updated: {} -> {}",
					candidate, previousVotes, selectedCan.getVotes());

			// Mark user as voted and record which candidate they voted for
			user.setStatus("Voted");
			user.setVotedFor(candidate);
			userServ.saveUser(user);
			logger.info("POST /vote - User '{}' marked as voted for '{}'", email, candidate);

			logger.info("POST /vote - Vote successfully cast by '{}' for '{}'", email, candidate);
			return ResponseEntity.ok("Vote cast successfully for: " + candidate);

		} catch (Exception e) {
			logger.error("POST /vote - Unexpected error while processing vote for user '{}', candidate '{}': {}",
					email, candidate, e.getMessage(), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong while casting your vote. Please try again.");
		}
	}

	// Kept for backward compatibility — mirrors /vote exactly
	@PostMapping("/addcandidate")
	@Transactional
	@Operation(summary = "Vote for a candidate (deprecated - use POST /vote instead)")
	public ResponseEntity<String> addCandidate(@RequestParam("candidate") String candidate,
			Principal p) {
		logger.info("POST /addcandidate (deprecated) - Received vote request for candidate: '{}', delegating to /vote logic", candidate);

		if (candidate == null || candidate.trim().isEmpty()) {
			logger.warn("POST /addcandidate - Rejected: candidate name is blank");
			return ResponseEntity.badRequest().body("Candidate name is required");
		}
		if (p == null) {
			logger.warn("POST /addcandidate - Rejected: unauthenticated request");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
		}

		String email = p.getName();
		User user = userServ.getUserByEmail(email);
		if (user == null) {
			logger.error("POST /addcandidate - User not found for email: '{}'", email);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
		}
		if ("Voted".equals(user.getStatus())) {
			logger.warn("POST /addcandidate - User '{}' has already voted for '{}'", email, user.getVotedFor());
			return ResponseEntity.badRequest().body("Already voted - you have already cast your vote for: " + user.getVotedFor());
		}

		Candidate selectedCan = canServ.getCandidateByCandidate(candidate);
		if (selectedCan == null) {
			logger.warn("POST /addcandidate - Candidate '{}' does not exist", candidate);
			return ResponseEntity.badRequest().body("Candidate does not exist: " + candidate);
		}

		try {
			int previousVotes = selectedCan.getVotes();
			selectedCan.setVotes(previousVotes + 1);
			canServ.addCandidate(selectedCan);
			logger.info("POST /addcandidate - Candidate '{}' vote count updated: {} -> {}", candidate, previousVotes, selectedCan.getVotes());

			user.setStatus("Voted");
			user.setVotedFor(candidate);
			userServ.saveUser(user);
			logger.info("POST /addcandidate - User '{}' marked as voted for '{}'", email, candidate);
			return ResponseEntity.ok("Vote cast successfully for: " + candidate);
		} catch (Exception e) {
			logger.error("POST /addcandidate - Error while processing vote for user '{}', candidate '{}': {}", email, candidate, e.getMessage(), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong while casting your vote.");
		}
	}
}

