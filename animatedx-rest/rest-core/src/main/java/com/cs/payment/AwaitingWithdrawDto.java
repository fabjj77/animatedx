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
 * @author Omid Alaepour
 */
@XmlRootElement
@XmlAccessorType(FIELD)
public class AwaitingWithdrawDto {
    @XmlElement
    @Nonnull
    private Long playerId;

    @XmlElement
    @Nonnull
    private Date date;

    @XmlElement
    @Nonnull
    private Currency currency;

    @XmlElement
    @Nonnull
    private PaymentStatus status;

    @XmlElement
    @Nullable
    private String paymentMethod;

    @XmlElement
    @Nonnull
    private EventCode code;

    @XmlElement
    @Nullable
    private String withdrawReference;

    @XmlElement
    @Nonnull
    private BigDecimal amount;

    @SuppressWarnings("UnusedDeclaration")
    public AwaitingWithdrawDto() {
    }

    public AwaitingWithdrawDto(final PaymentTransaction transaction) {
        playerId = transaction.getPlayer().getId();
        date = transaction.getCreatedDate();
        currency = transaction.getCurrency();
        status = transaction.getPaymentStatus();
        paymentMethod = transaction.getPaymentMethod();
        code = transaction.getEventCode();
        amount = transaction.getAmount().getEuroValueInBigDecimal();
        withdrawReference = transaction.getOriginalReference();
    }
}
