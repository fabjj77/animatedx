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
public class TransferRequest {

    @XmlElement
    @Nonnull
    @NotNull(message = "transferRequest.userId.notNull")
    private String userId;

    @XmlElement
    @Nullable
    private String authCode;

    @XmlElement
    @Nonnull
    @NotNull(message = "transferRequest.txAmount.notNull")
    private BigDecimal txAmount;

    @XmlElement
    @Nonnull
    @NotNull(message = "transferRequest.txAmountCy.notNull")
    private String txAmountCy;

    @XmlElement
    @Nonnull
    @NotNull(message = "transferRequest.txId.notNull")
    private String txId;

    @XmlElement
    @Nonnull
    @NotNull(message = "transferRequest.txTypeId.notNull")
    private Integer txTypeId;

    @XmlElement
    @Nonnull
    @NotNull(message = "transferRequest.txName.notNull")
    private String txName;

    @XmlElement
    @Nullable
    private String refTxId;

    @XmlElement
    @Nonnull
    @NotNull(message = "transferRequest.provider.notNull")
    private String provider;

    @XmlElement
    @Nullable
    private String accountId;

    @XmlElement
    @Nullable
    private String maskedAccount;

    @XmlElement
    @Nullable
    private Attributes attributes;

    @XmlElement
    @Nonnull
    private BigDecimal fee;

    @XmlElement
    @Nonnull
    private String feeCy;

    @XmlElement
    @Nonnull
    private BigDecimal txPspAmount;

    @XmlElement
    @Nonnull
    private String txPspAmountCy;

    public TransferRequest() {
    }

    @Nonnull
    public String getUserId() {
        return userId;
    }

    public DCPaymentTransaction getDCPaymentTransaction() {
        final DCPaymentTransaction dcPaymentTransaction = new DCPaymentTransaction();
        dcPaymentTransaction.setDcEventType(DCEventType.TRANSFER);
        dcPaymentTransaction.setAuthorizationCode(authCode);
        dcPaymentTransaction.setAmount(new Money(txAmount));
        dcPaymentTransaction.setCurrency(Currency.valueOf(txAmountCy));
        dcPaymentTransaction.setTransactionId(txId);
        dcPaymentTransaction.setTransactionType(txTypeId);
        dcPaymentTransaction.setTransactionName(txName);
        dcPaymentTransaction.setOriginalTransaction(refTxId);
        dcPaymentTransaction.setTransactionProvider(provider);
        dcPaymentTransaction.setAccountId(accountId);
        dcPaymentTransaction.setMaskedAccount(maskedAccount);
        dcPaymentTransaction.setAttributes(attributes != null ? attributes.getAllAttributes() : null);
        dcPaymentTransaction.setBonusCode(attributes != null ? attributes.getBonusId() : null);
        dcPaymentTransaction.setFee(new Money(fee));
        dcPaymentTransaction.setFeeCurrency(Currency.valueOf(feeCy));
        dcPaymentTransaction.setPspAmount(new Money(txPspAmount));
        dcPaymentTransaction.setPspCurrency(Currency.valueOf(txPspAmountCy));
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
                .add("txAmountCy", txAmountCy)
                .add("txId", txId)
                .add("txTypeId", txTypeId)
                .add("txName", txName)
                .add("refTxId", refTxId)
                .add("provider", provider)
                .add("accountId", accountId)
                .add("maskedAccount", maskedAccount)
                .add("attributes", attributes)
                .add("fee", fee)
                .add("feeCY", feeCy)
                .add("txPspAmount", txPspAmount)
                .add("txPspAmountCy", txPspAmountCy)
                .toString();
    }
}
