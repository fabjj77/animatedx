package com.cs.rest.status;

import com.cs.security.InvalidSessionException;

/**
 * @author Hadi Movaghar
 */
public class InvalidSessionMessage extends ErrorMessage {

    private static final long serialVersionUID = 1L;

    private InvalidSessionMessage(final String message) {
        super(StatusCode.INVALID_SESSION, message);
    }

    public static InvalidSessionMessage of(final InvalidSessionException exception) {
        return new InvalidSessionMessage(exception.getMessage());
    }
}
