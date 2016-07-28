package com.cs.payment;

/**
 * @author Omid Alaepour
 */
public class InvalidCreditAmountException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public InvalidCreditAmountException(final String message){
        super(message);
    }
}
