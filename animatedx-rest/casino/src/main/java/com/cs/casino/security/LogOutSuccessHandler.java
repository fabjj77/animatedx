package com.cs.casino.security;

import com.cs.player.PlayerService;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;

import javax.annotation.Nullable;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Omid Alaepour
 */
public class LogOutSuccessHandler extends SimpleUrlLogoutSuccessHandler implements LogoutSuccessHandler {
    private final PlayerService playerService;

    public LogOutSuccessHandler(final PlayerService playerService) {
        this.playerService = playerService;
    }

    @Override
    public void onLogoutSuccess(final HttpServletRequest request, final HttpServletResponse response, final Authentication authentication)
            throws IOException, ServletException {
        final Long playerId = tryParse(request.getParameter("playerId"));

        if (playerId != null) {
            playerService.logoffPlayer(playerId);
        }

        super.onLogoutSuccess(request, response, authentication);
    }

    @Nullable
    public Long tryParse(final String playerId) {
        try {
            return Long.parseLong(playerId);
        } catch (final NumberFormatException e) {
            logger.error("Failed to parse playerId " + playerId);
            return null;
        }
    }
}
