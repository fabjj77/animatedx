package com.cs.persistence;

/**
 * @author Hadi Movaghar
 */
public class ExpiredEntityException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ExpiredEntityException(final String message) {
        super(message);
    }

}
