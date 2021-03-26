package com.baralga.account;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
class SpringSecurityAuthenticationManager implements AuthenticationManager {

	@Override
	public Optional<User> getCurrentUser() {
		return Optional.ofNullable(SecurityContextHolder.getContext()
				.getAuthentication())
				.map(Authentication::getPrincipal)
				.filter(User.class::isInstance)
				.map(User.class::cast);
	}
}
