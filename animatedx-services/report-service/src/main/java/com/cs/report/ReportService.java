package com.cs.report;

import com.cs.game.GameTransaction;
import com.cs.messaging.sftp.PlayerActivity;
import com.cs.messaging.sftp.PlayerRegistration;
import com.cs.payment.CreditTransaction;
import com.cs.payment.PaymentTransaction;
import com.cs.player.Player;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.Date;
import java.util.List;

/**
 * @author Hadi Movaghar
 */
public interface ReportService {

    XSSFWorkbook getPaymentsReport(final Iterable<PaymentTransaction> transactions);

    XSSFWorkbook getPlayerReport(final Iterable<Player> players);

    XSSFWorkbook getFinancialReport(final List<Player> players, final Date startDate, final Date endDate);

    XSSFWorkbook getGameTransactionsReport(Iterable<GameTransaction> gameTransactions);

    XSSFWorkbook getCreditReport(Iterable<CreditTransaction> creditTransactions);

    List<PlayerRegistration> sendPlayerRegistrationsToNetrefer(final Date date);

    List<PlayerActivity> sendPlayerActivityToNetrefer(final Date date);
}
