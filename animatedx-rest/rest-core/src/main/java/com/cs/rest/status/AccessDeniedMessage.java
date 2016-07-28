package com.cs.rest.status;

import com.cs.security.AccessDeniedException;

/**
 * @author Joakim Gottzén
 */
public class AccessDeniedMessage extends ErrorMessage {

    private static final long serialVersionUID = 1L;

    private AccessDeniedMessage(final String message) {
        super(StatusCode.ACCESS_DENIED, message);
    }

    public static AccessDeniedMessage of(final AccessDeniedException exception) {
        return new AccessDeniedMessage(exception.getMessage());
    }
}
