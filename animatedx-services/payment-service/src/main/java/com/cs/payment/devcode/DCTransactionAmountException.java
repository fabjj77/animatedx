package com.cs.payment.devcode;

/**
 * @author Hadi Movaghar
 */
public class DCTransactionAmountException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final String playerId;

    public DCTransactionAmountException(final Long playerId, final String message) {
        super(message);
        this.playerId = playerId.toString();
    }

    public String getPlayerId() {
        return playerId;
    }
}
