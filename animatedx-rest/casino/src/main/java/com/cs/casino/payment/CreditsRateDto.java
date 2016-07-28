package com.cs.casino.payment;

import javax.annotation.Nonnull;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import static javax.xml.bind.annotation.XmlAccessType.FIELD;

/**
 * @author Joakim Gottzén
 */
@XmlRootElement
@XmlAccessorType(FIELD)
public class CreditsRateDto {
    @XmlElement
    @Nonnull
    private Double realRate;

    @XmlElement
    @Nonnull
    private Double bonusRate;


    @SuppressWarnings("UnusedDeclaration")
    public CreditsRateDto() {
    }

    public CreditsRateDto(@Nonnull final Double moneyCreditRate, @Nonnull final Double bonusCreditRate) {
        realRate = moneyCreditRate;
        bonusRate = bonusCreditRate;
    }
}
