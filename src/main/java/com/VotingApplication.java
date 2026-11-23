package com;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.model.Candidate;
import com.model.User;
import com.repository.CandidateRepository;
import com.repository.UserRepository;

@SpringBootApplication
public class VotingApplication implements CommandLineRunner{

	private static final Logger logger = LoggerFactory.getLogger(VotingApplication.class);

	public static void main(String[] args) {
		logger.info("Starting Voting Application");
		SpringApplication.run(VotingApplication.class, args);
	}
	
	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private CandidateRepository canRepo;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
    public void run(String... args) throws Exception {
		logger.info("Initializing application data");

		// Admin
    	User admin = userRepo.getUserByEmail("admin");
    	if (admin == null) {
    		admin = new User();
    		admin.setEmail("admin");
			admin.setName("admin");
			admin.setPassword(passwordEncoder.encode("admin"));
			admin.setPhone(1234);
			admin.setRole("ROLE_ADMIN");
			admin.setStatus("admin");
			userRepo.save(admin);
			logger.info("Seeded default administrator account");
		} else {
			if (admin.getPassword() != null
					&& !admin.getPassword().startsWith("$2a$")
					&& !admin.getPassword().startsWith("$2b$")
					&& !admin.getPassword().startsWith("$2y$")) {
				admin.setPassword(passwordEncoder.encode(admin.getPassword()));
				userRepo.save(admin);
				logger.info("Migrated default administrator password to BCrypt");
			} else {
				logger.info("Default administrator account already exists");
			}
		}
		
		// Candidates
		String[] candidateNames = {"candidate1", "candidate2", "candidate3", "candidate4"};
		int created = 0;
		for (String candidateName : candidateNames) {
			if (canRepo.getCandidateByCandidate(candidateName) == null) {
				Candidate candidate = new Candidate();
				candidate.setCandidate(candidateName);
				candidate.setVotes(0);
				canRepo.save(candidate);
				created++;
			}
		}
		logger.info("Application data initialization complete: {} default candidates created", created);
	}
	

}
