package com.cs.payment.devcode;

import javax.annotation.Nullable;

/**
 * @author Hadi Movaghar
 */
public class DCInvalidAuthorizationCodeException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public static final String INVALID_AUTHORIZATION_CODE = "Authorization code %s is invalid.";

    private final String playerId;

    public DCInvalidAuthorizationCodeException(final String playerId, @Nullable final String authorizationCode) {
        super(String.format(INVALID_AUTHORIZATION_CODE, authorizationCode));
        this.playerId = playerId;
    }

    public String getPlayerId() {
        return playerId;
    }
}
