package com.cs.devcode;

import com.cs.payment.DCPaymentTransaction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import static javax.xml.bind.annotation.XmlAccessType.FIELD;

/**
 * @author Hadi Movaghar
 */
@XmlRootElement
@XmlAccessorType(FIELD)
public class TransactionStatusResponse {

    @XmlElement
    @Nonnull
    @NotNull(message = "transactionStatusResponse.userId.notNull")
    private String userId;

    @XmlElement
    @Nonnull
    @NotNull(message = "transactionStatusResponse.success.notNull")
    private Boolean success;

    @XmlElement
    @Nonnull
    @NotNull(message = "transactionStatusResponse.amount.notNull")
    private Number amount;

    @XmlElement
    @Nonnull
    @NotNull(message = "transactionStatusResponse.amountCy.notNull")
    private String amountCy;

    @XmlElement
    @Nullable
    private Number txTypeId;

    @XmlElement
    @Nonnull
    @NotNull(message = "transactionStatusResponse.merchantTxId.notNull")
    private Number merchantTxId;

    @XmlElement
    @Nullable
    private Number errCode;

    @XmlElement
    @Nullable
    private String errMsg;

    @SuppressWarnings("UnusedDeclaration")
    public TransactionStatusResponse() {
    }

    public TransactionStatusResponse(final DCPaymentTransaction dcPaymentTransaction) {
        userId = dcPaymentTransaction.getPlayer().getId().toString();
        success = dcPaymentTransaction.getSuccess();
        amount = dcPaymentTransaction.getAmount().getEuroValueInBigDecimal();
        amountCy = dcPaymentTransaction.getPlayer().getCurrency().toString();
        merchantTxId = dcPaymentTransaction.getId();
    }
}
