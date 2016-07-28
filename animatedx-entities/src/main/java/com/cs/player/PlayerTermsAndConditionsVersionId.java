package com.cs.player;

import com.cs.agreement.TermsAndConditionsVersion;

import com.google.common.base.Objects;

import javax.annotation.Nonnull;
import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;
import java.io.Serializable;

@Embeddable
public class PlayerTermsAndConditionsVersionId implements Serializable {

    private static final long serialVersionUID = 1L;

    @ManyToOne
    @Nonnull
    private Player player;

    @ManyToOne
    @Nonnull
    private TermsAndConditionsVersion termsAndConditionsVersion;

    public PlayerTermsAndConditionsVersionId() {
    }

    public PlayerTermsAndConditionsVersionId(@Nonnull final Player player, @Nonnull final TermsAndConditionsVersion termsAndConditionsVersion) {
        this.player = player;
        this.termsAndConditionsVersion = termsAndConditionsVersion;
    }

    @Nonnull
    public Player getPlayer() {
        return player;
    }

    public void setPlayer(@Nonnull final Player player) {
        this.player = player;
    }

    @Nonnull
    public TermsAndConditionsVersion getTermsAndConditionsVersion() {
        return termsAndConditionsVersion;
    }

    public void setTermsAndConditionsVersion(@Nonnull final TermsAndConditionsVersion termsAndConditionsVersion) {

        this.termsAndConditionsVersion = termsAndConditionsVersion;
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

        return Objects.equal(player.getId(), that.player.getId()) &&
               Objects.equal(termsAndConditionsVersion.getId(), that.termsAndConditionsVersion.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(player.getId(), termsAndConditionsVersion.getId());
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("player", player.getId())
                .add("termsAndConditionsVersion", termsAndConditionsVersion.getId())
                .toString();
    }
}
