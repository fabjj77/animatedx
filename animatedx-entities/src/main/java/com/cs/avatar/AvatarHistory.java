package com.cs.avatar;

import com.cs.player.Player;

import com.google.common.base.Objects;

import javax.annotation.Nonnull;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

import static javax.persistence.GenerationType.IDENTITY;
import static javax.persistence.TemporalType.TIMESTAMP;

/**
 * @author Omid Alaepour
 */
@Entity
@Table(name = "avatar_histories")
public class AvatarHistory implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "player_id", referencedColumnName = "id")
    @Nonnull
    private Player player;

    @OneToOne
    @JoinColumn(name = "avatar_id", referencedColumnName = "id")
    @Nonnull
    private Avatar avatar;

    @Column(name = "created_date", nullable = false)
    @Temporal(TIMESTAMP)
    @Nonnull
    @NotNull(message = "avatar.createdDate.notNull")
    private Date createdDate;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
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
    public Avatar getAvatar() {
        return avatar;
    }

    public void setAvatar(@Nonnull final Avatar avatar) {
        this.avatar = avatar;
    }

    @Nonnull
    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(@Nonnull final Date createdDate) {
        this.createdDate = createdDate;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final AvatarHistory that = (AvatarHistory) o;

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
                .addValue(avatar != null ? avatar.getId() : null)
                .addValue(createdDate)
                .toString();
    }
}
