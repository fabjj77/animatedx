package com.cs.netent.wallet;

import com.cs.avatar.Level;
import com.cs.payment.Currency;
import com.cs.payment.Money;
import com.cs.persistence.BalanceException;
import com.cs.persistence.InvalidCurrencyException;
import com.cs.persistence.NegativeBetException;
import com.cs.persistence.NegativeWinException;
import com.cs.persistence.Status;
import com.cs.player.Player;
import com.cs.player.PlayerServiceImpl;
import com.cs.player.Wallet;

import com.casinomodule.walletserver._3_0.DepositFault;
import com.casinomodule.walletserver._3_0.GetBalanceFault;
import com.casinomodule.walletserver._3_0.GetPlayerCurrencyFault;
import com.casinomodule.walletserver._3_0.RollbackTransactionFault;
import com.casinomodule.walletserver._3_0.WithdrawAndDepositFault;
import com.casinomodule.walletserver._3_0.WithdrawFault;
import com.casinomodule.walletserver.types._3_0.BonusPrograms;
import com.casinomodule.walletserver.types._3_0.Deposit;
import com.casinomodule.walletserver.types._3_0.DepositResponse;
import com.casinomodule.walletserver.types._3_0.GetBalance;
import com.casinomodule.walletserver.types._3_0.GetPlayerCurrency;
import com.casinomodule.walletserver.types._3_0.RollbackTransaction;
import com.casinomodule.walletserver.types._3_0.Withdraw;
import com.casinomodule.walletserver.types._3_0.WithdrawAndDeposit;

import org.springframework.test.util.ReflectionTestUtils;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * @author Omid Alaepour
 */
public class WalletServerEndpointTest {
    public static final String VALID_USERNAME = "username";
    public static final String VALID_PASSWORD = "password";

    @InjectMocks
    private WalletServerEndpoint walletServerEndpoint;

    @Mock
    private PlayerServiceImpl playerService;
    @Mock
    private WalletServiceImpl netEntWalletService;

    @Before
    public void setup() {
        initMocks(this);

        ReflectionTestUtils.setField(walletServerEndpoint, "username", VALID_USERNAME);
        ReflectionTestUtils.setField(walletServerEndpoint, "password", VALID_PASSWORD);
    }

    private Player createPlayer() {
        final Player player = new Player();
        player.setId(1L);
        player.setCurrency(Currency.EUR);
        player.setLevel(new Level(20L));
        player.setStatus(Status.ACTIVE);

        final Wallet wallet = new Wallet(player);
        player.setWallet(wallet);

        return player;
    }

    private Withdraw createWithdraw(final Player player) {
        final Withdraw withdraw = new Withdraw();
        withdraw.setCallerId(VALID_USERNAME);
        withdraw.setCallerPassword(VALID_PASSWORD);
        withdraw.setCurrency(String.valueOf(player.getCurrency()));
        withdraw.setAmount(player.getWallet().getMoneyBalance().getEuroValueInDouble());

        return withdraw;
    }

    private Deposit createDeposit(final Player player) {
        final Deposit deposit = new Deposit();
        deposit.setCallerId(VALID_USERNAME);
        deposit.setCallerPassword(VALID_PASSWORD);
        deposit.setCurrency(String.valueOf(player.getCurrency()));
        deposit.setAmount(player.getWallet().getMoneyBalance().getEuroValueInDouble());
        deposit.setReason("Reason");
        deposit.setBonusPrograms(new BonusPrograms());

        return deposit;
    }

    @Test
    public void deposit_ShouldThrowFaultWithInvalidCredentials() {
        try {
            walletServerEndpoint.deposit(new Deposit());
            fail();
        } catch (final DepositFault fault) {
            assertThat(fault.getFaultInfo().getErrorCode(), is(equalTo(WalletServerEndpoint.AUTHENTICATION_FAILED_ERROR_CODE)));
        }
    }

    @Test
    public void deposit_TransactionIdShouldNotBeNull()
            throws DepositFault {
        final Player player = createPlayer();
        final Deposit deposit = createDeposit(player);

        when(playerService.getPlayerByCasinoUsername(anyString())).thenReturn(player);
        when(netEntWalletService.win(any(Player.class), anyString(), anyBoolean(), any(Money.class), anyString(), anyString(),
                                     anyString(), anyString(), anyString(), anyString(), anyLong())).thenReturn(100L);
        when(playerService.getPlayer(anyLong())).thenReturn(player);

        final DepositResponse depositResponse = walletServerEndpoint.deposit(deposit);

        assertThat(depositResponse.getTransactionId(), is(notNullValue()));
    }

    @Test(expected = DepositFault.class)
    public void deposit_InvalidCurrencyThrowsDepositFault()
            throws DepositFault {
        final Player player = createPlayer();
        final Deposit deposit = createDeposit(player);
        deposit.setCurrency("NOK");

        when(playerService.getPlayerByCasinoUsername(anyString())).thenReturn(player);
        when(netEntWalletService.win(any(Player.class), anyString(), anyBoolean(), any(Money.class), anyString(), anyString(),
                                     anyString(), anyString(), anyString(), anyString(), anyLong())).thenThrow(new InvalidCurrencyException(Currency.EUR));

        walletServerEndpoint.deposit(deposit);
    }

    @Test(expected = DepositFault.class)
    public void deposit_NegativeDepositThrowsDepositFault()
            throws DepositFault {
        final Player player = createPlayer();
        final Deposit deposit = createDeposit(player);
        deposit.setAmount(-1D);

        when(playerService.getPlayerByCasinoUsername(anyString())).thenReturn(player);
        when(netEntWalletService.win(any(Player.class), anyString(), anyBoolean(), any(Money.class), anyString(), anyString(),
                                     anyString(), anyString(), anyString(), anyString(), anyLong())).thenThrow(new NegativeWinException(new Money(10L)));

        walletServerEndpoint.deposit(deposit);
    }

    @Test
    public void withdraw_ShouldThrowFaultWithInvalidCredentials() {
        try {
            walletServerEndpoint.withdraw(new Withdraw());
            fail();
        } catch (final WithdrawFault fault) {
            assertThat(fault.getFaultInfo().getErrorCode(), is(equalTo(WalletServerEndpoint.AUTHENTICATION_FAILED_ERROR_CODE)));
        }
    }

//    @Test
//    public void withdraw_TransactionIdShouldNotBeNull()
//            throws WithdrawFault {
//        SetWithdraw();
//        WithdrawResponse withdrawResponse = walletServerEndpoint.withdraw(withdraw);
//        assertNotNull(withdrawResponse.getTransactionId());
//    }

//    @Test
//    public void withdraw_BalanceShouldReturnZero()
//            throws WithdrawFault {
//        SetWithdraw();
//        withdraw.setAmount((double) 20);
//        WithdrawResponse withdrawResponse = walletServerEndpoint.withdraw(withdraw);
//        assertEquals(Double.valueOf(0), Double.valueOf(withdrawResponse.getBalance()));
//    }

    @Test(expected = WithdrawFault.class)
    public void withdraw_InvalidCurrencyThrowsWithdrawFault()
            throws WithdrawFault {
        final Player player = createPlayer();
        final Withdraw withdraw = createWithdraw(player);
        withdraw.setCurrency("NOK");

        when(playerService.getPlayerByCasinoUsername(anyString())).thenReturn(player);
        when(netEntWalletService.bet(any(Player.class), anyString(), any(Money.class), anyString(), anyString(),
                                     anyString(), anyString(), anyString())).thenThrow(new InvalidCurrencyException(Currency.EUR));

        walletServerEndpoint.withdraw(withdraw);
    }

    @Test(expected = WithdrawFault.class)
    public void withdraw_NegativeWithdrawThrowsWithdrawFault()
            throws WithdrawFault {
        final Player player = createPlayer();
        final Withdraw withdraw = createWithdraw(player);
        withdraw.setAmount(-1D);

        when(playerService.getPlayerByCasinoUsername(anyString())).thenReturn(player);
        when(netEntWalletService.bet(any(Player.class), anyString(), any(Money.class), anyString(), anyString(),
                                     anyString(), anyString(), anyString())).thenThrow(new NegativeBetException(new Money(10L)));

        walletServerEndpoint.withdraw(withdraw);
    }

    @Test(expected = WithdrawFault.class)
    public void withdraw_NotEnoughMoneyWithdrawThrowsWithdrawFault()
            throws WithdrawFault {
        final Player player = createPlayer();
        final Withdraw withdraw = createWithdraw(player);
        withdraw.setAmount(100D);

        when(playerService.getPlayerByCasinoUsername(anyString())).thenReturn(player);
        when(netEntWalletService.bet(any(Player.class), anyString(), any(Money.class), anyString(), anyString(),
                                     anyString(), anyString(), anyString())).thenThrow(new BalanceException(new Money(10L)));

        walletServerEndpoint.withdraw(withdraw);
    }

//    @Test
//    public void GetBalance_ShouldBeWalletMoneyBalanceAndBonusBalance()
//            throws GetBalanceFault {
//        GetBalance getBalance = new GetBalance();
//        getBalance.setCallerId("0");
//        getBalance.setCurrency(String.valueOf(player.getCurrency()));
//        GetBalanceResponse getBalanceResponse = walletServerEndpoint.getBalance(getBalance);
//        assertEquals(Double.valueOf(wallet.getMoneyBalance().add(wallet.getBonusBalance()).doubleValue()), Double.valueOf(getBalanceResponse.getBalance()));
//    }

    @Test
    public void getBalance_ShouldThrowFaultWithInvalidCredentials() {
        try {
            walletServerEndpoint.getBalance(new GetBalance());
            fail();
        } catch (final GetBalanceFault fault) {
            assertThat(fault.getFaultInfo().getErrorCode(), is(equalTo(WalletServerEndpoint.AUTHENTICATION_FAILED_ERROR_CODE)));
        }
    }

    @Test
    public void rollbackTransaction_ShouldThrowFaultWithInvalidCredentials() {
        try {
            walletServerEndpoint.rollbackTransaction(new RollbackTransaction());
            fail();
        } catch (final RollbackTransactionFault fault) {
            assertThat(fault.getFaultInfo().getErrorCode(), is(equalTo(WalletServerEndpoint.AUTHENTICATION_FAILED_ERROR_CODE)));
        }
    }

    @Test
    public void getPlayerCurrency_ShouldThrowFaultWithInvalidCredentials() {
        try {
            walletServerEndpoint.getPlayerCurrency(new GetPlayerCurrency());
            fail();
        } catch (final GetPlayerCurrencyFault fault) {
            assertThat(fault.getFaultInfo().getErrorCode(), is(equalTo(WalletServerEndpoint.AUTHENTICATION_FAILED_ERROR_CODE)));
        }
    }

    @Test
    public void withdrawAndDeposit_ShouldThrowFaultWithInvalidCredentials() {
        try {
            walletServerEndpoint.withdrawAndDeposit(new WithdrawAndDeposit());
            fail();
        } catch (final WithdrawAndDepositFault fault) {
            assertThat(fault.getFaultInfo().getErrorCode(), is(equalTo(WalletServerEndpoint.AUTHENTICATION_FAILED_ERROR_CODE)));
        }
    }
}
