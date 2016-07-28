package com.cs.audit;

import com.cs.player.Player;

import com.google.common.base.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.validation.Valid;
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
@Table(name = "player_activities")
public class PlayerActivity implements Serializable {
    private static final long serialVersionUID = 3L;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    @Nonnull
    private Long id;

    @Column(name = "level")
    @Nonnull
    private Long level;

    @OneToOne
    @JoinColumn(name = "player_id", nullable = false)
    @Nonnull
    @Valid
    private Player player;

    @Column(name = "activity_type", nullable = false)
    @Enumerated(STRING)
    @Nonnull
    @NotNull(message = "playerActivity.activityType.notNull")
    private PlayerActivityType activity;

    @Column(name = "activity_date", nullable = false)
    @Temporal(TIMESTAMP)
    @Nonnull
    @NotNull(message = "playerActivity.activityDate..notNull")
    private Date activityDate;

    @Column(name = "session_id")
    @Nullable
    private String sessionId;

    @Column(name = "ip_address")
    @Nullable
    private String ipAddress;

    @Column(name = "description")
    @Nullable
    private String description;

    public PlayerActivity() {
    }

    public PlayerActivity(@Nonnull final Player player, @Nonnull final PlayerActivityType activity, @Nonnull final Date activityDate) {
        this.player = player;
        this.activity = activity;
        this.activityDate = activityDate;
        level = player.getLevel().getLevel();
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
    public PlayerActivityType getActivity() {
        return activity;
    }

    public void setActivity(@Nonnull final PlayerActivityType activity) {
        this.activity = activity;
    }

    @Nonnull
    public Date getActivityDate() {
        return activityDate;
    }

    public void setActivityDate(@Nonnull final Date activityDate) {
        this.activityDate = activityDate;
    }

    @Nullable
    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(@Nullable final String sessionId) {
        this.sessionId = sessionId;
    }

    @Nullable
    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(@Nullable final String ipAddress) {
        this.ipAddress = ipAddress;
    }

    @Nullable
    public String getDescription() {
        return description;
    }

    public void setDescription(@Nullable final String description) {
        this.description = description;
    }


    public void setLevel(@Nonnull final Long level) {
        this.level = level;
    }

    @Nonnull
    public Long getLevel() {
        return level;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final PlayerActivity that = (PlayerActivity) o;

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
                .addValue(player)
                .addValue(activity)
                .addValue(activityDate)
                .addValue(sessionId)
                .addValue(ipAddress)
                .addValue(description)
                .toString();
    }
}
