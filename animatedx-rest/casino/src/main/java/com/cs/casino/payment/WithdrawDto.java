package com.cs.casino.payment;

import com.cs.payment.Money;
import com.cs.validation.ValidationProperties;

import org.hibernate.validator.constraints.NotEmpty;

import javax.annotation.Nonnull;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;

import static javax.xml.bind.annotation.XmlAccessType.FIELD;

/**
 * @author Hadi Movaghar
 */
@XmlRootElement
@XmlAccessorType(FIELD)
public class WithdrawDto {

    @XmlElement
    @Nonnull
    @NotEmpty(message = "withdrawDto.withdrawReference.notEmpty")
    private String withdrawReference;

    @XmlElement
    @Nonnull
    @NotEmpty(message = "withdrawDto.paymentMethod.notEmpty")
    private String paymentMethod;

    @XmlElement
    @Nonnull
    @Min(value = 0, message = "withdrawDto.amount.min")
    private BigDecimal amount;

    @XmlElement
    @Nonnull
    @NotNull
    @Pattern(regexp = ValidationProperties.PASSWORD_PATTERN, message = "withdrawDto.password.notValid")
    private String password;

    public WithdrawDto() {
    }

    @Nonnull
    public String getWithdrawReference() {
        return withdrawReference;
    }

    @Nonnull
    public Money getAmountInCents() {
        return Money.getMoneyInCentsFromEuro(amount);
    }

    @Nonnull
    public String getPassword() {
        return password;
    }

    @Nonnull
    public String getPaymentMethod() {
        return paymentMethod;
    }
}
