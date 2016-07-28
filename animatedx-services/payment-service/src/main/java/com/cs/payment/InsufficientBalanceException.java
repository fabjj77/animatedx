package com.cs.payment;

/**
 * @author Joakim Gottzén
 */
public class InsufficientBalanceException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public InsufficientBalanceException(final Money balance, final Money requestedAmount) {
        super(String.format("balance: %s, amount: %s", balance, requestedAmount));
    }
}
