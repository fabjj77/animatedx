package com.cs.netent.wallet;

import com.cs.payment.Money;
import com.cs.persistence.BalanceException;
import com.cs.persistence.InvalidCurrencyException;
import com.cs.persistence.NegativeBetException;
import com.cs.persistence.NegativeWinException;
import com.cs.persistence.NotFoundException;
import com.cs.player.Player;
import com.cs.player.PlayerLimitationException;
import com.cs.player.PlayerService;
import com.cs.player.Wallet;

import com.casinomodule.walletserver._3_0.DepositFault;
import com.casinomodule.walletserver._3_0.GetBalanceFault;
import com.casinomodule.walletserver._3_0.GetPlayerCurrencyFault;
import com.casinomodule.walletserver._3_0.RollbackTransactionFault;
import com.casinomodule.walletserver._3_0.WalletServer;
import com.casinomodule.walletserver._3_0.WithdrawAndDepositFault;
import com.casinomodule.walletserver._3_0.WithdrawFault;
import com.casinomodule.walletserver.types._3_0.Deposit;
import com.casinomodule.walletserver.types._3_0.DepositResponse;
import com.casinomodule.walletserver.types._3_0.GetBalance;
import com.casinomodule.walletserver.types._3_0.GetBalanceResponse;
import com.casinomodule.walletserver.types._3_0.GetPlayerCurrency;
import com.casinomodule.walletserver.types._3_0.GetPlayerCurrencyResponse;
import com.casinomodule.walletserver.types._3_0.RollbackTransaction;
import com.casinomodule.walletserver.types._3_0.RollbackTransactionResponse;
import com.casinomodule.walletserver.types._3_0.Withdraw;
import com.casinomodule.walletserver.types._3_0.WithdrawAndDeposit;
import com.casinomodule.walletserver.types._3_0.WithdrawAndDepositResponse;
import com.casinomodule.walletserver.types._3_0.WithdrawResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

/**
 * @author Joakim Gottzén
 */
@WebService(name = "WalletServer", targetNamespace = "http://walletserver.casinomodule.com/3_0/")
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
public class WalletServerEndpoint extends SpringBeanAutowiringSupport implements WalletServer {

    private final Logger logger = LoggerFactory.getLogger(WalletServerEndpoint.class);

    private static final int NOT_ENOUGH_MONEY_ERROR_CODE = 1;
    private static final int ILLEGAL_CURRENCY_ERROR_CODE = 2;
    private static final int NEGATIVE_DEPOSIT_ERROR_CODE = 3;
    private static final int NEGATIVE_WITHDRAWAL_ERROR_CODE = 4;
    static final int AUTHENTICATION_FAILED_ERROR_CODE = 5;
    private static final int PLAYER_LIMIT_EXCEEDED_ERROR_CODE = 6;

    private static final int NO_PLAYER_FOUND_ERROR_CODE = 7;

    private static final int UNKNOWN_ERROR_ERROR_CODE = 1000;

    private PlayerService playerService;
    private WalletService walletService;

    private String username;
    private String password;

    @SuppressWarnings("SpringJavaAutowiredMembersInspection")
    @Autowired
    @WebMethod(exclude = true)
    public void setPlayerService(final PlayerService playerService) {
        this.playerService = playerService;
    }

    @SuppressWarnings("SpringJavaAutowiredMembersInspection")
    @Autowired
    @WebMethod(exclude = true)
    public void setWalletService(final WalletService walletService) {
        this.walletService = walletService;
    }

    @WebMethod(exclude = true)
    @PostConstruct
    public void init() {
        final AnnotationConfigWebApplicationContext context = (AnnotationConfigWebApplicationContext) ContextLoader.getCurrentWebApplicationContext();
        username = context.getBeanFactory().resolveEmbeddedValue("${wallet-server.net-ent.username}");
        password = context.getBeanFactory().resolveEmbeddedValue("${wallet-server.net-ent.password}");
    }

    private void validateCredentials(final String username, final String password)
            throws InvalidCredentialsException {
        if (!(this.username.equals(username) && this.password.equals(password))) {
            throw new InvalidCredentialsException("Access Denied");
        }
    }

    @Override
    public DepositResponse deposit(final Deposit parameters)
            throws DepositFault {
        try {
            validateCredentials(parameters.getCallerId(), parameters.getCallerPassword());
        } catch (final InvalidCredentialsException e) {
            logger.warn("Login attempt with invalid credentials: username={}, password={}", parameters.getCallerId(), parameters.getCallerPassword());
            final com.casinomodule.walletserver.types._3_0.DepositFault fault = new com.casinomodule.walletserver.types._3_0.DepositFault();
            fault.setErrorCode(AUTHENTICATION_FAILED_ERROR_CODE);
            throw new DepositFault(e.getMessage(), fault);
        }

        Long bonusProgramId = null;
        if (parameters.getReason().equalsIgnoreCase(WalletServiceImpl.WAGERED_BONUS)) {
            if (parameters.getBonusPrograms() == null || parameters.getBonusPrograms().getBonus() == null) {
                logger.error("NetEnt request of win with reason {} and null bonusProgramId", WalletServiceImpl.WAGERED_BONUS);
            } else if (parameters.getBonusPrograms().getBonus().size() != 1) {
                logger.error("NetEnt request of win with reason {} and more than one bonusProgramId {}", WalletServiceImpl.WAGERED_BONUS,
                             parameters.getBonusPrograms().getBonus().size());
            } else {
                bonusProgramId = parameters.getBonusPrograms().getBonus().get(0).getBonusProgramId();
            }
        }

        return doDeposit(parameters.getPlayerName(), parameters.getCurrency(), parameters.isBigWin(), new Money(parameters.getAmount()), parameters.getGameRoundRef(),
                         parameters.getTransactionRef(), parameters.getGameId(), parameters.getSessionId(), parameters.getReason(), parameters.getSource(),
                         bonusProgramId);
    }

    private DepositResponse doDeposit(final String username, final String currency, final Boolean bigWin, final Money amount, final String gameRoundRef,
                                      final String transactionRef, final String gameId, final String sessionId, final String reason, final String source,
                                      @Nullable final Long bonusProgramId)
            throws DepositFault {
        try {
            final Player player = playerService.getPlayerByCasinoUsername(username);
            final Long transactionId = walletService.win(player, currency, bigWin, amount, gameRoundRef, transactionRef, gameId, sessionId, reason, source,
                                                         bonusProgramId);
            final Wallet updatedWallet = playerService.getPlayer(player.getId()).getWallet();
            final DepositResponse depositResponse = new DepositResponse();
            depositResponse.setTransactionId(transactionId.toString());
            depositResponse.setBalance(updatedWallet.getTotalBalance().getEuroValueInDouble());
            return depositResponse;
        } catch (final RuntimeException e) {
            logger.info("Error handling doDeposit deposit: {}", e.getMessage());
            throw handleDepositError(e);
        }
    }

    private DepositFault handleDepositError(final RuntimeException e) {
        final com.casinomodule.walletserver.types._3_0.DepositFault fault = new com.casinomodule.walletserver.types._3_0.DepositFault();

        if (e instanceof NotFoundException) {
            fault.setErrorCode(NO_PLAYER_FOUND_ERROR_CODE);
        } else if (e instanceof NegativeBetException) {
            fault.setErrorCode(NEGATIVE_WITHDRAWAL_ERROR_CODE);
        } else if (e instanceof NegativeWinException) {
            fault.setErrorCode(NEGATIVE_DEPOSIT_ERROR_CODE);
        } else if (e instanceof BalanceException) {
            fault.setErrorCode(NOT_ENOUGH_MONEY_ERROR_CODE);
        } else if (e instanceof InvalidCurrencyException) {
            fault.setErrorCode(ILLEGAL_CURRENCY_ERROR_CODE);
        } else if (e instanceof PlayerLimitationException) {
            fault.setErrorCode(PLAYER_LIMIT_EXCEEDED_ERROR_CODE);
        } else {
            logger.error("Unexpected error: {}", e.getMessage(), e);
            fault.setErrorCode(UNKNOWN_ERROR_ERROR_CODE);
        }

        return new DepositFault(e.getMessage(), fault);
    }

    @Override
    public WithdrawResponse withdraw(final Withdraw parameters)
            throws WithdrawFault {
        try {
            validateCredentials(parameters.getCallerId(), parameters.getCallerPassword());
        } catch (final InvalidCredentialsException e) {
            logger.warn("Login attempt with invalid credentials: username={}, password={}", parameters.getCallerId(), parameters.getCallerPassword());
            final com.casinomodule.walletserver.types._3_0.WithdrawFault fault = new com.casinomodule.walletserver.types._3_0.WithdrawFault();
            fault.setErrorCode(AUTHENTICATION_FAILED_ERROR_CODE);
            throw new WithdrawFault(e.getMessage(), fault);
        }

        return doWithdraw(parameters.getPlayerName(), parameters.getCurrency(), parameters.getAmount(), parameters.getGameRoundRef(), parameters.getTransactionRef(),
                          parameters.getGameId(), parameters.getSessionId(), parameters.getReason());
    }

    private WithdrawResponse doWithdraw(final String username, final String currency, final double amount, final String gameRoundRef, final String transactionRef,
                                        final String gameId, final String sessionId, final String reason)
            throws WithdrawFault {
        try {
            final Player player = playerService.getPlayerByCasinoUsername(username);
            if (!player.getStatus().isAllowedToLogin()) {
                throw new NotFoundException("Player  " + player.getId() + " is " + player.getStatus() + " and not allowed to play");
            }
            final Long transactionId = walletService.bet(player, currency, new Money(amount), gameRoundRef, transactionRef, gameId, sessionId, reason);
            final Wallet updatedWallet = playerService.getPlayer(player.getId()).getWallet();
            final WithdrawResponse withdrawResponse = new WithdrawResponse();
            withdrawResponse.setTransactionId(transactionId);
            withdrawResponse.setBalance(updatedWallet.getTotalBalance().getEuroValueInDouble());
            return withdrawResponse;
        } catch (@SuppressWarnings("OverlyBroadCatchBlock") final RuntimeException e) {
            logger.info("Error handling withdraw request: {}", e.getMessage());
            throw handleWithdrawError(e);
        }
    }

    private WithdrawFault handleWithdrawError(final RuntimeException e) {
        final com.casinomodule.walletserver.types._3_0.WithdrawFault fault = new com.casinomodule.walletserver.types._3_0.WithdrawFault();

        if (e instanceof NotFoundException) {
            fault.setErrorCode(NO_PLAYER_FOUND_ERROR_CODE);
        } else if (e instanceof NegativeBetException) {
            fault.setErrorCode(NEGATIVE_WITHDRAWAL_ERROR_CODE);
        } else if (e instanceof NegativeWinException) {
            fault.setErrorCode(NEGATIVE_DEPOSIT_ERROR_CODE);
        } else if (e instanceof BalanceException) {
            fault.setErrorCode(NOT_ENOUGH_MONEY_ERROR_CODE);
        } else if (e instanceof InvalidCurrencyException) {
            fault.setErrorCode(ILLEGAL_CURRENCY_ERROR_CODE);
        } else if (e instanceof PlayerLimitationException) {
            fault.setErrorCode(PLAYER_LIMIT_EXCEEDED_ERROR_CODE);
        } else {
            logger.error("Unexpected error: {}", e.getMessage(), e);
            fault.setErrorCode(UNKNOWN_ERROR_ERROR_CODE);
        }

        return new WithdrawFault(e.getMessage(), fault);
    }

    @Override
    public GetBalanceResponse getBalance(final GetBalance parameters)
            throws GetBalanceFault {
        try {
            validateCredentials(parameters.getCallerId(), parameters.getCallerPassword());
        } catch (final InvalidCredentialsException e) {
            logger.warn("Login attempt with invalid credentials: username={}, password={}", parameters.getCallerId(), parameters.getCallerPassword());
            final com.casinomodule.walletserver.types._3_0.GetBalanceFault fault = new com.casinomodule.walletserver.types._3_0.GetBalanceFault();
            fault.setErrorCode(AUTHENTICATION_FAILED_ERROR_CODE);
            throw new GetBalanceFault(e.getMessage(), fault);
        }

        try {
            final Player player = playerService.getPlayerByCasinoUsername(parameters.getPlayerName());
            final Money balance = walletService.getBalance(player, parameters.getCurrency());
            final GetBalanceResponse getBalanceResponse = new GetBalanceResponse();
            getBalanceResponse.setBalance(balance.getEuroValueInDouble());
            return getBalanceResponse;
        } catch (final RuntimeException e) {
            logger.info("Error handling balance request: {}", e.getMessage());
            throw handleGetBalanceError(e);
        }
    }

    private GetBalanceFault handleGetBalanceError(final RuntimeException e) {
        final com.casinomodule.walletserver.types._3_0.GetBalanceFault fault = new com.casinomodule.walletserver.types._3_0.GetBalanceFault();

        if (e instanceof NotFoundException) {
            fault.setErrorCode(NO_PLAYER_FOUND_ERROR_CODE);
        } else if (e instanceof NegativeBetException) {
            fault.setErrorCode(NEGATIVE_WITHDRAWAL_ERROR_CODE);
        } else if (e instanceof NegativeWinException) {
            fault.setErrorCode(NEGATIVE_DEPOSIT_ERROR_CODE);
        } else if (e instanceof BalanceException) {
            fault.setErrorCode(NOT_ENOUGH_MONEY_ERROR_CODE);
        } else if (e instanceof InvalidCurrencyException) {
            fault.setErrorCode(ILLEGAL_CURRENCY_ERROR_CODE);
        } else if (e instanceof PlayerLimitationException) {
            fault.setErrorCode(PLAYER_LIMIT_EXCEEDED_ERROR_CODE);
        } else {
            logger.error("Unexpected error: {}", e.getMessage(), e);
            fault.setErrorCode(UNKNOWN_ERROR_ERROR_CODE);
        }

        return new GetBalanceFault(e.getMessage(), fault);
    }

    @Override
    public RollbackTransactionResponse rollbackTransaction(final RollbackTransaction parameters)
            throws RollbackTransactionFault {
        try {
            validateCredentials(parameters.getCallerId(), parameters.getCallerPassword());
        } catch (final InvalidCredentialsException e) {
            logger.warn("Login attempt with invalid credentials: username={}, password={}", parameters.getCallerId(), parameters.getCallerPassword());
            final com.casinomodule.walletserver.types._3_0.RollbackTransactionFault fault = new com.casinomodule.walletserver.types._3_0.RollbackTransactionFault();
            fault.setErrorCode(AUTHENTICATION_FAILED_ERROR_CODE);
            throw new RollbackTransactionFault(e.getMessage(), fault);
        }

        try {
            final Player player = playerService.getPlayerByCasinoUsername(parameters.getPlayerName());
            walletService.rollback(player, String.valueOf(parameters.getTransactionRef()));
        } catch (final RuntimeException e) {
            logger.info("Error handling rollbackTransaction request: {}", e.getMessage());
        }

        return new RollbackTransactionResponse();
    }

    @Override
    public GetPlayerCurrencyResponse getPlayerCurrency(final GetPlayerCurrency parameters)
            throws GetPlayerCurrencyFault {
        logger.info("getPlayerCurrency: Request received from NetEnt for Player: {}", parameters.getPlayerName());
        try {
            validateCredentials(parameters.getCallerId(), parameters.getCallerPassword());
        } catch (final InvalidCredentialsException e) {
            logger.warn("Login attempt with invalid credentials: username={}, password={}", parameters.getCallerId(), parameters.getCallerPassword());
            final com.casinomodule.walletserver.types._3_0.GetPlayerCurrencyFault fault = new com.casinomodule.walletserver.types._3_0.GetPlayerCurrencyFault();
            fault.setErrorCode(AUTHENTICATION_FAILED_ERROR_CODE);
            throw new GetPlayerCurrencyFault(e.getMessage(), fault);
        }

        try {
            final Player player = playerService.getPlayerByCasinoUsername(parameters.getPlayerName());
            final GetPlayerCurrencyResponse getPlayerCurrencyResponse = new GetPlayerCurrencyResponse();
            getPlayerCurrencyResponse.setCurrencyIsoCode(player.getCurrency().name());
            return getPlayerCurrencyResponse;
        } catch (final NotFoundException e) {
            logger.info("Error handling getPlayerCurrency request: {}", e.getMessage());
            final com.casinomodule.walletserver.types._3_0.GetPlayerCurrencyFault fault = new com.casinomodule.walletserver.types._3_0.GetPlayerCurrencyFault();
            fault.setErrorCode(NO_PLAYER_FOUND_ERROR_CODE);
            throw new GetPlayerCurrencyFault(e.getMessage(), fault);
        }
    }

    @Override
    public WithdrawAndDepositResponse withdrawAndDeposit(final WithdrawAndDeposit parameters)
            throws WithdrawAndDepositFault {
        try {
            validateCredentials(parameters.getCallerId(), parameters.getCallerPassword());
        } catch (final InvalidCredentialsException e) {
            logger.warn("Login attempt with invalid credentials: username={}, password={}", parameters.getCallerId(), parameters.getCallerPassword());
            final com.casinomodule.walletserver.types._3_0.WithdrawAndDepositFault fault = new com.casinomodule.walletserver.types._3_0.WithdrawAndDepositFault();
            fault.setErrorCode(AUTHENTICATION_FAILED_ERROR_CODE);
            throw new WithdrawAndDepositFault(e.getMessage(), fault);
        }

        return doWithdrawAndDeposit(parameters.getPlayerName(), parameters.getCurrency(), parameters.isBigWin(), new Money(parameters.getWithdraw()),
                                    new Money(parameters.getDeposit()), parameters.getTransactionRef(), parameters.getGameId(), parameters.getSessionId(),
                                    parameters.getReason(), parameters.getGameRoundRef(), parameters.getSource());
    }

    private WithdrawAndDepositResponse doWithdrawAndDeposit(final String username, final String currency, final Boolean bigWin, final Money withdrawAmount,
                                                            final Money depositAmount, final String transactionRef, final String gameId, final String sessionId,
                                                            final String reason, final String gameRoundRef, final String source)
            throws WithdrawAndDepositFault {

        try {
            final Player player = playerService.getPlayerByCasinoUsername(username);
            if (!player.getStatus().isAllowedToLogin()) {
                throw new NotFoundException("Player  " + player.getId() + " is " + player.getStatus() + " and not allowed to play");
            }
            final Long transactionId = walletService.betAndWin(player, currency, bigWin, withdrawAmount, depositAmount, transactionRef, gameId, sessionId, reason,
                                                               gameRoundRef, source);
            final WithdrawAndDepositResponse withdrawAndDepositResponse = new WithdrawAndDepositResponse();
            final Wallet updatedWallet = playerService.getPlayer(player.getId()).getWallet();
            withdrawAndDepositResponse.setTransactionId(transactionId.toString());
            withdrawAndDepositResponse.setNewBalance(updatedWallet.getTotalBalance().getEuroValueInDouble());
            return withdrawAndDepositResponse;
        } catch (@SuppressWarnings("OverlyBroadCatchBlock") final RuntimeException e) {
            logger.info("Error handling doWithdrawAndDeposit request: {}", e.getMessage());
            throw handleWithdrawAndDepositError(e);
        }
    }

    private WithdrawAndDepositFault handleWithdrawAndDepositError(final RuntimeException e) {
        final com.casinomodule.walletserver.types._3_0.WithdrawAndDepositFault fault = new com.casinomodule.walletserver.types._3_0.WithdrawAndDepositFault();

        if (e instanceof NotFoundException) {
            fault.setErrorCode(NO_PLAYER_FOUND_ERROR_CODE);
        } else if (e instanceof NegativeBetException) {
            fault.setErrorCode(NEGATIVE_WITHDRAWAL_ERROR_CODE);
        } else if (e instanceof NegativeWinException) {
            fault.setErrorCode(NEGATIVE_DEPOSIT_ERROR_CODE);
        } else if (e instanceof BalanceException) {
            fault.setErrorCode(NOT_ENOUGH_MONEY_ERROR_CODE);
        } else if (e instanceof InvalidCurrencyException) {
            fault.setErrorCode(ILLEGAL_CURRENCY_ERROR_CODE);
        } else if (e instanceof PlayerLimitationException) {
            fault.setErrorCode(PLAYER_LIMIT_EXCEEDED_ERROR_CODE);
        } else {
            logger.error("Unexpected error: {}", e.getMessage(), e);
            fault.setErrorCode(UNKNOWN_ERROR_ERROR_CODE);
        }

        return new WithdrawAndDepositFault(e.getMessage(), fault);
    }
}
