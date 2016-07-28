package com.cs.casino.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

import static org.springframework.transaction.annotation.Isolation.READ_COMMITTED;

/**
 * @author Joakim Gottzén
 */
@Component
@Transactional(isolation = READ_COMMITTED)
public class LogoutHelper {

    private final SessionRegistry sessionRegistry;

    @Autowired
    public LogoutHelper(final SessionRegistry sessionRegistry) {
        this.sessionRegistry = sessionRegistry;
    }

    public void logout(final Object currentPlayer, final HttpServletRequest request, final HttpServletResponse response) {
        final List<SessionInformation> list = sessionRegistry.getAllSessions(currentPlayer, false);
        for (final SessionInformation sessionInformation : list) {
            sessionRegistry.removeSessionInformation(sessionInformation.getSessionId());
        }

        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }
        SecurityContextHolder.getContext().setAuthentication(null);
    }
}
