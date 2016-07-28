package com.cs.avatar;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.validation.constraints.Min;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import static javax.xml.bind.annotation.XmlAccessType.FIELD;

/**
 * @author Joakim Gottzén
 */
@XmlRootElement
@XmlAccessorType(FIELD)
public class LevelDto {
    @XmlElement
    @Nonnull
    private Long level;

    @XmlElement(nillable = true, required = false)
    @Nullable
    @Min(value=0, message = "levelDto.turnover.min")
    private Double turnover;

    @XmlElement(nillable = true, required = false)
    @Nullable
    @Min(value=0, message = "levelDto.cashback.min")
    private Double cashbackPercentage;

    @XmlElement(nillable = true, required = false)
    @Nullable
    @Min(value=0, message = "levelDto.depositBonusPercentage.min")
    private Double depositBonusPercentage;


    @XmlElement(nillable = true, required = false)
    @Nullable
    @Min(value=1, message = "levelDto.creditDices.min")
    private Short creditDices;

    @SuppressWarnings("UnusedDeclaration")
    public LevelDto() {
    }

    public LevelDto(@Nonnull final Level level) {
        this.level = level.getLevel();
        turnover = level.getTurnover().doubleValue();
        cashbackPercentage = level.getCashbackPercentage().doubleValue();
        depositBonusPercentage = level.getDepositBonusPercentage().doubleValue();

        creditDices = level.getCreditDices();
    }

    @Nonnull
    public Long getLevel() {
        return level;
    }

    public void setLevel(@Nonnull final Long level) {
        this.level = level;
    }

    public void setTurnover(@Nullable final Double turnover) {
        this.turnover = turnover;
    }

    public void setCashbackPercentage(@Nullable final Double cashbackPercentage) {
        this.cashbackPercentage = cashbackPercentage;
    }

    public void setDepositBonusPercentage(@Nullable final Double depositBonusPercentage) {
        this.depositBonusPercentage = depositBonusPercentage;
    }

    public void setCreditDices(@Nullable final Short creditDices) {
        this.creditDices = creditDices;
    }
}
