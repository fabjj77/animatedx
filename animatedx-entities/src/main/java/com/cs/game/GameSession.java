package com.cs.game;

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
@Table(name = "game_sessions")
public class GameSession implements Serializable {
    private static final long serialVersionUID = 3L;

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

    @Column(name = "session_id")
    @Nullable
    private String sessionId;

    @Column(name = "start_date", nullable = false)
    @Temporal(TIMESTAMP)
    @Nonnull
    @NotNull(message = "gameSession.startDate.notNull")
    private Date startDate;

    @Column(name = "end_date")
    @Temporal(TIMESTAMP)
    @Nullable
    private Date endDate;

    @Column(name = "session_length")
    @Nonnull
    private Integer sessionLength;

    @Column(name = "channel", nullable = false)
    @Enumerated(STRING)
    @Nullable
    private Channel channel;

    public GameSession() {
        sessionLength = 0;
    }

    public GameSession(@Nonnull final Player player, @Nonnull final Date startDate) {
        this();
        this.player = player;
        this.startDate = startDate;
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

    @Nullable
    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(@Nullable final String sessionId) {
        this.sessionId = sessionId;
    }

    @Nonnull
    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(@Nonnull final Date startDate) {
        this.startDate = startDate;
    }

    @Nullable
    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(@Nullable final Date endDate) {
        this.endDate = endDate;
    }

    @Nonnull
    public Integer getSessionLength() {
        return sessionLength;
    }

    public void setSessionLength(@Nonnull final Integer sessionLength) {
        this.sessionLength = sessionLength;
    }

    @Nullable
    public Channel getChannel() {
        return channel;
    }

    public void setChannel(@Nullable final Channel channel) {
        this.channel = channel;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final GameSession that = (GameSession) o;

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
                .addValue(sessionId)
                .addValue(startDate)
                .addValue(endDate)
                .addValue(sessionLength)
                .addValue(channel)
                .toString();
    }
}
