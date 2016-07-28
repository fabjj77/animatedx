package com.cs.payment.devcode;

/**
 * @author Hadi Movaghar
 */
public class DCPlayerDetailException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final String playerId;

    public DCPlayerDetailException(final Long playerId, final String message) {
        super(message);
        this.playerId = playerId.toString();
    }

    public String getPlayerId() {
        return playerId;
    }
}
