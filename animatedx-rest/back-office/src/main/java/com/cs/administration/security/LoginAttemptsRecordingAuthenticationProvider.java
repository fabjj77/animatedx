package com.cs.administration.security;

import com.cs.user.UserService;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * @author Joakim Gottzén
 */
public class LoginAttemptsRecordingAuthenticationProvider extends DaoAuthenticationProvider {
    private final UserService userService;

    public LoginAttemptsRecordingAuthenticationProvider(final UserService userService) {
        this.userService = userService;
    }

    @Override
    protected void additionalAuthenticationChecks(final UserDetails userDetails, final UsernamePasswordAuthenticationToken authentication)
            throws AuthenticationException {
        // Could assert pre-conditions here, e.g. rate-limiting
        // and throw a custom AuthenticationException if necessary

        final BackOfficeUser user = (BackOfficeUser) userDetails;
        try {
            super.additionalAuthenticationChecks(userDetails, authentication);
            userService.resetLoginFailureCounter(user.getId());
        } catch (final AuthenticationException e) {
            if (e instanceof BadCredentialsException) {
                userService.recordLoginFailure(user.getId());
            }
            throw e;
        }
    }
}
