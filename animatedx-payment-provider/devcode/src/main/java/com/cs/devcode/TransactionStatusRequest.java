package com.cs.devcode;

import com.google.common.base.Objects;

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
public class TransactionStatusRequest {

    @XmlElement
    @Nonnull
    @NotNull(message = "transactionStatusRequest.userId.notNull")
    private String userId;

    @XmlElement
    @Nonnull
    @NotNull(message = "transactionStatusRequest.txId.notNull")
    private String txId;

    @XmlElement
    @Nullable
    private Long merchantTxId;

    public TransactionStatusRequest() {
    }

    @Nonnull
    public String getTxId() {
        return txId;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("userId", userId)
                .add("txId", txId)
                .add("merchantTxId", merchantTxId)
                .toString();
    }
}
