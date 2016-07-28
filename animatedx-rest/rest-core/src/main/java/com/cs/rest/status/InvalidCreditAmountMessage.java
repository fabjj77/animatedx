package com.cs.rest.status;

import com.cs.payment.InvalidCreditAmountException;

/**
 * @author Omid Alaepour
 */
public class InvalidCreditAmountMessage extends ErrorMessage {
    private static final long serialVersionUID = 1L;

    protected InvalidCreditAmountMessage(final String message) {
        super(StatusCode.INVALID_CREDIT_AMOUNT, message);
    }

    public static InvalidCreditAmountMessage of(final InvalidCreditAmountException exception) {
        return new InvalidCreditAmountMessage(exception.getMessage());
    }
}
