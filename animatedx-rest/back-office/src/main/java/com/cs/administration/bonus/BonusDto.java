package com.cs.administration.bonus;

import com.cs.administration.promotion.CriteriaDto;
import com.cs.bonus.Bonus;
import com.cs.bonus.BonusType;
import com.cs.bonus.PlayerBonus;
import com.cs.payment.Currency;
import com.cs.payment.Money;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.validation.Valid;
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
    @Nullable
    private Integer maxGrantNumbers;

    @XmlElement
    @Nonnull
    @NotNull(message = "bonusDto.bonusType.notNull")
    private BonusType bonusType;

    @XmlElement
    @Nullable
    private String netEntBonusCode;

    @XmlElement
    @Nullable
    private Date createdDate;

    @XmlElement
    @Nonnull
    @NotNull(message = "bonusDto.promotionId.notNull")
    private Long promotionId;

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

    @XmlElement
    @Nonnull
    @Valid
    private CriteriaDto criteria;

    @XmlElement
    @Nullable
    private Date usedDate;

    @XmlElement
    @Nullable
    private BigDecimal usedAmount;

    public BonusDto() {
    }

    public BonusDto(final Bonus bonus) {
        id = bonus.getId();
        name = bonus.getName();
        validFrom = bonus.getValidFrom();
        validTo = bonus.getValidTo();
        maxGrantNumbers = bonus.getMaxGrantNumbers();
        bonusType = bonus.getBonusType();
        netEntBonusCode = bonus.getNetEntBonusCode();
        createdDate = bonus.getCreatedDate();
        promotionId = bonus.getPromotion().getId();
        amount = bonus.getAmount() != null ? bonus.getAmount().getEuroValueInBigDecimal() : null;
        maxAmount = bonus.getMaxAmount() != null ? bonus.getMaxAmount().getEuroValueInBigDecimal() : null;
        quantity = bonus.getQuantity();
        percentage = bonus.getPercentage();
        currency = bonus.getCurrency();
        criteria = bonus.getCriteria() != null ? new CriteriaDto(bonus.getCriteria()) : new CriteriaDto();
    }

    public BonusDto(final PlayerBonus playerBonus) {
        this(playerBonus.getPk().getBonus());
        usedDate = playerBonus.getUsedDate();
        usedAmount = playerBonus.getUsedAmount() != null? playerBonus.getUsedAmount().getEuroValueInBigDecimal() : null;
    }

    public Bonus asBonus() {
        final Bonus bonus = new Bonus();
        bonus.setName(name);
        bonus.setValidFrom(validFrom);
        bonus.setValidTo(validTo);
        bonus.setMaxGrantNumbers(maxGrantNumbers);
        bonus.setNetEntBonusCode(netEntBonusCode);
        bonus.setBonusType(bonusType);
        final Money money = amount != null ? new Money(amount) : null;
        final Money maxMoney = maxAmount != null ? new Money(maxAmount) : null;
        bonus.setAmount(money);
        bonus.setMaxAmount(maxMoney);
        bonus.setPercentage(percentage);
        bonus.setQuantity(quantity);
        bonus.setCurrency(currency);
        bonus.setCriteria(criteria.asCriteria());
        return bonus;
    }

    public static List<BonusDto> getBonusDtoList(final List<PlayerBonus> playerBonuses) {
        final List<BonusDto> bonusList = new ArrayList<>();
        for (final PlayerBonus playerBonus : playerBonuses) {
            bonusList.add(new BonusDto(playerBonus));
        }
        return bonusList;
    }

    public static BonusDto getBonusDto(final PlayerBonus playerBonus) {
        return new BonusDto(playerBonus);
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
    public Long getPromotionId() {
        return promotionId;
    }

    @Nonnull
    public CriteriaDto getCriteria() {
        return criteria;
    }
}
