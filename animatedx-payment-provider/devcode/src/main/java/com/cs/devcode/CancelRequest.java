package com.cs.devcode;

import com.cs.payment.Currency;
import com.cs.payment.DCEventType;
import com.cs.payment.DCPaymentTransaction;
import com.cs.payment.Money;

import com.google.common.base.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;
import java.util.Date;

import static javax.xml.bind.annotation.XmlAccessType.FIELD;

/**
 * @author Hadi Movaghar
 */
@XmlRootElement
@XmlAccessorType(FIELD)
public class CancelRequest {

    @XmlElement
    @Nonnull
    @NotNull(message = "cancelRequest.userId.notNull")
    private String userId;

    @XmlElement
    @Nonnull
    @NotNull(message = "cancelRequest.authCode.notNull")
    private String authCode;

    @XmlElement
    @Nonnull
    @NotNull(message = "cancelRequest.txAmount.notNull")
    private BigDecimal txAmount;

    @XmlElement
    @Nonnull
    @NotNull(message = "cancelRequest.txCurrency.notNull")
    private String txAmountCy;

    @XmlElement
    @Nonnull
    @NotNull(message = "cancelRequest.txId.notNull")
    private String txId;

    @XmlElement
    @Nonnull
    @NotNull(message = "cancelRequest.txTypeId.notNull")
    private Integer txTypeId;

    @XmlElement
    @Nonnull
    @NotNull(message = "cancelRequest.txName.notNull")
    private String txName;

    @XmlElement
    @Nonnull
    @NotNull(message = "cancelRequest.provider.notNull")
    private String provider;

    @XmlElement
    @Nullable
    private String accountId;

    @XmlElement
    @Nullable
    private String maskedAccount;

    @XmlElement
    @Nullable
    private String statusCode;

    @XmlElement
    @Nullable
    private String pspStatusCode;

    @XmlElement
    @Nullable
    private Attributes attributes;

    public CancelRequest() {
    }

    @Nonnull
    public String getUserId() {
        return userId;
    }

    public DCPaymentTransaction getDCPaymentTransaction() {
        final DCPaymentTransaction dcPaymentTransaction = new DCPaymentTransaction();
        dcPaymentTransaction.setDcEventType(DCEventType.CANCEL);
        dcPaymentTransaction.setAuthorizationCode(authCode);
        dcPaymentTransaction.setAmount(new Money(txAmount));
        dcPaymentTransaction.setCurrency(Currency.valueOf(txAmountCy));
        dcPaymentTransaction.setTransactionId(txId);
        dcPaymentTransaction.setTransactionType(txTypeId);
        dcPaymentTransaction.setTransactionName(txName);
        dcPaymentTransaction.setTransactionProvider(provider);
        dcPaymentTransaction.setAccountId(accountId);
        dcPaymentTransaction.setMaskedAccount(maskedAccount);
        dcPaymentTransaction.setStatusCode(statusCode);
        dcPaymentTransaction.setPspStatusCode(pspStatusCode);
        dcPaymentTransaction.setAttributes(attributes != null ? attributes.getAllAttributes() : null);
        dcPaymentTransaction.setCreatedDate(new Date());
        dcPaymentTransaction.setSuccess(true);
        return dcPaymentTransaction;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("userId", userId)
                .add("authCode", authCode)
                .add("txAmount", txAmount)
                .add("txCurrency", txAmountCy)
                .add("txId", txId)
                .add("txTypeId", txTypeId)
                .add("txName", txName)
                .add("provider", provider)
                .add("accountId", accountId)
                .add("maskedAccount", maskedAccount)
                .add("statusCode", statusCode)
                .add("pspStatusCode", pspStatusCode)
                .add("attributes", attributes)
                .toString();
    }
}
