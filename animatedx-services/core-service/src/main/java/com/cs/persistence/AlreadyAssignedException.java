package com.cs.persistence;

/**
 * @author Hadi Movaghar
 */
public class AlreadyAssignedException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public AlreadyAssignedException(final String message) {
        super(message);
    }
}
