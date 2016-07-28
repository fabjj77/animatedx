package com.cs.report;

import com.cs.affiliate.AffiliateService;
import com.cs.bonus.BonusService;
import com.cs.game.GameTransaction;
import com.cs.game.GameTransactionService;
import com.cs.game.GameTransactionSummary;
import com.cs.job.ScheduledJob;
import com.cs.messaging.sftp.NetreferActivityMessage;
import com.cs.messaging.sftp.NetreferRegistrationMessage;
import com.cs.messaging.sftp.NetreferTransferService;
import com.cs.messaging.sftp.PlayerActivity;
import com.cs.messaging.sftp.PlayerRegistration;
import com.cs.payment.CreditTransaction;
import com.cs.payment.Money;
import com.cs.payment.PaymentSummary;
import com.cs.payment.PaymentTransaction;
import com.cs.payment.credit.CreditService;
import com.cs.payment.devcode.DCPaymentSummary;
import com.cs.payment.devcode.DevcodePaymentService;
import com.cs.payment.transaction.PaymentTransactionFacade;
import com.cs.player.Address;
import com.cs.player.Player;
import com.cs.player.PlayerService;
import com.cs.player.Wallet;
import com.cs.util.CalendarUtils;
import com.cs.util.DateFormatPatterns;
import com.cs.util.Pair;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.transaction.annotation.Propagation.SUPPORTS;

/**
 * @author Hadi Movaghar
 */
@Service
public class ReportServiceImpl implements ReportService {

    private final Logger logger = LoggerFactory.getLogger(ReportServiceImpl.class);
    private static final long secondsInMillisecond = 1000;
    private static final long minutesInMillisecond = secondsInMillisecond * 60;

    private final AffiliateService affiliateService;
    private final BonusService bonusService;
    private final CreditService creditService;
    private final DevcodePaymentService devcodePaymentService;
    private final GameTransactionService gameTransactionService;
    private final NetreferTransferService netreferTransferService;
    private final PaymentTransactionFacade paymentTransactionFacade;
    private final PlayerService playerService;

    @Autowired
    public ReportServiceImpl(final AffiliateService affiliateService, final BonusService bonusService, final CreditService creditService,
                             final DevcodePaymentService devcodePaymentService, final GameTransactionService gameTransactionService,
                             final NetreferTransferService netreferTransferService, final PaymentTransactionFacade paymentTransactionFacade,
                             final PlayerService playerService) {
        this.affiliateService = affiliateService;
        this.bonusService = bonusService;
        this.creditService = creditService;
        this.devcodePaymentService = devcodePaymentService;
        this.gameTransactionService = gameTransactionService;
        this.netreferTransferService = netreferTransferService;
        this.paymentTransactionFacade = paymentTransactionFacade;
        this.playerService = playerService;
    }

    @Transactional(propagation = SUPPORTS, readOnly = true)
    @Override
    public XSSFWorkbook getPaymentsReport(final Iterable<PaymentTransaction> paymentTransactions) {
        final ReportData reportData = new ReportData();
        reportData.setHeaders("ID", "PLAYER ID", "PLAYER FULL NAME", "EMAIL",
                              "EVENT CODE", "PAYMENT METHOD", "AMOUNT",
                              "CURRENCY", "PAYMENT STATUS", "PROVIDER REFERENCE",
                              "ORIGINAL REFERENCE", "WITHDRAW REFERENCE", "REASON",
                              "CREATION DATE", "PROCESS DATE", "CONFIRM/DECLINE DATE");

        for (final PaymentTransaction paymentTransaction : paymentTransactions) {
            final Player player = paymentTransaction.getPlayer();
            reportData.addRow(paymentTransaction.getId(), player.getId(), player.getFullName(), player.getEmailAddress(),
                              paymentTransaction.getEventCode(), paymentTransaction.getPaymentMethod(), paymentTransaction.getAmount().getEuroValueInBigDecimal(),
                              paymentTransaction.getCurrency(), paymentTransaction.getPaymentStatus(), paymentTransaction.getProviderReference(),
                              paymentTransaction.getOriginalReference(), paymentTransaction.getWithdrawReference(), paymentTransaction.getReason(),
                              paymentTransaction.getCreatedDate(), paymentTransaction.getProcessDate(), paymentTransaction.getWithdrawConfirmDate());
        }

        return getWorkbook(reportData);
    }

    @Transactional(propagation = SUPPORTS, readOnly = true)
    @Override
    public XSSFWorkbook getPlayerReport(final Iterable<Player> players) {
        final ReportData reportData = new ReportData();
        reportData.setHeaders("PLAYER ID", "FIRST NAME", "LAST NAME", "NICKNAME", "EMAIL", "PHONE NUMBER",
                              "BIRTHDAY", "STREET", "CITY", "STATE", "COUNTRY", "ZIP CODE",
                              "LANGUAGE", "CURRENCY", "LEVEL", "STATUS", "TRUST LEVEL",
                              "VERIFICATION", "EMAIL VERIFICATION", "RECEIVE PROMOTION SUBSCRIPTION", "BLOCK TYPE",
                              "BLOCK END DATE", "CREATED DATE", "MODIFIED DATE",
                              "FAILED LOGIN ATTEMPTS", "LAST FAILED LOGIN DATE");

        for (final Player player : players) {
            final Address address = player.getAddress();
            reportData.addRow(player.getId(), player.getFirstName(), player.getLastName(), player.getNickname(), player.getEmailAddress(), player.getPhoneNumber(),
                              player.getBirthday(), address.getStreet(), address.getCity(), address.getState(), address.getCountry(), address.getZipCode(),
                              player.getLanguage(), player.getCurrency(), player.getLevel().getLevel(), player.getStatus(), player.getTrustLevel(),
                              player.getPlayerVerification(), player.getEmailVerification(), player.getReceivePromotion(), player.getBlockType(),
                              player.getBlockEndDate(), player.getCreatedDate(), player.getModifiedDate(), player.getFailedLoginAttempts(),
                              player.getLastFailedLoginDate());
        }

        return getWorkbook(reportData);
    }

    @Transactional(propagation = SUPPORTS, readOnly = true, timeout = 180)
    @Override
    public XSSFWorkbook getFinancialReport(final List<Player> players, final Date startDate, final Date endDate) {
        final ReportData reportData = new ReportData();
        final Map<BigInteger, Money> playersTotalBet = gameTransactionService.getPlayersBetAmounts(startDate, endDate);
        final Map<BigInteger, Pair<Money, Money>> playersDepositsWithdrawals = paymentTransactionFacade.getPlayersDepositWithdraw(startDate, endDate);
        final Map<BigInteger, Pair<Money, Money>> playersReservedBonusBalances = bonusService.getPlayersInactiveReservedBonusBalances();
        final Map<BigInteger, String> playersIPs = playerService.getPlayerSignUpIp();

        reportData.setHeaders("USER ID", "FIRST NAME", "LAST NAME",
                              "BIRTH DATE", "EMAIL", "RESIDENCE", "IP",
                              "CURRENCY", "MONEY BALANCE", "RESERVED MONEY BALANCE",
                              "ACTIVE BONUS BALANCE", "INACTIVE BONUS BALANCE",
                              "TOTAL BONUS BALANCE", "RESERVED BONUS BALANCE",
                              "TOTAL DEPOSIT", "TOTAL WITHDRAWAL", "TOTAL BET",
                              "CREDIT BALANCE", "EQUIVALENT CREDITS MONEY", "EQUIVALENT CREDITS BONUS");
        for (final Player player : players) {
            final BigInteger playerId = BigInteger.valueOf(player.getId());
            final Wallet wallet = player.getWallet();
            final Money activeBonus = wallet.getBonusBalance();
            final Integer credits = wallet.getCreditsBalance();

            final Pair<Money, Money> paymentPair = playersDepositsWithdrawals.get(playerId);
            BigDecimal deposit = BigDecimal.ZERO;
            BigDecimal withdraw = BigDecimal.ZERO;
            if (paymentPair != null) {
                deposit = paymentPair.getLeft().getEuroValueInBigDecimal();
                withdraw = paymentPair.getRight().getEuroValueInBigDecimal();
            }
            final Pair<Money, Money> bonusesPair = playersReservedBonusBalances.get(playerId);
            BigDecimal inactiveBonusBalance = BigDecimal.ZERO;
            BigDecimal reservedBonusBalance = BigDecimal.ZERO;
            if (bonusesPair != null) {
                inactiveBonusBalance = bonusesPair.getLeft().getEuroValueInBigDecimal();
                reservedBonusBalance = bonusesPair.getRight().getEuroValueInBigDecimal();
            }

            final BigDecimal totalBet = playersTotalBet.get(playerId) != null ?
                    playersTotalBet.get(playerId).getEuroValueInBigDecimal() : BigDecimal.ZERO;

            reportData.addRow(player.getId(), player.getFirstName(), player.getLastName(),
                              player.getBirthday(), player.getEmailAddress(), player.getAddress().getCountry(), playersIPs.get(playerId),
                              player.getCurrency(), wallet.getMoneyBalance().getEuroValueInBigDecimal(), wallet.getReservedBalance().getEuroValueInBigDecimal(),
                              wallet.getBonusBalance().getEuroValueInBigDecimal(), inactiveBonusBalance,
                              activeBonus.getEuroValueInBigDecimal().add(inactiveBonusBalance), reservedBonusBalance,
                              deposit, withdraw, totalBet,
                              credits, creditService.calculateCredits(credits, player.getLevel().getMoneyCreditRate()).getEuroValueInBigDecimal(),
                              creditService.calculateCredits(credits, player.getLevel().getBonusCreditRate()).getEuroValueInBigDecimal());
        }

        return getWorkbook(reportData);
    }

    @Transactional(propagation = SUPPORTS, readOnly = true)
    @Override
    public XSSFWorkbook getGameTransactionsReport(final Iterable<GameTransaction> gameTransactions) {
        final ReportData reportData = new ReportData();
        reportData.setHeaders("ID", "PLAYER ID", "SESSION ID", "TRANSACTION REFERENCE",
                              "GAME ID", "GAME ROUND REFERENCE", "MONEY DEPOSIT (WIN)", "MONEY WITHDRAW (BET)",
                              "BONUS DEPOSIT (WIN)", "BONUS WITHDRAW (BET)", "MONEY BALANCE",
                              "BONUS BALANCE", "CURRENCY", "ACTIVE PLAYER-BONUS ID", "REACHED BONUS CONVERSION GOAL",
                              "REASON", "CREATION DATE", "ROLLBACK DATE");
        for (final GameTransaction gameTransaction : gameTransactions) {
            final Long activePlayerBonusId = gameTransaction.getActivePlayerBonus() != null ? gameTransaction.getActivePlayerBonus().getId() : null;
            reportData.addRow(gameTransaction.getId(), gameTransaction.getPlayer().getId(), gameTransaction.getSessionId(), gameTransaction.getTransactionRef(),
                              gameTransaction.getGameId(), gameTransaction.getGameRoundRef(), gameTransaction.getMoneyDeposit(), gameTransaction.getMoneyWithdraw(),
                              gameTransaction.getBonusDeposit(), gameTransaction.getBonusWithdraw(), gameTransaction.getMoneyBalance(),
                              gameTransaction.getBonusBalance(), gameTransaction.getCurrency(), activePlayerBonusId, gameTransaction.getReachedBonusConversionGoal(),
                              gameTransaction.getReason(), gameTransaction.getCreatedDate(), gameTransaction.getRollbackDate());
        }
        return getWorkbook(reportData);
    }

    @Transactional(propagation = SUPPORTS, readOnly = true)
    @Override
    public XSSFWorkbook getCreditReport(final Iterable<CreditTransaction> creditTransactions) {
        final ReportData reportData = new ReportData();
        reportData.setHeaders("ID", "PLAYER ID", "PLAYER FULL NAME",
                              "CURRENCY", "CREDITS", "CONVERTED REAL MONEY",
                              "CONVERTED BONUS MONEY", "MONEY CREDITS RATE",
                              "BONUS CREDITS RATE", "CONVERTED LEVEL", "PLAYER-BONUS ID", "USER NICKNAME", "CREATED DATE");

        for (final CreditTransaction creditTransaction : creditTransactions) {
            final Long playerBonusId = creditTransaction.getPlayerBonus() != null ? creditTransaction.getPlayerBonus().getId() : null;
            final String userNickname = creditTransaction.getUser() != null ? creditTransaction.getUser().getNickname() : null;
            reportData.addRow(creditTransaction.getId(), creditTransaction.getPlayer().getId(), creditTransaction.getPlayer().getFullName(),
                              creditTransaction.getCurrency(), creditTransaction.getCredit(), creditTransaction.getRealMoney().getEuroValueInBigDecimal(),
                              creditTransaction.getBonusMoney().getEuroValueInBigDecimal(), creditTransaction.getMoneyCreditRate(),
                              creditTransaction.getBonusCreditRate(), creditTransaction.getLevel(), playerBonusId, userNickname, creditTransaction.getCreatedDate());
        }
        return getWorkbook(reportData);
    }

    @ScheduledJob
    @Override
    public List<PlayerRegistration> sendPlayerRegistrationsToNetrefer(final Date date) {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        CalendarUtils.startOfDay(calendar);
        final Date startDate = calendar.getTime();
        CalendarUtils.endOfDay(calendar);
        final Date endDate = calendar.getTime();

        final SimpleDateFormat dateFormat = new SimpleDateFormat(DateFormatPatterns.DATE_ONLY);
        logger.info("Generating Netrefer registration report for {}", dateFormat.format(date));

        final List<PlayerRegistration> playerRegistrations = affiliateService.getPlayerRegistrations(startDate, endDate);
        netreferTransferService.sendMessage(new NetreferRegistrationMessage(playerRegistrations, date));
        logger.info("Sent {} number of PlayerRegistrations to Netrefer", playerRegistrations.size());

        return playerRegistrations;
    }

    @ScheduledJob
    @Override
    public List<PlayerActivity> sendPlayerActivityToNetrefer(final Date date) {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        CalendarUtils.startOfDay(calendar);
        final Date startDate = calendar.getTime();
        CalendarUtils.endOfDay(calendar);
        final Date endDate = calendar.getTime();

        final SimpleDateFormat dateFormat = new SimpleDateFormat(DateFormatPatterns.DATE_ONLY);
        logger.info("Generating Netrefer activity report for {}", dateFormat.format(date));

        final List<PlayerActivity> playerActivities = new ArrayList<>();
        final List<Player> players = affiliateService.getAllPlayers();
        final Map<BigInteger, GameTransactionSummary> gameTransactionsSummary = gameTransactionService.getAffiliatePlayersGameTransactionSummary(startDate, endDate);
        // subtract CANCEL transactions
        final Map<BigInteger, DCPaymentSummary> devcodePaymentsSummary = devcodePaymentService.getAffiliatePlayersPaymentsSummary(startDate, endDate);
        final Map<BigInteger, Money> currentBonusBalances = bonusService.getAffiliatePlayersBonusBalances();

//        final Map<BigInteger, PaymentSummary> internalPaymentsSummary = paymentTransactionFacade.getAffiliatePlayersPaymentSummary(startDate, endDate);
        // Intentionally disabled, since we don't really support it right now
        final Map<BigInteger, PaymentSummary> internalPaymentsSummary = new HashMap<>();

        for (final Player player : players) {
            final BigInteger playerId = BigInteger.valueOf(player.getId());
            final DCPaymentSummary dcPaymentSummary =
                    devcodePaymentsSummary.get(playerId) != null ? devcodePaymentsSummary.get(playerId) : new DCPaymentSummary();
            final PaymentSummary internalPaymentSummary =
                    internalPaymentsSummary.get(playerId) != null ? internalPaymentsSummary.get(playerId) : new PaymentSummary();
            final GameTransactionSummary gameTransactionSummary =
                    gameTransactionsSummary.get(playerId) != null ? gameTransactionsSummary.get(playerId) : new GameTransactionSummary();
            final Money currentBonusBalance = currentBonusBalances.get(playerId) != null ? currentBonusBalances.get(playerId) : Money.ZERO;
            final PlayerActivity playerActivity =
                    new PlayerActivity(player.getId(), date, PlayerActivity.CASINO_PRODUCT_ID, gameTransactionSummary.getGrossRevenue(),
                                       currentBonusBalance.getEuroValueInDouble(), internalPaymentSummary.getAdjustments(),
                                       dcPaymentSummary.getTotalDeposit().getEuroValueInDouble(), player.getWallet().getCreditsBalance(),
                                       gameTransactionSummary.getTotalMoneyBet(), dcPaymentSummary.getTotalWithdraw().getEuroValueInDouble(),
                                       (int) (dcPaymentSummary.getNumberOfTransactions() + gameTransactionSummary.getNumberOfTransactions()),
                                       internalPaymentSummary.getAdjustmentsTypeId());
            playerActivities.add(playerActivity);
        }

        netreferTransferService.sendMessage(new NetreferActivityMessage(playerActivities, date));
        logger.info("Sent {} number of PlayerActivities to Netrefer", playerActivities.size());
        return playerActivities;
    }

    private XSSFWorkbook getWorkbook(final ReportData reportData) {
        final XSSFWorkbook workbook = new XSSFWorkbook();
        final XSSFSheet sheet = workbook.createSheet();

        int rowCounter = 0;
        int cellNumber = 0;
        Row row = sheet.createRow(rowCounter);
        Cell cell;

        // write header data into sheet
        for (final String header : reportData.getHeaders()) {
            cell = row.createCell(cellNumber++);
            cell.setCellValue(header);
        }

        //Iterate over data and write to sheet
        for (final Object[] dataRow : reportData.getData()) {
            row = sheet.createRow(++rowCounter);
            cellNumber = 0;

            for (final Object dataCell : dataRow) {
                cell = row.createCell(cellNumber++);
                if (dataCell == null) {
                    cell.setCellValue("");
                } else if (dataCell instanceof Integer) {
                    cell.setCellValue((Integer) dataCell);
                } else if (dataCell instanceof Boolean) {
                    cell.setCellValue((Boolean) dataCell);
                } else if (dataCell instanceof Long) {
                    cell.setCellValue((Long) dataCell);
                } else {
                    cell.setCellValue(dataCell.toString());
                }
            }
        }

        final long elapsedTime = new Date().getTime() - reportData.getStartTime().getTime();
        logger.info("Report generation for {} rows and {} columns took {} minutes and {} seconds ({} milliseconds).", reportData.getData().size(),
                    reportData.getHeaders().length, elapsedTime / minutesInMillisecond, elapsedTime % minutesInMillisecond / secondsInMillisecond, elapsedTime);

        return workbook;
    }
}
