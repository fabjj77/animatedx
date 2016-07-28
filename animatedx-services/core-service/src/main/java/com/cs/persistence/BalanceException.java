package com.cs.persistence;

import com.cs.payment.Money;

/**
 * @author Hadi Movaghar
 */
public class BalanceException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public static final String NOT_ENOUGH_BALANCE_EXCEPTION = "The balance is less than: %f";

    public BalanceException(final Money balance) {
        super(String.format(NOT_ENOUGH_BALANCE_EXCEPTION, balance.getEuroValueInBigDecimal()));
    }
}
