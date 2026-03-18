package com.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.model.Candidate;
import com.repository.CandidateRepository;

@Service
public class CandidateService {

	private static final Logger logger = LoggerFactory.getLogger(CandidateService.class);

	@Autowired
	private CandidateRepository canRepo;

	@Transactional
	public Candidate addCandidate(Candidate can) {
		logger.info("Saving candidate: {}", can.getCandidate());
		Candidate saved = this.canRepo.save(can);
		logger.info("Candidate saved with id: {}, votes: {}", saved.getId(), saved.getVotes());
		return saved;
	}

	public List<Candidate> getAllCandidates() {
		logger.info("Fetching all candidates");
		List<Candidate> candidates = this.canRepo.findAll();
		logger.info("Total candidates found: {}", candidates.size());
		return candidates;
	}

	public Candidate getCandidateById(int id) {
		logger.info("Fetching candidate by id: {}", id);
		Optional<Candidate> candidate = this.canRepo.findById(id);
		if (candidate.isPresent()) {
			logger.info("Candidate found with id: {}", id);
			return candidate.get();
		}
		logger.warn("Candidate not found with id: {}", id);
		return null;
	}

	@Transactional
	public void delelteCandidate(int id) {
		logger.info("Deleting candidate with id: {}", id);
		this.canRepo.deleteById(id);
		logger.info("Candidate deleted with id: {}", id);
	}

	public int getNumOfVotes(String candidate) {
		logger.info("Fetching vote count for candidate: {}", candidate);
		int votes = this.canRepo.getNumOfVotes(candidate);
		logger.info("Vote count for '{}': {}", candidate, votes);
		return votes;
	}

	public Candidate getCandidateByCandidate(String candidate) {
		logger.info("Fetching candidate by name: {}", candidate);
		Candidate found = this.canRepo.getCandidateByCandidate(candidate);
		if (found != null) {
			logger.info("Candidate found: {} with {} votes", found.getCandidate(), found.getVotes());
		} else {
			logger.warn("Candidate not found with name: {}", candidate);
		}
		return found;
	}
}


