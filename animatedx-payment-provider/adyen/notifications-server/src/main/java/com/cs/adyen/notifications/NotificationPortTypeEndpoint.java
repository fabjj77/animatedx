package com.cs.adyen.notifications;

import com.cs.messaging.bronto.BrontoGateway;
import com.cs.messaging.bronto.InternalBrontoContact;
import com.cs.messaging.bronto.UpdateContactFieldMessage;
import com.cs.messaging.email.BrontoFieldName;
import com.cs.payment.Currency;
import com.cs.payment.EventCode;
import com.cs.payment.Money;
import com.cs.payment.Operation;
import com.cs.payment.PaymentService;
import com.cs.payment.PaymentStatus;
import com.cs.payment.PaymentTransaction;
import com.cs.payment.adyen.AdyenTransactionNotification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import com.adyen.services.notification.NotificationPortType;
import com.adyen.services.notification.NotificationRequest;
import com.adyen.services.notification.NotificationRequestItem;
import com.adyen.services.notification.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import java.util.Arrays;
import java.util.Date;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.cs.payment.EventCode.AUTHORISATION;
import static com.cs.payment.EventCode.PAYOUT_DECLINE;
import static com.cs.payment.EventCode.REFUND_WITH_DATA;
import static com.cs.payment.EventCode.REPORT_AVAILABLE;

/**
 * @author Hadi Movaghar
 */
@WebService(name = "NotificationsServer", targetNamespace = "http://notificationsserver.casinomodule.com/")
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
public class NotificationPortTypeEndpoint extends SpringBeanAutowiringSupport implements NotificationPortType {

    private static final String ACCEPTANCE_RESPONSE = "[accepted]";

    private final Logger logger = LoggerFactory.getLogger(NotificationPortTypeEndpoint.class);

    private String merchantAccount;

    private PaymentService paymentService;

    private BrontoGateway brontoGateway;

    @SuppressWarnings("SpringJavaAutowiredMembersInspection")
    @Autowired
    @WebMethod(exclude = true)
    public void setPaymentService(final PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @SuppressWarnings("SpringJavaAutowiredMembersInspection")
    @Autowired
    @WebMethod(exclude = true)
    public void setBrontoGateway(final BrontoGateway brontoGateway) {
        this.brontoGateway = brontoGateway;
    }

    @WebMethod(exclude = true)
    @PostConstruct
    public void init() {
        final AnnotationConfigWebApplicationContext context = (AnnotationConfigWebApplicationContext) ContextLoader.getCurrentWebApplicationContext();
        merchantAccount = context.getBeanFactory().resolveEmbeddedValue("${payment.merchant-account}");
    }

    @Override
    public String sendNotification(final NotificationRequest notification)
            throws ServiceException {
        for (final NotificationRequestItem notificationRequestItem : notification.getNotificationItems().getValue().getNotificationRequestItem()) {
            // TODO keep notifications in JMS queue  and send response for notifications
            processNotification(notificationRequestItem);
        }
        return ACCEPTANCE_RESPONSE;
    }

    private void processNotification(final NotificationRequestItem notificationRequestItem) {
        try {
            logger.info("Notification: {}", notificationRequestItem.getEventCode().getValue());
            logger.info("Provider reference: {}", notificationRequestItem.getPspReference().getValue());
            logger.info("Original reference: {}", notificationRequestItem.getOriginalReference().getValue());
            logger.info("Merchant reference: {}", notificationRequestItem.getMerchantReference().getValue());
            logger.info("Success field: {}", notificationRequestItem.isSuccess());

            if (isNotificationValid(notificationRequestItem)) {
                // TODO live field
                final String providerReference = notificationRequestItem.getPspReference().getValue();
                final String originalReference = notificationRequestItem.getOriginalReference().getValue();
                final String merchantReference = notificationRequestItem.getMerchantReference().getValue();
                final Date eventDate = notificationRequestItem.getEventDate().toGregorianCalendar().getTime();
                final PaymentStatus paymentStatus = notificationRequestItem.isSuccess() ? PaymentStatus.SUCCESS : PaymentStatus.FAILURE;
                final String paymentMethod = notificationRequestItem.getPaymentMethod().getValue();
                final EnumSet<Operation> operations = EnumSet.noneOf(Operation.class);
                // TODO check for null
                if (notificationRequestItem.getOperations().getValue() != null) {
                    for (final String s : notificationRequestItem.getOperations().getValue().getString()) {
                        operations.add(Operation.valueOf(s));
                    }
                }
                final String reason = notificationRequestItem.getReason().getValue();
                final Currency currency = Currency.valueOf(notificationRequestItem.getAmount().getValue().getCurrency().getValue());
                final Money amount = new Money(notificationRequestItem.getAmount().getValue().getValue());
                final EventCode eventCode = EventCode.valueOf(notificationRequestItem.getEventCode().getValue());

                final AdyenTransactionNotification adyenTransactionNotification =
                        new AdyenTransactionNotification(eventCode, providerReference, originalReference, merchantReference, currency, amount, paymentStatus, reason,
                                                         eventDate, paymentMethod, operations);

                switch (eventCode) {
                    case AUTHORISATION: // TODO Test to Live
                        logger.info("Received authorisation request from adyen for UUID {}", adyenTransactionNotification.getMerchantReference());
                        final PaymentTransaction paymentDepositTransaction = paymentService.processDeposit(adyenTransactionNotification);
                        if (paymentDepositTransaction != null) {
                            updateBrontoField(paymentDepositTransaction);
                        }
                        break;
                    case REFUND: // TODO Test to Live
                        paymentService.processRefund(adyenTransactionNotification);
                        break;
                    case CANCELLATION: // TODO Test to Live
                        paymentService.processCancel(adyenTransactionNotification);
                        break;
                    case CANCEL_OR_REFUND:
                    case CAPTURE_FAILED:
                        logger.warn("Notification {} received with provider reference: {}", eventCode, providerReference);
                        paymentService.processPaymentCancellation(adyenTransactionNotification);
                        break;
                    case CAPTURE: // TODO Test to Live
                        logger.warn("Notification {} received with provider reference: {}", eventCode, providerReference);
                        paymentService.processPaymentCapture(adyenTransactionNotification);
                        break;
                    case REFUNDED_REVERSED:
                    case REFUND_FAILED:
                        paymentService.processRefundReversed(adyenTransactionNotification);
                        break;
                    case REQUEST_FOR_INFORMATION:
                    case NOTIFICATION_OF_CHARGEBACK:
                        paymentService.defendChargeBack(adyenTransactionNotification);
                        break;
                    case CHARGEBACK:
                        paymentService.processChargeBack(adyenTransactionNotification);
                        break;
                    case CHARGEBACK_REVERSED:
                        paymentService.processChargeBackReversed(adyenTransactionNotification);
                        break;
                    case ADVICE_OF_DEBIT:
                        logger.warn("Notification {} received with provider reference: {}", eventCode, providerReference);
                        break;
                    case REPORT_AVAILABLE: // TODO Test to Live
                        // eventDate, merchantAccountCode, pspReference, reason, success
                        paymentService.processReports(adyenTransactionNotification);
                        break;
                    case REFUND_WITH_DATA:
                        final PaymentTransaction paymentWithdrawTransaction = paymentService.processWithdraw(adyenTransactionNotification);
                        if (paymentWithdrawTransaction != null) {
                            updateBrontoField(paymentWithdrawTransaction);
                        }
                        break;
                    case PAYOUT_DECLINE:
                        paymentService.processDeclinedWithdraw(adyenTransactionNotification);
                        break;
                    case BACK_OFFICE_MONEY_DEPOSIT:
                    case BACK_OFFICE_BONUS_DEPOSIT:
                    case BACK_OFFICE_MONEY_WITHDRAW:
                    case BACK_OFFICE_BONUS_WITHDRAW:
                    case BONUS_CONVERSION:
                    case CASHBACK:
                        // This will not be send from Adyen
                        logger.error("Ignoring notification {}", eventCode);
                        break;
                }
            }
        } catch (@SuppressWarnings("OverlyBroadCatchBlock") final RuntimeException e) {
            logger.error("Error processing Notification {} with provider reference {}",
                         notificationRequestItem.getEventCode().getValue(), notificationRequestItem.getPspReference().getValue(), e);
        }
    }

    private void updateBrontoField(final PaymentTransaction paymentTransaction) {
        final Map<BrontoFieldName, String> fieldNameMap = new EnumMap<BrontoFieldName, String>(BrontoFieldName.class);
        fieldNameMap.put(BrontoFieldName.money_balance, paymentTransaction.getPlayer().getWallet().getMoneyBalance().getEuroValueInBigDecimal().toString());
        brontoGateway.updateContactFields(new UpdateContactFieldMessage(new InternalBrontoContact(paymentTransaction.getPlayer()), fieldNameMap));
    }

    private boolean isNotificationValid(final NotificationRequestItem notificationRequestItem) {
        // TODO add in queue for further process
        // pspReference should not be null
        final String providerReference = notificationRequestItem.getPspReference().getValue();
        if (providerReference == null) {
            logger.error("Notification with empty provider reference was received.");
            return false;
        }

        final String eventCodeString = notificationRequestItem.getEventCode().getValue();
        final EventCode eventCode;
        try {
            eventCode = EventCode.valueOf(notificationRequestItem.getEventCode().getValue());
        } catch (IllegalArgumentException | NullPointerException e) {
            logger.error("Notification {} with reference {} contains invalid event code {}", eventCodeString, providerReference, eventCodeString);
            return false;
        }

        final String merchantAccountCode = notificationRequestItem.getMerchantAccountCode().getValue();
        if (merchantAccountCode != null && !merchantAccountCode.equals(merchantAccount)) {
            logger.error("Notification {} with reference {} contains invalid merchant account code {}", eventCode, providerReference, merchantAccountCode);
            return false;
        }

        // amount is not null even in REPORT_AVAILABLE
        final Long amount = notificationRequestItem.getAmount().getValue().getValue();
        if (amount < 0) {
            logger.error("Notification {} with reference {} contains invalid (negative) amount {}", eventCode, providerReference, amount);
            return false;
        }

        // currency is not null even in REPORT_AVAILABLE
        final String currency = notificationRequestItem.getAmount().getValue().getCurrency().getValue();
        try {
            Currency.valueOf(currency);
        } catch (IllegalArgumentException | NullPointerException e) {
            logger.error("Notification {} with reference {} contains invalid Currency {}", eventCode, providerReference, currency);
            return false;
        }

        // merchant reference could be null only for REPORT_AVAILABLE
        final String merchantReference = notificationRequestItem.getMerchantReference().getValue();
        if (REPORT_AVAILABLE != eventCode) {
            if (merchantReference == null) {
                logger.error("Notification {} with reference {} contains invalid (empty) merchant reference.", eventCode, providerReference);
                return false;
            }
        }

        // merchant reference should be converted to Long in REFUND_WITH_DATA (this is payment message id)
        if (REFUND_WITH_DATA == eventCode || PAYOUT_DECLINE == eventCode) {
            try {
                @SuppressWarnings("UnusedDeclaration")
                final Long aLong = Long.valueOf(merchantReference);
            } catch (final NumberFormatException e) {
                logger.error("Notification {} (Payout) with reference {} contains invalid merchant reference {}, invalid Long", eventCode, providerReference,
                             merchantReference);
                return false;
            }
        }

        // merchant reference should be converted to UUID except than in REFUND_WITH_DATA and REPORT_AVAILABLE
        if (REFUND_WITH_DATA != eventCode && REPORT_AVAILABLE != eventCode && PAYOUT_DECLINE != eventCode) {
            try {
                @SuppressWarnings("UnusedDeclaration")
                final UUID uuid = UUID.fromString(merchantReference);
            } catch (final IllegalArgumentException e) {
                logger.error("Notification {} with reference {} contains invalid merchant reference {}, invalid UUID", eventCode, providerReference, merchantReference);
                return false;
            }
        }

        // original reference is null only for AUTHORISATION and REPORT_AVAILABLE
        final String originalReference = notificationRequestItem.getMerchantReference().getValue();
        if (REPORT_AVAILABLE != eventCode && AUTHORISATION != eventCode) {
            if (originalReference == null) {
                logger.error("Notification {} with reference {} contains invalid (empty) original reference", eventCode, providerReference);
                return false;
            }
        }

        // if eventCode is AUTHORIZATION, Operations should match
        if (AUTHORISATION == EventCode.valueOf(notificationRequestItem.getEventCode().getValue()) && notificationRequestItem.getOperations().getValue() != null) {
            final List<String> operations = notificationRequestItem.getOperations().getValue().getString();
            try {
                for (final String operation : operations) {
                    Operation.valueOf(operation);
                }
            } catch (IllegalArgumentException | NullPointerException e) {
                logger.error("Notification {} with reference {} contains invalid operations {}", eventCode, providerReference, Arrays.toString(operations.toArray()));
                return false;
            }
        }

        final PaymentStatus paymentStatus = notificationRequestItem.isSuccess() ? PaymentStatus.SUCCESS : PaymentStatus.FAILURE;
        if (paymentService.isNotificationDuplicated(providerReference, EventCode.valueOf(notificationRequestItem.getEventCode().getValue()), paymentStatus)) {
            logger.error("Notification {} with reference {} already received", eventCodeString, providerReference);
            // TODO: Add system notification
            return false;
        }

        return true;
    }
}
