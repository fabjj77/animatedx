package com.cs.persistence;

/**
 * @author Hadi Movaghar
 */
public class IpBlockedException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public IpBlockedException(final String message) {
        super(message);
    }
}
