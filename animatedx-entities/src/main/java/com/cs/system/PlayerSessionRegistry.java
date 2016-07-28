package com.cs.system;

import com.cs.player.Player;

import com.google.common.base.Objects;
import org.hibernate.annotations.Type;

import javax.annotation.Nonnull;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import java.io.Serializable;
import java.util.Date;

import static javax.persistence.GenerationType.IDENTITY;
import static javax.persistence.TemporalType.TIMESTAMP;

/**
 * @author Hadi Movaghar
 */
@Entity
@Table(name = "players_session_registries")
public class PlayerSessionRegistry implements Serializable {

    private static final long serialVersionUID = 1L;
    public static final String REGISTRATION_SESSION_ID = "REGISTRATION SESSION ID";

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    @Nonnull
    private Long id;

    @OneToOne
    @JoinColumn(name = "player_id", nullable = false)
    @Nonnull
    private Player player;

    @Column(name = "session_id", nullable = false)
    @Nonnull
    private String sessionId;

    @Column(name = "last_request", nullable = false)
    @Temporal(TIMESTAMP)
    @Nonnull
    private Date lastRequest;

    @Column(name = "expired", nullable = false)
    @Type(type = "org.hibernate.type.NumericBooleanType")
    @Nonnull
    private Boolean expired;

    @Column(name = "active", nullable = false)
    @Type(type = "org.hibernate.type.NumericBooleanType")
    @Nonnull
    private Boolean active;

    @Column(name = "uuid", nullable = false)
    @Nonnull
    private String uuid;

    public PlayerSessionRegistry() {
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

    @Nonnull
    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(@Nonnull final String sessionId) {
        this.sessionId = sessionId;
    }

    @Nonnull
    public Date getLastRequest() {
        return lastRequest;
    }

    public void setLastRequest(@Nonnull final Date lastRequest) {
        this.lastRequest = lastRequest;
    }

    @Nonnull
    public Boolean isExpired() {
        return expired;
    }

    public void setExpired(@Nonnull final Boolean expired) {
        this.expired = expired;
    }

    @Nonnull
    public Boolean isActive() {
        return active;
    }

    public void setActive(@Nonnull final Boolean active) {
        this.active = active;
    }

    @Nonnull
    public String getUuid() {
        return uuid;
    }

    public void setUuid(@Nonnull final String uuid) {
        this.uuid = uuid;
    }

    public boolean isSessionValidForFirstDeposit(final String uuid) {
        return sessionId.equals(REGISTRATION_SESSION_ID) && this.uuid.equals(uuid) && active;
    }

    public boolean isSessionValidForPayment(final String uuid) {
        return this.uuid.equalsIgnoreCase(uuid) && active && !expired;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final PlayerSessionRegistry that = (PlayerSessionRegistry) o;

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
                .addValue(sessionId)
                .addValue(lastRequest)
                .addValue(expired)
                .addValue(active)
                .addValue(uuid)
                .toString();
    }
}
