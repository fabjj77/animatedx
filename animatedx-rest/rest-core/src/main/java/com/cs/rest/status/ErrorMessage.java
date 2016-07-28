package com.cs.rest.status;

/**
 * @author Joakim Gottzén
 */
public abstract class ErrorMessage extends Message {

    private static final long serialVersionUID = 1L;

    protected ErrorMessage(final StatusCode code, final String message) {
        super(code, message);
    }

    protected ErrorMessage(final StatusCode code, final String message, final String value) {
        super(code, message, value);
    }
}
