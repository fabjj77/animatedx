package com.cs.persistence;

/**
 * @author Omid Alaepour.
 */
public class ValidationException extends RuntimeException {
    public ValidationException(final String validationMessages) {
        super(validationMessages);
    }
}
