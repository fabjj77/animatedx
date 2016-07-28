package com.cs.rest.status;

import com.cs.persistence.InvalidArgumentException;

import static com.cs.rest.status.StatusCode.INVALID_ARGUMENT;

/**
 * @author Omid Alaepour.
 */
public class InvalidArgumentMessage extends ErrorMessage {

    private static final long serialVersionUID = 1L;

    public InvalidArgumentMessage(final String message) {
        super(INVALID_ARGUMENT, message);
    }

    public static InvalidArgumentMessage of(final InvalidArgumentException exception) {
        return new InvalidArgumentMessage(exception.getMessage());
    }
}
