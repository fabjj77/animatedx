package com.cs.devcode;

import com.cs.messaging.bronto.BrontoGateway;
import com.cs.messaging.bronto.InternalBrontoContact;
import com.cs.messaging.bronto.UpdateContactFieldMessage;
import com.cs.messaging.email.BrontoFieldName;
import com.cs.payment.DCPaymentTransaction;
import com.cs.payment.devcode.DevcodePaymentService;
import com.cs.player.Player;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.EnumMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * @author Hadi Movaghar
 */
@RestController
@RequestMapping(value = "/api/", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
public class DevcodeController {

    private final Logger logger = LoggerFactory.getLogger(DevcodeController.class);

    private final BrontoGateway brontoGateway;
    private final DevcodePaymentService devcodePaymentService;

    @Autowired
    public DevcodeController(final BrontoGateway brontoGateway, final DevcodePaymentService devcodePaymentService) {
        this.brontoGateway = brontoGateway;
        this.devcodePaymentService = devcodePaymentService;
    }

    @RequestMapping(method = POST, value = "/verifyuser")
    @ResponseStatus(OK)
    public VerifyUserResponse verifyUser(@RequestBody(required = true) final VerifyUserRequest verifyUserRequest) {
        logger.trace("Verifying user: {}", verifyUserRequest);
        final Player player = devcodePaymentService.verifyPlayer(verifyUserRequest.getUserId(), verifyUserRequest.getSessionId());
        return new VerifyUserResponse(player);
    }

    @RequestMapping(method = POST, value = "/authorize")
    @ResponseStatus(OK)
    public AuthorizeResponse authorizePayment(@RequestBody(required = true) final AuthorizeRequest authorizeRequest) {
        logger.trace("Authorizing payment: {}", authorizeRequest);
        final DCPaymentTransaction resultDcPaymentTransaction = devcodePaymentService.authorize(authorizeRequest.getUserId(), authorizeRequest.getDCPaymentTransaction());
        return new AuthorizeResponse(resultDcPaymentTransaction);
    }

    @RequestMapping(method = POST, value = "/transfer")
    @ResponseStatus(OK)
    public TransferResponse transferPayment(@RequestBody(required = true) final TransferRequest transferRequest) {
        logger.trace("Transferring payment: {}", transferRequest);
        final DCPaymentTransaction resultDcPaymentTransaction = devcodePaymentService.transfer(transferRequest.getUserId(), transferRequest.getDCPaymentTransaction());
        updateBrontoField(resultDcPaymentTransaction);
        return new TransferResponse(resultDcPaymentTransaction);
    }

    @RequestMapping(method = POST, value = "/cancel")
    @ResponseStatus(OK)
    public CancelResponse cancelPayment(@RequestBody(required = true) final CancelRequest cancelRequest) {
        logger.trace("Cancelling payment: {}", cancelRequest);
        final DCPaymentTransaction resultDcPaymentTransaction = devcodePaymentService.cancel(cancelRequest.getUserId(), cancelRequest.getDCPaymentTransaction());
        updateBrontoField(resultDcPaymentTransaction);
        return new CancelResponse(resultDcPaymentTransaction);
    }

    @RequestMapping(method = POST, value = "/txstatus")
    @ResponseStatus(OK)
    public TransactionStatusResponse getPaymentStatus(@RequestBody(required = true) final TransactionStatusRequest transactionStatusRequest) {
        logger.trace("Getting payment status: {}", transactionStatusRequest);
        final DCPaymentTransaction resultDcPaymentTransaction = devcodePaymentService.getStatus(transactionStatusRequest.getTxId());
        return new TransactionStatusResponse(resultDcPaymentTransaction);
    }

    private void updateBrontoField(final DCPaymentTransaction dcPaymentTransaction) {
        final Map<BrontoFieldName, String> fieldNameMap = new EnumMap<BrontoFieldName, String>(BrontoFieldName.class);
        fieldNameMap.put(BrontoFieldName.money_balance, dcPaymentTransaction.getPlayer().getWallet().getMoneyBalance().getEuroValueInBigDecimal().toString());
        brontoGateway.updateContactFields(new UpdateContactFieldMessage(new InternalBrontoContact(dcPaymentTransaction.getPlayer()), fieldNameMap));
    }
}
