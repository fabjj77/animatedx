package com.cs.administration.promotion;

import com.cs.administration.bonus.BonusDto;
import com.cs.bonus.Bonus;
import com.cs.promotion.Promotion;
import com.cs.promotion.PromotionTrigger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;

import static javax.xml.bind.annotation.XmlAccessType.FIELD;

/**
 * @author Hadi Movaghar
 */
@XmlRootElement
@XmlAccessorType(FIELD)
public class PromotionDto {
    @XmlElement(nillable = true, required = true)
    @Nullable
    private Long id;

    @XmlElement
    @Nonnull
    private Date validFrom;

    @XmlElement
    @Nonnull
    private Date validTo;

    @XmlElement
    @Nonnull
    @NotNull(message = "promotionDto.name.notNull")
    private String name;

    @XmlElement
    @Nullable
    private Date createdDate;

    @XmlElement
    @Nonnull
    private List<PromotionTrigger> promotionTriggers;

    @XmlElement
    @Nonnull
    @NotNull(message = "promotionDto.level.notNull")
    private Long level;

    @XmlElement
    @Nullable
    private List<BonusDto> bonusList;

    @SuppressWarnings("UnusedDeclaration")
    public PromotionDto() {
    }

    public PromotionDto(final Promotion promotion) {
        id = promotion.getId();
        validFrom = promotion.getValidFrom();
        validTo = promotion.getValidTo();
        name = promotion.getName();
        createdDate = promotion.getCreatedDate();
        promotionTriggers = new ArrayList<>(promotion.getPromotionTriggers());
        if (promotion.getBonusList() != null) {
            bonusList = getBonusList(promotion.getBonusList());
        }
        level = promotion.getLevel().getLevel();
    }

    public Promotion asPromotion() {
        final Promotion promotion = new Promotion();
        promotion.setValidFrom(validFrom);
        promotion.setValidTo(validTo);
        promotion.setName(name);
        promotion.setPromotionTriggers(EnumSet.copyOf(promotionTriggers));
        return promotion;
    }

    private List<BonusDto> getBonusList(final List<Bonus> bonuses) {
        final List<BonusDto> bonusDtoList = new ArrayList<>();
        for (final Bonus bonus : bonuses) {
            final BonusDto bonusDto = new BonusDto(bonus);
            bonusDtoList.add(bonusDto);
        }
        return bonusDtoList;
    }

    @Nullable
    public Long getId() {
        return id;
    }

    @Nonnull
    public String getName() {
        return name;
    }

    @Nonnull
    public Long getLevel() {
        return level;
    }
}
