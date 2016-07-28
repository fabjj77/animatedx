package com.cs.rest.status;

import com.cs.game.PlayerVerificationException;

/**
 * @author Joakim Gottzén
 */
public class PlayerVerificationMessage extends ErrorMessage {

    private static final long serialVersionUID = 1L;

    private PlayerVerificationMessage(final String message) {
        super(StatusCode.PLAYER_UNVERIFIED, message);
    }

    public static PlayerVerificationMessage of(final PlayerVerificationException exception) {
        return new PlayerVerificationMessage(exception.getMessage());
    }
}
