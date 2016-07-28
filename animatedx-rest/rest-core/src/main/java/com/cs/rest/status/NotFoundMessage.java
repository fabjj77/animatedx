package com.cs.rest.status;

import com.cs.persistence.NotFoundException;

/**
 * @author Joakim Gottzén
 */
public class NotFoundMessage extends ErrorMessage {

    private static final long serialVersionUID = 1L;

    private NotFoundMessage(final String message) {
        super(StatusCode.NOT_FOUND, message);
    }

    public static NotFoundMessage of(final NotFoundException exception) {
        return new NotFoundMessage(exception.getMessage());
    }
}
