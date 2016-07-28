package com.cs.administration.bonus;

import javax.annotation.Nonnull;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;
import java.util.Date;

import static javax.xml.bind.annotation.XmlAccessType.FIELD;

/**
 * @author Hadi Movaghar
 */
@XmlRootElement
@XmlAccessorType(FIELD)
public class AdjustablePlayerBonusDto {

    @XmlElement
    @Nonnull
    private BigDecimal amount;

    @XmlElement
    @Nonnull
    private BigDecimal maxAmount;

    @XmlElement
    @Nonnull
    private Date validFrom;

    @XmlElement
    @Nonnull
    private Date validTo;

    public AdjustablePlayerBonusDto() {
    }

    @Nonnull
    public BigDecimal getAmount() {
        return amount;
    }

    @Nonnull
    public BigDecimal getMaxAmount() {
        return maxAmount;
    }

    @Nonnull
    public Date getValidFrom() {
        return validFrom;
    }

    @Nonnull
    public Date getValidTo() {
        return validTo;
    }
}
