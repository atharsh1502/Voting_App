package com.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Lock;

import javax.persistence.LockModeType;

import com.model.Candidate;

public interface CandidateRepository extends JpaRepository<Candidate, Integer>{
	
	
	@Query("select votes from Candidate where candidate = :candidate")
	public int getNumOfVotes(@Param("candidate") String candidate);

	
	
	@Query("select c from Candidate c where c.candidate = :candidate")
	public Candidate getCandidateByCandidate(@Param("candidate") String candidate);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select c from Candidate c where c.candidate = :candidate")
	public Candidate getCandidateByCandidateForUpdate(@Param("candidate") String candidate);
	
	
}
