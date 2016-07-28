package com.cs.casino.bonus;

import com.cs.bonus.Bonus;
import com.cs.bonus.BonusType;
import com.cs.payment.Currency;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static javax.xml.bind.annotation.XmlAccessType.FIELD;

/**
 * @author Hadi Movaghar
 */
@XmlRootElement
@XmlAccessorType(FIELD)
public class BonusDto {
    @XmlElement(nillable = true, required = true)
    @Nullable
    private Long id;

    @XmlElement
    @Nonnull
    @NotNull(message = "bonusDto.name.notNull")
    private String name;

    @XmlElement
    @Nonnull
    @NotNull(message = "bonusDto.validFrom.notNull")
    private Date validFrom;

    @XmlElement
    @Nonnull
    @NotNull(message = "bonusDto.validFrom.notNull")
    private Date validTo;

    @XmlElement
    @Nonnull
    @NotNull(message = "bonusDto.bonusType.notNull")
    private BonusType bonusType;

    @XmlElement
    @Nullable
    private BigDecimal amount;

    @XmlElement
    @Nullable
    private BigDecimal maxAmount;

    @XmlElement
    @Nullable
    private Integer percentage;

    @XmlElement
    @Nullable
    private Integer quantity;

    @XmlElement
    @Nonnull
    @NotNull(message = "bonusDto.currency.notNull")
    private Currency currency;


    public BonusDto() {
    }

    public BonusDto(final Bonus bonus) {
        id = bonus.getId();
        name = bonus.getName();
        validFrom = bonus.getValidFrom();
        validTo = bonus.getValidTo();
        bonusType = bonus.getBonusType();
        amount = bonus.getAmount() != null ? bonus.getAmount().getEuroValueInBigDecimal() : null;
        maxAmount = bonus.getMaxAmount() != null ? bonus.getMaxAmount().getEuroValueInBigDecimal() : null;
        percentage = bonus.getPercentage();
        quantity = bonus.getQuantity() != null ? bonus.getQuantity() : null;
        currency = bonus.getCurrency();
    }

    public static List<BonusDto> getBonusList(final List<Bonus> bonusList) {
        final List<BonusDto> bonusDtoList = new ArrayList<>();
        for (final Bonus bonus : bonusList) {
            bonusDtoList.add(new BonusDto(bonus));
        }
        return bonusDtoList;
    }

    public void setName(@Nonnull final String name) {
        this.name = name;
    }

    public void setValidTo(@Nonnull final Date validTo) {
        this.validTo = validTo;
    }

    public void setAmount(@Nullable final BigDecimal amount) {
        this.amount = amount;
    }

    public void setQuantity(@Nullable final Integer quantity) {
        this.quantity = quantity;
    }

}
