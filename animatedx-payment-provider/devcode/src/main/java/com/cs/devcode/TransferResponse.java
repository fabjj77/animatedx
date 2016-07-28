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
public class TransferResponse {

    @XmlElement
    @Nonnull
    @NotNull(message = "transferResponse.userId.notNull")
    private String userId;

    @XmlElement
    @Nonnull
    @NotNull(message = "transferResponse.success.notNull")
    private Boolean success;

    @XmlElement
    @Nonnull
    @NotNull(message = "transferResponse.txId.notNull")
    private String txId;

    @XmlElement
    @Nonnull
    @NotNull(message = "transferResponse.merchantTxId.notNull")
    private Long merchantTxId;

    @XmlElement
    @Nullable
    private Number errCode;

    @XmlElement
    @Nullable
    private String errMsg;

    @SuppressWarnings("UnusedDeclaration")
    public TransferResponse() {
    }

    public TransferResponse(final DCPaymentTransaction dcPaymentTransaction) {
        userId = dcPaymentTransaction.getPlayer().getId().toString();
        success = dcPaymentTransaction.getSuccess();
        txId = dcPaymentTransaction.getTransactionId();
        merchantTxId = dcPaymentTransaction.getId();
    }
}
