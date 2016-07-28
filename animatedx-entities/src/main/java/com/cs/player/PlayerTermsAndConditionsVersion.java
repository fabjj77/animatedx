package com.cs.player;

import com.cs.agreement.TermsAndConditionsVersion;

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
import javax.validation.Valid;
import java.io.Serializable;
import java.util.Date;

import static javax.persistence.TemporalType.TIMESTAMP;

@Entity
@Table(name = "players_terms_and_conditions_versions")
@AssociationOverrides(value = {
        @AssociationOverride(name = "pk.player", joinColumns = @JoinColumn(name = "player_id")),
        @AssociationOverride(name = "pk.termsAndConditionsVersion", joinColumns = @JoinColumn(name = "terms_and_conditions_versions_id"))
})
public class PlayerTermsAndConditionsVersion implements Serializable {

    private static final long serialVersionUID = 1L;

    @EmbeddedId
    @Nonnull
    @Valid
    private PlayerTermsAndConditionsVersionId pk;

    @Column(name = "accepted_date")
    @Temporal(TIMESTAMP)
    @Nonnull
    private Date acceptedDate;

    public PlayerTermsAndConditionsVersion() {
    }

    public PlayerTermsAndConditionsVersion(@Nonnull final PlayerTermsAndConditionsVersionId pk) {
        this.pk = pk;
    }

    @Nonnull
    public PlayerTermsAndConditionsVersionId getPk() {
        return pk;
    }

    public void setPk(@Nonnull final PlayerTermsAndConditionsVersionId id) {
        pk = id;
    }

    public Player getPlayer() {
        return pk.getPlayer();
    }

    public void setPlayer(final Player player) {
        pk.setPlayer(player);
    }

    public TermsAndConditionsVersion getTermAndConditionVersion() {
        return pk.getTermsAndConditionsVersion();
    }

    public void setTermAndConditionVersion(final TermsAndConditionsVersion termsAndConditionsVersion) {
        pk.setTermsAndConditionsVersion(termsAndConditionsVersion);
    }

    @Nonnull
    public Date getAcceptedDate() {
        return acceptedDate;
    }

    public void setAcceptedDate(@Nonnull final Date acceptedDate) {
        this.acceptedDate = acceptedDate;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final PlayerTermsAndConditionsVersionId that = (PlayerTermsAndConditionsVersionId) o;

        return Objects.equal(pk, that);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(pk);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("pk", pk)
                .add("acceptedDate", acceptedDate)
                .toString();
    }
}
