package com.cs.session;

import com.cs.player.Player;

/**
 * @author Hadi Movaghar
 */
public interface PlayerSessionService {

    Boolean isPlayerSessionValidForPayment(final Player player, final String uuid);

    String  getPaymentUuid(final Player player);
}
