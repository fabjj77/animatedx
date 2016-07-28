package com.cs.rest.status;

import com.cs.payment.InvalidOriginalPaymentException;

import static com.cs.rest.status.StatusCode.INVALID_ORIGINAL_PAYMENT;

/**
 * @author Hadi Movaghar
 */
public class InvalidOriginalPaymentMessage extends ErrorMessage {

    private static final long serialVersionUID = 1L;

    public InvalidOriginalPaymentMessage(final String message) {
        super(INVALID_ORIGINAL_PAYMENT, message);
    }

    public static InvalidOriginalPaymentMessage of(final InvalidOriginalPaymentException exception) {
        return new InvalidOriginalPaymentMessage(exception.getMessage());
    }
}

