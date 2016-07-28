package com.cs.persistence;

import com.cs.payment.Money;

/**
 * @author Hadi Movaghar
 */
public class NegativeBetException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public static final String NEGATIVE_BET_EXCEPTION = "The bet amount is negative: %f";

    public NegativeBetException(final Money amount) {
        super(String.format(NEGATIVE_BET_EXCEPTION, amount.getEuroValueInBigDecimal()));
    }
}
