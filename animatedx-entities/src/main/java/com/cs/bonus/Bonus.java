package com.cs.bonus;

import com.cs.item.Item;
import com.cs.payment.Currency;
import com.cs.payment.Money;
import com.cs.payment.MoneyConverter;
import com.cs.player.Player;
import com.cs.promotion.Criteria;
import com.cs.promotion.Promotion;
import com.cs.user.User;

import com.google.common.base.Objects;
import org.hibernate.annotations.Type;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Converts;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

import static javax.persistence.EnumType.STRING;
import static javax.persistence.GenerationType.IDENTITY;
import static javax.persistence.TemporalType.TIMESTAMP;

/**
 * @author Hadi Movaghar
 */
@Entity
@Table(name = "bonuses")
@Converts(value = {
        @Convert(attributeName = "amount", converter = MoneyConverter.class),
        @Convert(attributeName = "maxAmount", converter = MoneyConverter.class),
        @Convert(attributeName = "maxRedemptionAmount", converter = MoneyConverter.class)
})
public class Bonus implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final Long CUSTOMER_SUPPORT_ADJUSTABLE_BONUS_MONEY = 127L;
    public static final Long CREDITS_TO_BONUS_MONEY_CONVERSION_BONUS = 128L;
    public static final Long FREE_ROUNDS_COMPLETION_BONUS = 141L;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    @Nonnull
    private Long id;

    @Column(name = "name", nullable = false)
    @Nonnull
    @NotNull(message = "bonus.name.notNull")
    private String name;

    @Column(name = "valid_from", nullable = false)
    @Temporal(TIMESTAMP)
    @Nonnull
    @NotNull(message = "bonus.validFrom.notNull")
    private Date validFrom;

    @Column(name = "valid_to", nullable = false)
    @Temporal(TIMESTAMP)
    @Nonnull
    @NotNull(message = "bonus.validFrom.notNull")
    private Date validTo;

    @Column(name = "type", nullable = false)
    @Enumerated(STRING)
    @Nonnull
    @NotNull(message = "bonus.bonusType.notNull")
    private BonusType bonusType;

    @Column(name = "bonus_code", nullable = false)
    @Nullable
    private String bonusCode;

    @Column(name = "netent_bonus_code")
    @Nullable
    private String netEntBonusCode;

    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false)
    @Nonnull
    @NotNull(message = "bonus.createdBy.notNull")
    private User createdBy;

    @Column(name = "created_date", nullable = false)
    @Temporal(TIMESTAMP)
    @Nonnull
    @NotNull(message = "bonus.createdDate.notNull")
    private Date createdDate;

    @ManyToOne
    @JoinColumn(name = "modified_by")
    @Nullable
    private User modifiedBy;

    @Column(name = "modified_date")
    @Temporal(TIMESTAMP)
    @Nullable
    private Date modifiedDate;

    @ManyToOne
    @JoinColumn(name = "promotion_id", nullable = false)
    @Nonnull
    private Promotion promotion;

    @Column(name = "amount")
    @Nullable
    private Money amount;

    @Column(name = "maximum_amount")
    @Nullable
    private Money maxAmount;

    @Column(name = "quantity")
    @Nullable
    private Integer quantity;

    @Column(name = "percentage")
    @Nullable
    private Integer percentage;

    @Column(name = "currency", length = 3)
    @Enumerated(STRING)
    @Nullable
    private Currency currency;

    @Column(name = "max_grant_numbers")
    @Nullable
    private Integer maxGrantNumbers;

    @Column(name = "max_redemption_amount")
    @Nullable
    private Money maxRedemptionAmount;

    @Column(name = "bonus_group")
    @Nullable
    private Integer bonusGroup;

    @Column(name = "initial_grant_delay")
    @Nullable
    private Integer initialGrantDelayHours;

    @Column(name = "next_grant_interval")
    @Nullable
    private Integer nextGrantIntervalHours;

    @Embedded
    @Nullable
    private Criteria criteria;

    @OneToOne(mappedBy = "bonus")
    @Nullable
    private Item item;

    @Column(name = "auto_grant_next", nullable = false)
    @Type(type = "org.hibernate.type.NumericBooleanType")
    @Nonnull
    private Boolean nextAutoGrant;

    @Column(name = "netent_bonus_id")
    @Nullable
    private Long netentBonusId;

    // Field is hardcoded for now, will be converted to a column later
    @Transient
    @Nullable
    private Integer wagerTimes = 38;

    public Bonus() {
    }

    @Nonnull
    public Long getId() {
        return id;
    }

    public void setId(@Nonnull final Long id) {
        this.id = id;
    }

    @Nonnull
    public String getName() {
        return name;
    }

    public void setName(@Nonnull final String name) {
        this.name = name;
    }

    @Nonnull
    public Date getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(@Nonnull final Date validFrom) {
        this.validFrom = validFrom;
    }

    @Nonnull
    public Date getValidTo() {
        return validTo;
    }

    public void setValidTo(@Nonnull final Date validTo) {
        this.validTo = validTo;
    }

    @Nonnull
    public BonusType getBonusType() {
        return bonusType;
    }

    public void setBonusType(@Nonnull final BonusType bonusType) {
        this.bonusType = bonusType;
    }

    @Nullable
    public String getBonusCode() {
        return bonusCode;
    }

    public void setBonusCode(@Nullable final String bonusCode) {
        this.bonusCode = bonusCode;
    }

    @Nullable
    public String getNetEntBonusCode() {
        return netEntBonusCode;
    }

    public void setNetEntBonusCode(@Nullable final String netEntBonusCode) {
        this.netEntBonusCode = netEntBonusCode;
    }

    @Nonnull
    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(@Nonnull final User createdBy) {
        this.createdBy = createdBy;
    }

    @Nonnull
    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(@Nonnull final Date createdDate) {
        this.createdDate = createdDate;
    }

    @Nullable
    public User getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(@Nullable final User modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    @Nullable
    public Date getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(@Nullable final Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    @Nonnull
    public Promotion getPromotion() {
        return promotion;
    }

    public void setPromotion(@Nonnull final Promotion promotion) {
        this.promotion = promotion;
    }

    @Nullable
    public Money getAmount() {
        return amount;
    }

    public void setAmount(@Nullable final Money amount) {
        this.amount = amount;
    }

    @Nullable
    public Money getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(@Nullable final Money maxAmount) {
        this.maxAmount = maxAmount;
    }

    @Nullable
    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(@Nullable final Integer quantity) {
        this.quantity = quantity;
    }

    @Nullable
    public Integer getPercentage() {
        return percentage;
    }

    public void setPercentage(@Nullable final Integer percentage) {
        this.percentage = percentage;
    }

    @Nullable
    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(@Nullable final Currency currency) {
        this.currency = currency;
    }

    @Nullable
    public Integer getMaxGrantNumbers() {
        return maxGrantNumbers;
    }

    public void setMaxGrantNumbers(@Nullable final Integer maxGrantNumbers) {
        this.maxGrantNumbers = maxGrantNumbers;
    }

    @Nullable
    public Money getMaxRedemptionAmount() {
        return maxRedemptionAmount;
    }

    public void setMaxRedemptionAmount(@Nullable final Money maxRedemptionAmount) {
        this.maxRedemptionAmount = maxRedemptionAmount;
    }

    @Nullable
    public Criteria getCriteria() {
        return criteria;
    }

    public void setCriteria(@Nullable final Criteria criteria) {
        this.criteria = criteria;
    }

    @Nullable
    public Integer getBonusGroup() {
        return bonusGroup;
    }

    public void setBonusGroup(@Nullable final Integer bonusGroup) {
        this.bonusGroup = bonusGroup;
    }

    @Nullable
    public Integer getInitialGrantDelayHours() {
        return initialGrantDelayHours;
    }

    public void setInitialGrantDelayHours(@Nullable final Integer initialGrantDelayHours) {
        this.initialGrantDelayHours = initialGrantDelayHours;
    }

    @Nullable
    public Integer getNextGrantIntervalHours() {
        return nextGrantIntervalHours;
    }

    public void setNextGrantIntervalHours(@Nullable final Integer nextGrantIntervalHours) {
        this.nextGrantIntervalHours = nextGrantIntervalHours;
    }

    @Nullable
    public Item getItem() {
        return item;
    }

    public void setItem(@Nullable final Item item) {
        this.item = item;
    }

    @Nonnull
    public Boolean isAutoGrant() {
        return nextAutoGrant;
    }

    public void setNextAutoGrant(@Nonnull final Boolean autoGrant) {
        nextAutoGrant = autoGrant;
    }

    @Nullable
    public Long getNetentBonusId() {
        return netentBonusId;
    }

    public void setNetentBonusId(@Nullable final Long netentBonusId) {
        this.netentBonusId = netentBonusId;
    }

    @Nullable
    public Integer getWagerTimes() {
        return wagerTimes;
    }

    public boolean isItem() {
        return item != null;
    }

    public boolean isBonusPeriodValidAgainstPromotion(final Promotion promotion) {
        return validFrom.after(promotion.getValidFrom()) && validTo.before(promotion.getValidTo());
    }

    public boolean isBonusPeriodValidForUsage() {
        return validFrom.before(new Date()) && validTo.after(new Date());
    }

    public boolean isLevelDepositBonus(final Player player) {
        return player.getLevel().getDepositBonus() != null && player.getLevel().getDepositBonus().getId().equals(id);
    }

    public boolean isExpired() {
        return validTo.before(new Date());
    }

    @SuppressWarnings("ConstantConditions")
    public boolean updateRunningBonusFromBonus(final Bonus bonus) {
        boolean updated = false;

        // TODO which fields to be able to update

        if (bonus.amount != null) {
            amount = bonus.amount;
            updated = true;
        }

        if (bonus.maxAmount != null) {
            maxAmount = bonus.maxAmount;
            updated = true;
        }

        if (bonus.percentage != null) {
            percentage = bonus.percentage;
            updated = true;
        }

        if (bonus.quantity != null) {
            quantity = bonus.quantity;
            updated = true;
        }

        if (bonus.maxGrantNumbers != null) {
            maxGrantNumbers = bonus.maxGrantNumbers;
            updated = true;
        }

        if (bonus.netEntBonusCode != null) {
            netEntBonusCode = bonus.netEntBonusCode;
            updated = true;
        }

        if (bonus.validTo != null && promotion.getValidTo().after(bonus.validTo) && validFrom.before(bonus.validTo)) {
            validTo = bonus.validTo;
            updated = true;
        }

        if (bonus.name != null) {
            name = bonus.name;
            updated = true;
        }

        if (bonus.currency != null) {
            currency = bonus.currency;
            updated = true;
        }

        return updated;
    }

    @SuppressWarnings("ConstantConditions")
    public boolean updateFutureBonusFromBonus(final Bonus bonus, final Promotion promotion) {
        boolean updated = false;

        // TODO which fields to update?
        if (bonus.validFrom != null && bonus.validFrom.after(new Date()) && promotion.getValidFrom().before(bonus.getValidFrom())) {
            validFrom = bonus.validFrom;
            updated = true;
        }

        if (!promotion.equals(this.promotion) && isBonusPeriodValidAgainstPromotion(promotion)) {
            this.promotion = promotion;
            updated = true;
        }

        updated = updated || updateRunningBonusFromBonus(bonus);

        return updated;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final Bonus that = (Bonus) o;

        return Objects.equal(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .addValue(id)
                .addValue(name)
                .addValue(validFrom)
                .addValue(validTo)
                .addValue(bonusType)
                .addValue(bonusCode)
                .addValue(netEntBonusCode)
                .addValue(createdBy.getId())
                .addValue(createdDate)
                .addValue(modifiedBy != null ? modifiedBy.getId() : null)
                .addValue(modifiedDate)
                .addValue(promotion.getId())
                .addValue(amount)
                .addValue(maxAmount)
                .addValue(currency)
                .addValue(percentage)
                .addValue(quantity)
                .addValue(maxGrantNumbers)
                .addValue(maxRedemptionAmount)
                .addValue(nextAutoGrant)
                .addValue(bonusGroup)
                .addValue(initialGrantDelayHours)
                .addValue(nextGrantIntervalHours)
                .addValue(criteria)
                .addValue(item != null ? item.getId() : null)
                .addValue(netentBonusId)
                .toString();
    }
}
