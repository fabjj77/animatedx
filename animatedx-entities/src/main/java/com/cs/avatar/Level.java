package com.cs.avatar;

import com.cs.bonus.Bonus;
import com.cs.item.Item;
import com.cs.payment.Money;
import com.cs.payment.MoneyConverter;
import com.cs.persistence.Status;
import com.cs.user.User;
import com.cs.validation.MoneyAmount;

import com.google.common.base.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Converts;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import static com.cs.validation.MoneyAmount.Mode.NON_NEGATIVE;
import static javax.persistence.EnumType.STRING;
import static javax.persistence.TemporalType.TIMESTAMP;

/**
 * @author Joakim Gottzén
 */
@Entity
@Table(name = "levels")
@Converts(value = @Convert(attributeName = "turnover", converter = MoneyConverter.class))
public class Level implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "level", nullable = false, unique = true)
    @Nonnull
    private Long level;

    @Column(name = "turnover", nullable = false)
    @NotNull(message = "level.turnover.notNull")
    @MoneyAmount(value = NON_NEGATIVE, message = "level.turnover.moneyAmount")
    @Nonnull
    private Money turnover;

    @Column(name = "cashback_percentage", nullable = false)
    @Nonnull
    private BigDecimal cashbackPercentage;

    @Column(name = "deposit_bonus_percentage", nullable = false)
    @Nonnull
    private BigDecimal depositBonusPercentage;

    @Column(name = "credit_dices", nullable = false)
    @Nonnull
    private Short creditDices;

    @Column(name = "money_credit_rate", nullable = false)
    @Nonnull
    private Double moneyCreditRate;

    @Column(name = "bonus_credit_rate", nullable = false)
    @Nonnull
    private Double bonusCreditRate;

    @OneToOne(optional = true)
    @JoinColumn(name = "item_id", referencedColumnName = "id")
    @Nullable
    private Item item;

    @OneToOne(optional = true)
    @JoinColumn(name = "bonus_id", referencedColumnName = "id")
    @Nullable
    private Bonus depositBonus;

    @Column(name = "status", nullable = false)
    @Enumerated(STRING)
    @Nonnull
    @NotNull
    private Status status;

    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false)
    @Nonnull
    private User createdBy;

    @Column(name = "created_date", nullable = false)
    @Temporal(TIMESTAMP)
    @Nonnull
    private Date createdDate;

    @ManyToOne
    @JoinColumn(name = "modified_by")
    @Nullable
    private User modifiedBy;

    @Column(name = "modified_date")
    @Temporal(TIMESTAMP)
    @Nullable
    private Date modifiedDate;

    public Level() {}

    public Level(@Nonnull final Long level) {
        this.level = level;
    }

    @Nonnull
    public Long getLevel() {
        return level;
    }

    public void setLevel(@Nonnull final Long level) {
        this.level = level;
    }

    @Nonnull
    public Money getTurnover() {
        return turnover;
    }

    public void setTurnover(@Nonnull final Money turnover) {
        this.turnover = turnover;
    }

    @Nonnull
    public BigDecimal getCashbackPercentage() {
        return cashbackPercentage;
    }

    public void setCashbackPercentage(@Nonnull final BigDecimal cashbackPercentage) {
        this.cashbackPercentage = cashbackPercentage;
    }

    @Nonnull
    public BigDecimal getDepositBonusPercentage() {
        return depositBonusPercentage;
    }

    public void setDepositBonusPercentage(@Nonnull final BigDecimal depositBonusPercentage) {
        this.depositBonusPercentage = depositBonusPercentage;
    }

    @Nonnull
    public Short getCreditDices() {
        return creditDices;
    }

    public void setCreditDices(@Nonnull final Short creditDices) {
        this.creditDices = creditDices;
    }

    @Nullable
    public Item getItem() {
        return item;
    }

    public void setItem(@Nullable final Item item) {
        this.item = item;
    }

    @Nullable
    public Bonus getDepositBonus() {
        return depositBonus;
    }

    public void setDepositBonus(@Nullable final Bonus bonus) {
        depositBonus = bonus;
    }

    @Nonnull
    public Double getBonusCreditRate() {
        return bonusCreditRate;
    }

    public void setBonusCreditRate(@Nonnull final Double bonusCreditRate) {
        this.bonusCreditRate = bonusCreditRate;
    }

    @Nonnull
    public Double getMoneyCreditRate() {
        return moneyCreditRate;
    }

    public void setMoneyCreditRate(@Nonnull final Double moneyCreditRate) {
        this.moneyCreditRate = moneyCreditRate;
    }

    @Nonnull
    public Status getStatus() {
        return status;
    }

    public void setStatus(@Nonnull final Status status) {
        this.status = status;
    }

    @Nonnull
    public User getCreatedBy() {
        return createdBy;
    }

    @Nonnull
    public Date getCreatedDate() {
        return createdDate;
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

    public boolean hasDepositBonus() {
        return depositBonus != null;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final Level that = (Level) o;

        return Objects.equal(level, that.level);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(level);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .addValue(level)
                .addValue(turnover)
                .addValue(cashbackPercentage)
                .addValue(depositBonusPercentage)
                .addValue(creditDices)
                .addValue(moneyCreditRate)
                .addValue(bonusCreditRate)
                .addValue(item != null ? item.getId() : null)
                .addValue(depositBonus != null ? depositBonus.getId() : null)
                .addValue(status)
                .addValue(createdBy.getId())
                .addValue(createdDate)
                .addValue(modifiedBy != null ? modifiedBy.getId() : null)
                .addValue(modifiedDate)
                .toString();
    }
}
