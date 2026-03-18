package com.config;

import java.io.IOException;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
public class CustomSuccessHandler implements AuthenticationSuccessHandler {

	private static final Logger logger = LoggerFactory.getLogger(CustomSuccessHandler.class);

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {

		String username = authentication.getName();
		Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());
		logger.info("Authentication successful for user: '{}', roles: {}", username, roles);

		if (roles.contains("ROLE_ADMIN")) {
			logger.info("Redirecting admin user '{}' to /admin", username);
			response.sendRedirect("/admin");
		} else if (roles.contains("ROLE_NORMAL")) {
			logger.info("Redirecting normal user '{}' to /user", username);
			response.sendRedirect("/user");
		} else {
			logger.warn("User '{}' has no recognized role, redirecting to /", username);
			response.sendRedirect("/");
		}
	}
}

