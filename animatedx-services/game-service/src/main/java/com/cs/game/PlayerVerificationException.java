package com.cs.game;

/**
 * @author Joakim Gottzén
 */
public class PlayerVerificationException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public static final String PLAYER_UNVERIFIED = "Player %d is not verified";

    public PlayerVerificationException(final Long playerId) {
        super(String.format(PLAYER_UNVERIFIED, playerId));
    }
}
