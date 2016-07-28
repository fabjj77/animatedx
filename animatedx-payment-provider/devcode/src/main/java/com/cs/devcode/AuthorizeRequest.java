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
public class AuthorizeRequest {

    @XmlElement
    @Nonnull
    @NotNull(message = "authorizeRequest.userId.notNull")
    private String userId;

    @XmlElement
    @Nonnull
    @NotNull(message = "authorizeRequest.txAmount.notNull")
    private BigDecimal txAmount;

    @XmlElement
    @Nonnull
    @NotNull(message = "authorizeRequest.txAmountCy.notNull")
    private String txAmountCy;

    @XmlElement
    @Nonnull
    @NotNull(message = "authorizeRequest.txId.notNull")
    private String txId;

    @XmlElement
    @Nonnull
    @NotNull(message = "authorizeRequest.txTypeId.notNull")
    private Integer txTypeId;

    @XmlElement
    @Nonnull
    @NotNull(message = "authorizeRequest.txName.notNull")
    private String txName;

    @XmlElement
    @Nullable
    private String accountId;

    @XmlElement
    @Nullable
    private String maskedAccount;

    @XmlElement
    @Nullable
    private String provider;

    @XmlElement
    @Nullable
    private Attributes attributes;

    public AuthorizeRequest() {
    }

    @Nonnull
    public String getUserId() {
        return userId;
    }

    public DCPaymentTransaction getDCPaymentTransaction() {
        final DCPaymentTransaction dcPaymentTransaction = new DCPaymentTransaction();
        dcPaymentTransaction.setDcEventType(DCEventType.AUTHORIZE);
        dcPaymentTransaction.setAmount(new Money(txAmount));
        dcPaymentTransaction.setCurrency(Currency.valueOf(txAmountCy));
        dcPaymentTransaction.setTransactionId(txId);
        dcPaymentTransaction.setTransactionType(txTypeId);
        dcPaymentTransaction.setTransactionName(txName);
        dcPaymentTransaction.setTransactionProvider(provider);
        dcPaymentTransaction.setAccountId(accountId);
        dcPaymentTransaction.setMaskedAccount(maskedAccount);
        dcPaymentTransaction.setAttributes(attributes != null ? attributes.getAllAttributes() : null);
        dcPaymentTransaction.setBonusCode(attributes != null ? attributes.getBonusId() : null);
        dcPaymentTransaction.setCreatedDate(new Date());
        dcPaymentTransaction.setSuccess(true);
        return dcPaymentTransaction;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("userId", userId)
                .add("txAmount", txAmount)
                .add("txAmountCy", txAmountCy)
                .add("txId", txId)
                .add("txTypeId", txTypeId)
                .add("txName", txName)
                .add("accountId", accountId)
                .add("maskedAccount", maskedAccount)
                .add("provider", provider)
                .add("attributes", attributes)
                .toString();
    }
}
