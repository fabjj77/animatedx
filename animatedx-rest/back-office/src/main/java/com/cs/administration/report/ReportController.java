package com.cs.administration.report;

import com.cs.game.GameTransaction;
import com.cs.game.GameTransactionService;
import com.cs.payment.CreditTransaction;
import com.cs.payment.EventCode;
import com.cs.payment.PaymentStatus;
import com.cs.payment.PaymentTransaction;
import com.cs.payment.credit.CreditService;
import com.cs.payment.transaction.PaymentTransactionFacade;
import com.cs.persistence.CommunicationException;
import com.cs.player.BlockType;
import com.cs.player.LimitationStatus;
import com.cs.player.Player;
import com.cs.player.PlayerService;
import com.cs.report.ReportService;
import com.cs.user.UserService;
import com.cs.util.CalendarUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * @author Hadi Movaghar
 */
@RestController
@RequestMapping(value = "/api/reports", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
public class ReportController {

    private final Logger logger = LoggerFactory.getLogger(ReportController.class);

    final CreditService creditService;
    final GameTransactionService gameTransactionService;
    final PlayerService playerService;
    final PaymentTransactionFacade paymentTransactionFacade;
    final ReportService reportService;
    final UserService userService;

    @Autowired
    public ReportController(final CreditService creditService, final GameTransactionService gameTransactionService, final PlayerService playerService,
                            final PaymentTransactionFacade paymentTransactionFacade, final ReportService reportService, final UserService userService) {
        this.creditService = creditService;
        this.gameTransactionService = gameTransactionService;
        this.playerService = playerService;
        this.paymentTransactionFacade = paymentTransactionFacade;
        this.reportService = reportService;
        this.userService = userService;
    }

    @RequestMapping(method = GET, value = "/payments")
    @ResponseStatus(OK)
    public void getPaymentTransactions(@RequestParam(value = "startDate", required = true) @DateTimeFormat(iso = ISO.DATE) final Date startDate,
                                       @RequestParam(value = "endDate", required = true) @DateTimeFormat(iso = ISO.DATE) final Date endDate,
                                       @RequestParam(value = "playerId", required = false) final Long playerId,
                                       @RequestParam(value = "code", required = false) final EventCode eventCode,
                                       @RequestParam(value = "status", required = false) final PaymentStatus paymentStatus,
                                       final HttpServletResponse response) {
        final Iterable<PaymentTransaction> transactions = paymentTransactionFacade.getPayments(playerId, eventCode, paymentStatus, startDate, endDate);
        final XSSFWorkbook workbook = reportService.getPaymentsReport(transactions);
        writeResponse(workbook, response);
    }

    /**
     * Search for customers (a player is a playing player on the site), call service as following example: {@code /api/players?
     * playerId=1&emailAddress=anima&nickname='PAPI'&firstname=Ma&lastname=Wa}
     *
     * @param playerId     If a playerId is passed in the method will try to get that player
     * @param emailAddress Like search (%value%)
     * @param nickname     Like search (%value%)
     * @param firstName    Like search (%value%)
     * @param lastName     Like search (%value%)
     */
    @RequestMapping(method = GET, value = "/players")
    @ResponseStatus(OK)
    public void getPlayers(@RequestParam(value = "playerId", required = false) final Long playerId,
                           @RequestParam(value = "emailAddress", required = false) final String emailAddress,
                           @RequestParam(value = "nickname", required = false) final String nickname,
                           @RequestParam(value = "firstName", required = false) final String firstName,
                           @RequestParam(value = "lastName", required = false) final String lastName,
                           @RequestParam(value = "blockType", required = false) final BlockType blockType,
                           @RequestParam(value = "limitStatus", required = false) final LimitationStatus limitationStatus,
                           final HttpServletResponse response) {
        final Iterable<Player> players = playerId == null ? playerService.searchPlayers(emailAddress, nickname, firstName, lastName, blockType,
                                                                                        limitationStatus) : Collections.singletonList(playerService.getPlayer(playerId));
        final XSSFWorkbook workbook = reportService.getPlayerReport(players);
        writeResponse(workbook, response);
    }

    @RequestMapping(method = GET, value = "/financial")
    @ResponseStatus(OK)
    public void getPlayersBalance(@RequestParam(value = "startDate", required = true) @DateTimeFormat(iso = ISO.DATE) final Date startDate,
                                  @RequestParam(value = "endDate", required = true) @DateTimeFormat(iso = ISO.DATE) final Date endDate,
                                  final HttpServletResponse response) {
        final Calendar instance = Calendar.getInstance();
        final Date startDateTrimmed = CalendarUtils.startOfDay(instance, startDate);
        final Date endDateTrimmed = CalendarUtils.endOfDay(instance, endDate);

        final List<Player> players = playerService.getPlayersRegisteredBefore(endDateTrimmed);
        final XSSFWorkbook workbook = reportService.getFinancialReport(players, startDateTrimmed, endDateTrimmed);
        writeResponse(workbook, response);
    }

    @RequestMapping(method = GET, value = "/play")
    @ResponseStatus(OK)
    public void getGameSessionHistory(@RequestParam(value = "playerId", required = true) final Long playerId,
                                      @RequestParam(value = "startDate", required = true) @DateTimeFormat(iso = ISO.DATE) final Date startDate,
                                      @RequestParam(value = "endDate", required = true) @DateTimeFormat(iso = ISO.DATE) final Date endDate,
                                      final HttpServletResponse response) {
        final Player player = playerService.getPlayer(playerId);
        final Iterable<GameTransaction> gameTransactions = gameTransactionService.getGameTransactions(player, startDate, endDate);
        final XSSFWorkbook workbook = reportService.getGameTransactionsReport(gameTransactions);
        writeResponse(workbook, response);
    }

    @RequestMapping(method = GET, value = "/credits")
    @ResponseStatus(OK)
    public void getCreditTransactions(@RequestParam(value = "startDate", required = true) @DateTimeFormat(iso = ISO.DATE) final Date startDate,
                                                   @RequestParam(value = "endDate", required = true) @DateTimeFormat(iso = ISO.DATE) final Date endDate,
                                                   @RequestParam(value = "playerId", required = false) final Long playerId,
                                                   final HttpServletResponse response) {
        final Iterable<CreditTransaction> creditTransactions = creditService.getCreditTransactions(playerId, startDate, endDate);
        final XSSFWorkbook workbook = reportService.getCreditReport(creditTransactions);
        writeResponse(workbook, response);
    }

    private void writeResponse(final XSSFWorkbook workbook, final HttpServletResponse response) {
        try {
            workbook.write(response.getOutputStream());
            response.getOutputStream().flush();
        } catch (final IOException e) {
            logger.error("Error while writing XSL report.");
            throw new CommunicationException("Can not right data to HttpServletResponse");
        }
    }
}
