package com.cs.promotion;

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
public class PlayerPromotionId implements Serializable {
    private static final long serialVersionUID = 1L;

    @ManyToOne
    @Nonnull
    private Player player;

    @ManyToOne
    @Nonnull
    private Promotion promotion;

    public PlayerPromotionId() {
    }

    public PlayerPromotionId(@Nonnull final Player player, @Nonnull final Promotion promotion) {
        this.player = player;
        this.promotion = promotion;
    }

    @Nonnull
    public Player getPlayer() {
        return player;
    }

    public void setPlayer(@Nonnull final Player player) {
        this.player = player;
    }

    @Nonnull
    public Promotion getPromotion() {
        return promotion;
    }

    public void setPromotion(@Nonnull final Promotion promotion) {
        this.promotion = promotion;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final PlayerPromotionId that = (PlayerPromotionId) o;

        return Objects.equal(player.getId(), that.player.getId()) &&
               Objects.equal(promotion.getId(), that.promotion.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(player.getId(), promotion.getId());
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .addValue(player.getId())
                .addValue(promotion.getId())
                .toString();
    }
}
