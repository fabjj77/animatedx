package com.cs.administration.security;

import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author Joakim Gottzén
 */
@Target({PARAMETER, TYPE})
@Retention(RUNTIME)
@Documented
@AuthenticationPrincipal
public @interface CurrentUser {
}
