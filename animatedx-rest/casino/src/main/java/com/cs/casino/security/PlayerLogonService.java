package com.cs.casino.security;

import com.cs.persistence.NotFoundException;
import com.cs.player.Player;
import com.cs.player.PlayerService;

import org.springframework.beans.factory.annotation.Autowired;
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
public class PlayerLogonService implements UserDetailsService {

    private final Logger logger = LoggerFactory.getLogger(PlayerLogonService.class);

    private final PlayerService playerService;

    @Autowired
    public PlayerLogonService(final PlayerService playerService) {
        this.playerService = playerService;
    }

    @Override
    public UserDetails loadUserByUsername(final String username)
            throws UsernameNotFoundException {
        try {
            logger.debug("Authenticating user '{}'", username);
            final Player player = playerService.getPlayerForAuthentication(username);
            return new PlayerUser(player);
        } catch (final NotFoundException e) {
            logger.debug("No user '{}' found", username);
            throw new UsernameNotFoundException(e.getMessage(), e);
        }
    }
}
