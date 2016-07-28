package com.cs.bonus;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;
import java.util.Date;

import static javax.xml.bind.annotation.XmlAccessType.FIELD;

/**
 * @author Joakim Gottzén
 */
@XmlRootElement
@XmlAccessorType(FIELD)
public abstract class BasicPlayerBonusDto {

    @XmlElement(nillable = true, required = true)
    @Nullable
    private Long id;

    @XmlElement
    @Nonnull
    @NotNull(message = "playerBonusDto.name.notNull")
    private String name;

    @XmlElement
    @Nonnull
    private BonusType bonusType;

    @XmlElement
    @Nonnull
    @NotNull(message = "playerBonusDto.validFrom.notNull")
    private Date validFrom;

    @XmlElement
    @Nonnull
    @NotNull(message = "playerBonusDto.validTo.notNull")
    private Date validTo;

    @XmlElement
    @Nonnull
    @NotNull(message = "playerBonusDto.bonusStatus.notNull")
    private BonusStatus bonusStatus;

    @XmlElement
    @Nullable
    private BigDecimal initialAmount;

    @XmlElement
    @Nullable
    private BigDecimal currentBalance;

    @XmlElement
    @Nullable
    private BigDecimal bonusConversionGoal;

    @XmlElement
    @Nullable
    private BigDecimal bonusConversionProgress;

    @XmlElement
    @Nullable
    private BigDecimal bonusConversionProgressPercentage;

    @XmlElement
    @Nullable
    private BigDecimal maxRedemptionAmount;

    @XmlElement
    @Nullable
    private Integer wagerTimes;

    @XmlElement
    @Nonnull
    @NotNull(message = "playerBonusDto.createdDate.notNull")
    private Date createdDate;

    @SuppressWarnings("UnusedDeclaration")
    protected BasicPlayerBonusDto() {
    }

    protected BasicPlayerBonusDto(final PlayerBonus playerBonus) {
        final Bonus bonus = playerBonus.getPk().getBonus();
        id = playerBonus.getId();
        name = bonus.getName();
        bonusType = bonus.getBonusType();
        validFrom = playerBonus.getValidFrom() != null ? playerBonus.getValidFrom() : bonus.getValidFrom();
        validTo = playerBonus.getValidTo() != null ? playerBonus.getValidTo() : bonus.getValidTo();
        bonusStatus = playerBonus.getStatus();
        initialAmount = playerBonus.getUsedAmount() != null ? playerBonus.getUsedAmount().getEuroValueInBigDecimal(): null;
        currentBalance = playerBonus.getCurrentBalance() != null ? playerBonus.getCurrentBalance().getEuroValueInBigDecimal() : null;
        bonusConversionGoal = playerBonus.getBonusConversionGoal() != null ? playerBonus.getBonusConversionGoal().getEuroValueInBigDecimal() : null;
        bonusConversionProgress = playerBonus.getBonusConversionProgress() != null ? playerBonus.getBonusConversionProgress().getEuroValueInBigDecimal() : null;
        if (bonusConversionGoal != null) {
            bonusConversionProgressPercentage = bonusConversionGoal.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO :
                    bonusConversionProgress.multiply(new BigDecimal(100)).divide(bonusConversionGoal, 2, BigDecimal.ROUND_DOWN);
        } else {
            bonusConversionProgressPercentage = BigDecimal.ZERO;
        }
        maxRedemptionAmount = playerBonus.getMaxRedemptionAmount() != null ? playerBonus.getMaxRedemptionAmount().getEuroValueInBigDecimal() : null;
        wagerTimes = bonus.getWagerTimes();
        createdDate = playerBonus.getCreatedDate();
    }
}
