package com.cs.game;

import com.cs.bonus.PlayerBonus;
import com.cs.payment.Currency;
import com.cs.payment.Money;
import com.cs.player.Player;

import org.springframework.data.domain.Page;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Joakim Gottzén
 */
public interface GameTransactionService {

    @Nonnull
    Long addTransaction(final Player player, final Money moneyDeposit, final Money moneyWithdraw, final Money bonusDeposit, final Money bonusWithdraw,
                        final Money moneyBalance, final Money bonusBalance, final Currency currency, final String transactionRef, final String reason,
                        final Boolean reachedBonusConversionGoal, @Nullable final PlayerBonus activePlayerBonus);

    @Nonnull
    Long addTransaction(final Player player, final Money moneyDeposit, final Money moneyWithdraw, final Money bonusDeposit, final Money bonusWithdraw,
                        final Money moneyBalance, final Money bonusBalance, final Currency currency, final String gameRoundRef, final String transactionRef,
                        final String gameId, final String sessionId, final String reason, final Boolean reachedBonusConversionGoal,
                        @Nullable final PlayerBonus activePlayerBonus);

    @Nonnull
    Long addTransaction(final Player player, final Money moneyDeposit, final Money moneyWithdraw, final Money bonusDeposit, final Money bonusWithdraw,
                        final Money moneyBalance, final Money bonusBalance, final Currency currency, final String gameRoundRef, final String transactionRef,
                        final String gameId, final String sessionId, final String reason, final String source, final Boolean reachedBonusConversionGoal,
                        @Nullable final PlayerBonus activePlayerBonus);

    @Nullable
    GameTransaction getTransaction(final String transactionRef);

    void updateTransaction(final GameTransaction gameTransaction);

    @Nonnull
    List<GameTransaction> getTransactionsByPlayerAndGameRound(final Player player, final String gameRef);

    Page<GameTransaction> getGameTransactions(Player player, Date startDate, Date endDate, final Integer page, final Integer size);

    Iterable<GameTransaction> getGameTransactions(final Player player, final Date startDate, final Date endDate);

    Map<BigInteger, Money> getPlayersBetAmounts(final Date startDateTrimmed, final Date endDateTrimmed);

    List<LeaderboardEntry> getLeaderPlayersOfTheWeek(@Nonnull Date desiredDate);

    Map<BigInteger, GameTransactionSummary> getAffiliatePlayersGameTransactionSummary(final Date startDate, final Date endDate);
}
