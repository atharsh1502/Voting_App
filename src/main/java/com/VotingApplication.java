package com;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.model.Candidate;
import com.model.User;
import com.repository.CandidateRepository;
import com.repository.UserRepository;

@SpringBootApplication
public class VotingApplication implements CommandLineRunner {

	private static final Logger logger = LoggerFactory.getLogger(VotingApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(VotingApplication.class, args);
	}

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private CandidateRepository canRepo;

	@Override
	public void run(String... args) throws Exception {
		logger.info("VotingApplication startup - initializing seed data...");

		// Admin user — only create if not already present
		if (userRepo.getUserByEmail("admin") == null) {
			User admin = new User();
			admin.setEmail("admin@gmail.com");
			admin.setName("admin");
			admin.setPassword("admin");
			admin.setPhone(1234);
			admin.setRole("ROLE_ADMIN");
			admin.setStatus("admin");
			userRepo.save(admin);
			logger.info("Seed: Admin user created (email=admin)");
		} else {
			logger.info("Seed: Admin user already exists, skipping creation");
		}

		// Candidates — only create if not already present
		seedCandidate("candidate1");
		seedCandidate("candidate2");
		seedCandidate("candidate3");
		seedCandidate("candidate4");

		logger.info("VotingApplication startup - seed data initialization complete");
	}

	private void seedCandidate(String name) {
		if (canRepo.getCandidateByCandidate(name) == null) {
			Candidate candidate = new Candidate();
			candidate.setCandidate(name);
			canRepo.save(candidate);
			logger.info("Seed: Candidate '{}' created", name);
		} else {
			logger.info("Seed: Candidate '{}' already exists, skipping creation", name);
		}
	}
}


