package com.cs.payment;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
public class MethodDto {
    @XmlElement
    @Nonnull
    private String method;

    @XmlElement(nillable = true, required = true)
    @Nonnull
    private Double depositMinAmount;

    @XmlElement(nillable = true, required = true)
    @Nullable
    private Double depositMaxAmount;

    @XmlElement(nillable = true, required = true)
    @Nonnull
    private Double withdrawalMinAmount;

    @XmlElement(nillable = true, required = true)
    @Nullable
    private Double withdrawalMaxAmount;

    @SuppressWarnings("UnusedDeclaration")
    public MethodDto() {
    }

    public MethodDto(@Nonnull final PaymentMethod method) {
        this.method = method.getMethodName();
        depositMinAmount = 10D;
        depositMaxAmount = method.getMaxLimit().compareTo(BigDecimal.ZERO) == 0 ? null : method.getMaxLimit().doubleValue();
        withdrawalMinAmount = 10D;
        withdrawalMaxAmount = null;
    }
}
