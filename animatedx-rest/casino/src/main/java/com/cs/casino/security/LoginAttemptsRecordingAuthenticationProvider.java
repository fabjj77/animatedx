package com.cs.casino.security;

import com.cs.player.PlayerService;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * @author Joakim Gottzén
 */
@SuppressWarnings("SpringJavaAutowiringInspection")
public class LoginAttemptsRecordingAuthenticationProvider extends DaoAuthenticationProvider {

    private final PlayerService playerService;

    public LoginAttemptsRecordingAuthenticationProvider(final PlayerService playerService) {
        this.playerService = playerService;
    }

    @Override
    protected void additionalAuthenticationChecks(final UserDetails userDetails, final UsernamePasswordAuthenticationToken authentication)
            throws AuthenticationException {
        // Could assert pre-conditions here, e.g. rate-limiting
        // and throw a custom AuthenticationException if necessary

        final PlayerUser player = (PlayerUser) userDetails;
        try {
            super.additionalAuthenticationChecks(userDetails, authentication);
            playerService.resetLoginFailureCounter(player.getId());
        } catch (final AuthenticationException e) {
            if (e instanceof BadCredentialsException) {
                playerService.recordLoginFailure(player.getId());
            }
            throw e;
        }
    }
}
