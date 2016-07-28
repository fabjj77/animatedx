package com.cs.rest.status;

/**
 * @author Omid Alaepour.
 */
public class IllegalStateMessage extends ErrorMessage {

    private static final long serialVersionUID = 1L;

    protected IllegalStateMessage(final String message) {
        super(StatusCode.ILLEGAL_STATE, message);
    }

    public static IllegalStateMessage of(final IllegalStateException exception) {
        return new IllegalStateMessage(exception.getMessage());
    }
}
