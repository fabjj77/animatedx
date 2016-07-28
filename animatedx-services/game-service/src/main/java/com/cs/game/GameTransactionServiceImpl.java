package com.cs.game;

import com.cs.bonus.PlayerBonus;
import com.cs.payment.Currency;
import com.cs.payment.Money;
import com.cs.persistence.Constants;
import com.cs.player.Player;
import com.cs.util.CalendarUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.querydsl.QSort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.mysema.query.types.Order;
import com.mysema.query.types.OrderSpecifier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.transaction.annotation.Propagation.SUPPORTS;

/**
 * @author Joakim Gottzén
 */
@Service
@Transactional(isolation = Isolation.READ_COMMITTED)
public class GameTransactionServiceImpl implements GameTransactionService {

    private final GameTransactionRepository gameTransactionRepository;

    @Autowired
    public GameTransactionServiceImpl(final GameTransactionRepository gameTransactionRepository) {
        this.gameTransactionRepository = gameTransactionRepository;
    }

    @Nonnull
    @Override
    public Long addTransaction(final Player player, final Money moneyDeposit, final Money moneyWithdraw, final Money bonusDeposit, final Money bonusWithdraw,
                               final Money moneyBalance, final Money bonusBalance, final Currency currency, final String transactionRef, final String reason,
                               final Boolean reachedBonusConversionGoal, @Nullable final PlayerBonus activePlayerBonus) {
        return addTransactionInternal(player, moneyDeposit, moneyWithdraw, bonusDeposit, bonusWithdraw, moneyBalance, bonusBalance, currency, null, transactionRef,
                                      null, null, reason, null, reachedBonusConversionGoal, activePlayerBonus);
    }

    @Nonnull
    private Long addTransactionInternal(final Player player, final Money moneyDeposit, final Money moneyWithdraw, final Money bonusDeposit, final Money bonusWithdraw,
                                        final Money moneyBalance, final Money bonusBalance, final Currency currency, @Nullable final String gameRoundRef,
                                        final String transactionRef, @Nullable final String gameId, @Nullable final String sessionId, final String reason,
                                        @Nullable final String source, final Boolean reachedBonusConversionGoal, @Nullable final PlayerBonus activePlayerBonus) {
        GameTransaction netEntTransaction = new GameTransaction(player, moneyDeposit, moneyWithdraw, bonusDeposit, bonusWithdraw, moneyBalance, bonusBalance,
                                                                currency, new Date(), transactionRef, gameRoundRef, gameId, reason, sessionId, source,
                                                                reachedBonusConversionGoal, activePlayerBonus);
        netEntTransaction = gameTransactionRepository.save(netEntTransaction);
        return netEntTransaction.getId();
    }

    @Nonnull
    @Override
    public Long addTransaction(final Player player, final Money moneyDeposit, final Money moneyWithdraw, final Money bonusDeposit, final Money bonusWithdraw,
                               final Money moneyBalance, final Money bonusBalance, final Currency currency, final String gameRoundRef, final String transactionRef,
                               final String gameId, final String sessionId, final String reason, final Boolean reachedBonusConversionGoal,
                               @Nullable final PlayerBonus activePlayerBonus) {
        return addTransactionInternal(player, moneyDeposit, moneyWithdraw, bonusDeposit, bonusWithdraw, moneyBalance, bonusBalance, currency, gameRoundRef,
                                      transactionRef, gameId, sessionId, reason, null, reachedBonusConversionGoal, activePlayerBonus);
    }

    @Nonnull
    @Override
    public Long addTransaction(final Player player, final Money moneyDeposit, final Money moneyWithdraw, final Money bonusDeposit, final Money bonusWithdraw,
                               final Money moneyBalance, final Money bonusBalance, final Currency currency, final String gameRoundRef, final String transactionRef,
                               final String gameId, final String sessionId, final String reason, final String source, final Boolean reachedBonusConversionGoal,
                               @Nullable final PlayerBonus activePlayerBonus) {
        return addTransactionInternal(player, moneyDeposit, moneyWithdraw, bonusDeposit, bonusWithdraw, moneyBalance, bonusBalance, currency, gameRoundRef,
                                      transactionRef, gameId, sessionId, reason, source, reachedBonusConversionGoal, activePlayerBonus);
    }

    @Transactional(propagation = SUPPORTS)
    @Nullable
    @Override
    public GameTransaction getTransaction(final String transactionRef) {
        return gameTransactionRepository.findByTransactionRef(transactionRef);
    }

    @Override
    public void updateTransaction(final GameTransaction gameTransaction) {
        gameTransactionRepository.save(gameTransaction);
    }

    @Transactional(propagation = SUPPORTS)
    @Nonnull
    @Override
    public List<GameTransaction> getTransactionsByPlayerAndGameRound(final Player player, final String gameRef) {
        return gameTransactionRepository.findPlayerGameRound(player, gameRef);
    }

    @Transactional(propagation = SUPPORTS)
    @Override
    public Page<GameTransaction> getGameTransactions(final Player player, final Date startDate, final Date endDate, final Integer page, final Integer size) {
        final Calendar instance = Calendar.getInstance();
        final Date startDateTrimmed = CalendarUtils.startOfDay(instance, startDate);
        final Date endDateTrimmed = CalendarUtils.endOfDay(instance, endDate);

        final QSort sort = new QSort(new OrderSpecifier<>(Order.DESC, QGameTransaction.gameTransaction.createdDate));
        return gameTransactionRepository.findByPlayerBetweenDates(player, startDateTrimmed, endDateTrimmed, new PageRequest(page, size, sort));
    }

    @Transactional(propagation = SUPPORTS)
    @Override
    public Iterable<GameTransaction> getGameTransactions(final Player player, final Date startDate, final Date endDate) {
        final Calendar instance = Calendar.getInstance();
        final Date startDateTrimmed = CalendarUtils.startOfDay(instance, startDate);
        final Date endDateTrimmed = CalendarUtils.endOfDay(instance, endDate);

        return gameTransactionRepository.findByPlayerBetweenDates(player, startDateTrimmed, endDateTrimmed);
    }

    @Transactional(propagation = SUPPORTS, readOnly = true)
    @Override
    public Map<BigInteger, Money> getPlayersBetAmounts(final Date startDateTrimmed, final Date endDateTrimmed) {
        final Map<BigInteger, Money> map = new HashMap<>();
        final List<Object[]> list = gameTransactionRepository.getTotalBetByPlayerBetweenDates(startDateTrimmed, endDateTrimmed);
        for (final Object[] tuple : list) {
            map.put((BigInteger)tuple[0], new Money((BigDecimal) tuple[1]));
        }
        return map;
    }

    @Override
    public List<LeaderboardEntry> getLeaderPlayersOfTheWeek(@Nonnull Date desiredDate) {
        //
        Calendar startCalendarDate = Calendar.getInstance();
        Calendar endCalendarDate = Calendar.getInstance();

        startCalendarDate.setTime(desiredDate);
        if (CalendarUtils.isStartOfWeek(startCalendarDate)) {
            // Need to sum 1 to week
            startCalendarDate.add(Calendar.DAY_OF_WEEK, 1);
            endCalendarDate.setTime(desiredDate);
            endCalendarDate.add(Calendar.DAY_OF_WEEK, 1);
            endCalendarDate.add(Calendar.WEEK_OF_YEAR, 1);
        } else {
            // Need to assign the "desiredDate" to the "endCalendarDate" and then sum 1 week
            endCalendarDate.setTime(desiredDate);
            CalendarUtils.startOfWeek(startCalendarDate);
            CalendarUtils.startOfWeek(endCalendarDate);
            startCalendarDate.add(Calendar.DAY_OF_WEEK, 1);
            endCalendarDate.add(Calendar.DAY_OF_WEEK, 1);
            endCalendarDate.add(Calendar.WEEK_OF_YEAR, 1);
        }

        //PageSize was set in the query since we cannot use PageRequest in NativeQueries
        final List<Object[]> playerObjectList = gameTransactionRepository.getLeaderPlayersByWeek(startCalendarDate.getTime(),
                                                                                           endCalendarDate.getTime(),
                                                                                           Constants.DEFAULT_PAGE_SIZE);

        List<LeaderboardEntry> leaderboardEntryList = new ArrayList<>();

        for (Object[] playerObject : playerObjectList){
            LeaderboardEntry leaderboardEntry = new LeaderboardEntry(playerObject);
            leaderboardEntryList.add(leaderboardEntry);
        }

        //Looking for immutability
        Collections.unmodifiableList(leaderboardEntryList);

        return leaderboardEntryList;
    }

    @Transactional(propagation = SUPPORTS, readOnly = true)
    @Override
    public Map<BigInteger, GameTransactionSummary> getAffiliatePlayersGameTransactionSummary(final Date startDate, final Date endDate) {
        final Map<BigInteger, GameTransactionSummary> map = new HashMap<>();
        final List<Object[]> list = gameTransactionRepository.getAffiliatePlayersGameTransactionSummary(startDate, endDate);
        for (final Object[] tuple : list) {
            final BigInteger playerId = (BigInteger) tuple[0];
            final long numberOfTransactions = ((BigInteger) tuple[1]).longValue();
            final Money totalMoneyWin = Money.getMoneyFromCents((BigDecimal) tuple[2]);
            final Money totalMoneyBet = Money.getMoneyFromCents((BigDecimal) tuple[3]);
            final Money totalBonusWin = Money.getMoneyFromCents((BigDecimal) tuple[4]);
            final Money totalBonusBet = Money.getMoneyFromCents((BigDecimal) tuple[5]);

            map.put(playerId, new GameTransactionSummary(totalMoneyWin, totalMoneyBet, totalBonusWin, totalBonusBet, numberOfTransactions));
        }
        return map;
    }
}
