package com.cs.payment;

import com.cs.bonus.PlayerBonus;
import com.cs.player.Player;
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
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Date;

import static com.cs.validation.MoneyAmount.Mode.NON_NEGATIVE;
import static javax.persistence.EnumType.STRING;
import static javax.persistence.GenerationType.IDENTITY;
import static javax.persistence.TemporalType.TIMESTAMP;

/**
 * @author Omid Alaepour
 */
@Entity
@Table(name = "credit_transactions")
@Converts(value = {
        @Convert(attributeName = "bonusMoney", converter = MoneyConverter.class),
        @Convert(attributeName = "realMoney", converter = MoneyConverter.class)
})
public class CreditTransaction {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Nonnull
    private Long id;

    @OneToOne
    @JoinColumn(name = "player_id", nullable = false)
    @Nonnull
    @Valid
    private Player player;

    @Column(name = "credit", nullable = false)
    @Min(value = 0, message = "creditTransaction.credit.min")
    @Nonnull
    private Integer credit;

    @Column(name = "bonus_money")
    @MoneyAmount(value = NON_NEGATIVE, message = "creditTransaction.bonusMoney.moneyAmount")
    @Nonnull
    private Money bonusMoney;

    @Column(name = "real_money")
    @MoneyAmount(value = NON_NEGATIVE, message = "creditTransaction.realMoney.moneyAmount")
    @Nonnull
    private Money realMoney;

    @Column(name = "currency", nullable = false)
    @Enumerated(STRING)
    @Nonnull
    @NotNull(message = "creditTransaction.currency.notNull")
    private Currency currency;

    @Column(name = "created_date", nullable = false)
    @Temporal(TIMESTAMP)
    @Nonnull
    @NotNull(message = "creditTransaction.createdBy.notNull")
    private Date createdDate;

    @OneToOne
    @JoinColumn(name = "user_id")
    @Nonnull
    @Valid
    private User user;

    @Column(name = "level", nullable = false)
    @Nonnull
    private Long level;

    @Column(name = "money_credit_rate")
    @Nullable
    private Double moneyCreditRate;

    @Column(name = "bonus_credit_rate")
    @Nullable
    private Double bonusCreditRate;

    @OneToOne
    @JoinColumn(name = "player_bonus_id")
    @Nullable
    private PlayerBonus playerBonus;

    @Column(name = "transaction_type", nullable = false)
    @Nonnull
    @Enumerated(STRING)
    private CreditTransactionType creditTransactionType;

    @Nonnull
    public Long getId() {
        return id;
    }

    public void setId(@Nonnull final Long id) {
        this.id = id;
    }

    @Nonnull
    public Player getPlayer() {
        return player;
    }

    public void setPlayer(@Nonnull final Player player) {
        this.player = player;
    }

    @Nonnull
    public Integer getCredit() {
        return credit;
    }

    public void setCredit(@Nonnull final Integer credit) {
        this.credit = credit;
    }

    @Nonnull
    public Money getBonusMoney() {
        return bonusMoney;
    }

    public void setBonusMoney(@Nonnull final Money bonusMoney) {
        this.bonusMoney = bonusMoney;
    }

    @Nonnull
    public Money getRealMoney() {
        return realMoney;
    }

    public void setRealMoney(@Nonnull final Money realMoney) {
        this.realMoney = realMoney;
    }

    @Nonnull
    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(@Nonnull final Currency currency) {
        this.currency = currency;
    }

    @Nonnull
    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(@Nonnull final Date createdDate) {
        this.createdDate = createdDate;
    }

    @Nonnull
    public User getUser() {
        return user;
    }

    public void setUser(@Nonnull final User user) {
        this.user = user;
    }

    @Nonnull
    public Long getLevel() {
        return level;
    }

    public void setLevel(@Nonnull final Long level) {
        this.level = level;
    }

    @Nullable
    public Double getMoneyCreditRate() {
        return moneyCreditRate;
    }

    public void setMoneyCreditRate(@Nullable final Double moneyCreditRate) {
        this.moneyCreditRate = moneyCreditRate;
    }

    @Nullable
    public Double getBonusCreditRate() {
        return bonusCreditRate;
    }

    public void setBonusCreditRate(@Nullable final Double bonusCreditRate) {
        this.bonusCreditRate = bonusCreditRate;
    }

    @Nullable
    public PlayerBonus getPlayerBonus() {
        return playerBonus;
    }

    public void setPlayerBonus(@Nullable final PlayerBonus playerBonus) {
        this.playerBonus = playerBonus;
    }

    @Nonnull
    public CreditTransactionType getCreditTransactionType() {
        return creditTransactionType;
    }

    public void setCreditTransactionType(@Nonnull final CreditTransactionType creditTransactionType) {
        this.creditTransactionType = creditTransactionType;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final CreditTransaction that = (CreditTransaction) o;

        return Objects.equal(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .addValue(id)
                .addValue(player != null ? player.getId() : null)
                .addValue(credit)
                .addValue(bonusMoney)
                .addValue(realMoney)
                .addValue(currency)
                .addValue(createdDate)
                .addValue(level)
                .addValue(moneyCreditRate)
                .addValue(bonusCreditRate)
                .addValue(playerBonus != null ? playerBonus.getId() : null)
                .addValue(user != null ? user.getId() : null)
                .addValue(creditTransactionType)
                .toString();
    }
}
