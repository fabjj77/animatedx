package com.cs.casino.payment;

import com.cs.payment.Money;

import org.hibernate.validator.constraints.NotBlank;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;

import static javax.xml.bind.annotation.XmlAccessType.FIELD;

/**
 * @author Joakim Gottzén
 */
@XmlRootElement
@XmlAccessorType(FIELD)
public class DepositRequestDto {
    @XmlElement
    @Nonnull
    @Min(value = 0, message = "depositRequestDto.amount.min")
    private BigDecimal amount;

    @XmlElement
    @Nonnull
    @NotBlank
    private String skin;

    @XmlElement
    @NotNull(message="depositRequestDto.paymentMethod.notNull")
    private String paymentMethod;

    @XmlElement
    @Nullable
    private Long bonusId;

    @Nonnull
    public Money getAmount() {
        return new Money(amount);
    }

    @Nonnull
    public String getSkin() {
        return skin;
    }

    @Nonnull
    public String getPaymentMethod() {
        return paymentMethod;
    }

    @Nullable
    public Long getBonusId() {
        return bonusId;
    }
}
