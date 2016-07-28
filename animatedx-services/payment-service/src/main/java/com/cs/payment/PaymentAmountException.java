package com.cs.payment;

/**
 * @author Hadi Movaghar
 */
public class PaymentAmountException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private final String value;

    @SuppressWarnings("ConstantConditions")
    public PaymentAmountException(final String reason) {
        this(reason ,null);
    }

    public PaymentAmountException(final String reason, final String value) {
        super(reason);
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
