package com.cs.rest.status;

import com.cs.security.SessionExpiredException;

/**
 * @author Joakim Gottzén
 */
public class SessionExpiredMessage extends ErrorMessage {

    private static final long serialVersionUID = 1L;

    private SessionExpiredMessage(final String message) {
        super(StatusCode.SESSION_EXPIRED, message);
    }

    public static SessionExpiredMessage of(final SessionExpiredException exception) {
        return new SessionExpiredMessage(exception.getMessage());
    }
}
