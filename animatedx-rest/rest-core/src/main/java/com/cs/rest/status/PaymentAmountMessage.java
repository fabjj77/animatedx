package com.cs.rest.status;

import com.cs.payment.PaymentAmountException;

/**
 * @author Hadi Movaghar
 */
public class PaymentAmountMessage extends ErrorMessage {

    private static final long serialVersionUID = 1L;

    public PaymentAmountMessage(final String message, final String value) {
        super(StatusCode.INVALID_AMOUNT, message, value);
    }

    public static PaymentAmountMessage of(final PaymentAmountException exception) {
        return new PaymentAmountMessage(exception.getMessage(), exception.getValue());
    }
}
