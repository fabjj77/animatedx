package com.cs.persistence;

/**
 * @author Omid Alaepour.
 */
public class InvalidArgumentException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public InvalidArgumentException(final String message) {
        super(message);
    }
}
