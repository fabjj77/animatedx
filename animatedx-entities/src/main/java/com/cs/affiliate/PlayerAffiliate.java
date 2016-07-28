package com.cs.affiliate;

import com.cs.player.Player;

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
import javax.persistence.Temporal;
import java.io.Serializable;
import java.util.Date;

import static javax.persistence.GenerationType.IDENTITY;
import static javax.persistence.TemporalType.TIMESTAMP;

/**
 * @author Joakim Gottzén
 */
@Entity
@Table(name = "players_affiliates")
public class PlayerAffiliate implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    @Nonnull
    private Long id;

    @OneToOne
    @JoinColumn(name = "player_id", referencedColumnName = "id", nullable = false)
    @Nonnull
    private Player player;

    @Column(name = "affiliate_id", nullable = false)
    @Nonnull
    private String affiliateId;

    @Column(name = "btag", nullable = false)
    @Nonnull
    private String bTag;

    @Column(name = "created_date", nullable = false)
    @Temporal(TIMESTAMP)
    @Nonnull
    private Date createdDate;

    @Column(name = "reported_date", nullable = true)
    @Temporal(TIMESTAMP)
    @Nullable
    private Date reportedDate;

    @Column(name = "ip_address", nullable = true)
    @Nullable
    private String ipAddress;

    public PlayerAffiliate() {
    }

    public PlayerAffiliate(@Nonnull final Player player, @Nonnull final String affiliateId, @Nonnull final String bTag, @Nonnull final Date createdDate,
                           @Nullable final String ipAddress) {
        this.player = player;
        this.affiliateId = affiliateId;
        this.bTag = bTag;
        this.createdDate = createdDate;
        this.ipAddress = ipAddress;
    }

    @Nonnull
    public Long getId() {
        return id;
    }

    @Nonnull
    public Player getPlayer() {
        return player;
    }

    @Nonnull
    public String getAffiliateId() {
        return affiliateId;
    }

    @Nonnull
    public String getBTag() {
        return bTag;
    }

    @Nonnull
    public Date getCreatedDate() {
        return createdDate;
    }

    @Nullable
    public Date getReportedDate() {
        return reportedDate;
    }

    @Nullable
    public String getIpAddress() {
        return ipAddress;
    }

    public void setReportedDate(@Nullable final Date reportedDate) {
        this.reportedDate = reportedDate;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final PlayerAffiliate that = (PlayerAffiliate) o;

        return Objects.equal(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("id", id)
                .add("player", player.getId())
                .add("affiliateId", affiliateId)
                .add("bTag", bTag)
                .add("createdDate", createdDate)
                .add("reportedDate", reportedDate)
                .add("ipAddress", ipAddress)
                .toString();
    }
}
