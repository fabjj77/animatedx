package com.cs.payment;

/**
 * @author Hadi Movaghar
 */
public class LimitationException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    
    public LimitationException(final String reason) {
        super(reason);
    }
}
