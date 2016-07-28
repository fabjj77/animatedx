package com.cs.rest.status;

import com.cs.payment.devcode.DCPlayerDetailException;

/**
 * @author Hadi Movaghar
 */
public class DCPlayerDetailMessage extends DevcodeErrorMessage{
    private static final long serialVersionUID = 1L;

    public DCPlayerDetailMessage(final String playerId, final String errorMessage) {
        super(playerId, StatusCode.DEVCODE_INVALID_PLAYER_DETAIL, errorMessage);
    }

    public static DCPlayerDetailMessage of(final DCPlayerDetailException exception) {
        return new DCPlayerDetailMessage(exception.getPlayerId(), exception.getMessage());
    }
}
