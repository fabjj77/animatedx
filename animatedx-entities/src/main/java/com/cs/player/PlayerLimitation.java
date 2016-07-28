package com.cs.player;

import com.cs.payment.Money;
import com.cs.payment.MoneyConverter;
import com.cs.validation.MoneyAmount;

import com.google.common.base.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Converts;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.Transient;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import static com.cs.player.LimitationType.BET_AMOUNT;
import static com.cs.player.LimitationType.LOSS_AMOUNT;
import static com.cs.validation.MoneyAmount.Mode.NON_NEGATIVE;
import static javax.persistence.GenerationType.IDENTITY;
import static javax.persistence.TemporalType.TIMESTAMP;

/**
 * @author Hadi Movaghar
 */
@Entity
@Table(name = "player_limitations")
@Converts(value = {@Convert(attributeName = "lossLimit", converter = MoneyConverter.class),
        @Convert(attributeName = "betLimit", converter = MoneyConverter.class)})
public class PlayerLimitation implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    @Nonnull
    private Long id;

    @OneToOne
    @JoinColumn(name = "player_id", nullable = false)
    @Nonnull
    @Valid
    private Player player;

    @Column(name = "loss_limit", nullable = false)
    @NotNull(message = "playerLimitation.lossLimit.notNull")
    @MoneyAmount(value = NON_NEGATIVE, message = "playerLimitation.lossLimit.moneyAmount")
    @Nonnull
    private Money lossLimit;

    @Column(name = "loss_time_unit", nullable = false)
    @Enumerated(EnumType.STRING)
    @Nonnull
    private TimeUnit lossTimeUnit;

    @Column(name = "bet_limit", nullable = false)
    @NotNull(message = "playerLimitation.betLimit.notNull")
    @MoneyAmount(value = NON_NEGATIVE, message = "playerLimitation.betLimit.moneyAmount")
    @Nonnull
    private Money betLimit;

    @Column(name = "bet_time_unit", nullable = false)
    @Enumerated(EnumType.STRING)
    @Nonnull
    private TimeUnit betTimeUnit;

    @Column(name = "session_length", nullable = false)
    @Nonnull
    private Integer sessionLength;

    @Column(name = "modified_date")
    @Temporal(TIMESTAMP)
    @Nullable
    private Date modifiedDate;

    @Transient
    private Integer betPercentage;

    @Transient
    private Integer lossPercentage;

    public PlayerLimitation() {
    }

    public PlayerLimitation(@Nonnull final Player player, @Nonnull final Money lossLimit, @Nonnull final TimeUnit lossTimeUnit, @Nonnull final Money betLimit,
                            @Nonnull final TimeUnit betTimeUnit, @Nonnull final Integer sessionLength) {
        this.player = player;
        this.lossLimit = lossLimit;
        this.lossTimeUnit = lossTimeUnit;
        this.betLimit = betLimit;
        this.betTimeUnit = betTimeUnit;
        this.sessionLength = sessionLength;
    }

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
    public Money getLossLimit() {
        return lossLimit;
    }

    public void setLossLimit(@Nonnull final Money lossLimit) {
        this.lossLimit = lossLimit;
    }

    @Nonnull
    public TimeUnit getLossTimeUnit() {
        return lossTimeUnit;
    }

    public void setLossTimeUnit(@Nonnull final TimeUnit lossTimeUnit) {
        this.lossTimeUnit = lossTimeUnit;
    }

    @Nonnull
    public Money getBetLimit() {
        return betLimit;
    }

    public void setBetLimit(@Nonnull final Money betLimit) {
        this.betLimit = betLimit;
    }

    @Nonnull
    public TimeUnit getBetTimeUnit() {
        return betTimeUnit;
    }

    public void setBetTimeUnit(@Nonnull final TimeUnit betTimeUnit) {
        this.betTimeUnit = betTimeUnit;
    }

    @Nonnull
    public Integer getSessionLength() {
        return sessionLength;
    }

    public void setSessionLength(@Nonnull final Integer sessionLength) {
        this.sessionLength = sessionLength;
    }

    @Nullable
    public Date getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(@Nullable final Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public Integer getBetPercentage() {
        return betPercentage;
    }

    public void setBetPercentage(@Nullable final Integer betPercentage) {
        this.betPercentage = betPercentage;
    }

    public Integer getLossPercentage() {
        return lossPercentage;
    }

    public void setLossPercentage(@Nullable final Integer lossPercentage) {
        this.lossPercentage = lossPercentage;
    }

    public Money getLimitByLimitationType(final LimitationType limitationType) {
        if (limitationType == LimitationType.LOSS_AMOUNT) {
            return lossLimit;
        } else if (limitationType == LimitationType.BET_AMOUNT) {
            return betLimit;
        }
        throw new IllegalArgumentException("Unsupported LimitationType " + limitationType);
    }

    public void setLimitByLimitationType(final LimitationType limitationType, final Money amount) {
        if (limitationType == LOSS_AMOUNT) {
            lossLimit = amount;
        } else if (limitationType == BET_AMOUNT) {
            betLimit = amount;
        }
    }

    public TimeUnit getTimeUnitByLimitationType(final LimitationType limitationType) {
        if (limitationType == LOSS_AMOUNT) {
            return lossTimeUnit;
        } else if (limitationType == BET_AMOUNT) {
            return betTimeUnit;
        }
        throw new IllegalArgumentException("Unsupported LimitationType " + limitationType);
    }

    public void setTimeUnitByLimitationType(final LimitationType limitationType, final TimeUnit timeUnit) {
        if (limitationType == LOSS_AMOUNT) {
            lossTimeUnit = timeUnit;
        } else if (limitationType == BET_AMOUNT) {
            betTimeUnit = timeUnit;
        }
    }

    public void setLimit(final LimitationType limitationType, final TimeUnit timeUnit, final Money amount) {
        setLimitByLimitationType(limitationType, amount);
        setTimeUnitByLimitationType(limitationType, timeUnit);
    }

    public boolean isSessionLengthLimitHarsher(final Integer newLimit) {
        return sessionLength != 0 && sessionLength < newLimit;
    }

    public boolean isContainingDefaultValue(final LimitationType limitationType) {
        return getLimitByLimitationType(limitationType).isZero();
    }

    public boolean isSessionTimeOver(final Integer spentTime) {
        return sessionLength != 0 && spentTime >= sessionLength;
    }

    public boolean isCurrentLimitHarsher(final LimitationType limitationType, final TimeUnit newTimeUnit, final Money newLimit) {
        final BigDecimal timeUnitConversionValue = TimeUnit.getTimeUnitConversionValue(getTimeUnitByLimitationType(limitationType), newTimeUnit);
        final Money convertedAmount = getLimitByLimitationType(limitationType).multiply(timeUnitConversionValue);
        return !isContainingDefaultValue(limitationType) && (newLimit.isNegative() || newLimit.isGreaterThan(convertedAmount));
    }

    public Integer calculatePercentage(final LimitationType limitationType, final Money spentAmount) {
        return (int) (spentAmount.getCents() / getLimitByLimitationType(limitationType).doubleValue() * 100);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final PlayerLimitation that = (PlayerLimitation) o;

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
                .addValue(lossLimit)
                .addValue(lossTimeUnit)
                .addValue(betLimit)
                .addValue(betTimeUnit)
                .addValue(sessionLength)
                .addValue(modifiedDate)
                .toString();
    }
}
