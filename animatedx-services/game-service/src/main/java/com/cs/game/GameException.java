package com.cs.game;

/**
 * @author Hadi Movaghar
 */
public class GameException extends RuntimeException{
    public GameException(final String reason){
        super(reason);
    }

    public GameException(final String reason, final Exception e) {
        super(reason, e);
    }
}
