package com.cs.rest.status;

import com.cs.payment.WithdrawalFailedException;

/**
 * @author Joakim Gottzén
 */
public class WithdrawalFailedMessage extends ErrorMessage {

    private static final long serialVersionUID = 1L;

    public WithdrawalFailedMessage(final String message) {
        super(StatusCode.WITHDRAWAL_FAILED, message);
    }

    public static WithdrawalFailedMessage of(final WithdrawalFailedException exception) {
        return new WithdrawalFailedMessage(exception.getMessage());
    }
}
