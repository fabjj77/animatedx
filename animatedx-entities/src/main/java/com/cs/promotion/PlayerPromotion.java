package com.cs.promotion;

import com.google.common.base.Objects;

import javax.annotation.Nonnull;
import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import java.io.Serializable;
import java.util.Date;

import static javax.persistence.TemporalType.TIMESTAMP;

/**
 * @author Omid Alaepour
 */

@Entity
@Table(name = "players_promotions")
@AssociationOverrides(value = {
        @AssociationOverride(name = "pk.player", joinColumns = @JoinColumn(name = "player_id")),
        @AssociationOverride(name = "pk.promotion", joinColumns = @JoinColumn(name = "promotion_id"))
})
public class PlayerPromotion implements Serializable {
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    @Nonnull
    private PlayerPromotionId pk;

    @Column(name = "activation_date")
    @Temporal(TIMESTAMP)
    private Date activationDate;

    public PlayerPromotion() {
    }

    public PlayerPromotion(@Nonnull final PlayerPromotionId pk, final Date activationDate) {
        this.pk = pk;
        this.activationDate = activationDate;
    }

    @Nonnull
    public PlayerPromotionId getPk() {
        return pk;
    }

    public void setPk(@Nonnull final PlayerPromotionId pk) {
        this.pk = pk;
    }

    public Date getActivationDate() {
        return activationDate;
    }

    public void setActivationDate(final Date activationDate) {
        this.activationDate = activationDate;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final PlayerPromotion that = (PlayerPromotion) o;

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
                .addValue(activationDate)
                .toString();
    }
}
