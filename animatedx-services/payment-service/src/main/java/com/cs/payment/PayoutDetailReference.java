package com.cs.payment;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Date;

import static javax.xml.bind.annotation.XmlAccessType.FIELD;

/**
 * @author Hadi Movaghar
 */
@XmlRootElement
@XmlAccessorType(FIELD)
public class PayoutDetailReference implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement
    @Nonnull
    private String payoutReference;

    @XmlElement
    @Nonnull
    private String variant;

    @XmlElement
    @Nonnull
    private String holderName;

    @XmlElement
    @Nonnull
    private Date creationDate;

    @XmlElement
    @Nullable
    private String cardNumber;

    @XmlElement
    @Nullable
    private String accountNumber;

    @XmlElement
    @Nullable
    private String iban;

    @SuppressWarnings("UnusedDeclaration")
    public PayoutDetailReference() {
    }

    public PayoutDetailReference(@Nonnull final String payoutReference, @Nonnull final String variant, @Nonnull final String holderName,
                                 @Nonnull final Date creationDate) {
        this.payoutReference = payoutReference;
        this.variant = variant;
        this.holderName = holderName;
        this.creationDate = creationDate;
    }

    @Nonnull
    public String getPayoutReference() {
        return payoutReference;
    }

    @Nonnull
    public String getVariant() {
        return variant;
    }

    @Nonnull
    public String getHolderName() {
        return holderName;
    }

    @Nonnull
    public Date getCreationDate() {
        return creationDate;
    }

    @Nullable
    public String getIban() {
        return iban;
    }

    public void setIban(@Nullable final String iban) {
        this.iban = iban;
    }

    @Nullable
    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(@Nullable final String cardNumber) {
        this.cardNumber = cardNumber;
    }

    @Nullable
    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(@Nullable final String accountNumber) {
        this.accountNumber = accountNumber;
    }
}
