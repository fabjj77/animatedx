package com.cs.item;

import com.cs.player.Player;

import com.google.common.base.Objects;

import javax.annotation.Nonnull;
import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;
import java.io.Serializable;

/**
 * @author Omid Alaepour.
 */
@Embeddable
public class PlayerItemId implements Serializable {
    private static final long serialVersionUID = 1L;

    @ManyToOne
    @Nonnull
    private Player player;

    @ManyToOne
    @Nonnull
    private Item item;

    public PlayerItemId() {
    }

    public PlayerItemId(@Nonnull final Player player, @Nonnull final Item item) {
        this.player = player;
        this.item = item;
    }

    @Nonnull
    public Player getPlayer() {
        return player;
    }

    public void setPlayer(@Nonnull final Player player) {
        this.player = player;
    }

    @Nonnull
    public Item getItem() {
        return item;
    }

    public void setItem(@Nonnull final Item item) {
        this.item = item;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final PlayerItemId that = (PlayerItemId) o;

        return Objects.equal(player.getId(), that.player.getId()) &&
               Objects.equal(item.getId(), that.item.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(player.getId(), item.getId());
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .addValue(player.getId())
                .addValue(item.getId())
                .toString();
    }
}
