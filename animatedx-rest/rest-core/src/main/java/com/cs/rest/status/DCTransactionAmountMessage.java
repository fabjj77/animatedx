package com.cs.rest.status;

import com.cs.payment.devcode.DCTransactionAmountException;

/**
 * @author Hadi Movaghar
 */
public class DCTransactionAmountMessage extends DevcodeErrorMessage {
    private static final long serialVersionUID = 1L;

    public DCTransactionAmountMessage(final String playerId, final String errorMessage) {
        super(playerId, StatusCode.DEVCODE_INVALID_AMOUNT, errorMessage);
    }

    public static DCTransactionAmountMessage of(final DCTransactionAmountException exception) {
        return new DCTransactionAmountMessage(exception.getPlayerId(), exception.getMessage());
    }
}
