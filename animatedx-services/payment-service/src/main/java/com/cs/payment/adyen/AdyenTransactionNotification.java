package com.cs.payment.adyen;

import com.cs.payment.Currency;
import com.cs.payment.EventCode;
import com.cs.payment.Money;
import com.cs.payment.Operation;
import com.cs.payment.PaymentStatus;

import com.google.common.base.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;
import java.util.EnumSet;

import static javax.xml.bind.annotation.XmlAccessType.FIELD;

/**
 * @author Hadi Movaghar
 */
@XmlRootElement
@XmlAccessorType(FIELD)
public class AdyenTransactionNotification {

    @XmlElement
    @Nonnull
    private EventCode eventCode;

    @XmlElement
    @Nonnull
    private String providerReference;

    @XmlElement
    @Nonnull
    private String originalReference;

    @XmlElement(nillable = true, required = true)
    @Nonnull
    private String merchantReference;

    @XmlElement
    @Nonnull
    private Currency currency;

    @XmlElement
    @Nonnull
    private Money amount;

    @XmlElement
    @Nonnull
    private PaymentStatus paymentStatus;

    @XmlElement(nillable = true, required = true)
    @Nullable
    private String reason;

    @XmlElement
    @Nonnull
    private Date eventDate;

    @XmlElement(nillable = true, required = true)
    @Nullable
    private String paymentMethod;

    @XmlElement(nillable = true, required = true)
    @Nonnull
    private EnumSet<Operation> operations;

    public AdyenTransactionNotification() {
    }

    public AdyenTransactionNotification(@Nonnull final EventCode eventCode, @Nonnull final String providerReference, @Nonnull final String originalReference,
                                        @Nonnull final String merchantReference, @Nonnull final Currency currency, @Nonnull final Money amount,
                                        @Nonnull final PaymentStatus paymentStatus, @Nullable final String reason, @Nonnull final Date eventDate,
                                        @Nullable final String paymentMethod, @Nonnull final EnumSet<Operation> operations) {
        this.eventCode = eventCode;
        this.providerReference = providerReference;
        this.originalReference = originalReference;
        this.merchantReference = merchantReference;
        this.currency = currency;
        this.amount = amount;
        this.paymentStatus = paymentStatus;
        this.reason = reason;
        this.eventDate = eventDate;
        this.paymentMethod = paymentMethod;
        this.operations = operations;
    }

    @Nonnull
    public EventCode getEventCode() {
        return eventCode;
    }

    public void setEventCode(@Nonnull final EventCode eventCode) {
        this.eventCode = eventCode;
    }

    @Nonnull
    public String getProviderReference() {
        return providerReference;
    }

    public void setProviderReference(@Nonnull final String providerReference) {
        this.providerReference = providerReference;
    }

    @Nonnull
    public String getOriginalReference() {
        return originalReference;
    }

    public void setOriginalReference(@Nonnull final String originalReference) {
        this.originalReference = originalReference;
    }

    @Nonnull
    public String getMerchantReference() {
        return merchantReference;
    }

    public void setMerchantReference(@Nonnull final String merchantReference) {
        this.merchantReference = merchantReference;
    }

    @Nonnull
    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(@Nonnull final Currency currency) {
        this.currency = currency;
    }

    @Nonnull
    public Money getAmount() {
        return amount;
    }

    public void setAmount(@Nonnull final Money amount) {
        this.amount = amount;
    }

    @Nonnull
    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(@Nonnull final PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    @Nullable
    public String getReason() {
        return reason;
    }

    public void setReason(@Nullable final String reason) {
        this.reason = reason;
    }

    @Nonnull
    public Date getEventDate() {
        return eventDate;
    }

    @Nullable
    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(@Nullable final String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    @Nonnull
    public EnumSet<Operation> getOperations() {
        return operations;
    }

    @Nonnull
    public Long getTransactionId() {
        return Long.valueOf(merchantReference);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("eventCode", eventCode)
                .add("providerReference", providerReference)
                .add("originalReference", originalReference)
                .add("merchantReference", merchantReference)
                .add("currency", currency)
                .add("amount", amount)
                .add("paymentStatus", paymentStatus)
                .add("reason", reason)
                .add("eventDate", eventDate)
                .add("paymentMethod", paymentMethod)
                .add("operations", operations)
                .toString();
    }
}
