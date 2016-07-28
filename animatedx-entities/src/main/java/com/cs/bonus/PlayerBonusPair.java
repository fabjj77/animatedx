package com.cs.bonus;

import com.cs.player.Player;

import com.google.common.base.Objects;

import javax.annotation.Nonnull;
import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;
import java.io.Serializable;

/**
 * @author Hadi Movaghar
 */
@Embeddable
public class PlayerBonusPair implements Serializable {
    private static final long serialVersionUID = 1L;

    @ManyToOne
    @Nonnull
    private Player player;

    @ManyToOne
    @Nonnull
    private Bonus bonus;

    public PlayerBonusPair() {
    }

    public PlayerBonusPair(@Nonnull final Player player, @Nonnull final Bonus bonus) {
        this.player = player;
        this.bonus = bonus;
    }

    @Nonnull
    public Player getPlayer() {
        return player;
    }

    public void setPlayer(@Nonnull final Player player) {
        this.player = player;
    }

    @Nonnull
    public Bonus getBonus() {
        return bonus;
    }

    public void setBonus(@Nonnull final Bonus bonus) {
        this.bonus = bonus;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final PlayerBonusPair that = (PlayerBonusPair) o;

        return Objects.equal(player, that.player) &&
               Objects.equal(bonus, that.bonus);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(player, bonus);
    }

    @Override
    public String toString() {
        //noinspection ConstantConditions
        return Objects.toStringHelper(this)
                .addValue(player != null ? player.getId() : null)
                .addValue(bonus != null ? bonus.getId() : null)
                .toString();
    }
}
