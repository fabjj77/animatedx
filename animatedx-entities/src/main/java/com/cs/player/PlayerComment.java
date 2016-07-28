package com.cs.player;

import com.cs.user.User;

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
import java.io.Serializable;
import java.util.Date;

import static javax.persistence.GenerationType.IDENTITY;
import static javax.persistence.TemporalType.TIMESTAMP;

/**
 * @author Hadi Movaghar
 */
@Entity
@Table(name = "player_comments")
public class PlayerComment implements Serializable {
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

    @Column(name = "created_date", nullable = false)
    @Temporal(TIMESTAMP)
    @Nonnull
    private Date createdDate;

    @Column(name = "comment", nullable = false)
    @Nonnull
    private String comment;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    @Nonnull
    private User user;

    public PlayerComment(@Nonnull final Player player, @Nonnull final Date createdDate, @Nonnull final String comment, @Nonnull final User user) {
        this.player = player;
        this.createdDate = createdDate;
        this.comment = comment;
        this.user = user;
    }

    public PlayerComment() {}

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
    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(@Nonnull final Date createdDate) {
        this.createdDate = createdDate;
    }

    @Nonnull
    public String getComment() {
        return comment;
    }

    public void setComment(@Nonnull final String comment) {
        this.comment = comment;
    }

    @Nonnull
    public User getUser() {
        return user;
    }

    public void setUser(@Nonnull final User user) {
        this.user = user;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final PlayerComment that = (PlayerComment) o;

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
                .addValue(createdDate)
                .addValue(comment)
                .addValue(user.getId())
                .toString();
    }
}
