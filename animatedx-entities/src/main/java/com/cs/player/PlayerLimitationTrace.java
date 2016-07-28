package com.cs.player;

import com.cs.payment.Money;
import com.cs.payment.MoneyConverter;
import com.cs.user.User;

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
import java.io.Serializable;
import java.util.Date;

import static com.cs.player.LimitationType.BET_AMOUNT;
import static com.cs.player.LimitationType.LOSS_AMOUNT;
import static javax.persistence.GenerationType.IDENTITY;
import static javax.persistence.TemporalType.TIMESTAMP;

/**
 * @author Hadi Movaghar
 */
@Entity
@Table(name = "player_limitation_traces")
@Converts(value = {@Convert(attributeName = "lossLimit", converter = MoneyConverter.class),
        @Convert(attributeName = "betLimit", converter = MoneyConverter.class)})
public class PlayerLimitationTrace implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    @Nonnull
    private Long id;

    @OneToOne
    @JoinColumn(name = "player_id", nullable = false)
    @Nonnull
    private Player player;

    @OneToOne
    @JoinColumn(name = "user_id")
    @Nullable
    private User user;

    @Column(name = "loss_limit")
    @Nullable
    private Money lossLimit;

    @Column(name = "loss_time_unit")
    @Enumerated(EnumType.STRING)
    @Nullable
    private TimeUnit lossTimeUnit;

    @Column(name = "bet_limit")
    @Nullable
    private Money betLimit;

    @Column(name = "bet_time_unit")
    @Enumerated(EnumType.STRING)
    @Nullable
    private TimeUnit betTimeUnit;

    @Column(name = "session_length")
    @Nullable
    private Integer sessionLength;

    @Column(name = "created_date", nullable = false)
    @Temporal(TIMESTAMP)
    @Nonnull
    private Date createdDate;

    @Column(name = "apply_date", nullable = false)
    @Temporal(TIMESTAMP)
    @Nonnull
    private Date applyDate;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    @Nonnull
    private LimitationStatus limitationStatus;

    public PlayerLimitationTrace() {
    }

    public PlayerLimitationTrace(@Nonnull final Player player, @Nonnull final Date createdDate, @Nonnull final Date applyDate,
                                 @Nonnull final LimitationStatus limitationStatus) {
        this.player = player;
        this.createdDate = createdDate;
        this.applyDate = applyDate;
        this.limitationStatus = limitationStatus;
    }

    public PlayerLimitationTrace(@Nonnull final Player player, @Nullable final User user, @Nonnull final Date createdDate, @Nonnull final Date applyDate,
                                 @Nonnull final LimitationStatus limitationStatus) {
        this(player, createdDate, applyDate, limitationStatus);
        this.user = user;
    }

    @Nonnull
    public Long getId() {
        return id;
    }

    @Nonnull
    public Player getPlayer() {
        return player;
    }

    public void setPlayer(@Nonnull final Player player) {
        this.player = player;
    }

    @Nullable
    public User getUser() {
        return user;
    }

    public void setUser(@Nullable final User user) {
        this.user = user;
    }

    @Nullable
    public Money getLossLimit() {
        return lossLimit;
    }

    public void setLossLimit(@Nullable final Money lossLimit) {
        this.lossLimit = lossLimit;
    }

    @Nullable
    public TimeUnit getLossTimeUnit() {
        return lossTimeUnit;
    }

    public void setLossTimeUnit(@Nullable final TimeUnit lossTimeUnit) {
        this.lossTimeUnit = lossTimeUnit;
    }

    @Nullable
    public Money getBetLimit() {
        return betLimit;
    }

    public void setBetLimit(@Nullable final Money betLimit) {
        this.betLimit = betLimit;
    }

    @Nullable
    public TimeUnit getBetTimeUnit() {
        return betTimeUnit;
    }

    public void setBetTimeUnit(@Nullable final TimeUnit betTimeUnit) {
        this.betTimeUnit = betTimeUnit;
    }

    @Nullable
    public Integer getSessionLength() {
        return sessionLength;
    }

    public void setSessionLength(@Nullable final Integer sessionLength) {
        this.sessionLength = sessionLength;
    }

    @Nonnull
    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(@Nonnull final Date createdDate) {
        this.createdDate = createdDate;
    }

    @Nonnull
    public Date getApplyDate() {
        return applyDate;
    }

    public void setApplyDate(@Nonnull final Date applyDate) {
        this.applyDate = applyDate;
    }

    @Nonnull
    public LimitationStatus getLimitationStatus() {
        return limitationStatus;
    }

    public void setLimitationStatus(@Nonnull final LimitationStatus limitationStatus) {
        this.limitationStatus = limitationStatus;
    }

    public void setLimitByLimitationType(final LimitationType limitationType, final Money amount) {
        if (limitationType == LOSS_AMOUNT) {
            lossLimit = amount;
        } else if (limitationType == BET_AMOUNT) {
            betLimit = amount;
        }
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

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final PlayerLimitationTrace that = (PlayerLimitationTrace) o;

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
                .addValue(player.getId())
                .addValue(user != null ? user.getId() : null)
                .addValue(lossLimit)
                .addValue(lossTimeUnit)
                .addValue(betLimit)
                .addValue(betTimeUnit)
                .addValue(sessionLength)
                .addValue(createdDate)
                .addValue(applyDate)
                .addValue(limitationStatus)
                .toString();
    }
}

