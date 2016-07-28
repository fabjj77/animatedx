package com.cs.payment.devcode;

/**
 * @author Hadi Movaghar
 */
public class DCInvalidPlayerException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final String playerId;
    public static final String INVALID_PLAYER = "Player %s is not valid";

    public DCInvalidPlayerException(final String playerId) {
        super(String.format(INVALID_PLAYER, playerId));
        this.playerId = playerId;
    }

    public String getPlayerId() {
        return playerId;
    }
}
