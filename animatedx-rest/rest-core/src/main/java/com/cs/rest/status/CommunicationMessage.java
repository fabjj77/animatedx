package com.cs.rest.status;

import com.cs.persistence.CommunicationException;

import static com.cs.rest.status.StatusCode.COMMUNICATION_EXCEPTION;

/**
 * @author Hadi Movaghar
 */
public class CommunicationMessage extends ErrorMessage {

    private static final long serialVersionUID = 1L;

    public CommunicationMessage(final String message) {
        super(COMMUNICATION_EXCEPTION, message);
    }

    public static CommunicationMessage of(final CommunicationException exception) {
        return new CommunicationMessage(exception.getMessage());
    }
}
