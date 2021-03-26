package com.baralga.account.web;

import com.baralga.account.AuthenticationManager;
import com.baralga.account.User;
import com.baralga.core.web.LoggedIn;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;
import java.util.Optional;

/**
 * {@link HandlerMethodArgumentResolver} to inject the {@link User} of the currently logged in user into Spring
 * MVC controller method parameters annotated with {@link LoggedIn}.
 */
@Component
@RequiredArgsConstructor
class LoggedInAccountArgumentResolver implements HandlerMethodArgumentResolver, WebMvcConfigurer {

	private static final String USER_ACCOUNT_EXPECTED = "Expected to find a current %s but none available!";

	private final @NonNull AuthenticationManager authenticationManager;

	/*
	 * (non-Javadoc)
	 * @see org.springframework.web.method.support.HandlerMethodArgumentResolver#resolveArgument(org.springframework.core.MethodParameter, org.springframework.web.method.support.ModelAndViewContainer, org.springframework.web.context.request.NativeWebRequest, org.springframework.web.bind.support.WebDataBinderFactory)
	 */
	@Override
	@org.springframework.lang.NonNull
	public Object resolveArgument(MethodParameter parameter,
			@Nullable ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest,
			@Nullable WebDataBinderFactory binderFactory) throws Exception {

		Class<?> type = parameter.getParameterType();

		return resolve(type).orElseThrow(
				() -> new ServletRequestBindingException(String.format(USER_ACCOUNT_EXPECTED, type.getSimpleName())));
	}

	private Optional<?> resolve(Class<?> type) {

		Optional<User> account = authenticationManager.getCurrentUser();

		if (User.class.isAssignableFrom(type)) {
			return account;
		}

		throw new IllegalStateException("Unsupported user account type!");
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.web.method.support.HandlerMethodArgumentResolver#supportsParameter(org.springframework.core.MethodParameter)
	 */
	@Override
	public boolean supportsParameter(MethodParameter parameter) {

		return parameter.hasParameterAnnotation(LoggedIn.class)
				&& User.class.equals(parameter.getParameterType());
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.web.servlet.config.annotation.WebMvcConfigurer#addArgumentResolvers(java.util.List)
	 */
	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
		resolvers.add(this);
	}
}
