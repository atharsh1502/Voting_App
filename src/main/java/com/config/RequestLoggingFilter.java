package com.config;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestLoggingFilter extends OncePerRequestFilter {

	private static final Logger logger = LoggerFactory.getLogger(RequestLoggingFilter.class);

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
			FilterChain filterChain) throws ServletException, IOException {
		long startTime = System.nanoTime();
		String method = request.getMethod();
		String uri = request.getRequestURI();

		logger.debug("HTTP request started: {} {}", method, uri);
		try {
			filterChain.doFilter(request, response);
		} finally {
			long durationMillis = (System.nanoTime() - startTime) / 1_000_000;
			int status = response.getStatus();
			if (status >= 500) {
				logger.error("HTTP request completed: {} {} status={} durationMs={}", method, uri, status, durationMillis);
			} else if (status >= 400) {
				logger.warn("HTTP request completed: {} {} status={} durationMs={}", method, uri, status, durationMillis);
			} else {
				logger.info("HTTP request completed: {} {} status={} durationMs={}", method, uri, status, durationMillis);
			}
		}
	}
}
