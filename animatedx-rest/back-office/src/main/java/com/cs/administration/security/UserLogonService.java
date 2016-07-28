package com.cs.administration.security;

import com.cs.persistence.NotFoundException;
import com.cs.user.User;
import com.cs.user.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Joakim Gottzén
 */
@Service
public class UserLogonService implements UserDetailsService {
    private final Logger logger = LoggerFactory.getLogger(UserLogonService.class);

    private final UserService userService;

    @Value("${user.lock-time-for-bad-credentials-in-minutes}")
    private Integer badCredentialsLockoutTime;

    @Value("${user.maximum-failed-login-attempts}")
    private Integer maximumFailedLoginAttempts;

    @Autowired
    public UserLogonService(final UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(final String username)
            throws UsernameNotFoundException {
        try {
            logger.debug("Authenticating user '{}'", username);
            final User user = userService.getUserForAuthentication(username);
            return new BackOfficeUser(user, maximumFailedLoginAttempts, badCredentialsLockoutTime);
        } catch (final NotFoundException e) {
            logger.debug("No user '{}' found", username);
            throw new UsernameNotFoundException(e.getMessage(), e);
        }
    }
}
