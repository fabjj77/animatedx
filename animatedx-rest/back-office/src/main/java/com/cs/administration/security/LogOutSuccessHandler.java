package com.cs.administration.security;

import com.cs.audit.AuditService;
import com.cs.audit.UserActivityType;
import com.cs.user.User;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;

import javax.annotation.Nullable;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Joakim Gottzén
 */
public class LogOutSuccessHandler extends SimpleUrlLogoutSuccessHandler implements LogoutSuccessHandler {
    private final AuditService auditService;

    public LogOutSuccessHandler(final AuditService auditService) {
        this.auditService = auditService;
    }

    @Override
    public void onLogoutSuccess(final HttpServletRequest request, final HttpServletResponse response, final Authentication authentication)
            throws IOException, ServletException {
        final Long userId = tryParse(request.getParameter("userId"));

        if (userId != null) {
            auditService.trackUserActivity(new User(userId), UserActivityType.LOGOUT);
        }

        super.onLogoutSuccess(request, response, authentication);
    }

    @Nullable
    public Long tryParse(final String userId) {
        try {
            return Long.parseLong(userId);
        } catch (final NumberFormatException e) {
            logger.error("Failed to parse userId " + userId);
            return null;
        }
    }
}
