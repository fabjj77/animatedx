package com.cs.netent.wallet;

import com.cs.avatar.Avatar;
import com.cs.avatar.Level;
import com.cs.bonus.Bonus;
import com.cs.bonus.BonusService;
import com.cs.bonus.PlayerBonus;
import com.cs.bonus.PlayerBonusPair;
import com.cs.comment.PlayerCommentRepository;
import com.cs.game.Game;
import com.cs.game.GameCategory;
import com.cs.game.GameService;
import com.cs.game.GameTransaction;
import com.cs.game.GameTransactionService;
import com.cs.game.PlayerLimitationLockoutRepository;
import com.cs.game.PlayerLimitationRepository;
import com.cs.payment.Currency;
import com.cs.payment.Money;
import com.cs.persistence.BalanceException;
import com.cs.persistence.InvalidCurrencyException;
import com.cs.persistence.NegativeBetException;
import com.cs.persistence.NegativeWinException;
import com.cs.player.Address;
import com.cs.player.LimitationType;
import com.cs.player.Player;
import com.cs.player.PlayerLimitation;
import com.cs.player.PlayerLimitationException;
import com.cs.player.PlayerRepository;
import com.cs.player.PlayerService;
import com.cs.player.TimeUnit;
import com.cs.player.TrustLevel;
import com.cs.player.Wallet;
import com.cs.player.WalletRepository;
import com.cs.util.CalendarUtils;

import org.springframework.test.util.ReflectionTestUtils;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import static com.cs.payment.Currency.EUR;
import static com.cs.payment.Money.ONE;
import static com.cs.payment.Money.TEN;
import static com.cs.payment.Money.ZERO;
import static com.cs.persistence.Language.ENGLISH;
import static com.cs.persistence.Status.ACTIVE;
import static com.cs.player.BlockType.BET_LIMIT;
import static com.cs.player.BlockType.UNBLOCKED;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * @author Hadi Movaghar
 */
public class WalletServiceImplTest {

    @InjectMocks
    private WalletServiceImpl walletServiceImpl;

    @Mock
    private BonusService bonusService;
    @Mock
    private GameService gameService;
    @Mock
    private GameTransactionService gameTransactionService;
    @Mock
    private PlayerCommentRepository playerCommentRepository;
    @Mock
    private PlayerLimitationRepository playerLimitationRepository;
    @Mock
    private PlayerLimitationLockoutRepository playerLimitationLockoutRepository;
    @Mock
    private PlayerRepository playerRepository;
    @Mock
    private PlayerService playerService;
    @Mock
    private WalletRepository walletRepository;

    private final Date tomorrow;

    public WalletServiceImplTest() {
        final Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 1);
        tomorrow = calendar.getTime();
    }

    @Before
    public void setup() {
        initMocks(this);

        ReflectionTestUtils.setField(walletServiceImpl, "progressTurnoverChangeLevel", 30);
        ReflectionTestUtils.setField(walletServiceImpl, "minimumBonusBalanceToConvertToMoney", new Money(1L));
    }

    private Player createPlayer() {
        final Player player = new Player("Bob", "Pitt", "email", "pass", "Bob", new Date(), new Avatar(), new Level(20L), new Address(), EUR, "0799999", ACTIVE,
                                         TrustLevel.GREEN, UNBLOCKED, ENGLISH);
        player.setId(1L);
        final Wallet wallet = new Wallet(player);
        wallet.setMoneyBalance(Money.TEN);
        final PlayerBonus activePlayerBonus = new PlayerBonus();
        activePlayerBonus.setCurrentBalance(Money.TEN);
        wallet.setActivePlayerBonus(activePlayerBonus);
        wallet.setCreditsBalance(10);

        player.setWallet(wallet);

        return player;
    }

    private PlayerLimitation createPlayerLimitationWithLossAndBetAsDayAndNoLimitation(final Player player) {
        return new PlayerLimitation(player, Money.ZERO, TimeUnit.DAY, Money.ZERO, TimeUnit.DAY, 60);
    }

    private GameTransaction createNetEntTransaction(final Player player) {
        return new GameTransaction(player, TEN, TEN, TEN, TEN, TEN, TEN, player.getCurrency(), new Date(), "", "", "", "", "", "", false, null);
    }

    @Test(expected = NegativeWinException.class)
    public void win_NegativeAmountThrowsNegativeWinException() {
        final Player player = createPlayer();

        walletServiceImpl.win(player, player.getCurrency().name(), Boolean.FALSE, new Money(-20L), "", "", "", "", "", "", 1L);
    }

    @Test(expected = InvalidCurrencyException.class)
    public void win_ThrowsExceptionInvalidCurrency() {
        final Player player = createPlayer();

        walletServiceImpl.win(player, "NOK", Boolean.FALSE, new Money(100L), "", "", "", "", "", "", 1L);
    }

    @Test
    public void win_MoneyAndBonusBalanceIncreaseEquallyIfPlayerBetsEquallyOnMoneyAndBonus() {
        final Player player = createPlayer();
        final Wallet wallet = player.getWallet();
        final PlayerLimitation playerLimitation = createPlayerLimitationWithLossAndBetAsDayAndNoLimitation(player);
        playerLimitation.setBetLimit(new Money(1000L));
        final GameTransaction transaction = createNetEntTransaction(player);

        final PlayerBonus activePlayerBonus = new PlayerBonus();
        activePlayerBonus.setId(10L);
        final Bonus bonus = new Bonus();
        bonus.setValidTo(tomorrow);
        activePlayerBonus.setPk(new PlayerBonusPair(player, bonus));
        activePlayerBonus.setCurrentBalance(TEN);
        activePlayerBonus.setBonusConversionGoal(new Money(380L));
        activePlayerBonus.setBonusConversionProgress(Money.ZERO);
        transaction.setActivePlayerBonus(activePlayerBonus);

        when(playerLimitationRepository.findByPlayer(player)).thenReturn(playerLimitation);
        when(gameTransactionService.getTransactionsByPlayerAndGameRound(player, "")).thenReturn(Collections.singletonList(transaction));

        walletServiceImpl.win(player, player.getCurrency().name(), Boolean.FALSE, new Money(30L), "", "", "", "", "", "", 1L);

        assertThat(wallet.getMoneyBalance(), is(new Money(25L)));
        assertThat(activePlayerBonus.getCurrentBalance(), is(new Money(25L)));
    }

    @Test
    public void win_WinAmountGoesToBonusIfPlayerBetsOnlyOnBonus() {
        final Player player = createPlayer();
        final Wallet wallet = player.getWallet();
        final PlayerBonus activePlayerBonus = new PlayerBonus();
        activePlayerBonus.setId(10L);
        final Bonus bonus = new Bonus();
        bonus.setValidTo(tomorrow);
        activePlayerBonus.setPk(new PlayerBonusPair(player, bonus));
        activePlayerBonus.setCurrentBalance(TEN);
        activePlayerBonus.setBonusConversionGoal(new Money(380L));
        activePlayerBonus.setBonusConversionProgress(Money.ZERO);
        final GameTransaction transaction = new GameTransaction(player, ZERO, ZERO, ZERO, TEN, ZERO, ZERO, player.getCurrency(), new Date(), "", "", "", "", "", "",
                                                                    false, activePlayerBonus);
        final PlayerLimitation playerLimitation = createPlayerLimitationWithLossAndBetAsDayAndNoLimitation(player);
        playerLimitation.setBetLimit(new Money(1000L));

        when(playerLimitationRepository.findByPlayer(player)).thenReturn(playerLimitation);
        when(gameTransactionService.getTransactionsByPlayerAndGameRound(player, "")).thenReturn(Collections.singletonList(transaction));

        walletServiceImpl.win(player, player.getCurrency().name(), Boolean.FALSE, new Money(20L), "", "", "", "", "", "", 1L);

        assertThat(wallet.getMoneyBalance(), is(equalTo(TEN)));
        assertThat(activePlayerBonus.getCurrentBalance(), is(equalTo(new Money(30L))));
    }

    @Test
    public void win_WinAmountGoesToMoneyIfPlayerBetsOnlyOnMoney() {
        final Player player = createPlayer();
        final PlayerLimitation playerLimitation = createPlayerLimitationWithLossAndBetAsDayAndNoLimitation(player);
        playerLimitation.setBetLimit(new Money(1000L));
        final GameTransaction transaction = createNetEntTransaction(player);
        transaction.setBonusWithdraw(ZERO);
        transaction.setMoneyWithdraw(TEN);

        when(playerLimitationRepository.findByPlayer(player)).thenReturn(playerLimitation);
        when(gameTransactionService.getTransactionsByPlayerAndGameRound(eq(player), anyString())).thenReturn(Collections.singletonList(transaction));

        walletServiceImpl.win(player, player.getCurrency().name(), Boolean.FALSE, new Money(20L), "", "", "", "", "", "", 1L);

        assertThat(player.getWallet().getBonusBalance(), is(equalTo(TEN)));
        assertThat(player.getWallet().getMoneyBalance(), is(equalTo(new Money(30L))));
    }

    @Test
    public void win_BigWinIsFalseShouldNotShortOut() {
        final Player player = createPlayer();
        final PlayerLimitation playerLimitation = createPlayerLimitationWithLossAndBetAsDayAndNoLimitation(player);
        playerLimitation.setBetLimit(new Money(1000L));

        final PlayerBonus activePlayerBonus = new PlayerBonus();
        activePlayerBonus.setId(10L);
        final Bonus bonus = new Bonus();
        bonus.setValidTo(tomorrow);
        activePlayerBonus.setPk(new PlayerBonusPair(player, bonus));
        activePlayerBonus.setCurrentBalance(TEN);
        activePlayerBonus.setBonusConversionGoal(new Money(380L));
        activePlayerBonus.setBonusConversionProgress(Money.ZERO);

        when(bonusService.getActivePlayerBonus(player)).thenReturn(activePlayerBonus);
        when(playerLimitationRepository.findByPlayer(player)).thenReturn(playerLimitation);

        walletServiceImpl.win(player, player.getCurrency().name(), Boolean.FALSE, new Money(20L), "", "", "", "", "", "", 1L);

        assertThat(player.getWallet().getMoneyBalance(), is(equalTo(TEN)));
        assertThat(activePlayerBonus.getCurrentBalance(), is(equalTo(new Money(30L))));
    }

    @Test
    public void win_BigWinIsNullShouldNotShortOut() {
        final Player player = createPlayer();
        final PlayerLimitation playerLimitation = createPlayerLimitationWithLossAndBetAsDayAndNoLimitation(player);
        playerLimitation.setBetLimit(new Money(1000L));

        final PlayerBonus activePlayerBonus = new PlayerBonus();
        activePlayerBonus.setId(10L);
        final Bonus bonus = new Bonus();
        bonus.setValidTo(tomorrow);
        activePlayerBonus.setPk(new PlayerBonusPair(player, bonus));
        activePlayerBonus.setCurrentBalance(TEN);
        activePlayerBonus.setBonusConversionGoal(new Money(380L));
        activePlayerBonus.setBonusConversionProgress(Money.ZERO);

        when(bonusService.getActivePlayerBonus(player)).thenReturn(activePlayerBonus);
        when(playerLimitationRepository.findByPlayer(player)).thenReturn(playerLimitation);

        walletServiceImpl.win(player, player.getCurrency().name(), null, new Money(20L), "", "", "", "", "", "", 1L);

        assertThat(player.getWallet().getMoneyBalance(), is(equalTo(TEN)));
        assertThat(activePlayerBonus.getCurrentBalance(), is(equalTo(new Money(30L))));
    }

    @Test
    public void win_BigWinIsTrueShouldReturnPreviousTransactionIfOneExists() {
        final Player player = createPlayer();
        final PlayerLimitation playerLimitation = createPlayerLimitationWithLossAndBetAsDayAndNoLimitation(player);
        playerLimitation.setBetLimit(new Money(1000L));

        when(playerLimitationRepository.findByPlayer(player)).thenReturn(playerLimitation);
        final GameTransaction netEntTransaction = new GameTransaction();
        netEntTransaction.setId(1234L);
        when(gameTransactionService.getTransaction(anyString())).thenReturn(netEntTransaction);

        final Long transactionId = walletServiceImpl.win(player, player.getCurrency().name(), Boolean.TRUE, new Money(20L), "", "", "", "", "", "", 1L);

        assertThat(transactionId, is(equalTo(netEntTransaction.getId())));
        verify(gameTransactionService, never()).addTransaction(any(Player.class), any(Money.class), any(Money.class), any(Money.class), any(Money.class), any(Money.class),
                                                           any(Money.class), any(Currency.class), anyString(), anyString(), anyString(), anyString(), anyString(),
                                                           anyString(), anyBoolean(), any(PlayerBonus.class));
    }

    @Test
    public void win_BigWinIsTrueShouldCreateNewTransactionIfNoOneExists() {
        final Player player = createPlayer();
        final PlayerLimitation playerLimitation = createPlayerLimitationWithLossAndBetAsDayAndNoLimitation(player);
        playerLimitation.setBetLimit(new Money(1000L));

        final PlayerBonus activePlayerBonus = new PlayerBonus();
        activePlayerBonus.setId(10L);
        final Bonus bonus = new Bonus();
        bonus.setValidTo(tomorrow);
        activePlayerBonus.setPk(new PlayerBonusPair(player, bonus));
        activePlayerBonus.setCurrentBalance(TEN);
        activePlayerBonus.setBonusConversionGoal(new Money(380L));
        activePlayerBonus.setBonusConversionProgress(Money.ZERO);

        when(bonusService.getActivePlayerBonus(player)).thenReturn(activePlayerBonus);

        when(playerLimitationRepository.findByPlayer(player)).thenReturn(playerLimitation);
        when(gameTransactionService.getTransaction(anyString())).thenReturn(null);
        final long createdTransactionId = 1234L;
        when(gameTransactionService.addTransaction(any(Player.class), any(Money.class), any(Money.class), any(Money.class), any(Money.class), any(Money.class),
                                               any(Money.class), any(Currency.class), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(),
                                               anyBoolean(), any(PlayerBonus.class)))
                .thenReturn(createdTransactionId);

        final Long transactionId = walletServiceImpl.win(player, player.getCurrency().name(), Boolean.TRUE, new Money(20L), "", "", "", "", "", "", 1L);

        assertThat(transactionId, is(equalTo(createdTransactionId)));
        verify(gameTransactionService, times(1)).addTransaction(any(Player.class), any(Money.class), any(Money.class), any(Money.class), any(Money.class), any(Money.class),
                                                            any(Money.class), any(Currency.class), anyString(), anyString(), anyString(), anyString(), anyString(),
                                                            anyString(), anyBoolean(), any(PlayerBonus.class));
    }

    @Test(expected = NegativeBetException.class)
    public void bet_NegativeAmountThrowsNegativeBetException() {
        final Player player = createPlayer();

        walletServiceImpl.bet(player, player.getCurrency().name(), new Money(-20L), "", "", "", "", "");
    }

    @Test(expected = InvalidCurrencyException.class)
    public void bet_ThrowsExceptionInvalidCurrency() {
        final Player player = createPlayer();

        walletServiceImpl.bet(player, "NOK", new Money(100L), "", "", "", "", "");
    }

    @Test(expected = BalanceException.class)
    public void bet_BalanceExceptionWhenSumOfMoneyAndBonusBalanceIsLessThanBetAmount() {
        final Player player = createPlayer();

        walletServiceImpl.bet(player, player.getCurrency().name(), new Money(21L), "", "", "", "", "");
    }

    @Test
    public void bet_BetOnlyFromMoneyWhenBetAmountIsLessThanBalance() {
        final Player player = createPlayer();
        final PlayerLimitation playerLimitation = createPlayerLimitationWithLossAndBetAsDayAndNoLimitation(player);
        playerLimitation.setBetLimit(new Money(1000L));
        final Game game = new Game("gameId");
        game.setCategory(GameCategory.SLOTS);

        when(gameService.getGame(anyString())).thenReturn(game);
        when(playerLimitationRepository.findByPlayer(player)).thenReturn(playerLimitation);

        walletServiceImpl.bet(player, player.getCurrency().name(), TEN, "", "", "", "", "");

        assertThat(player.getWallet().getMoneyBalance(), is(equalTo(ZERO)));
        assertThat(player.getWallet().getBonusBalance(), is(equalTo(TEN)));
        assertThat(player.getWallet().getAccumulatedWeeklyTurnover(), is(equalTo(TEN)));
        assertThat(player.getWallet().getAccumulatedMonthlyTurnover(), is(equalTo(TEN)));
    }

    @Test
    public void bet_BetFromBothMoneyAndBonusWhenBetAmountIsMoreThanBalance() {
        final Player player = createPlayer();
        final PlayerLimitation playerLimitation = createPlayerLimitationWithLossAndBetAsDayAndNoLimitation(player);
        playerLimitation.setBetLimit(new Money(1000L));
        final Game game = new Game("gameId");
        game.setCategory(GameCategory.SLOTS);

        final PlayerBonus activePlayerBonus = new PlayerBonus();
        activePlayerBonus.setId(10L);
        final Bonus bonus = new Bonus();
        bonus.setValidTo(tomorrow);
        activePlayerBonus.setPk(new PlayerBonusPair(player, bonus));
        activePlayerBonus.setCurrentBalance(TEN);
        activePlayerBonus.setBonusConversionGoal(new Money(380L));
        activePlayerBonus.setBonusConversionProgress(Money.ZERO);

        when(bonusService.getActivePlayerBonus(player)).thenReturn(activePlayerBonus);

        when(gameService.getGame(anyString())).thenReturn(game);
        when(playerLimitationRepository.findByPlayer(player)).thenReturn(playerLimitation);

        walletServiceImpl.bet(player, player.getCurrency().name(), new Money(20L), "", "", "", "", "");

        assertThat(player.getWallet().getMoneyBalance(), is(equalTo(ZERO)));
        assertThat(activePlayerBonus.getCurrentBalance(), is(equalTo(ZERO)));
        assertThat(player.getWallet().getAccumulatedWeeklyTurnover(), is(equalTo(TEN)));
        assertThat(player.getWallet().getAccumulatedMonthlyTurnover(), is(equalTo(TEN)));
    }

    @Test(expected = NegativeBetException.class)
    public void betAndWin_NegativeBetAmountThrowsNegativeBetException() {
        final Player player = createPlayer();

        walletServiceImpl.betAndWin(player, player.getCurrency().name(), Boolean.FALSE, new Money(-20L), ONE, "", "", "", "", "gameRoundRef", "");
    }

    @Test(expected = NegativeWinException.class)
    public void betAndWin_NegativeAmountThrowsNegativeWinException() {
        final Player player = createPlayer();

        walletServiceImpl.betAndWin(player, player.getCurrency().name(), Boolean.FALSE, TEN, new Money(-10L), "", "", "", "", "gameRoundRef", "");
    }

    @Test(expected = InvalidCurrencyException.class)
    public void betAndWin_ThrowsExceptionInvalidCurrency() {
        final Player player = createPlayer();

        walletServiceImpl.betAndWin(player, "NOK", Boolean.FALSE, new Money(100L), ONE, "", "", "", "", "gameRoundRef", "");
    }

    @Test(expected = BalanceException.class)
    public void betAndWin_BalanceExceptionWhenSumOfMoneyAndBonusBalanceIsLessThanBetAmount() {
        final Player player = createPlayer();

        walletServiceImpl.betAndWin(player, player.getCurrency().name(), Boolean.FALSE, new Money(21L), ONE, "", "", "", "", "gameRoundRef", "");
    }

    @Test
    public void betAndWin_betFromBothMoneyAndBonusAddWinToBothMoneyAndBonus() {
        final Player player = createPlayer();
        final PlayerLimitation playerLimitation = createPlayerLimitationWithLossAndBetAsDayAndNoLimitation(player);
        playerLimitation.setBetLimit(new Money(1000L));
        final Game game = new Game("gameId");
        game.setCategory(GameCategory.SLOTS);

        final PlayerBonus activePlayerBonus = new PlayerBonus();
        activePlayerBonus.setId(10L);
        final Bonus bonus = new Bonus();
        bonus.setValidTo(tomorrow);
        activePlayerBonus.setPk(new PlayerBonusPair(player, bonus));
        activePlayerBonus.setCurrentBalance(TEN);
        activePlayerBonus.setBonusConversionGoal(new Money(380L));
        activePlayerBonus.setBonusConversionProgress(Money.ZERO);

        when(bonusService.getActivePlayerBonus(player)).thenReturn(activePlayerBonus);

        when(gameService.getGame(anyString())).thenReturn(game);
        when(playerLimitationRepository.findByPlayer(eq(player))).thenReturn(playerLimitation);
        when(gameTransactionService.getTransactionsByPlayerAndGameRound(eq(player), anyString())).thenReturn(Collections.<GameTransaction>emptyList());

        walletServiceImpl.betAndWin(player, player.getCurrency().name(), Boolean.FALSE, new Money(20L), new Money(20L), "", "", "", "", "gameRoundRef", "");

        assertThat(player.getWallet().getMoneyBalance(), is(TEN));
        assertThat(activePlayerBonus.getCurrentBalance(), is(TEN));
        assertThat(player.getWallet().getAccumulatedWeeklyTurnover(), is(TEN));
        assertThat(player.getWallet().getAccumulatedMonthlyTurnover(), is(TEN));
        assertThat(player.getTrustLevel(), is(TrustLevel.GREEN));
    }

    @Test
    public void betAndWin_netEntFailedTest() {
        final Player player = createPlayer();
        final GameTransaction transaction = createNetEntTransaction(player);
        final PlayerLimitation playerLimitation = createPlayerLimitationWithLossAndBetAsDayAndNoLimitation(player);
        playerLimitation.setBetLimit(new Money(1000L));
        when(playerLimitationRepository.findByPlayer(player)).thenReturn(playerLimitation);
        when(gameTransactionService.getTransactionsByPlayerAndGameRound(player, "")).thenReturn(Collections.singletonList(transaction));

        // all money
        player.getWallet().setMoneyBalance(new Money(95430L));

        walletServiceImpl.betAndWin(player, player.getCurrency().name(), Boolean.FALSE, new Money(0.60), new Money(0.0), "", "", "", "", "gameRoundRef", "");

        assertThat(player.getWallet().getMoneyBalance(), is(new Money(95370L)));

        // all bonus
        player.getWallet().setMoneyBalance(Money.ZERO);
        final PlayerBonus activePlayerBonus = new PlayerBonus();
        activePlayerBonus.setId(10L);
        final Bonus bonus = new Bonus();
        bonus.setValidTo(tomorrow);
        activePlayerBonus.setPk(new PlayerBonusPair(player, bonus));
        activePlayerBonus.setCurrentBalance(new Money(95430L));
        activePlayerBonus.setBonusConversionGoal(new Money(380L));
        activePlayerBonus.setBonusConversionProgress(Money.ZERO);

        when(bonusService.getActivePlayerBonus(player)).thenReturn(activePlayerBonus);

        walletServiceImpl.betAndWin(player, player.getCurrency().name(), Boolean.FALSE, new Money(0.60), new Money(0.0), "", "", "", "", "gameRoundRef", "");

        assertThat(player.getWallet().getMoneyBalance(), is(Money.ZERO));
        assertThat(activePlayerBonus.getCurrentBalance(), is(new Money(95370L)));

        // half money, half bonus
        player.getWallet().setMoneyBalance(new Money(30L));
        activePlayerBonus.setCurrentBalance(new Money(95430L));

        walletServiceImpl.betAndWin(player, player.getCurrency().name(), Boolean.FALSE, new Money(0.60), new Money(0.0), "", "", "", "", "gameRoundRef", "");

        assertThat(player.getWallet().getMoneyBalance(), is(Money.ZERO));
        assertThat(activePlayerBonus.getCurrentBalance(), is(new Money(95400L)));

        // get euro
        player.getWallet().setMoneyBalance(new Money(120L));

        walletServiceImpl.betAndWin(player, player.getCurrency().name(), Boolean.FALSE, new Money(0.60), new Money(0.0), "", "", "", "", "gameRoundRef", "");

        assertThat(player.getWallet().getMoneyBalance(), is(new Money(60L)));
        assertThat(player.getWallet().getMoneyBalance().getEuroValueInBigDecimal().toString(), is("0.60"));

        // get euro
        player.getWallet().setMoneyBalance(new Money(1237L));

        walletServiceImpl.betAndWin(player, player.getCurrency().name(), Boolean.FALSE, new Money(0.40), new Money(0.0), "", "", "", "", "gameRoundRef", "");

        assertThat(player.getWallet().getMoneyBalance(), is(new Money(1197L)));
        assertThat(player.getWallet().getMoneyBalance().getEuroValueInBigDecimal().toString(), is("11.97"));
    }

    @Test
    public void betAndWin_flagPlayerAsRedIfBetAmountExceedsMaximum() {
        final Player player = createPlayer();
        player.getWallet().setMoneyBalance(new Money(500000L));
        player.getWallet().setAccumulatedWeeklyTurnover(new Money(1400000L)); // weekly max turnover is 1500000
        player.getWallet().setAccumulatedMonthlyTurnover(new Money(2400000L)); // monthly max turnover is 2500000
        final PlayerLimitation playerLimitation = createPlayerLimitationWithLossAndBetAsDayAndNoLimitation(player);
        playerLimitation.setBetLimit(new Money(100000L));
        final Game game = new Game("gameId");
        game.setCategory(GameCategory.SLOTS);

        when(gameService.getGame(anyString())).thenReturn(game);
        when(playerLimitationRepository.findByPlayer(player)).thenReturn(playerLimitation);

        walletServiceImpl.betAndWin(player, player.getCurrency().name(), Boolean.FALSE, new Money(100100L), ZERO, "", "", "", "", "gameRoundRef", "");

        assertThat(player.getWallet().getMoneyBalance(), is(new Money(399900L)));
        assertThat(player.getWallet().getAccumulatedWeeklyTurnover(), is(new Money(1500100L)));
        assertThat(player.getWallet().getAccumulatedMonthlyTurnover(), is(new Money(2500100L)));
    }

    @Test
    public void betAndWin_addBothMoneyAndBonusToProgressTurnoverIfLevelBelow30() {
        final Player player = createPlayer();
        player.getWallet().setMoneyBalance(new Money(1000L));
        player.getLevel().setLevel(29L);
        player.getWallet().setLevelProgress(ZERO);
        final PlayerLimitation playerLimitation = createPlayerLimitationWithLossAndBetAsDayAndNoLimitation(player);
        playerLimitation.setBetLimit(new Money(1000L));
        final Game game = new Game("gameId");
        game.setCategory(GameCategory.SLOTS);

        final PlayerBonus activePlayerBonus = new PlayerBonus();
        activePlayerBonus.setId(10L);
        final Bonus bonus = new Bonus();
        bonus.setValidTo(tomorrow);
        activePlayerBonus.setPk(new PlayerBonusPair(player, bonus));
        activePlayerBonus.setCurrentBalance(new Money(1000L));
        activePlayerBonus.setBonusConversionGoal(new Money(38000L));
        activePlayerBonus.setBonusConversionProgress(Money.ZERO);
        when(bonusService.getActivePlayerBonus(player)).thenReturn(activePlayerBonus);

        when(gameService.getGame(anyString())).thenReturn(game);
        when(playerLimitationRepository.findByPlayer(player)).thenReturn(playerLimitation);

        walletServiceImpl.betAndWin(player, player.getCurrency().name(), Boolean.FALSE, new Money(2000L), ZERO, "", "", "", "", "gameRoundRef", "");

        assertThat(player.getWallet().getLevelProgress(), is(new Money(2000L)));
    }

    @Test
    public void betAndWin_addBothMoneyAndBonusToProgressTurnoverIfLevelAboveAndEqualTo30() {
        final Player player = createPlayer();
        player.getWallet().setMoneyBalance(new Money(1000L));
        player.getLevel().setLevel(30L);
        player.getWallet().setLevelProgress(ZERO);
        final PlayerLimitation playerLimitation = createPlayerLimitationWithLossAndBetAsDayAndNoLimitation(player);
        playerLimitation.setBetLimit(new Money(1000L));
        final Game game = new Game("gameId");
        game.setCategory(GameCategory.SLOTS);

        final PlayerBonus activePlayerBonus = new PlayerBonus();
        activePlayerBonus.setId(10L);
        final Bonus bonus = new Bonus();
        bonus.setValidTo(tomorrow);
        activePlayerBonus.setPk(new PlayerBonusPair(player, bonus));
        activePlayerBonus.setCurrentBalance(new Money(1000L));
        activePlayerBonus.setBonusConversionGoal(new Money(38000L));
        activePlayerBonus.setBonusConversionProgress(Money.ZERO);
        when(bonusService.getActivePlayerBonus(player)).thenReturn(activePlayerBonus);

        when(gameService.getGame(anyString())).thenReturn(game);
        when(playerLimitationRepository.findByPlayer(player)).thenReturn(playerLimitation);

        walletServiceImpl.betAndWin(player, player.getCurrency().name(), Boolean.FALSE, new Money(2000L), ZERO, "", "", "", "", "gameRoundRef", "");

        assertThat(player.getWallet().getLevelProgress(), is(new Money(1000L)));
    }

    @Test(expected = PlayerLimitationException.class)
    public void bet_throwExceptionIfPlayerIsBlocked() {
        final Player player = createPlayer();
        player.setBlockType(BET_LIMIT);

        walletServiceImpl.bet(player, player.getCurrency().name(), TEN, "gameRoundRef", "transactionRef", "gameId", "sessionId", "reason");
    }

    @Test(expected = PlayerLimitationException.class)
    public void betAndWin_throwExceptionIfPlayerIsBlocked() {
        final Player player = createPlayer();
        player.setBlockType(BET_LIMIT);

        walletServiceImpl.betAndWin(player, player.getCurrency().name(), Boolean.FALSE, TEN, TEN, "gameRoundRef", "transactionRef", "gameId", "sessionId",
                                    "gameRoundRef", "");
    }

    @Test
    public void bet_blocksPlayerIfExceedsDailyBet() {
        final Player player = createPlayer();
        final PlayerLimitation playerLimitation = createPlayerLimitationWithLossAndBetAsDayAndNoLimitation(player);
        final TimeUnit betTimeUnit = TimeUnit.DAY;
        playerLimitation.setBetTimeUnit(betTimeUnit);
        playerLimitation.setBetLimit(new Money(5L));

        when(playerLimitationRepository.findByPlayer(player)).thenReturn(playerLimitation);

        walletServiceImpl.bet(player, player.getCurrency().name(), TEN, "gameRoundRef", "transactionRef", "gameId", "sessionId", WalletServiceImpl.GAME_PLAY_FINAL);

        assertThat(player.getBlockType(), is(BET_LIMIT));
        assertThat(player.getWallet().getAccumulatedBetAmountByTimeUnit(betTimeUnit), is(TEN));
        assertThat(player.getWallet().getAccumulatedLossAmountByTimeUnit(TimeUnit.DAY), is(ZERO));
    }

    @Test
    public void bet_blocksPlayerIfAmountIsEqualToWeeklyBet() {
        final Player player = createPlayer();
        final Money amount = new Money(1001L);
        player.getWallet().setMoneyBalance(amount);
        final PlayerLimitation playerLimitation = createPlayerLimitationWithLossAndBetAsDayAndNoLimitation(player);
        final TimeUnit betTimeUnit = TimeUnit.WEEK;
        playerLimitation.setBetTimeUnit(betTimeUnit);
        playerLimitation.setBetLimit(new Money(1000L));

        when(playerLimitationRepository.findByPlayer(player)).thenReturn(playerLimitation);

        walletServiceImpl.bet(player, player.getCurrency().name(), amount, "gameRoundRef", "transactionRef", "gameId", "sessionId",
                              WalletServiceImpl.GAME_PLAY_FINAL);

        assertThat(player.getBlockType(), is(BET_LIMIT));
        assertThat(player.getWallet().getAccumulatedBetAmountByTimeUnit(betTimeUnit), is(amount));
    }

    @Test
    public void bet_blocksPlayerIfExceedsMonthlyBet() {
        final Player player = createPlayer();
        final PlayerLimitation playerLimitation = createPlayerLimitationWithLossAndBetAsDayAndNoLimitation(player);
        final TimeUnit betTimeUnit = TimeUnit.MONTH;
        playerLimitation.setBetTimeUnit(betTimeUnit);
        playerLimitation.setBetLimit(new Money(200L));
        final Money initialAccumulateBetLimit = new Money(100L);
        player.getWallet().setAccumulatedBetAmountByTimeUnit(betTimeUnit, initialAccumulateBetLimit);

        final PlayerBonus activePlayerBonus = new PlayerBonus();
        activePlayerBonus.setId(10L);
        final Bonus bonus = new Bonus();
        bonus.setValidTo(tomorrow);
        activePlayerBonus.setPk(new PlayerBonusPair(player, bonus));
        final Money amount = new Money(1000L);
        activePlayerBonus.setCurrentBalance(amount);
        activePlayerBonus.setBonusConversionGoal(new Money(38000L));
        activePlayerBonus.setBonusConversionProgress(Money.ZERO);
        when(bonusService.getActivePlayerBonus(player)).thenReturn(activePlayerBonus);

        when(playerLimitationRepository.findByPlayer(player)).thenReturn(playerLimitation);

        walletServiceImpl.bet(player, player.getCurrency().name(), amount, "gameRoundRef", "transactionRef", "gameId", "sessionId",
                              WalletServiceImpl.GAME_PLAY_FINAL);

        assertThat(player.getBlockType(), is(BET_LIMIT));
        assertThat(player.getWallet().getAccumulatedBetAmountByTimeUnit(betTimeUnit), is(initialAccumulateBetLimit.add(amount)));
    }

    @Test
    public void betAndWin_blocksPlayerIfExceedsDailyBetAccumulatedLossValuesDoNotChangeIfBetAndWinAmountAreTheSame() {
        final Player player = createPlayer();
        final PlayerLimitation playerLimitation = createPlayerLimitationWithLossAndBetAsDayAndNoLimitation(player);
        final TimeUnit timeUnit = TimeUnit.DAY;
        playerLimitation.setBetTimeUnit(timeUnit);
        playerLimitation.setBetLimit(new Money(5L));
        playerLimitation.setLossTimeUnit(timeUnit);
        playerLimitation.setLossLimit(new Money(1000L));
        player.getWallet().setAccumulatedLossAmountByTimeUnit(timeUnit, TEN);

        when(playerLimitationRepository.findByPlayer(player)).thenReturn(playerLimitation);

        walletServiceImpl.betAndWin(player, player.getCurrency().name(), Boolean.FALSE, TEN, TEN, "gameRoundRef", "gameId", "sessionId",
                                    WalletServiceImpl.GAME_PLAY_FINAL, "gameRoundRef", "");

        assertThat(player.getBlockType(), is(BET_LIMIT));
        final Wallet wallet = player.getWallet();
        assertThat(wallet.getAccumulatedBetAmountByTimeUnit(timeUnit), is(TEN));
        assertThat(wallet.getAccumulatedLossAmountByTimeUnit(timeUnit), is(TEN));
    }

    @Test
    public void betAndWin_doNotBlocksPlayerIfEqualsWeeklyBet() {
        final Player player = createPlayer();
        player.getWallet().setMoneyBalance(new Money(1000L));
        final PlayerLimitation playerLimitation = createPlayerLimitationWithLossAndBetAsDayAndNoLimitation(player);
        final TimeUnit betTimeUnit = TimeUnit.WEEK;
        playerLimitation.setBetTimeUnit(betTimeUnit);
        playerLimitation.setBetLimit(new Money(1000L));

        when(playerLimitationRepository.findByPlayer(player)).thenReturn(playerLimitation);

        walletServiceImpl.betAndWin(player, player.getCurrency().name(), Boolean.FALSE, new Money(1000L), TEN, "gameRoundRef", "transactionRef", "gameId",
                                    "sessionId", "gameRoundRef", "");

        assertThat(player.getBlockType(), is(UNBLOCKED));
        assertThat(player.getWallet().getAccumulatedBetAmountByTimeUnit(betTimeUnit), is(new Money(1000L)));
    }

    @Test
    public void betAndWin_blocksPlayerIfExceedsMonthlyBet() {
        final Player player = createPlayer();
        final PlayerLimitation playerLimitation = createPlayerLimitationWithLossAndBetAsDayAndNoLimitation(player);
        final TimeUnit betTimeUnit = TimeUnit.MONTH;
        playerLimitation.setBetTimeUnit(betTimeUnit);
        final Money initialAccumulatedBetLimit = new Money(200L);
        playerLimitation.setBetLimit(initialAccumulatedBetLimit);
        player.getWallet().setAccumulatedBetAmountByTimeUnit(betTimeUnit, initialAccumulatedBetLimit);

        final PlayerBonus activePlayerBonus = new PlayerBonus();
        activePlayerBonus.setId(10L);
        final Bonus bonus = new Bonus();
        bonus.setValidTo(tomorrow);
        activePlayerBonus.setPk(new PlayerBonusPair(player, bonus));
        final Money amount = new Money(1000L);
        activePlayerBonus.setCurrentBalance(amount);
        activePlayerBonus.setBonusConversionGoal(new Money(38000L));
        activePlayerBonus.setBonusConversionProgress(Money.ZERO);
        when(bonusService.getActivePlayerBonus(player)).thenReturn(activePlayerBonus);

        when(playerLimitationRepository.findByPlayer(player)).thenReturn(playerLimitation);

        walletServiceImpl.betAndWin(player, player.getCurrency().name(), Boolean.FALSE, amount, TEN, "transactionRef", "gameId", "sessionId",
                                    WalletServiceImpl.GAME_PLAY_FINAL, "gameRoundRef", "");

        assertThat(player.getBlockType(), is(BET_LIMIT));
        assertThat(player.getWallet().getAccumulatedBetAmountByTimeUnit(betTimeUnit), is(initialAccumulatedBetLimit.add(amount)));
    }

    @Test
    public void betAndWin_doNotBlockPlayerIfLossAmountExceedsLimitButIsNotFinishedRound() {
        final Player player = createPlayer();
        final PlayerLimitation playerLimitation = createPlayerLimitationWithLossAndBetAsDayAndNoLimitation(player);
        final TimeUnit lossTimeUnit = TimeUnit.DAY;
        playerLimitation.setLossTimeUnit(lossTimeUnit);
        final Money initialLossAmount = new Money(1000L);
        playerLimitation.setLossLimit(initialLossAmount);
        player.getWallet().setMoneyBalance(initialLossAmount);
        player.getWallet().setAccumulatedLossAmountByTimeUnit(lossTimeUnit, initialLossAmount);
        final PlayerBonus activePlayerBonus = new PlayerBonus();
        activePlayerBonus.setId(10L);
        final Bonus bonus = new Bonus();
        bonus.setValidTo(tomorrow);
        activePlayerBonus.setPk(new PlayerBonusPair(player, bonus));
        activePlayerBonus.setCurrentBalance(new Money(10L));
        activePlayerBonus.setBonusConversionGoal(new Money(38000L));
        activePlayerBonus.setBonusConversionProgress(Money.ZERO);

        final Money amount = new Money(200L);

        when(bonusService.getActivePlayerBonus(player)).thenReturn(activePlayerBonus);
        when(playerLimitationRepository.findByPlayer(player)).thenReturn(playerLimitation);

        walletServiceImpl.betAndWin(player, player.getCurrency().name(), Boolean.FALSE, amount, TEN, "", "", "", "", "", WalletServiceImpl.GAME_PLAY);

        assertThat(player.getBlockType(), is(UNBLOCKED));
        assertThat(player.getWallet().getAccumulatedLossAmountByTimeUnit(lossTimeUnit), is(initialLossAmount.add(amount).subtract(TEN)));
    }

    @Test
    public void win_doNotBlockPlayerIfLossAmountEqualsDailyLossLimit() {
        final Player player = createPlayer();
        final PlayerLimitation playerLimitation = createPlayerLimitationWithLossAndBetAsDayAndNoLimitation(player);
        final TimeUnit betTimeUnit = TimeUnit.DAY;
        playerLimitation.setBetTimeUnit(betTimeUnit);
        playerLimitation.setLossLimit(new Money(1000L));
        player.getWallet().setAccumulatedLossAmountByTimeUnit(betTimeUnit, new Money(1200L));
        final PlayerBonus activePlayerBonus = new PlayerBonus();
        activePlayerBonus.setId(10L);
        final Bonus bonus = new Bonus();
        bonus.setValidTo(tomorrow);
        activePlayerBonus.setPk(new PlayerBonusPair(player, bonus));
        activePlayerBonus.setCurrentBalance(new Money(10L));
        activePlayerBonus.setBonusConversionGoal(new Money(38000L));
        activePlayerBonus.setBonusConversionProgress(Money.ZERO);

        when(bonusService.getActivePlayerBonus(player)).thenReturn(activePlayerBonus);
        when(playerLimitationRepository.findByPlayer(player)).thenReturn(playerLimitation);

        walletServiceImpl.win(player, player.getCurrency().name(), Boolean.FALSE, new Money(200L), "", "", "", "", WalletServiceImpl.GAME_PLAY_FINAL, "", 1L);

        assertThat(player.getBlockType(), is(UNBLOCKED));
        assertThat(player.getWallet().getAccumulatedLossAmountByTimeUnit(betTimeUnit), is(new Money(1000L)));
    }

    @Test
    public void win_wontChangeAccumulatedBetAmounts() {
        final Player player = createPlayer();
        final PlayerLimitation playerLimitation = createPlayerLimitationWithLossAndBetAsDayAndNoLimitation(player);
        playerLimitation.setBetLimit(new Money(1000L));
        final TimeUnit lossTimeUnit = TimeUnit.MONTH;
        playerLimitation.setLossTimeUnit(lossTimeUnit);
        playerLimitation.setLossLimit(new Money(1000L));

        final PlayerBonus activePlayerBonus = new PlayerBonus();
        activePlayerBonus.setId(10L);
        final Bonus bonus = new Bonus();
        bonus.setValidTo(tomorrow);
        activePlayerBonus.setPk(new PlayerBonusPair(player, bonus));
        activePlayerBonus.setCurrentBalance(new Money(10L));
        activePlayerBonus.setBonusConversionGoal(new Money(38000L));
        activePlayerBonus.setBonusConversionProgress(Money.ZERO);

        when(bonusService.getActivePlayerBonus(player)).thenReturn(activePlayerBonus);
        when(playerLimitationRepository.findByPlayer(player)).thenReturn(playerLimitation);

        walletServiceImpl.win(player, player.getCurrency().name(), Boolean.FALSE, new Money(200L), "", "", "", "", WalletServiceImpl.GAME_PLAY_FINAL, "", 1L);

        assertThat(player.getBlockType(), is(UNBLOCKED));
        assertThat(player.getWallet().getAccumulatedLossAmountByTimeUnit(lossTimeUnit), is(new Money(-200L)));
        assertThat(player.getWallet().getAccumulatedBetAmountByTimeUnit(TimeUnit.DAY), is(ZERO));
    }

    @Test
    public void rollback_shouldDoNothingIfTransactionRefDoesNotExist() {
        final Player player = createPlayer();

        final String transactionRef = "1000108890289";
        final GameTransaction originalTransaction = new GameTransaction(player, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, EUR, new Date(), transactionRef, "", "", "",
                                                                            "", "", false, null);
        originalTransaction.setMoneyWithdraw(new Money(100L)); // bet
        originalTransaction.setMoneyDeposit(new Money(50L)); // win

        when(gameTransactionService.getTransaction(transactionRef)).thenReturn(null);

        walletServiceImpl.rollback(player, transactionRef);

        verify(walletRepository, never()).save(any(Wallet.class));
        verify(gameTransactionService, never()).updateTransaction(any(GameTransaction.class));
    }

    @Test
    public void rollback_shouldDoNothingIfTransactionRefDoesNotMatchPlayer() {
        final Player player = createPlayer();

        final String transactionRef = "1000108890289";
        final Player transactionPlayer = createPlayer();
        transactionPlayer.setId(player.getId() + 1);
        final GameTransaction originalTransaction = new GameTransaction(transactionPlayer, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, EUR, new Date(), transactionRef, "",
                                                                            "", "", "", "", false, null);
        originalTransaction.setMoneyWithdraw(new Money(100L)); // bet
        originalTransaction.setMoneyDeposit(new Money(50L)); // win

        when(gameTransactionService.getTransaction(transactionRef)).thenReturn(originalTransaction);

        walletServiceImpl.rollback(player, transactionRef);

        verify(walletRepository, never()).save(any(Wallet.class));
        verify(gameTransactionService, never()).updateTransaction(any(GameTransaction.class));
    }

    @Test
    public void rollback_accumulatedLimitsShouldBeRevertedIfPlayerHasLimit() {
        final Player player = createPlayer();
        final TimeUnit betTimeUnit = TimeUnit.DAY;
        player.getWallet().setAccumulatedBetAmountByTimeUnit(betTimeUnit, new Money(100L));
        final TimeUnit lossTimeUnit = TimeUnit.DAY;
        player.getWallet().setAccumulatedLossAmountByTimeUnit(lossTimeUnit, new Money(4000L));

        final String transactionRef = "1000108890289";
        final GameTransaction originalTransaction = new GameTransaction(player, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, EUR, new Date(), transactionRef, "", "", "",
                                                                            "", "", false, null);
        originalTransaction.setMoneyWithdraw(new Money(100L)); // bet
        originalTransaction.setMoneyDeposit(new Money(50L)); // win

        final PlayerLimitation playerLimitation = createPlayerLimitationWithLossAndBetAsDayAndNoLimitation(player);
        playerLimitation.setLimit(LimitationType.BET_AMOUNT, betTimeUnit, new Money(100000L));
        playerLimitation.setLimit(LimitationType.LOSS_AMOUNT, betTimeUnit, new Money(100000L));

        when(playerLimitationRepository.findByPlayer(eq(player))).thenReturn(playerLimitation);
        when(gameTransactionService.getTransaction(eq(transactionRef))).thenReturn(originalTransaction);

        walletServiceImpl.rollback(player, transactionRef);

        assertThat(player.getWallet().getAccumulatedBetAmountByTimeUnit(betTimeUnit), is(Money.ZERO));
        assertThat(player.getWallet().getAccumulatedLossAmountByTimeUnit(lossTimeUnit), is(new Money(3950L)));
    }

    @Test
    public void rollback_accumulatedLimitsShouldNotBeRevertedIfPlayerHasNoLimits() {
        final Player player = createPlayer();
        final TimeUnit betTimeUnit = TimeUnit.DAY;
        final Money euro1 = new Money(100L);
        player.getWallet().setAccumulatedBetAmountByTimeUnit(betTimeUnit, euro1);
        final TimeUnit lossTimeUnit = TimeUnit.DAY;
        final Money euro40 = new Money(4000L);
        player.getWallet().setAccumulatedLossAmountByTimeUnit(lossTimeUnit, euro40);

        final String transactionRef = "1000108890289";
        final GameTransaction originalTransaction = new GameTransaction(player, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, EUR, new Date(), transactionRef, "", "", "",
                                                                            "", "", false, null);
        originalTransaction.setMoneyWithdraw(euro1); // bet
        originalTransaction.setMoneyDeposit(new Money(50L)); // win

        final PlayerLimitation playerLimitation = createPlayerLimitationWithLossAndBetAsDayAndNoLimitation(player);

        when(playerLimitationRepository.findByPlayer(eq(player))).thenReturn(playerLimitation);
        when(gameTransactionService.getTransaction(eq(transactionRef))).thenReturn(originalTransaction);

        walletServiceImpl.rollback(player, transactionRef);

        assertThat(player.getWallet().getAccumulatedBetAmountByTimeUnit(betTimeUnit), is(euro1));
        assertThat(player.getWallet().getAccumulatedLossAmountByTimeUnit(lossTimeUnit), is(euro40));
    }

    @Test
    public void bet_blocksPlayerForOneWeekIfAmountIsAboveToWeeklyBet() {
        final Player player = createPlayer();
        final Money amount = new Money(1001L);
        player.getWallet().setMoneyBalance(amount);
        final PlayerLimitation playerLimitation = createPlayerLimitationWithLossAndBetAsDayAndNoLimitation(player);
        final TimeUnit betTimeUnit = TimeUnit.WEEK;
        playerLimitation.setBetTimeUnit(betTimeUnit);
        playerLimitation.setBetLimit(new Money(1000L));

        final PlayerBonus activePlayerBonus = new PlayerBonus();
        activePlayerBonus.setId(10L);
        activePlayerBonus.setCurrentBalance(new Money(1000L));
        when(bonusService.getActivePlayerBonus(player)).thenReturn(activePlayerBonus);

        when(playerLimitationRepository.findByPlayer(player)).thenReturn(playerLimitation);

        walletServiceImpl.bet(player, player.getCurrency().name(), amount, "gameRoundRef", "transactionRef", "gameId", "sessionId",
                              WalletServiceImpl.GAME_PLAY_FINAL);

        assertThat(player.getBlockType(), is(BET_LIMIT));
        assertThat(player.getWallet().getAccumulatedBetAmountByTimeUnit(betTimeUnit), is(amount));

        final Calendar expectedCalendar = Calendar.getInstance();
        expectedCalendar.add(Calendar.DATE, betTimeUnit.getTimeValue());
        final Date expectedDate = expectedCalendar.getTime();

        assertThat(CalendarUtils.isSameDay(expectedDate, player.getBlockEndDate()), is(true));
    }
}
