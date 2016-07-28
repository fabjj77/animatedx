package com.cs.security;

/**
 * @author Joakim Gottzén
 */
@SuppressWarnings("serial")
public class AccessDeniedException extends RuntimeException {
    public AccessDeniedException(final String message) {
        super(message);
    }
}
