package com.cs.rest.status;

import com.cs.payment.devcode.DCInvalidSessionException;

/**
 * @author Hadi Movaghar
 */
public class DCInvalidSessionMessage extends DevcodeErrorMessage{
    private static final long serialVersionUID = 1L;

    public DCInvalidSessionMessage(final String playerId, final String errorMessage) {
        super(playerId, StatusCode.DEVCODE_INVALID_SESSION , errorMessage);
    }

    public static DCInvalidSessionMessage of(final DCInvalidSessionException exception) {
        return new DCInvalidSessionMessage(exception.getPlayerId(), exception.getMessage());
    }
}
