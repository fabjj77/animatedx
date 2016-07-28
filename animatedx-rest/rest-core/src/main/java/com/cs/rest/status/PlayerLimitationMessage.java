package com.cs.rest.status;

import com.cs.player.PlayerLimitationException;

/**
 * @author Hadi Movaghar
 */
public class PlayerLimitationMessage extends ErrorMessage {

    private static final long serialVersionUID = 1L;

    public PlayerLimitationMessage(final String message) {
        super(StatusCode.INVALID_LIMITATION, message);
    }

    public static PlayerLimitationMessage of(final PlayerLimitationException exception) {
        return new PlayerLimitationMessage(exception.getMessage());
    }
}
