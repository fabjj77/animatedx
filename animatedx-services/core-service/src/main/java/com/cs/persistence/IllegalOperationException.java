package com.cs.persistence;

/**
 * @author Hadi Movaghar
 */
public class IllegalOperationException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    public static final String ILLEGAL_OPERATION_IN_COUNTRY = "The operation is not permitted in %s";

    public IllegalOperationException(final String message) {
        super(message);
    }

    public IllegalOperationException(final Country country) {
        super(String.format(ILLEGAL_OPERATION_IN_COUNTRY, country));
    }
}
