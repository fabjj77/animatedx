package com.cs.security;

/**
 * @author Joakim Gottzén
 */
public class SessionExpiredException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public SessionExpiredException(final String message) {
        super(message);
    }
}
