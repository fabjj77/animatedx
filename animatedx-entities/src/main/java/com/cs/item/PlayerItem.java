package com.cs.item;

import com.cs.player.Player;

import com.google.common.base.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.validation.Valid;
import java.io.Serializable;
import java.util.Date;

import static javax.persistence.EnumType.STRING;
import static javax.persistence.TemporalType.TIMESTAMP;

/**
 * @author Omid Alaepour
 */
@Entity
@Table(name = "players_items")
@AssociationOverrides(value = {
        @AssociationOverride(name = "pk.player", joinColumns = @JoinColumn(name = "player_id")),
        @AssociationOverride(name = "pk.item", joinColumns = @JoinColumn(name = "item_id"))
})
public class PlayerItem implements Serializable {
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    @Nonnull
    @Valid
    private PlayerItemId pk;

    @Column(name = "item_state", nullable = false)
    @Enumerated(STRING)
    @Nonnull
    @Valid
    private ItemState itemState;

    @Column(name = "used_date")
    @Temporal(TIMESTAMP)
    @Nullable
    private Date usedDate;

    public PlayerItem() {
    }

    public PlayerItem(@Nonnull final PlayerItemId pk) {
        this.pk = pk;
    }

    public PlayerItem(@Nonnull final PlayerItemId pk, @Nonnull final ItemState itemState) {
        this(pk);
        this.itemState = itemState;
    }

    @Nonnull
    public PlayerItemId getPk() {
        return pk;
    }

    public void setPk(@Nonnull final PlayerItemId id) {
        pk = id;
    }

    public Player getPlayer() {
        return getPk().getPlayer();
    }

    public void setPlayer(final Player player) {
        getPk().setPlayer(player);
    }

    public Item getItem() {
        return getPk().getItem();
    }

    public void setItem(final Item item) {
        getPk().setItem(item);
    }

    @Nonnull
    public ItemState getItemState() {
        return itemState;
    }

    public void setItemState(@Nonnull final ItemState itemState) {
        this.itemState = itemState;
    }

    @Nullable
    public Date getUsedDate() {
        return usedDate;
    }

    public void setUsedDate(@Nullable final Date usedDate) {
        this.usedDate = usedDate;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final PlayerItem that = (PlayerItem) o;

        return Objects.equal(pk, that.pk);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(pk);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .addValue(pk)
                .toString();
    }
}
