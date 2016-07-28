package com.cs.administration.payment;

import com.cs.administration.player.BackOfficeWalletDto;
import com.cs.administration.security.BackOfficeUser;
import com.cs.administration.security.CurrentUser;
import com.cs.payment.AwaitingWithdrawPageableDto;
import com.cs.payment.BankAccountDto;
import com.cs.payment.DCPaymentTransaction;
import com.cs.payment.EventCode;
import com.cs.payment.Operation;
import com.cs.payment.PaymentPageableDto;
import com.cs.payment.PaymentService;
import com.cs.payment.PaymentStatus;
import com.cs.payment.PaymentTransaction;
import com.cs.payment.devcode.DevcodePaymentService;
import com.cs.payment.transaction.PaymentTransactionFacade;
import com.cs.user.User;
import com.cs.user.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;

import static com.cs.persistence.Constants.BO_DEFAULT_SIZE;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

/**
 * @author Marcus Wass
 */
@RestController
@RequestMapping(value = "/api/payments", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
public class PaymentController {

    private final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    private final DevcodePaymentService devcodePaymentService;
    private final PaymentService paymentService;
    private final PaymentTransactionFacade paymentTransactionFacade;
    private final UserService userService;

    @Autowired
    public PaymentController(final DevcodePaymentService devcodePaymentService, final PaymentService paymentService,
                             final PaymentTransactionFacade paymentTransactionFacade, final UserService userService) {
        this.devcodePaymentService = devcodePaymentService;
        this.paymentService = paymentService;
        this.paymentTransactionFacade = paymentTransactionFacade;
        this.userService = userService;
    }

    @RequestMapping(method = GET)
    @ResponseStatus(OK)
    public PaymentPageableDto getPaymentTransactions(@RequestParam(value = "startDate", required = true) @DateTimeFormat(iso = ISO.DATE) final Date startDate,
                                                     @RequestParam(value = "endDate", required = true) @DateTimeFormat(iso = ISO.DATE) final Date endDate,
                                                     @RequestParam(value = "playerId", required = false) final Long playerId,
                                                     @RequestParam(value = "code", required = false) final EventCode eventCode,
                                                     @RequestParam(value = "status", required = false) final PaymentStatus paymentStatus,
                                                     @RequestParam(value = "page", required = false, defaultValue = "0") final Integer page,
                                                     @RequestParam(value = "size", required = false, defaultValue = BO_DEFAULT_SIZE) final Integer size) {
        // TODO:Joakim Remove the size parameter when Adyen is removed form the system, the size is necessary for returning the correct number of elements
//        final Page<PaymentTransaction> transactions = paymentTransactionFacade.getPayments(playerId, eventCode, paymentStatus, startDate, endDate, page, size);
//        final Page<DCPaymentTransaction> dcPaymentTransactions = devcodePaymentService.getPayments(playerId, startDate, endDate, page, size);

        final Page<PaymentTransaction> transactions = paymentTransactionFacade.getPayments(playerId, eventCode, paymentStatus, startDate, endDate, 0, 1000);
        final Page<DCPaymentTransaction> dcPaymentTransactions = devcodePaymentService.getPayments(playerId, startDate, endDate, 0, 1000);
        return new PaymentPageableDto(transactions, dcPaymentTransactions, page, size);
    }

    @RequestMapping(method = GET, value = "/refund/{playerId}")
    @ResponseStatus(OK)
    public List<RefundCancelDetailDto> getPlayerRefundDetail(@PathVariable("playerId") final Long playerId) {
        return RefundCancelDetailDto.getList(paymentService.getPaymentsContainingOperation(playerId, Operation.REFUND));
    }

    @RequestMapping(method = PUT, value = "/refund/{playerId}/{originalReference}")
    @ResponseStatus(NO_CONTENT)
    public void sendRefundRequest(@PathVariable("playerId") final Long playerId, @PathVariable("originalReference") final String originalReference,
                                  @CurrentUser final BackOfficeUser currentUser) {
        final User user = userService.getUser(currentUser.getId());
        paymentService.refundDeposit(originalReference, playerId, user);
    }

    @RequestMapping(method = GET, value = "/cancel/{playerId}")
    @ResponseStatus(OK)
    public List<RefundCancelDetailDto> getPlayerCancelDetail(@PathVariable("playerId") final Long playerId) {
        return RefundCancelDetailDto.getList(paymentService.getPaymentsContainingOperation(playerId, Operation.CANCEL));
    }

    @RequestMapping(method = PUT, value = "/cancel/{playerId}/{originalReference}")
    @ResponseStatus(NO_CONTENT)
    public void sendCancelRequest(@PathVariable("playerId") final Long playerId, @PathVariable("originalReference") final String originalReference,
                                  @CurrentUser final BackOfficeUser currentUser) {
        final User user = userService.getUser(currentUser.getId());
        paymentService.cancelDeposit(originalReference, playerId, user);
    }

    @RequestMapping(method = PUT, value = "/{playerId}")
    @ResponseStatus(OK)
    public BackOfficeWalletDto updatePlayerWallet(@PathVariable("playerId") final Long playerId, @CurrentUser final BackOfficeUser currentUser,
                                                  @RequestBody(required = true) final BackOfficeWalletDto walletDto) {
        logger.info("Received a request to update wallet for player: {} from user: {} with money amount: {}", playerId, currentUser.getId(), walletDto.getMoneyBalance());

        final User user = userService.getUser(currentUser.getId());
        return new BackOfficeWalletDto(paymentService.updatePlayerWallet(playerId, user, walletDto.asWallet()));
    }

    @RequestMapping(method = POST, value = "/confirm")
    @ResponseStatus(OK)
    public ConfirmDeclineWithdrawDto confirmWithdrawals(@RequestBody(required = true) final ConfirmDeclineWithdrawDto confirmDeclineWithdrawDto,
                                                        @CurrentUser final BackOfficeUser currentUser) {
        final User user = userService.getUser(currentUser.getId());
        return new ConfirmDeclineWithdrawDto(paymentService.confirmWithdrawals(confirmDeclineWithdrawDto.getWithdrawReferences(), user));
    }

    @RequestMapping(method = POST, value = "/decline")
    @ResponseStatus(OK)
    public ConfirmDeclineWithdrawDto declineWithdraws(@RequestBody(required = true) final ConfirmDeclineWithdrawDto declineWithdrawDto,
                                                      @CurrentUser final BackOfficeUser currentUser) {
        final User user = userService.getUser(currentUser.getId());
        return new ConfirmDeclineWithdrawDto(paymentService.declineWithdrawals(declineWithdrawDto.getWithdrawReferences(), user));
    }

    @RequestMapping(method = GET, value = "/awaiting")
    @ResponseStatus(OK)
    public AwaitingWithdrawPageableDto getAwaitingPayouts(@RequestParam(value = "page", required = false, defaultValue = "0") final Integer page,
                                                          @RequestParam(value = "size", required = false, defaultValue = BO_DEFAULT_SIZE) final Integer size) {
        final Page<PaymentTransaction> awaitingWithdraws = paymentService.getAwaitingWithdrawals(page, size);
        return new AwaitingWithdrawPageableDto(awaitingWithdraws);
    }

    @RequestMapping(method = POST, value = "/bank/{playerId}")
    @ResponseStatus(OK)
    public void storeBankAccountForWithdrawal(@PathVariable("playerId") final Long playerId, @CurrentUser final BackOfficeUser currentUser,
                                              @RequestBody @Valid final BankAccountDto bankAccountDto) {
        paymentService.backOfficeStoreBankAccountForWithdrawal(userService.getUser(currentUser.getId()), playerId, bankAccountDto.getIban(), bankAccountDto.getBic(),
                                                               bankAccountDto.getBankName(), bankAccountDto.getName());
    }
}
