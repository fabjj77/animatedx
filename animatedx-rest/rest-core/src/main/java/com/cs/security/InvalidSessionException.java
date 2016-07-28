package com.cs.security;

/**
 * @author Hadi Movaghar
 */
public class InvalidSessionException  extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public InvalidSessionException(final String message) {
        super(message);
    }
}
