package com.cs.payment;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;
import java.util.Date;

import static javax.xml.bind.annotation.XmlAccessType.FIELD;

/**
 * @author Omid Alaepour.
 */
@XmlRootElement
@XmlAccessorType(FIELD)
public class PaymentTransactionDto {

    @XmlElement
    @Nonnull
    private final Long playerId;

    @XmlElement
    @Nonnull
    private final Long level;

    @XmlElement
    @Nonnull
    private final Date date;

    @XmlElement
    @Nonnull
    private final Currency currency;

    @XmlElement
    @Nonnull
    private final PaymentStatus status;

    @XmlElement
    @Nullable
    private final String paymentMethod;

    @XmlElement
    @Nonnull
    private final EventCode code;

    @XmlElement
    @Nonnull
    private final BigDecimal amount;

    @XmlElement
    @Nonnull
    private final String providerReference;

    @XmlElement
    @Nullable
    private final String withdrawReference;

    public PaymentTransactionDto(final PaymentTransaction transaction) {
        playerId = transaction.getPlayer().getId();
        level = transaction.getLevel();
        date = transaction.getCreatedDate();
        currency = transaction.getCurrency();
        status = transaction.getPaymentStatus();
        paymentMethod = transaction.getPaymentMethod();
        code = transaction.getEventCode();
        amount = transaction.getAmount().getEuroValueInBigDecimal();
        providerReference = transaction.getProviderReference();
        withdrawReference = transaction.getWithdrawReference();
    }

    public PaymentTransactionDto(final DCPaymentTransaction transaction) {
        playerId = transaction.getPlayer().getId();
        level = transaction.getLevel();
        date = transaction.getCreatedDate();
        currency = transaction.getCurrency();
        status = getPaymentStatus(transaction);
        paymentMethod = transaction.getTransactionProvider();
        code = getEventCode(transaction);
        amount = transaction.getAmount().abs().getEuroValueInBigDecimal();
        providerReference = transaction.getTransactionId();
        withdrawReference = null;
    }

    @Nonnull
    Date getDate() {
        return date;
    }

    private EventCode getEventCode(final DCPaymentTransaction dcPaymentTransaction) {
        if (dcPaymentTransaction.getAmount().isLessOrEqualThan(Money.ZERO)) {
            return EventCode.REFUND_WITH_DATA;
        } else {
            return EventCode.AUTHORISATION;
        }
    }

    private PaymentStatus getPaymentStatus(final DCPaymentTransaction dcPaymentTransaction) {
        if (!dcPaymentTransaction.getSuccess()) {
            return PaymentStatus.FAILURE;
        }

        switch (dcPaymentTransaction.getDcEventType()) {
            case AUTHORIZE:
                if (dcPaymentTransaction.getAmount().isPositive()) {
                    return PaymentStatus.AWAITING_PAYMENT;
                } else {
                    return PaymentStatus.AWAITING_APPROVAL;
                }
            case TRANSFER:
                return PaymentStatus.SUCCESS;
            case CANCEL:
                if (dcPaymentTransaction.getAmount().isPositive()) {
                    return PaymentStatus.CANCELED;
                } else {
                    return PaymentStatus.DECLINED;
                }
        }

        return PaymentStatus.SUCCESS;
    }
}
