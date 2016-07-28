package com.cs.administration.bonus;

import com.cs.bonus.Bonus;
import com.cs.bonus.BonusType;
import com.cs.payment.Currency;
import com.cs.payment.Money;

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
 * @author Hadi Movaghar
 */
@XmlRootElement
@XmlAccessorType(FIELD)
public class UpdateBonusDto {
    @XmlElement(required = true)
    @Nonnull
    @NotNull(message = "updateBonusDto.id.notNull")
    private Long id;

    @XmlElement
    @Nullable
    private String name;

    @XmlElement
    @Nullable
    private Date validFrom;

    @XmlElement
    @Nullable
    private Date validTo;

    @XmlElement
    @Nullable
    private Integer maxGrantNumbers;

    @XmlElement
    @Nullable
    private BonusType bonusType;

    @XmlElement
    @Nullable
    private String netEntBonusCode;

    @XmlElement
    @Nonnull
    @NotNull(message = "updateBonusDto.promotionId.notNull")
    private Long promotionId;

    @XmlElement
    @Nullable
    private BigDecimal amount;

    @XmlElement
    @Nullable
    private BigDecimal maxAmount;

    @XmlElement
    @Nullable
    private Integer quantity;

    @XmlElement
    @Nullable
    private Currency currency;

    public UpdateBonusDto() {
    }

    public Bonus asBonus() {
        final Bonus bonus = new Bonus();
        bonus.setId(id);
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
        bonus.setQuantity(quantity);
        bonus.setCurrency(currency);
        return bonus;
    }

    @Nonnull
    public Long getPromotionId() {
        return promotionId;
    }
}
