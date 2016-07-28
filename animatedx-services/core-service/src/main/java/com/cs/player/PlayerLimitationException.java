package com.cs.player;

/**
 * @author Hadi Movaghar
 */
public class PlayerLimitationException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public PlayerLimitationException(final String message) {
        super(message);
    }
}
