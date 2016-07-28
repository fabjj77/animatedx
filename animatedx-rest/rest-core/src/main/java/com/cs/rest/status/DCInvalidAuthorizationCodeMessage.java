package com.cs.rest.status;

import com.cs.payment.devcode.DCInvalidAuthorizationCodeException;

/**
 * @author Hadi Movaghar
 */
public class DCInvalidAuthorizationCodeMessage extends DevcodeErrorMessage {

    private static final long serialVersionUID = 1L;

    public DCInvalidAuthorizationCodeMessage(final String playerId, final String errorMessage) {
        super(playerId, StatusCode.DEVCODE_INVALID_AUTHORIZATION_CODE, errorMessage);
    }

    public static DCInvalidAuthorizationCodeMessage of(final DCInvalidAuthorizationCodeException exception) {
        return new DCInvalidAuthorizationCodeMessage(exception.getPlayerId(), exception.getMessage());
    }
}
