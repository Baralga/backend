package com.baralga.core.web;

import com.baralga.account.User;
import org.springframework.web.bind.ServletRequestBindingException;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark the method parameter with that shall get the {@link User} corresponding to the currently
 * logged in user injected. The method parameter needs to be of type {@code User} A
 * {@link ServletRequestBindingException} will be thrown if no {@link User} could have been obtained in the
 * first place.
 */
@Target({ ElementType.TYPE, ElementType.METHOD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface LoggedIn {
}
