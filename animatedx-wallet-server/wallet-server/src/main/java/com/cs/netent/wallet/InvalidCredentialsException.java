package com.cs.netent.wallet;

/**
 * @author Joakim Gottzén
 */
public class InvalidCredentialsException extends Exception {
    private static final long serialVersionUID = 1L;

    public InvalidCredentialsException(final String message) {
        super(message);
    }
}
