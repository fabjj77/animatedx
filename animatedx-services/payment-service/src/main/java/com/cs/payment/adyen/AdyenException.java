package com.cs.payment.adyen;

/**
 * @author Joakim Gottzén
 */
public class AdyenException extends Exception {

    private static final long serialVersionUID = 1L;

    public AdyenException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
