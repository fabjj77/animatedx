package com.cs.game;

import com.cs.persistence.Status;
import com.cs.user.User;

import com.google.common.base.Objects;
import org.hibernate.annotations.Type;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

import static javax.persistence.EnumType.STRING;
import static javax.persistence.TemporalType.TIMESTAMP;

/**
 * @author Omid Alaepour
 */
@Entity
@Table(name = "netent_games")
public class Game implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "game_id", nullable = false, unique = true)
    @Nonnull
    private String gameId;

    @Column(name = "full_name", nullable = false)
    @Nonnull
    private String fullName;

    @Column(name = "name", nullable = false)
    @Nonnull
    private String name;

    @Column(name = "category", nullable = false)
    @Enumerated(STRING)
    @Nonnull
    private GameCategory category;

    @Column(name = "featured", nullable = false)
    @Type(type = "org.hibernate.type.NumericBooleanType")
    @Nonnull
    private Boolean featured;

    @Column(name = "slug", nullable = false)
    @Nonnull
    private String slug;

    @Column(name = "status", nullable = false)
    @Enumerated(STRING)
    @Nonnull
    @NotNull(message = "netEntGames.status.notNull")
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

    @OneToMany(mappedBy = "game")
    @Nonnull
    private List<GameInfo> gameInfos;

    public Game() {}

    public Game(@Nonnull final String gameId) {
        this.gameId = gameId;
    }

    @Nonnull
    public String getGameId() {
        return gameId;
    }

    public void setGameId(@Nonnull final String gameId) {
        this.gameId = gameId;
    }

    @Nonnull
    public String getFullName() {
        return fullName;
    }

    public void setFullName(@Nonnull final String fullName) {
        this.fullName = fullName;
    }

    @Nonnull
    public String getName() {
        return name;
    }

    public void setName(@Nonnull final String name) {
        this.name = name;
    }

    @Nonnull
    public GameCategory getCategory() {
        return category;
    }

    public void setCategory(@Nonnull final GameCategory category) {
        this.category = category;
    }

    @Nonnull
    public Boolean getFeatured() {
        return featured;
    }

    public void setFeatured(@Nonnull final Boolean featured) {
        this.featured = featured;
    }

    @Nonnull
    public String getSlug() {
        return slug;
    }

    public void setSlug(@Nonnull final String slug) {
        this.slug = slug;
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
    public List<GameInfo> getGameInfos() {
        return gameInfos;
    }

    public void setGameInfos(@Nonnull final List<GameInfo> gameInfos) {
        this.gameInfos = gameInfos;
    }

    @SuppressWarnings("ConstantConditions")
    public void updateFromGame(final Game game) {
        if (game.fullName != null) {
            fullName = game.fullName;
        }
        if (game.name != null) {
            name = game.name;
        }
        if (game.status != null) {
            status = game.status;
        }
        if (game.category != null) {
            category = game.category;
        }
        if (game.featured != null) {
            featured = game.featured;
        }
        if (game.slug != null) {
            slug = game.slug;
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final Game that = (Game) o;

        return Objects.equal(gameId, that.gameId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(gameId);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("gameId", gameId)
                .add("fullName", fullName)
                .add("name", name)
                .add("category", category)
                .add("featured", featured)
                .add("slug", slug)
                .add("status", status)
                .add("gameInfos", gameInfos)
                .add("createdBy", createdBy != null ? createdBy.getId() : null)
                .add("createdDate", createdDate)
                .add("modifiedBy", modifiedBy != null ? modifiedBy.getId() : null)
                .add("modifiedDate", modifiedDate)
                .toString();
    }
}
