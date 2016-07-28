package com.cs.netent.wallet;

import com.cs.bonus.BonusService;
import com.cs.bonus.BonusStatus;
import com.cs.bonus.PlayerBonus;
import com.cs.game.Game;
import com.cs.game.GameService;
import com.cs.game.GameTransaction;
import com.cs.game.GameTransactionService;
import com.cs.game.PlayerLimitationLockoutRepository;
import com.cs.game.PlayerLimitationRepository;
import com.cs.payment.Currency;
import com.cs.payment.EventCode;
import com.cs.payment.Money;
import com.cs.payment.PaymentService;
import com.cs.payment.ProviderReferences;
import com.cs.persistence.BalanceException;
import com.cs.persistence.InvalidCurrencyException;
import com.cs.persistence.NegativeBetException;
import com.cs.persistence.NegativeWinException;
import com.cs.player.Player;
import com.cs.player.PlayerLimitation;
import com.cs.player.PlayerLimitationException;
import com.cs.player.PlayerLimitationLockout;
import com.cs.player.PlayerService;
import com.cs.player.TimeUnit;
import com.cs.player.Wallet;
import com.cs.player.WalletRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.annotations.VisibleForTesting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.cs.payment.Money.ZERO;
import static com.cs.player.BlockType.BET_LIMIT;
import static com.cs.player.BlockType.LOSS_LIMIT;
import static com.cs.player.BlockType.UNBLOCKED;
import static com.cs.player.LimitationType.BET_AMOUNT;
import static com.cs.player.LimitationType.LOSS_AMOUNT;
import static org.springframework.transaction.annotation.Isolation.READ_COMMITTED;
import static org.springframework.transaction.annotation.Propagation.SUPPORTS;

/**
 * @author Hadi Movaghar
 */
@Service
@Transactional(isolation = READ_COMMITTED)
public class WalletServiceImpl implements WalletService {

    private final Logger logger = LoggerFactory.getLogger(WalletServiceImpl.class);

    // These are the official reasons sent by Casino Module
    // Don't remove, we might want to do some validation against them
    @SuppressWarnings("UnusedDeclaration")
    private static final String AWARD_TOURNAMENT_WIN = "AWARD_TOURNAMENT_WIN";
    static final String WAGERED_BONUS = "WAGERED_BONUS";
    @SuppressWarnings("UnusedDeclaration")
    private static final String CLEAR_HANGED_GAME_STATE = "CLEAR_HANGED_GAME_STATE";
    static final String GAME_PLAY = "GAME_PLAY";
    static final String GAME_PLAY_FINAL = "GAME_PLAY_FINAL";
    @SuppressWarnings("UnusedDeclaration")
    private static final String BUY_TOURNAMENT_TICKET = "BUY_TOURNAMENT_TICKET";
    @SuppressWarnings("UnusedDeclaration")
    private static final String REFUND_TOURNAMENT_TICKET = "REFUND_TOURNAMENT_TICKET";

    @Value("${payment.progress_turnover_change_level}")
    private Integer progressTurnoverChangeLevel;

    @Value("#{new com.cs.payment.Money('${payment.minimum-bonus-balance-to-convert-to-money-in-cents}')}")
    private Money minimumBonusBalanceToConvertToMoney;

    @Value("${bonus.bonus-conversion-goal-multiplier}")
    private Double bonusConversionGoalMultiplier;

    private final BonusService bonusService;
    private final GameService gameService;
    private final GameTransactionService gameTransactionService;
    private final PaymentService paymentService;
    private final PlayerService playerService;
    private final PlayerLimitationRepository playerLimitationRepository;
    private final PlayerLimitationLockoutRepository playerLimitationLockoutRepository;
    private final WalletRepository walletRepository;

    @Autowired
    public WalletServiceImpl(final BonusService bonusService, final GameService gameService, final GameTransactionService gameTransactionService,
                             final PaymentService paymentService, final PlayerService playerService, final PlayerLimitationRepository playerLimitationRepository,
                             final PlayerLimitationLockoutRepository playerLimitationLockoutRepository, final WalletRepository walletRepository) {
        this.bonusService = bonusService;
        this.gameService = gameService;
        this.gameTransactionService = gameTransactionService;
        this.paymentService = paymentService;
        this.playerService = playerService;
        this.playerLimitationRepository = playerLimitationRepository;
        this.playerLimitationLockoutRepository = playerLimitationLockoutRepository;
        this.walletRepository = walletRepository;
    }

    @Override
    public Long win(final Player player, final String currency, @Nullable final Boolean bigWin, final Money winAmount, final String gameRoundRef,
                    final String transactionRef, final String gameId, final String sessionId, final String reason, final String source,
                    final Long bonusProgramId) {
        validateWinAmount(winAmount);
        validateCurrency(player, currency);

        logger.info("SessionId: {}. Player {} WON {} in game {}, gameRoundRef: {}, transactionRef: {}, reason: {}, source: {}, bigWin: {}", sessionId, player.getId(),
                    winAmount, gameId, gameRoundRef, transactionRef, reason, source, bigWin);

        // Big Wins are reported as separate #bet and #win calls, and can be retried by the CasinoModule
        // So, if a transaction already exists, do not create another row, and return the existing one
        if (bigWin != null && bigWin == Boolean.TRUE) {
            final GameTransaction gameTransaction = gameTransactionService.getTransaction(transactionRef);
            if (gameTransaction != null) {
                logger.warn("Ignoring request since a transaction with transactionRef {} already exists, and BIG_WIN is {}", transactionRef, true);
                return gameTransaction.getId();
            }
        }

        final List<GameTransaction> transactions = gameTransactionService.getTransactionsByPlayerAndGameRound(player, gameRoundRef);
        final GameRoundTransactions gameRoundTransactions = new GameRoundTransactions(transactions);

        // If it's a new game round, then get the current active bonus, otherwise use the bonus from the previous rounds
        // This ensures that only one bonus is used during a single game round
        final PlayerBonus activePlayerBonus = getPlayerBonus(player, gameRoundTransactions);

        final Wallet wallet = player.getWallet();
        final Money winMoney;
        final Money winBonus;

        if (transactions.isEmpty()) {
            winBonus = winAmount;
            winMoney = ZERO;

            // Special handling of the result of a free spins bonus
            if (WAGERED_BONUS.equalsIgnoreCase(reason)) {
                return handleFreeSpinCompletion(player, winBonus, transactionRef, reason, bonusProgramId);
            } else {
                logger.warn("No previous game transactions associated with game round {} and reason is not {}", gameRoundRef, WAGERED_BONUS);
            }
        } else {
            if (gameRoundTransactions.reachedBonusConversionGoal()) {
                // This is a real edge case:
                // The bet that resulted in this win has put the player above the progression goal, so we have to count
                // the win as real money.
                winBonus = ZERO;
                winMoney = winAmount;
            } else {
                final Money betMoney = gameRoundTransactions.getBetMoney();
                final Money betBonus = gameRoundTransactions.getBetBonus();
                winBonus = winAmount.calculateBonusMoney(betBonus, betMoney);
                winMoney = winAmount.subtract(winBonus);
            }
        }

        if (winMoney.isPositive()) {
            logger.debug("SessionId: {}. Adding {} to player {} money balance", sessionId, winMoney, player.getId());
            wallet.setMoneyBalance(wallet.getMoneyBalance().add(winMoney));
        }
        if (winBonus.isPositive()) {
            if (activePlayerBonus == null) {
                throw new IllegalStateException("There has to be an active bonus");
            }

            logger.debug("SessionId: {}. Adding {} to player {} bonus balance of player-bonus {}", sessionId, winBonus, player.getId(), activePlayerBonus.getId());
            @SuppressWarnings("ConstantConditions") final Money balance = activePlayerBonus.getCurrentBalance().add(winBonus);
            activePlayerBonus.setCurrentBalance(balance);
            bonusService.updatePlayerBonus(activePlayerBonus);
        }

        checkActivePlayerBonusIsLost(player, activePlayerBonus, reason);
        checkForSmallBonusMoneyConversion(player, sessionId, transactionRef, activePlayerBonus, reason);
        checkPlayerLossLimits(player, winAmount, reason);

        walletRepository.save(wallet);
        @SuppressWarnings("ConstantConditions") @Nonnull final Money bonusBalance = activePlayerBonus != null ? activePlayerBonus.getCurrentBalance() : ZERO;
        return gameTransactionService.addTransaction(player, winMoney, ZERO, winBonus, ZERO, wallet.getMoneyBalance(), bonusBalance, player.getCurrency(),
                                                     gameRoundRef, transactionRef, gameId, sessionId, reason, source, false, activePlayerBonus);
    }

    private void validateWinAmount(final Money amount) {
        if (amount.isNegative()) {
            throw new NegativeWinException(amount);
        }
    }

    private void validateCurrency(final Player player, final String currency) {
        try {
            if (player.getCurrency() != Currency.valueOf(currency)) {
                throw new InvalidCurrencyException(currency);
            }
        } catch (final IllegalArgumentException e) {
            throw new InvalidCurrencyException(currency);
        }
    }

    private Long handleFreeSpinCompletion(final Player player, final Money winBonus, final String transactionRef, final String reason,
                                          final Long bonusProgramId) {
        PlayerBonus playerBonus = null;
        if (bonusProgramId != null) {
            playerBonus = bonusService.handleFreeRoundsCompletionBonus(player, winBonus, bonusProgramId);
            paymentService.createBlingCityPayment(player, winBonus, ProviderReferences.BLING_CITY_BONUS, EventCode.FREE_SPIN_BONUS);
        } else {
            logger.error("Netent bonus id is null while giving {} free round win to player {}", winBonus.getEuroValueInBigDecimal(), player.getId());
        }

        return gameTransactionService.addTransaction(player, ZERO, ZERO, winBonus, ZERO, player.getWallet().getMoneyBalance(), winBonus, player.getCurrency(),
                                                     transactionRef, reason, false, playerBonus);
    }

    @Nullable
    private PlayerBonus getPlayerBonus(final Player player, final GameRoundTransactions gameRoundTransactions) {
        return gameRoundTransactions.isNewRound() ? bonusService.getActivePlayerBonus(player) : gameRoundTransactions.getActivePlayerBonus();
    }

    private void checkForSmallBonusMoneyConversion(final Player player, @Nullable final String sessionId, final String transactionRef,
                                                   @Nullable final PlayerBonus activePlayerBonus, final String reason) {
        if (activePlayerBonus == null || !reason.equals(GAME_PLAY_FINAL)) {
            return;
        }

        @SuppressWarnings("ConstantConditions") @Nonnull final Money bonusBalance = activePlayerBonus.getCurrentBalance();

        if (bonusBalance.isGreaterThan(ZERO) && bonusBalance.isLessOrEqualThan(minimumBonusBalanceToConvertToMoney)) {
            final PlayerBonus nextPlayerBonus = bonusService.finishCurrentAndActivateNextBonus(player, activePlayerBonus, BonusStatus.MOVED_TO_NEXT_BONUS);
            if (nextPlayerBonus != null) {
                @SuppressWarnings("ConstantConditions") @Nonnull final Money currentBalance = nextPlayerBonus.getCurrentBalance();
                nextPlayerBonus.setCurrentBalance(currentBalance.add(bonusBalance));
                @SuppressWarnings("ConstantConditions") @Nonnull final Money bonusConversionGoal = nextPlayerBonus.getBonusConversionGoal();
                nextPlayerBonus.setBonusConversionGoal(bonusConversionGoal.add(bonusBalance.multiply(bonusConversionGoalMultiplier)));
                bonusService.updatePlayerBonus(nextPlayerBonus);
                logger.info("SessionId: {}, TransactionRef: {}. Player {} tiny active bonus balance {} of player-bonus {} was moved to next active player-bonus {}.",
                            sessionId, transactionRef, player.getId(), bonusBalance, activePlayerBonus.getId(), nextPlayerBonus.getId());
            } else {
                activePlayerBonus.setStatus(BonusStatus.NEGLIGIBLE_CONVERTED);
                bonusService.updatePlayerBonus(activePlayerBonus);
                final Wallet wallet = player.getWallet();
                wallet.setMoneyBalance(wallet.getMoneyBalance().add(bonusBalance));
                logger.info("SessionId: {}, TransactionRef: {}. Player {} bonus balance {} of player-bonus {} was less than {}. Was added to real money since next " +
                            "active  player-bonus not found. Player-bonus status was set to {}", transactionRef, sessionId, player.getId(), bonusBalance,
                            activePlayerBonus.getId(), minimumBonusBalanceToConvertToMoney, BonusStatus.NEGLIGIBLE_CONVERTED);
                paymentService.createBonusConversionDeposit(player, bonusBalance);
            }
        }
    }

    private void checkPlayerLossLimits(final Player player, final Money amount, final String reason) {
        // Only ignore a zero amount if it's not a finished game round
        if (amount.isZero() && !GAME_PLAY_FINAL.equalsIgnoreCase(reason)) {
            return;
        }

        final PlayerLimitation playerLimitation = playerLimitationRepository.findByPlayer(player);
        if (!playerLimitation.isContainingDefaultValue(LOSS_AMOUNT)) {
            final Wallet wallet = player.getWallet();
            final TimeUnit lossTimeUnit = playerLimitation.getLossTimeUnit();
            final Money accumulatedLosses;
            if (!amount.isZero()) {
                accumulatedLosses = wallet.getAccumulatedLossAmountByTimeUnit(lossTimeUnit).subtract(amount);
                wallet.setAccumulatedLossAmountByTimeUnit(lossTimeUnit, accumulatedLosses);
            } else {
                accumulatedLosses = wallet.getAccumulatedLossAmountByTimeUnit(lossTimeUnit);
            }

            // Only check for the exceeding the loss limit if it's a game round finish
            if (GAME_PLAY_FINAL.equalsIgnoreCase(reason)) {
                if (accumulatedLosses.isGreaterThan(playerLimitation.getLossLimit())) {
                    final Date limitBlockStartDate = new Date();
                    logger.info("Player {} exceeded loss amount limitation on {}", player.getId(), limitBlockStartDate);
                    final Date limitBlockEndDate = calculateLockoutEndDate(limitBlockStartDate, lossTimeUnit.getTimeValue());
                    final PlayerLimitationLockout lockout = new PlayerLimitationLockout(player, LOSS_LIMIT, limitBlockStartDate, limitBlockEndDate);
                    player.setBlockType(LOSS_LIMIT);
                    player.setBlockEndDate(limitBlockEndDate);
                    playerService.savePlayer(player);
                    playerLimitationLockoutRepository.save(lockout);
                    // TODO push message through web-socket
                    gameService.logoutPlayer(player);
                } else {
                    // TODO check if loss amount is getting close to loss limit and push through web socket
                }
            }
        }
    }

    private Date calculateLockoutEndDate(final Date currentDate, final Integer selfExclusionDays) {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(Calendar.DATE, selfExclusionDays);
        return calendar.getTime();
    }

    @Override
    public Long bet(final Player player, final String currency, final Money betAmount, final String gameRoundRef, final String transactionRef, final String gameId,
                    final String sessionId, final String reason) {
        validatePlayerLimitations(player);
        validateBetAmount(betAmount);
        validateCurrency(player, currency);

        logger.info("SessionId: {}. Player {} BET {} in game {}, gameRoundRef: {}, transactionRef: {}, reason: {}", sessionId, player.getId(), betAmount, gameId,
                    gameRoundRef, transactionRef, reason);

        // Load previous rounds in this game round
        final List<GameTransaction> transactions = gameTransactionService.getTransactionsByPlayerAndGameRound(player, gameRoundRef);
        // We need to keep track of total bets
        final GameRoundTransactions gameRoundTransactions = new GameRoundTransactions(transactions);
        // If it's a new game round, then get the current active bonus, otherwise use the bonus from the previous rounds
        // This ensures that only one bonus is used during a single game round
        final PlayerBonus activePlayerBonus = getPlayerBonus(player, gameRoundTransactions);

        final Wallet wallet = player.getWallet();
        final Money totalBalance = getTotalBalance(activePlayerBonus, wallet);
        validateBalance(betAmount, totalBalance);

        final Bets bets = processBets(player, betAmount, Money.ZERO, sessionId, gameId, gameRoundRef, transactionRef, gameRoundTransactions, activePlayerBonus, reason);

        walletRepository.save(wallet);
        if (activePlayerBonus != null) {
            bonusService.updatePlayerBonus(activePlayerBonus);
        }
        @SuppressWarnings("ConstantConditions") @Nonnull final Money bonusBalance = activePlayerBonus != null ? activePlayerBonus.getCurrentBalance() : ZERO;
        return gameTransactionService.addTransaction(player, ZERO, bets.getMoneyBet(), ZERO, bets.getBonusBet(), wallet.getMoneyBalance(), bonusBalance,
                                                     player.getCurrency(), gameRoundRef, transactionRef, gameId, sessionId, reason, bets.reachedBonusConversionGoal(),
                                                     bets.getActivePlayerBonus());
    }

    private Money getTotalBalance(@Nullable final PlayerBonus activePlayerBonus, final Wallet wallet) {
        if (activePlayerBonus != null) {
            @SuppressWarnings("ConstantConditions") @Nonnull final Money currentBalance = activePlayerBonus.getCurrentBalance();
            return wallet.getMoneyBalance().add(currentBalance);
        } else {
            return wallet.getMoneyBalance();
        }
    }

    private void validatePlayerLimitations(final Player player) {
        if (player.getBlockType() != UNBLOCKED) {
            logger.warn("Player {} tried to make a bet while was blocked {}", player.getId(), player.getBlockType());
            throw new PlayerLimitationException("Player is blocked.");
        }
    }

    private void validateBetAmount(final Money amount) {
        if (amount.isNegative()) {
            throw new NegativeBetException(amount);
        }
    }

    private void validateBalance(final Money amount, final Money balance) {
        if (balance.isLessThan(amount)) {
            throw new BalanceException(amount);
        }
    }

    private Bets processBets(final Player player, final Money betAmount, final Money winAmount, final String sessionId, final String gameId, final String gameRoundRef,
                             final String transactionRef, final GameRoundTransactions gameRoundTransactions, @Nullable final PlayerBonus activePlayerBonus,
                             final String reason) {
        final Wallet wallet = player.getWallet();

        // Subtract the bet from the real money balance, this will go negative if it is 0,
        // this is handled below
        final Money remainingMoneyBalance = wallet.getMoneyBalance().subtract(betAmount);

        final Money moneyBet;
        final Money bonusBet;
        final boolean reachedGoal;
        // It's not enough to check that the current money balance is not negative, since this might be the last transaction in a
        // game round. So short circuit the check if any of the previous bets had at least one cent of bonus bet
        if (!remainingMoneyBalance.isNegative() && gameRoundTransactions.isRealMoneyOnlyBet()) {
            // Betting only real money

            moneyBet = betAmount;
            bonusBet = ZERO;
            wallet.setMoneyBalance(wallet.getMoneyBalance().subtract(moneyBet));
            logger.debug("SessionId: {}. Subtracting {} from player {} money balance", sessionId, moneyBet, player.getId());
            reachedGoal = false;
        } else {
            // Mixed or bonus only bet

            // The bet money is the lower of the total bet amount or the current money balance
            // This handles the event when real money was deposited into the wallet in between game transactions
            // or other sources add to the wallet
            moneyBet = betAmount.min(wallet.getMoneyBalance());
            // Subtract the bet from the money balance
            if (moneyBet.isPositive()) {
                wallet.setMoneyBalance(wallet.getMoneyBalance().subtract(moneyBet));
                logger.debug("SessionId: {}. Subtracting {} from player {} money balance", sessionId, moneyBet, player.getId());
            }
            bonusBet = betAmount.subtract(moneyBet);
            if (bonusBet.isPositive()) {
                if (activePlayerBonus == null) {
                    throw new IllegalStateException("There has to be an active bonus");
                }

                // Subtract the bonus bet from the bonus balance
                @SuppressWarnings("ConstantConditions") @Nonnull final Money currentBalance = activePlayerBonus.getCurrentBalance();
                activePlayerBonus.setCurrentBalance(currentBalance.subtract(bonusBet));
                logger.debug("SessionId: {}. Subtracting {} from player {} bonus balance of player bonus {}",
                             sessionId, bonusBet, player.getId(), activePlayerBonus.getId());

                // Add the bonus bet to the conversion progress
                // If the game is unknown, use 0% contribution
                final Double turnoverContributionFraction = geTurnoverContributionFraction(player, gameId, 0.00D);
                final Money bonusProgressContribution = bonusBet.multiply(turnoverContributionFraction);
                @SuppressWarnings("ConstantConditions") @Nonnull final Money bonusConversionProgress = activePlayerBonus.getBonusConversionProgress();
                activePlayerBonus.setBonusConversionProgress(bonusConversionProgress.add(bonusProgressContribution));
                logger.debug("SessionId: {}. Add {} to player {} bonus conversion progress of player-bonus {}",
                             sessionId, bonusProgressContribution, player.getId(), activePlayerBonus.getId());

                if (!winAmount.isPositive()) {
                    checkActivePlayerBonusIsLost(player, activePlayerBonus, reason);
                }

                reachedGoal = gameRoundTransactions.reachedBonusConversionGoal() || checkBonusConversionProgressReachedGoal(player, activePlayerBonus);
            } else {
                reachedGoal = false;
            }
        }

        addTurnovers(sessionId, gameId, player, moneyBet, bonusBet, gameRoundRef);
        checkForSmallBonusMoneyConversion(player, sessionId, transactionRef, activePlayerBonus, reason);
        checkPlayerBetLimits(player, betAmount);

        return new Bets(moneyBet, bonusBet, reachedGoal, activePlayerBonus);
    }

    private Double geTurnoverContributionFraction(final Player player, @Nullable final String gameId, @Nonnull final Double defaultContribution) {
        final Double turnoverContributionFraction;
        final Game game = gameService.getGame(gameId);
        if (game == null) {
            logger.error("Player {} played game {} that doesn't exist, setting turnover contribution to 100%", player.getId(), gameId);
            turnoverContributionFraction = defaultContribution;
        } else {
            turnoverContributionFraction = game.getCategory().getTurnoverContributionFraction();
        }
        return turnoverContributionFraction;
    }

    private boolean checkBonusConversionProgressReachedGoal(final Player player, @Nonnull final PlayerBonus activePlayerBonus) {
        @SuppressWarnings("ConstantConditions") @Nonnull final Money bonusConversionProgress = activePlayerBonus.getBonusConversionProgress();
        @SuppressWarnings("ConstantConditions") @Nonnull final Money bonusConversionGoal = activePlayerBonus.getBonusConversionGoal();
        @SuppressWarnings("ConstantConditions") @Nonnull final Money bonusBalance = activePlayerBonus.getCurrentBalance();

        final boolean reachedGoal = bonusConversionGoal.isPositive() && bonusConversionProgress.isGreaterOrEqualThan(bonusConversionGoal);
        if (reachedGoal) {
            logger.info("Player {} bonus conversion progress {} reached bonus conversion goal {}", player.getId(), bonusConversionProgress, bonusConversionGoal);
        }

        if (bonusBalance.isPositive() && reachedGoal) {
            final Money maxRedemptionAmount = activePlayerBonus.getPk().getBonus().getMaxRedemptionAmount();
            final Money maxRedemptionAmountPlayerBonus = activePlayerBonus.getMaxRedemptionAmount();
            Money redeemedAmount = maxRedemptionAmount != null ? bonusBalance.min(maxRedemptionAmount) : bonusBalance;
            redeemedAmount = maxRedemptionAmountPlayerBonus != null ? redeemedAmount.min(maxRedemptionAmountPlayerBonus) : redeemedAmount;

            final Wallet wallet = player.getWallet();
            final Money moneyBalance = wallet.getMoneyBalance().add(redeemedAmount);

            logger.info("Redeemed player {} bonus {}, balance {} was converted to {} real money, money balance is {}", player.getId(), activePlayerBonus.getId(),
                        bonusBalance, redeemedAmount, moneyBalance);
            wallet.setMoneyBalance(moneyBalance);
            bonusService.finishCurrentAndActivateNextBonus(player, activePlayerBonus, BonusStatus.GOAL_CONVERTED);

            paymentService.createBonusConversionDeposit(player, redeemedAmount);
        }
        return reachedGoal;
    }

    private void checkActivePlayerBonusIsLost(final Player player, @Nullable final PlayerBonus activePlayerBonus, final String reason) {
        if (activePlayerBonus == null) {
            return;
        }

        @SuppressWarnings("ConstantConditions") @Nonnull final Money bonusConversionProgress = activePlayerBonus.getBonusConversionProgress();
        @SuppressWarnings("ConstantConditions") @Nonnull final Money bonusConversionGoal = activePlayerBonus.getBonusConversionGoal();
        @SuppressWarnings("ConstantConditions") @Nonnull final Money bonusBalance = activePlayerBonus.getCurrentBalance();

        if (reason.equals(GAME_PLAY_FINAL) && bonusBalance.isLessOrEqualThan(ZERO) && (bonusConversionProgress.isPositive() || bonusConversionGoal.isPositive())) {
            logger.info("Player {} bonus balance is {}, setting bonus conversion progress and bonus conversion goal to {}", player.getId(), bonusBalance, ZERO);
            bonusService.finishCurrentAndActivateNextBonus(player, activePlayerBonus, BonusStatus.LOST);
        }
    }

    @VisibleForTesting
    void addTurnovers(final String sessionId, final String gameId, final Player player, final Money betMoney, final Money betBonus, final String gameRoundRef) {
        // If the game is unknown, use 0% contribution
        final Double turnoverContributionFraction = geTurnoverContributionFraction(player, gameId, 0.00D);

        final Money moneyTurnoverContribution = betMoney.multiply(turnoverContributionFraction);
        final Money bonusTurnoverContribution = betBonus.multiply(turnoverContributionFraction);

        final Wallet wallet = player.getWallet();

        if (moneyTurnoverContribution.isPositive()) {
            logger.debug("SessionId: {}, GameRound: {}. Adding {} to player {} turnover money", sessionId, gameRoundRef, moneyTurnoverContribution, player.getId());
            wallet.setAccumulatedMoneyTurnover(wallet.getAccumulatedMoneyTurnover().add(moneyTurnoverContribution));
            logger.debug("SessionId: {}, GameRound: {}. Adding {} to player {} weekly turnover money", sessionId, gameRoundRef, moneyTurnoverContribution,
                         player.getId());
            wallet.setAccumulatedWeeklyTurnover(wallet.getAccumulatedWeeklyTurnover().add(moneyTurnoverContribution));
            logger.debug("SessionId: {}, GameRound: {}. Adding {} to player {} monthly turnover bonus", sessionId, gameRoundRef, moneyTurnoverContribution,
                         player.getId());
            wallet.setAccumulatedMonthlyTurnover(wallet.getAccumulatedMonthlyTurnover().add(moneyTurnoverContribution));
        }

        if (bonusTurnoverContribution.isPositive()) {
            logger.debug("SessionId: {}, GameRound: {}. Adding {} to player {} turnover bonus", sessionId, gameRoundRef, bonusTurnoverContribution, player.getId());
            wallet.setAccumulatedBonusTurnover(wallet.getAccumulatedBonusTurnover().add(bonusTurnoverContribution));
            logger.debug("SessionId: {}, GameRound: {}. Adding {} to player {} monthly turnover bonus", sessionId, gameRoundRef, bonusTurnoverContribution,
                         player.getId());
            wallet.setAccumulatedMonthlyBonusTurnover(wallet.getAccumulatedMonthlyBonusTurnover().add(bonusTurnoverContribution));
        }

        addProgressTurnover(sessionId, player, moneyTurnoverContribution, bonusTurnoverContribution);
    }

    @VisibleForTesting
    void addProgressTurnover(final String sessionId, final Player player, final Money moneyBet, final Money bonusBet) {
        logger.debug("SessionId: {}. Adding {} to player {} turnover progress", sessionId, moneyBet, player.getId());
        final Wallet wallet = player.getWallet();

        if (moneyBet.isPositive()) {
            wallet.setLevelProgress(wallet.getLevelProgress().add(moneyBet));
        }
        if (player.getLevel().getLevel() < progressTurnoverChangeLevel && bonusBet.isPositive()) {
            logger.debug("SessionId: {}. Player {} is level {} and below level {}, adding {} bonus money to level progress", sessionId, player.getId(),
                         player.getLevel().getLevel(), progressTurnoverChangeLevel, bonusBet);
            wallet.setLevelProgress(wallet.getLevelProgress().add(bonusBet));
        }
        if (moneyBet.isPositive() || bonusBet.isPositive()) {
            playerService.checkLevelByProgressTurnover(player);
        }
    }

    private void checkPlayerBetLimits(final Player player, final Money amount) {
        if (amount.isZero()) {
            return;
        }

        final PlayerLimitation playerLimitation = playerLimitationRepository.findByPlayer(player);
        final Wallet wallet = player.getWallet();

        if (!playerLimitation.isContainingDefaultValue(LOSS_AMOUNT)) {
            // The bet is added to the loss, then later the win amount (if any) is subtracted
            final TimeUnit lossTimeUnit = playerLimitation.getLossTimeUnit();
            final Money accumulatedLosses = wallet.getAccumulatedLossAmountByTimeUnit(lossTimeUnit).add(amount);
            wallet.setAccumulatedLossAmountByTimeUnit(lossTimeUnit, accumulatedLosses);
        }

        if (!playerLimitation.isContainingDefaultValue(BET_AMOUNT)) {
            final TimeUnit betTimeUnit = playerLimitation.getBetTimeUnit();
            final Money accumulatedBets = wallet.getAccumulatedBetAmountByTimeUnit(betTimeUnit).add(amount);
            wallet.setAccumulatedBetAmountByTimeUnit(betTimeUnit, accumulatedBets);

            if (accumulatedBets.isGreaterThan(playerLimitation.getBetLimit())) {
                final Date limitBlockStartDate = new Date();
                logger.info("Player {} exceeded bet amount limitation on {}", player.getId(), limitBlockStartDate);
                final Date limitBlockEndDate = calculateLockoutEndDate(limitBlockStartDate, betTimeUnit.getTimeValue());
                final PlayerLimitationLockout lockout = new PlayerLimitationLockout(player, BET_LIMIT, limitBlockStartDate, limitBlockEndDate);
                player.setBlockType(BET_LIMIT);
                player.setBlockEndDate(limitBlockEndDate);
                playerService.savePlayer(player);
                playerLimitationLockoutRepository.save(lockout);
                // TODO push message through web-socket
                gameService.logoutPlayer(player);
            } else {
                // TODO check if bet amount is getting close to bet limit and push through web socket
            }
        }
    }

    @Override
    public Long betAndWin(final Player player, final String currency, final Boolean bigWin, final Money betAmount, final Money winAmount, final String transactionRef,
                          final String gameId, final String sessionId, final String reason, final String gameRoundRef, final String source) {
        validatePlayerLimitations(player);
        validateBetAmount(betAmount);
        validateWinAmount(winAmount);
        validateCurrency(player, currency);

        logger.info("SessionId: {}. Player {} BET {} and WON {} in game {}, gameRoundRef: {}, transactionRef: {}, reason: {}, source: {}, bigWin: {}", sessionId,
                    player.getId(), betAmount, winAmount, gameId, gameRoundRef, transactionRef, reason, source, bigWin);

        // Big Wins are reported as separate #bet and #win calls, and can be retried by the CasinoModule
        // So, if a transaction already exists, do not create another row, and return the existing one
        if (bigWin != null && bigWin == Boolean.TRUE) {
            final GameTransaction gameTransaction = gameTransactionService.getTransaction(transactionRef);
            if (gameTransaction != null) {
                logger.warn("Ignoring request since a transaction with transactionRef {} already exists, and BIG_WIN is {}", transactionRef, true);
                return gameTransaction.getId();
            }
        }

        // Load previous rounds in this game round
        final List<GameTransaction> transactions = gameTransactionService.getTransactionsByPlayerAndGameRound(player, gameRoundRef);
        // We need to keep track of total bets
        final GameRoundTransactions gameRoundTransactions = new GameRoundTransactions(transactions);
        // If it's a new game round, then get the current active bonus, otherwise use the bonus from the previous rounds
        // This ensures that only one bonus is used during a single game round
        final PlayerBonus activePlayerBonus = getPlayerBonus(player, gameRoundTransactions);

        final Wallet wallet = player.getWallet();
        final Money totalBalance = getTotalBalance(activePlayerBonus, wallet);
        validateBalance(betAmount, totalBalance);

        final Bets bets;
        if (betAmount.isPositive()) {
            bets = processBets(player, betAmount, winAmount, sessionId, gameId, gameRoundRef, transactionRef, gameRoundTransactions, activePlayerBonus, reason);
        } else {
            bets = new Bets(ZERO, ZERO, gameRoundTransactions.reachedBonusConversionGoal(), activePlayerBonus);
        }

        final Money moneyWin;
        final Money bonusWin;
        if (bets.reachedBonusConversionGoal()) {
            // This is a real edge case:
            // The bet that resulted in this win has put the player above the progression goal, so we have to count
            // the win as real money.
            bonusWin = ZERO;
            moneyWin = winAmount;
        } else {
            final Money totalBetMoney = gameRoundTransactions.getBetMoney().add(bets.getMoneyBet());
            final Money totalBetBonus = gameRoundTransactions.getBetBonus().add(bets.getBonusBet());
            bonusWin = winAmount.calculateBonusMoney(totalBetBonus, totalBetMoney);
            moneyWin = winAmount.subtract(bonusWin);
        }

        if (moneyWin.isPositive()) {
            logger.debug("SessionId: {}. Adding {} to player {} money balance", sessionId, moneyWin, player.getId());
            wallet.setMoneyBalance(wallet.getMoneyBalance().add(moneyWin));
        }
        if (bonusWin.isPositive()) {
            if (activePlayerBonus == null) {
                throw new IllegalStateException("There has to be an active bonus");
            }

            logger.debug("SessionId: {}. Adding {} to player {} bonus balance of player-bonus {}", sessionId, bonusWin, player.getId(), activePlayerBonus.getId());
            @SuppressWarnings("ConstantConditions") @Nonnull final Money currentBalance = activePlayerBonus.getCurrentBalance();
            activePlayerBonus.setCurrentBalance(currentBalance.add(bonusWin));
            bonusService.updatePlayerBonus(activePlayerBonus);
        }

        // we don't need to check if active player-bonus is lost here
        // since if win amount is zero we did that on processBet
        // if win amount is more than zero so it is more than zero!
        checkPlayerLossLimits(player, winAmount, reason);

        walletRepository.save(wallet);
        @SuppressWarnings("ConstantConditions") @Nonnull final Money bonusBalance = activePlayerBonus != null ? activePlayerBonus.getCurrentBalance() : ZERO;
        return gameTransactionService.addTransaction(player, moneyWin, bets.getMoneyBet(), bonusWin, bets.getBonusBet(), wallet.getMoneyBalance(), bonusBalance,
                                                     player.getCurrency(), gameRoundRef, transactionRef, gameId, sessionId, reason, source,
                                                     bets.reachedBonusConversionGoal(), activePlayerBonus);
    }

    @Transactional(propagation = SUPPORTS, readOnly = true)
    @Override
    public Money getBalance(final Player player, final String currency) {
        validateCurrency(player, currency);
        return player.getWallet().getTotalBalance();
    }

    @Override
    public void rollback(final Player player, final String transactionRef) {
        final GameTransaction transaction = gameTransactionService.getTransaction(transactionRef);

        // If the transaction doesn't exist, then just return without throwing an error
        // This is a 'valid' state, according to the api
        if (transaction == null || transaction.getRollbackDate() != null) {
            logger.error("Rollback transaction with reference {} does not have a valid corresponding GameTransaction.", transactionRef);
            return;
        }

        logger.info("Rolling back transactionRef {}", transactionRef);

        final Player playerFromTransaction = transaction.getPlayer();
        if (!player.equals(playerFromTransaction)) {
            logger.error("Player from NetEnt {} doesn't match player {} from transaction {}", player.getId(), playerFromTransaction.getId(), transactionRef);
            return;
        }

        final String gameId = transaction.getGameId();
        // If the game is unknown, use 100% contribution for rollback
        final Double turnoverContributionFraction = geTurnoverContributionFraction(player, gameId, 1.00D);

        final Wallet wallet = player.getWallet();

        final Money winMoney = transaction.getMoneyDeposit();
        final Money winBonus = transaction.getBonusDeposit();
        final Money betMoney = transaction.getMoneyWithdraw();
        final Money betBonus = transaction.getBonusWithdraw();
        final PlayerBonus activePlayerBonus = transaction.getActivePlayerBonus();

        final Money moneyTurnoverContribution = betMoney.multiply(turnoverContributionFraction);
        final Money bonusTurnoverContribution = betBonus.multiply(turnoverContributionFraction);

        if (winMoney.isPositive()) {
            logger.debug("Subtracting {} from from player {} money balance", winMoney, player.getId());
            wallet.setMoneyBalance(wallet.getMoneyBalance().subtract(winMoney));
        }
        if (winBonus.isPositive()) {
            if (activePlayerBonus == null) {
                logger.error("No associated bonus with game transaction {}, although the game round {} contained bonus money bet", transactionRef,
                             transaction.getGameRoundRef());
            } else {
                logger.debug("Subtracting {} from player {} bonus balance of player-bonus {}", winBonus, player.getId(), activePlayerBonus.getId());
                @SuppressWarnings("ConstantConditions") @Nonnull final Money currentBalance = activePlayerBonus.getCurrentBalance();
                activePlayerBonus.setCurrentBalance(currentBalance.subtract(winBonus));
                // TODO what if state has be changed
            }
        }
        if (betMoney.isPositive()) {
            logger.debug("Adding {} money to player {} money balance", betMoney, player.getId());
            wallet.setMoneyBalance(wallet.getMoneyBalance().add(betMoney));

            if (moneyTurnoverContribution.isPositive()) {
                logger.debug("Subtracting {} from player {} turnover balance", moneyTurnoverContribution, player.getId());
                wallet.setAccumulatedMoneyTurnover(wallet.getAccumulatedMoneyTurnover().subtract(moneyTurnoverContribution));
                logger.debug("Subtracting {} from player {} weekly turnover balance", moneyTurnoverContribution, player.getId());
                wallet.setAccumulatedWeeklyTurnover(wallet.getAccumulatedWeeklyTurnover().subtract(moneyTurnoverContribution));
                logger.debug("Subtracting {} from player {} monthly turnover balance", moneyTurnoverContribution, player.getId());
                wallet.setAccumulatedMonthlyTurnover(wallet.getAccumulatedMonthlyTurnover().subtract(moneyTurnoverContribution));

                logger.debug("Subtracting {} from player {} turnover progress", moneyTurnoverContribution, player.getId());
                wallet.setLevelProgress(wallet.getLevelProgress().subtract(moneyTurnoverContribution));
                // We cannot de-level yet - and we probably never will bother
            }
        }
        if (betBonus.isPositive()) {
            if (activePlayerBonus == null) {
                logger.error("No associated bonus with game transaction {}, although the game round {} contained bonus money bet", transactionRef,
                             transaction.getGameRoundRef());
            } else {
                logger.debug("Adding {} to player {} bonus balance of player-bonus {}", betBonus, player.getId(), activePlayerBonus.getId());
                @SuppressWarnings("ConstantConditions") @Nonnull final Money currentBalance = activePlayerBonus.getCurrentBalance();
                activePlayerBonus.setCurrentBalance(currentBalance.add(betMoney));

                if (bonusTurnoverContribution.isPositive()) {
                    logger.debug("Subtracting {} from player {} turnover bonus", bonusTurnoverContribution, player.getId());
                    wallet.setAccumulatedBonusTurnover(wallet.getAccumulatedBonusTurnover().subtract(bonusTurnoverContribution));
                    logger.debug("Subtracting {} from player {} monthly turnover bonus", bonusTurnoverContribution, player.getId());
                    wallet.setAccumulatedMonthlyBonusTurnover(wallet.getAccumulatedMonthlyBonusTurnover().subtract(bonusTurnoverContribution));

                    // TODO reset reached bonus goal

                    if (player.getLevel().getLevel() < progressTurnoverChangeLevel) {
                        logger.debug("Subtracting {} from player {} turnover progress", bonusTurnoverContribution, player.getId());
                        wallet.setLevelProgress(wallet.getLevelProgress().subtract(bonusTurnoverContribution));
                        // We cannot de-level yet - and we probably never will bother
                    }
                }
            }
        }

        rollBackOnAccumulatedLimits(player, betMoney.add(betBonus), winMoney.add(winBonus));
//        checkForSmallBonusMoneyConversion(player, null, transactionRef, activePlayerBonus);

        walletRepository.save(wallet);
        if (activePlayerBonus != null) {
            bonusService.updatePlayerBonus(activePlayerBonus);
        }
        transaction.setRollbackDate(new Date());
        gameTransactionService.updateTransaction(transaction);
    }

    private void rollBackOnAccumulatedLimits(final Player player, final Money betAmount, final Money winAmount) {
        final PlayerLimitation playerLimitation = playerLimitationRepository.findByPlayer(player);
        final TimeUnit lossTimeUnit = playerLimitation.getLossTimeUnit();
        final TimeUnit betTimeUnit = playerLimitation.getBetTimeUnit();
        final Wallet wallet = player.getWallet();

        final Money lossAmount = winAmount.subtract(betAmount);
        if (!playerLimitation.isContainingDefaultValue(LOSS_AMOUNT) && !lossAmount.isZero()) {
            wallet.setAccumulatedLossAmountByTimeUnit(lossTimeUnit, wallet.getAccumulatedLossAmountByTimeUnit(lossTimeUnit).add(lossAmount));
        }

        if (!playerLimitation.isContainingDefaultValue(BET_AMOUNT) && !betAmount.isZero()) {
            wallet.setAccumulatedBetAmountByTimeUnit(betTimeUnit, wallet.getAccumulatedBetAmountByTimeUnit(betTimeUnit).subtract(betAmount));
            if (wallet.getAccumulatedBetAmountByTimeUnit(betTimeUnit).isNegative()) {
                wallet.setAccumulatedBetAmountByTimeUnit(betTimeUnit, Money.ZERO);
            }
        }

        // Do not bother to check if the player limit lockout should be rollbacked
    }
}
