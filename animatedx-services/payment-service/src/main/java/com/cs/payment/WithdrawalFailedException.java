package com.cs.payment;

/**
 * @author Joakim Gottzén
 */
public class WithdrawalFailedException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public WithdrawalFailedException(final String message) {
        super(message);
    }
}
