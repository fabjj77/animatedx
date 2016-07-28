package com.cs.payment;

/**
 * @author Hadi Movaghar
 */
public class InvalidOriginalPaymentException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    public static final String ILLEGAL_OPERATION_EXCEPTION = "The operation: %s is not permitted.";
    public static final String ORIGINAL_PAYMENT_NOT_FOUND_EXCEPTION = "The successful original payment: %s not found.";

    public InvalidOriginalPaymentException(final Operation operation) {
        super(String.format(ILLEGAL_OPERATION_EXCEPTION, operation));
    }

    public InvalidOriginalPaymentException(final String reason) {
        super(String.format(ORIGINAL_PAYMENT_NOT_FOUND_EXCEPTION, reason));
    }
}
