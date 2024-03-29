package com.merantory.dostavim.security;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.merantory.dostavim.exception.JWTExpiredTokenException;
import com.merantory.dostavim.model.Person;
import com.merantory.dostavim.service.PersonService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {
	private final JwtUtil jwtUtil;
	private final PersonService personService;

	@Autowired
	public JwtFilter(JwtUtil jwtUtil, PersonService personService) {
		this.jwtUtil = jwtUtil;
		this.personService = personService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request,
									HttpServletResponse response,
									FilterChain filterChain) throws ServletException, IOException {
		String authHeader = request.getHeader("Authorization");

		if (authHeader != null && !authHeader.isBlank() && authHeader.startsWith("Bearer ")) {
			String jwtToken = authHeader.substring(7);
			if (jwtToken.isBlank()) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST,
						"Invalid JWT token in authorization bearer header");
			} else {
				try {
					String personEmail = jwtUtil.validateTokenAndRetrieveClaim(jwtToken);
					Person personDetails = (Person) personService.loadUserByUsername(personEmail);

					if (!personDetails.isEnabled()) {
						response.sendError(HttpServletResponse.SC_FORBIDDEN,
								"Account with received credentials has been blocked");
						return;
					}

					UsernamePasswordAuthenticationToken authenticationToken =
							new UsernamePasswordAuthenticationToken(personDetails,
									personDetails.getPassword(),
									personDetails.getAuthorities());

					if (SecurityContextHolder.getContext().getAuthentication() == null) {
						SecurityContextHolder.getContext().setAuthentication(authenticationToken);
					}
				} catch (JWTExpiredTokenException exception) {
					response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
							"JWT token has been expired");
					return;
				} catch (JWTVerificationException exception) {
					response.sendError(HttpServletResponse.SC_BAD_REQUEST,
							"Invalid JWT token");
					return;
				}
			}
		}

		filterChain.doFilter(request, response);
	}
}
