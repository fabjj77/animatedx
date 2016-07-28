package com.cs.rest.status;

import com.cs.payment.devcode.DCInvalidPlayerException;

/**
 * @author Hadi Movaghar
 */
public class DCInvalidPlayerMessage extends DevcodeErrorMessage {
    private static final long serialVersionUID = 1L;

    public DCInvalidPlayerMessage(final String playerId, final String errorMessage) {
        super(playerId, StatusCode.DEVCODE_INVALID_PLAYER , errorMessage);
    }

    public static DCInvalidPlayerMessage of(final DCInvalidPlayerException exception) {
        return new DCInvalidPlayerMessage(exception.getPlayerId(), exception.getMessage());
    }
}
