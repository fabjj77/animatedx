package com.cs.persistence;

import com.cs.payment.Money;

/**
 * @author Hadi Movaghar
 */
public class NegativeWinException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public static final String NEGATIVE_WIN_EXCEPTION = "The win amount is negative: %f";

    public NegativeWinException(final Money amount) {
        super(String.format(NEGATIVE_WIN_EXCEPTION, amount.getEuroValueInBigDecimal()));
    }
}
