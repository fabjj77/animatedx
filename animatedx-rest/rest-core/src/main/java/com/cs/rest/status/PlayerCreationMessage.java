package com.cs.rest.status;

import com.cs.player.PlayerCreationException;

/**
 * @author Omid Alaepour
 */
public class PlayerCreationMessage extends ErrorMessage {

    private static final long serialVersionUID = 1L;

    protected PlayerCreationMessage(final String message) {
        super(StatusCode.INVALID_PLAYER, message);
    }

    public static PlayerCreationMessage of(final PlayerCreationException exception) {
        return new PlayerCreationMessage(exception.getMessage());
    }
}
