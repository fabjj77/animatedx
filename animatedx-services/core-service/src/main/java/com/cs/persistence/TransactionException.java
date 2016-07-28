package com.cs.persistence;

/**
 * @author Hadi Movaghar
 */
public class TransactionException extends RuntimeException {
    public static final String TRANSACTION_INSERTION_FAILURE = "Transaction insertion failure";

    public TransactionException() {
        super(TRANSACTION_INSERTION_FAILURE);
    }
}
