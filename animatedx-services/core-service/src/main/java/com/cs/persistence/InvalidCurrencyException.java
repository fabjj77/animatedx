package com.cs.persistence;

import com.cs.payment.Currency;

/**
 * @author Omid Alaepour
 */
public class InvalidCurrencyException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public static final String CURRENCY_IS_NOT_VALID = "Currency: %s not found";

    public InvalidCurrencyException(final Currency currency) {
        super(String.format(CURRENCY_IS_NOT_VALID, currency));
    }

    public InvalidCurrencyException(final String message) {
        super(String.format(CURRENCY_IS_NOT_VALID, message));
    }
}
