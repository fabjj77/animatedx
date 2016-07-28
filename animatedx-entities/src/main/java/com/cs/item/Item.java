package com.cs.item;

import com.cs.avatar.Level;
import com.cs.bonus.Bonus;
import com.cs.user.User;

import com.google.common.base.Objects;
import org.hibernate.validator.constraints.NotEmpty;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

import static javax.persistence.EnumType.STRING;
import static javax.persistence.GenerationType.IDENTITY;
import static javax.persistence.TemporalType.TIMESTAMP;

/**
 * @author Omid Alaepour
 */
@Entity
@Table(name = "items")
public class Item implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    @Nonnull
    private Long id;

    @Column(name = "url", nullable = false, length = 120)
    @Nonnull
    @NotEmpty(message = "items.url.notEmpty")
    private String url;

    @Column(name = "status", nullable = false, length = 120)
    @Enumerated(STRING)
    @Nonnull
    @NotNull(message = "items.status.notNull")
    private ItemStatus status;

    @OneToOne(mappedBy = "item", optional = false)
    @Nonnull
    @NotNull(message = "item.level.notNull")
    private Level level;

    @OneToOne
    @JoinColumn(name = "bonus_id")
    @Nonnull
    @NotNull(message = "item.bonus.notNull")
    private Bonus bonus;

    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false)
    @Nonnull
    @NotNull(message = "item.createdBy.notNull")
    private User createdBy;

    @Column(name = "created_date", nullable = false)
    @Temporal(TIMESTAMP)
    @Nonnull
    @NotNull(message = "item.createdDate.notNull")
    private Date createdDate;

    @ManyToOne
    @JoinColumn(name = "modified_by")
    @Nullable
    private User modifiedBy;

    @Column(name = "modified_date")
    @Temporal(TIMESTAMP)
    @Nullable
    private Date modifiedDate;

    @OneToMany(mappedBy = "pk.item")
    @Nullable
    private List<PlayerItem> playerItems;

    public Item() {}

    public Item(@Nonnull final Long id) {
        this.id = id;
    }

    @Nonnull
    public Long getId() {
        return id;
    }

    public void setId(@Nonnull final Long id) {
        this.id = id;
    }

    @Nonnull
    public String getUrl() {
        return url;
    }

    public void setUrl(@Nonnull final String url) {
        this.url = url;
    }

    @Nonnull
    public ItemStatus getStatus() {
        return status;
    }

    public void setStatus(@Nonnull final ItemStatus status) {
        this.status = status;
    }

    @Nonnull
    public Level getLevel() {
        return level;
    }

    public void setLevel(@Nonnull final Level level) {
        this.level = level;
    }

    @Nonnull
    public Bonus getBonus() {
        return bonus;
    }

    public void setBonus(@Nonnull final Bonus bonus) {
        this.bonus = bonus;
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

    @Nullable
    public List<PlayerItem> getPlayerItems() {
        return playerItems;
    }

    public void setPlayerItems(@Nullable final List<PlayerItem> playerItems) {
        this.playerItems = playerItems;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final Item that = (Item) o;

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
                .addValue(url)
                .addValue(status)
                .addValue(level != null ? level.getLevel() : null)
                .addValue(bonus != null ? bonus.getId() : null)
                .addValue(createdBy != null ? createdBy.getId() : null)
                .addValue(createdDate)
                .addValue(modifiedBy != null ? modifiedBy.getId() : null)
                .addValue(modifiedDate)
                .addValue(playerItems != null ? playerItems.size() : 0)
                .toString();
    }
}
