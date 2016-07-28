package com.cs.player;

import com.google.common.base.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.io.Serializable;

import static javax.persistence.GenerationType.IDENTITY;

/**
 * @author Hadi Movaghar
 */
@Entity
@Table(name = "players_register_tracks")
public class PlayerRegisterTrack implements Serializable {
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

    @Column(name = "campaign", nullable = false, length = 200)
    @Nullable
    private String campaign;

    @Column(name = "source", nullable = false, length = 200)
    @Nullable
    private String source;

    @Column(name = "medium", nullable = false, length = 200)
    @Nullable
    private String medium;

    @Column(name = "content", nullable = false, length = 1000)
    @Nullable
    private String content;

    @Column(name = "version", nullable = false, length = 100)
    @Nullable
    private String version;

    public PlayerRegisterTrack() {
    }

    @Nonnull
    public Long getId() {
        return id;
    }

    public void setId(@Nonnull final Long id) {
        this.id = id;
    }

    @Nonnull
    public Player getPlayer() {
        return player;
    }

    public void setPlayer(@Nonnull final Player player) {
        this.player = player;
    }

    @Nullable
    public String getCampaign() {
        return campaign;
    }

    public void setCampaign(@Nullable final String campaign) {
        this.campaign = campaign;
    }

    @Nullable
    public String getSource() {
        return source;
    }

    public void setSource(@Nullable final String source) {
        this.source = source;
    }

    @Nullable
    public String getMedium() {
        return medium;
    }

    public void setMedium(@Nullable final String medium) {
        this.medium = medium;
    }

    @Nullable
    public String getContent() {
        return content;
    }

    public void setContent(@Nullable final String content) {
        this.content = content;
    }

    @Nullable
    public String getVersion() {
        return version;
    }

    public void setVersion(@Nullable final String version) {
        this.version = version;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final PlayerRegisterTrack that = (PlayerRegisterTrack) o;

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
                .add("id", id)
                .add("player", player != null ? player.getId() : null)
                .add("campaign", campaign)
                .add("source", source)
                .add("medium", medium)
                .add("content", content)
                .add("version", version)
                .toString();
    }
}
