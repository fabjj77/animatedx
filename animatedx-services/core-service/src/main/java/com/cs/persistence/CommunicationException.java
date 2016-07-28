package com.cs.persistence;

/**
 * @author Hadi Movaghar
 */
public class CommunicationException extends RuntimeException {

    private static final long serialVersionUID = 4L;

    public CommunicationException(final String reason) {
        super(reason);
    }

    public CommunicationException(final String reason, final Exception e) {
        super(reason, e);
    }
}
