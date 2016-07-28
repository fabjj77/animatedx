package com.cs.payment.adyen;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.adyen.modification.Payment;
import com.adyen.modification.PaymentPortType;
import com.adyen.payout.PayoutLocator;
import com.adyen.payout.PayoutPortType;
import com.adyen.recurring.RecurringPortType;
import com.adyen.recurring.Recurring_ServiceLocator;
import org.apache.axis.client.Call;
import org.apache.axis.client.Stub;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.rpc.ServiceException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author Joakim Gottzén
 */
@Component
public class AdyenEndpoints {

    private final Logger logger = LoggerFactory.getLogger(AdyenEndpoints.class);

    @Value("${payment.payout-soap-endpoint}")
    private String adyenPayoutEndpoint;
    @Value("${payment.store-payout-web-service-user}")
    private String storePayoutWebServiceUser;
    @Value("${payment.store-payout-web-service-password}")
    private String storePayoutWebServicePassword;

    @Value("${payment.review-payout-web-service-user}")
    private String reviewPayoutWebServiceUser;
    @Value("${payment.review-payout-web-service-password}")
    private String reviewPayoutWebServicePassword;

    @Value("${payment.recurring-soap-endpoint}")
    private String adyenRecurringEndpoint;

    @Value("${payment.payment-soap-endpoint}")
    private String adyenPaymentEndpoint;
    @Value("${payment.payment-web-service-user}")
    private String paymentWebServiceUser;
    @Value("payment.payment-web-service-password")
    private String paymentWebServicePassword;

    public PayoutPortType getWithdrawalEndpoint()
            throws AdyenException {
        try {
            final PayoutPortType payoutPortType = new PayoutLocator().getPayoutHttpPort(new URL(adyenPayoutEndpoint));
            ((Stub) payoutPortType)._setProperty(Call.USERNAME_PROPERTY, storePayoutWebServiceUser);
            ((Stub) payoutPortType)._setProperty(Call.PASSWORD_PROPERTY, storePayoutWebServicePassword);
            return payoutPortType;
        } catch (final ServiceException | MalformedURLException | RuntimeException e) {
            logger.error("Error creating Adyen payout endpoint: {}", e.getMessage());
            throw new AdyenException(e.getMessage(), e);
        }
    }

    public RecurringPortType getWithdrawalInformationEndpoint()
            throws AdyenException {
        try {
            final RecurringPortType payoutPortType = new Recurring_ServiceLocator().getRecurringHttpPort(new URL(adyenRecurringEndpoint));
            ((Stub) payoutPortType)._setProperty(Call.USERNAME_PROPERTY, storePayoutWebServiceUser);
            ((Stub) payoutPortType)._setProperty(Call.PASSWORD_PROPERTY, storePayoutWebServicePassword);
            return payoutPortType;
        } catch (final ServiceException | MalformedURLException | RuntimeException e) {
            logger.error("Error creating Adyen recurring endpoint: {}", e.getMessage());
            throw new AdyenException(e.getMessage(), e);
        }
    }

    public PayoutPortType getWithdrawalReviewEndpoint()
            throws AdyenException {
        try {
            final PayoutPortType payoutPortType = new PayoutLocator().getPayoutHttpPort(new URL(adyenPayoutEndpoint));
            ((Stub) payoutPortType)._setProperty(Call.USERNAME_PROPERTY, reviewPayoutWebServiceUser);
            ((Stub) payoutPortType)._setProperty(Call.PASSWORD_PROPERTY, reviewPayoutWebServicePassword);
            return payoutPortType;
        } catch (final ServiceException | MalformedURLException | RuntimeException e) {
            logger.error("Error creating Adyen payout endpoint: {}", e.getMessage());
            throw new AdyenException(e.getMessage(), e);
        }
    }

    public PayoutPortType getDetailStoreEndpoint()
            throws AdyenException {
        try {
            final PayoutPortType payoutPortType = new PayoutLocator().getPayoutHttpPort(new URL(adyenPayoutEndpoint));
            ((Stub) payoutPortType)._setProperty(Call.USERNAME_PROPERTY, storePayoutWebServiceUser);
            ((Stub) payoutPortType)._setProperty(Call.PASSWORD_PROPERTY, storePayoutWebServicePassword);
            return payoutPortType;
        } catch (final ServiceException | MalformedURLException | RuntimeException e) {
            logger.error("Error creating Adyen payout endpoint: {}", e.getMessage());
            throw new AdyenException(e.getMessage(), e);
        }
    }

    public PaymentPortType getPaymentEndpoint()
            throws AdyenException {
        try {
            final PaymentPortType paymentPortType = new Payment(new URL(adyenPaymentEndpoint)).getPaymentHttpPort();
            ((Stub) paymentPortType)._setProperty(Call.USERNAME_PROPERTY, paymentWebServiceUser);
            ((Stub) paymentPortType)._setProperty(Call.PASSWORD_PROPERTY, paymentWebServicePassword);
            return paymentPortType;
        } catch (final MalformedURLException | RuntimeException e) {
            logger.error("Error creating Adyen payment endpoint: {}", e.getMessage());
            throw new AdyenException(e.getMessage(), e);
        }
    }
}
