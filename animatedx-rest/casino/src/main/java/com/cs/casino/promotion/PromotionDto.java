package com.cs.casino.promotion;

import com.cs.bonus.Bonus;
import com.cs.casino.bonus.BonusDto;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static javax.xml.bind.annotation.XmlAccessType.FIELD;

/**
 * @author Hadi Movaghar
 */
@XmlRootElement
@XmlAccessorType(FIELD)
public class PromotionDto {
    @XmlElement
    @Nonnull
    @NotNull(message = "promotionDto.name.notNull")
    private String name;

    @XmlElement
    @Nonnull
    @NotNull(message = "promotionDto.validTo.notNull")
    private Date validTo;

    @XmlElement
    @Nullable
    private Date activationDate;

    @XmlElement
    @Nullable
    private List<BonusDto> bonusList;

    public PromotionDto() {
    }

    public void setName(@Nonnull final String name) {
        this.name = name;
    }

    public void setValidTo(@Nonnull final Date validTo) {
        this.validTo = validTo;
    }

    public void setActivationDate(@Nonnull final Date activationDate) {
        this.activationDate = activationDate;
    }

    public void setBonusList(@Nullable final List<Bonus> bonusList) {
        final List<BonusDto> bonusDtos = new ArrayList<>();
        if (bonusList != null) {
            for (final Bonus bonus : bonusList) {
                final BonusDto bonusDto = new BonusDto();
                bonusDto.setName(bonus.getName());
                bonusDto.setValidTo(bonus.getValidTo());
                bonusDto.setAmount(bonus.getAmount() != null ? bonus.getAmount().getEuroValueInBigDecimal() : null);
                bonusDto.setQuantity(bonus.getQuantity());
    //            bonusDto.setUsedDate(bonus.get); TODO
                bonusDtos.add(bonusDto);
            }
        }
        this.bonusList = bonusDtos;
    }
}
