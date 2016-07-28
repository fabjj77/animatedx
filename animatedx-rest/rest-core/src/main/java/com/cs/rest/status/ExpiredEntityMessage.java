package com.cs.rest.status;

import com.cs.persistence.ExpiredEntityException;

/**
 * @author Hadi Movaghar
 */
public class ExpiredEntityMessage extends ErrorMessage {

    private static final long serialVersionUID = 1L;

    protected ExpiredEntityMessage(final String message) {
        super(StatusCode.EXPIRED_ENTITY, message);
    }

    public static ExpiredEntityMessage of(final ExpiredEntityException exception) {
        return new ExpiredEntityMessage(exception.getMessage());
    }
}
