package com.cs.rest.status;

import com.cs.persistence.InvalidPasswordException;

import static com.cs.rest.status.StatusCode.INCORRECT_PASSWORD_CHANGE;

/**
 * @author Omid Alaepour.
 */
public class InvalidPasswordMessage extends ErrorMessage {

    private static final long serialVersionUID = 1L;

    public InvalidPasswordMessage(final String message) {
        super(INCORRECT_PASSWORD_CHANGE, message);
    }

    public static InvalidPasswordMessage of(final InvalidPasswordException exception) {
        return new InvalidPasswordMessage(exception.getMessage());
    }
}
