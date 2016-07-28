package com.cs.payment;

/**
 * @author Hadi Movaghar
 */
public class IncorrectBankAccountException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public IncorrectBankAccountException(final String message) {
        super(message);
    }

    public IncorrectBankAccountException(final String reason, final Exception e) {
        super(reason, e);
    }
}
