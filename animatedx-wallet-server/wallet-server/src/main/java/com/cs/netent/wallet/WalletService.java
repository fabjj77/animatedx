package com.cs.netent.wallet;

import com.cs.payment.Money;
import com.cs.player.Player;

import javax.annotation.Nullable;

/**
 * @author Hadi Movaghar
 */
public interface WalletService {
    Long win(final Player player, final String currency, @Nullable final Boolean bigWin, final Money winAmount, final String gameRoundRef, final String transactionRef,
             final String gameId, final String sessionId, final String reason, final String source, @Nullable final Long bonusProgramId);

    Long bet(final Player player, final String currency, final Money betAmount, final String gameRoundRef, final String transactionRef, final String gameId,
             final String sessionId, final String reason);

    Long betAndWin(final Player player, final String currency, final Boolean bigWin, final Money betAmount, final Money winAmount, final String transactionRef,
                   final String gameId, final String sessionId, final String reason, final String gameRoundRef, final String source);

    Money getBalance(final Player player, final String currency);

    void rollback(final Player player, final String transactionRef);
}
