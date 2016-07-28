package com.cs.rest.status;

/**
 * @author Omid Alaepour.
 */
public abstract class ResponseMessage extends Message {

    private static final long serialVersionUID = 1L;

    protected ResponseMessage(final StatusCode code, final String message) {
        super(code, message);
    }
}
