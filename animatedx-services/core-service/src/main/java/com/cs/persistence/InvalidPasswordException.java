package com.cs.persistence;

/**
 * @author Omid Alaepour.
 */
public class InvalidPasswordException extends RuntimeException {
    public InvalidPasswordException(final String message) {
        super(message);
    }
}
