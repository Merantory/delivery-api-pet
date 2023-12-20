package com.merantory.dostavim.util.security;

import com.merantory.dostavim.exception.PersonAuthFailedException;
import com.merantory.dostavim.model.Person;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationHelper {
	public Person getAuthenticationPerson() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.isAuthenticated()
				&& !(authentication instanceof AnonymousAuthenticationToken)) {
			return (Person) authentication.getPrincipal();
		} else {
			throw new PersonAuthFailedException("Person authentication failed");
		}
	}
}
