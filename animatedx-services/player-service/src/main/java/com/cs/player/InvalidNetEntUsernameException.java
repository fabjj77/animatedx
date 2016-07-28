package com.cs.player;

import com.cs.persistence.NotFoundException;

/**
 * @author Joakim Gottzén
 */
public class InvalidNetEntUsernameException extends NotFoundException {
    private static final long serialVersionUID = 1L;

    public InvalidNetEntUsernameException(final String message) {
        super(message);
    }
}
