package com.cs.player;

/**
 * @author Hadi Movaghar
 */
public class BlockedPlayerException extends RuntimeException{
    public BlockedPlayerException(final String message) {
        super(message);
    }
}
