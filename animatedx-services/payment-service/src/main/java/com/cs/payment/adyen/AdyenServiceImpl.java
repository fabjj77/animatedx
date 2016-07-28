package com.cs.payment.adyen;

import com.cs.payment.EventCode;
import com.cs.payment.Money;
import com.cs.payment.PaymentMethod;
import com.cs.payment.RecurringContract;
import com.cs.player.Player;
import com.cs.util.Base64.Encoder;
import com.cs.util.DateFormatPatterns;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriUtils;

import com.adyen.modification.ModificationRequest;
import com.adyen.modification.ModificationResult;
import com.adyen.modification.ObjectFactory;
import com.adyen.modification.ServiceException_Exception;
import com.adyen.payout.Amount;
import com.adyen.payout.BankAccount;
import com.adyen.payout.ModifyRequest;
import com.adyen.payout.ModifyResponse;
import com.adyen.payout.Recurring;
import com.adyen.payout.StoreDetailRequest;
import com.adyen.payout.StoreDetailResponse;
import com.adyen.payout.SubmitRequest;
import com.adyen.payout.SubmitResponse;
import com.adyen.recurring.RecurringDetailsRequest;
import com.adyen.recurring.RecurringDetailsResult;
import com.adyen.recurring.Recurring_Type;
import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.rmi.RemoteException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author Joakim Gottzén
 */
@Service
@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.SUPPORTS, readOnly = true)
public class AdyenServiceImpl implements AdyenService {

    private final Logger logger = LoggerFactory.getLogger(AdyenServiceImpl.class);

    private static final String HMAC_SHA_1 = "HmacSHA1";

    @Value("${payment.merchant-account}")
    private String merchantAccount;

    @Value("${payment.payout-description}")
    private String payoutDescription;

    @Value("${payment.deposit-url}")
    private String depositUrl;
    @Value("${payment.hmac}")
    private String hmac;

    private final AdyenEndpoints adyenEndpoints;

    private Mac mac;

    @Autowired
    public AdyenServiceImpl(final AdyenEndpoints adyenEndpoints) {
        this.adyenEndpoints = adyenEndpoints;
    }

    @PostConstruct
    public void init() {
        final Key hmacKeySpec = new SecretKeySpec(hmac.getBytes(), HMAC_SHA_1);
        try {
            mac = Mac.getInstance(HMAC_SHA_1);
            logger.info("Initializing HMac '{}'...", HMAC_SHA_1);
            mac.init(hmacKeySpec);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            logger.error("Error initializing bean with HMAC: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public SubmitResponse withdraw(final Player player, final RecurringContract recurringContract, final String targetReference, final Money amount,
                                   final Long reference)
            throws AdyenException {
        final SubmitRequest submitRequest = new SubmitRequest();

        final Recurring recurring = new Recurring();
        recurring.setContract(recurringContract.toString());
        submitRequest.setRecurring(recurring);

        submitRequest.setSelectedRecurringDetailReference(targetReference);
        submitRequest.setMerchantAccount(merchantAccount);

        final Amount amountObject = new Amount();
        amountObject.setCurrency(player.getCurrency().toString());
        amountObject.setValue(amount.getCents());
        submitRequest.setAmount(amountObject);

        submitRequest.setShopperEmail(player.getEmailAddress());
        submitRequest.setShopperReference(player.getId().toString());
        submitRequest.setShopperStatement(payoutDescription);
        submitRequest.setReference(reference.toString());

        try {
            return adyenEndpoints.getWithdrawalEndpoint().submit(submitRequest);
        } catch (final RemoteException e) {
            logger.error("Error submitting withdrawal request to Adyen: {}", e.getMessage());
            throw new AdyenException(e.getMessage(), e);
        }
    }

    @Override
    public ModifyResponse confirmWithdrawal(@Nullable final String originalReference)
            throws AdyenException {
        final ModifyRequest modifyRequest = new ModifyRequest();
        modifyRequest.setMerchantAccount(merchantAccount);
        //noinspection ConstantConditions
        modifyRequest.setOriginalReference(originalReference);

        try {
            return adyenEndpoints.getWithdrawalReviewEndpoint().confirm(modifyRequest);
        } catch (final RemoteException e) {
            logger.error("Error submitting withdrawal confirmation request to Adyen: {}", e.getMessage());
            throw new AdyenException(e.getMessage(), e);
        }
    }

    @Override
    public ModifyResponse declineWithdrawal(@Nullable final String originalReference)
            throws AdyenException {
        final ModifyRequest modifyRequest = new ModifyRequest();
        modifyRequest.setMerchantAccount(merchantAccount);
        //noinspection ConstantConditions
        modifyRequest.setOriginalReference(originalReference);

        try {
            return adyenEndpoints.getWithdrawalReviewEndpoint().decline(modifyRequest);
        } catch (final RemoteException e) {
            logger.error("Error submitting withdrawal confirmation request to Adyen: {}", e.getMessage());
            throw new AdyenException(e.getMessage(), e);
        }
    }

    @Override
    public StoreDetailResponse storeBankAccount(final Player player, final String ownerName, final String bankName, final String iban, final String bic,
                                                final RecurringContract recurringContract)
            throws AdyenException {
        final BankAccount account = new BankAccount();
        account.setIban(iban);
        account.setBic(bic);
        account.setBankName(bankName);
        account.setCountryCode(player.getAddress().getCountry().toIso());
        account.setOwnerName(ownerName);

        // TODO save in db to be reviewed and confirmed?
        final StoreDetailRequest storeDetailRequest = new StoreDetailRequest();
        storeDetailRequest.setMerchantAccount(merchantAccount);
        storeDetailRequest.setRecurring(new Recurring(recurringContract.toString(), ""));
        storeDetailRequest.setBank(account);
        storeDetailRequest.setShopperEmail(player.getEmailAddress());
        storeDetailRequest.setShopperReference(player.getId().toString());

        try {
            return adyenEndpoints.getDetailStoreEndpoint().storeDetail(storeDetailRequest);
        } catch (final RemoteException e) {
            logger.error("Error submitting bank account details request to Adyen: {}", e.getMessage());
            throw new AdyenException(e.getMessage(), e);
        }
    }

    @Override
    public RecurringDetailsResult getWithdrawalDetails(final Player player, final RecurringContract recurringContract)
            throws AdyenException {
        final Recurring_Type recurring = new Recurring_Type();
        recurring.setContract(recurringContract.toString());
        @SuppressWarnings("ConstantConditions") final RecurringDetailsRequest recurringDetailsRequest =
                new RecurringDetailsRequest(null, merchantAccount, recurring, player.getId().toString());

        try {
            return adyenEndpoints.getWithdrawalInformationEndpoint().listRecurringDetails(recurringDetailsRequest);
        } catch (@SuppressWarnings("OverlyBroadCatchBlock") final RemoteException e) {
            logger.error("Error submitting withdrawal details request to Adyen: {}", e.getMessage());
            throw new AdyenException(e.getMessage(), e);
        }
    }

    @Override
    public ModificationResult refundDeposit(final Player player, final String originalReference, final Money amount, final EventCode eventCode, final Long reference)
            throws AdyenException {
        final ModificationRequest modificationRequest = getRefundOrCancelModificationRequest(player, originalReference, amount, eventCode, reference);

        try {
            return adyenEndpoints.getPaymentEndpoint().refund(modificationRequest);
        } catch (final ServiceException_Exception e) {
            logger.error("Error submitting refund deposit request to Adyen: {}", e.getFaultInfo().getError().getValue().value());
            throw new AdyenException(e.getFaultInfo().getError().getValue().value(), e);
        }
    }

    private ModificationRequest getRefundOrCancelModificationRequest(final Player player, final String originalReference, final Money amount, final EventCode eventCode,
                                                                     final Long reference) {
        final ModificationRequest modificationRequest = new ModificationRequest();
        final ObjectFactory paymentObjectFactory = new ObjectFactory();
        modificationRequest.setMerchantAccount(paymentObjectFactory.createModificationRequestMerchantAccount(merchantAccount));
        modificationRequest.setOriginalReference(paymentObjectFactory.createModificationRequestOriginalReference(originalReference));
        final com.adyen.modification.Amount amountObject = new com.adyen.modification.Amount();
        amountObject.setValue(amount.getCents());
        amountObject.setCurrency(player.getCurrency().toString());
        if (eventCode == EventCode.REFUND) {
            modificationRequest.setModificationAmount(paymentObjectFactory.createModificationRequestModificationAmount(amountObject));
        }
        modificationRequest.setReference(paymentObjectFactory.createModificationRequestReference(reference.toString()));

        return modificationRequest;
    }

    @Override
    public ModificationResult cancelDeposit(final Player player, final String originalReference, final Money amount, final EventCode eventCode, final Long reference)
            throws AdyenException {
        final ModificationRequest modificationRequest = getRefundOrCancelModificationRequest(player, originalReference, amount, eventCode, reference);

        try {
            return adyenEndpoints.getPaymentEndpoint().cancel(modificationRequest);
        } catch (final ServiceException_Exception e) {
            logger.error("Error submitting cancel deposit request to Adyen: {}", e.getFaultInfo().getError().getValue().value());
            throw new AdyenException(e.getFaultInfo().getError().getValue().value(), e);
        }
    }

    @Override
    public String createAdyenDepositUrl(final Money amount, final String skin, final PaymentMethod paymentMethodType, final Player player,
                                        final String merchantReference) {
        final Calendar calendar = Calendar.getInstance();
        final Date now = calendar.getTime();
        calendar.add(Calendar.YEAR, 1);
        final SimpleDateFormat dateFormat = new SimpleDateFormat(DateFormatPatterns.DATE_ONLY);
        final String shipBeforeDate = dateFormat.format(calendar.getTime());
        calendar.setTime(now);
        calendar.add(Calendar.DATE, 1);
        final String sessionValidity = dateFormat.format(calendar.getTime());
        final String recurringContract = paymentMethodType.getRecurringContractsString();

        final String allowedMethods = paymentMethodType.getMethodName();

        // DO NOT TOUCH THE ORDERING
        // The ordering matters for the HMAC computation
        final String stringAmount = "" + amount.getCents();
        final String merchantData = stringAmount + player.getCurrency() + shipBeforeDate + merchantReference + skin + merchantAccount + sessionValidity +
                                    player.getEmailAddress() + player.getId() + recurringContract + allowedMethods + AdyenConstants.DEFAULT_OFFSET;

        final String merchantSignature = Encoder.encodeToString(mac.doFinal(merchantData.getBytes(Charsets.UTF_8)));
        logger.info("Created merchant signature '{}' from data: '{}'", merchantSignature, merchantData);

        final String brandCode = "";
        final String issuerId = "";

        final String fragments = Joiner.on("&").join(concatAndEscape(AdyenConstants.MERCHANT_REFERENCE, merchantReference),
                                                     concatAndEscape(AdyenConstants.PAYMENT_AMOUNT, stringAmount),
                                                     concatAndEscape(AdyenConstants.CURRENCY_CODE, player.getCurrency().toString()),
                                                     concatAndEscape(AdyenConstants.SHIP_BEFORE_DATE, shipBeforeDate),
                                                     concatAndEscape(AdyenConstants.SKIN_CODE, skin),
                                                     concatAndEscape(AdyenConstants.MERCHANT_ACCOUNT, merchantAccount),
                                                     concatAndEscape(AdyenConstants.SESSION_VALIDITY, sessionValidity),
                                                     concatAndEscape(AdyenConstants.SHOPPER_LOCALE, player.getAddress().getCountry().getLocale()),
                                                     concatAndEscape(AdyenConstants.COUNTRY_CODE, player.getAddress().getCountry().toIso()),
                                                     concatAndEscape(AdyenConstants.SHOPPER_EMAIL, player.getEmailAddress()),
                                                     concatAndEscape(AdyenConstants.SHOPPER_REFERENCE, player.getId().toString()),
                                                     concatAndEscape(AdyenConstants.RECURRING_CONTRACT, recurringContract),
                                                     concatAndEscape(AdyenConstants.ALLOWED_METHODS, allowedMethods),
                                                     concatAndEscape(AdyenConstants.OFFSET, AdyenConstants.DEFAULT_OFFSET),
                                                     concatAndEscape(AdyenConstants.BRAND_CODE, brandCode),
                                                     concatAndEscape(AdyenConstants.ISSUER_ID, issuerId),
                                                     concatAndEscape(AdyenConstants.MERCHANT_SIG, merchantSignature));

        final String url = depositUrl + "?" + fragments;
        logger.info("Created Adyen deposit url '{}'", url);
        return url;
    }

    private String concatAndEscape(final String key, final String value) {
        try {
            return key + "=" + UriUtils.encodeQueryParam(value, Charsets.UTF_8.name());
        } catch (final UnsupportedEncodingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
