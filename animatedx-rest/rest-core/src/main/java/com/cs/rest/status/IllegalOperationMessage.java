package com.cs.rest.status;

import com.cs.persistence.IllegalOperationException;

/**
 * @author Hadi Movaghar
 */
public class IllegalOperationMessage extends ErrorMessage {

    private static final long serialVersionUID = 1L;

    protected IllegalOperationMessage(final String message) {
        super(StatusCode.ILLEGAL_OPERATION, message);
    }

    public static IllegalOperationMessage of(final IllegalOperationException exception) {
        return new IllegalOperationMessage(exception.getMessage());
    }
}
