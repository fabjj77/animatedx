package com.cs.casino.payment;

import com.cs.payment.ConversionType;

import javax.annotation.Nonnull;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import static javax.xml.bind.annotation.XmlAccessType.FIELD;

/**
 * @author Omid Alaepour
 */
@XmlRootElement
@XmlAccessorType(FIELD)
public class CreditsDto {
    @XmlElement
    @Nonnull
    @Min(value = 0, message = "creditsDto.creditAmount.min")
    private Integer creditAmount;

    @XmlElement
    @Nonnull
    @NotNull(message = "creditsDto.conversionType.notNull")
    private ConversionType conversionType;

    @Nonnull
    public Integer getCreditAmount() {
        return creditAmount;
    }

    @Nonnull
    public ConversionType getConversionType() {
        return conversionType;
    }
}
