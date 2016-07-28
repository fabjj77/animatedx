package com.cs.payment;

/**
 * @author Joakim Gottzén
 */
@SuppressWarnings("serial")
public class DepositException extends RuntimeException {
    public DepositException(final Throwable cause) {
        super(cause);
    }
}
