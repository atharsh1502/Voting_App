package com.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.model.User;
import com.repository.UserRepository;

public class UserDetailsServiceImpl implements UserDetailsService {

	private static final Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

	@Autowired
	private UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		logger.info("loadUserByUsername - Attempting to load user: '{}'", username);

		User user = userRepository.getUserByEmail(username);

		if (user == null) {
			logger.warn("loadUserByUsername - User not found for username: '{}'", username);
			throw new UsernameNotFoundException("Could not find user: " + username);
		}

		logger.info("loadUserByUsername - User found: '{}', role: '{}'", username, user.getRole());
		CustomUserDetails customUserDetails = new CustomUserDetails(user);
		return customUserDetails;
	}
}


