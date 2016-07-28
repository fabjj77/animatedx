package com.cs.rest.status;

import com.cs.persistence.BonusException;

/**
 * @author Omid Alaepour.
 */
public class IllegalBonusMessage extends ErrorMessage {

    private static final long serialVersionUID = 1L;

    protected IllegalBonusMessage(final String message) {
        super(StatusCode.INVALID_ARGUMENT, message);
    }

    public static IllegalBonusMessage of(final BonusException exception) {
        return new IllegalBonusMessage(exception.getMessage());
    }
}
