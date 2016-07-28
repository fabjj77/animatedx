package com.cs.player;

import com.google.common.base.Objects;

import javax.annotation.Nonnull;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
 * @author Hadi Movaghar
 */
@Entity
@Table(name = "player_limitation_lockouts")
public class PlayerLimitationLockout implements Serializable{
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

    @Column(name = "block_type", nullable = false)
    @Enumerated(EnumType.STRING)
    @Nonnull
    private BlockType blockType;

    @Column(name = "start_date", nullable = false)
    @Temporal(TIMESTAMP)
    @Nonnull
    private Date startDate;

    @Column(name = "end_date", nullable = false)
    @Temporal(TIMESTAMP)
    @Nonnull
    private Date endDate;

    public PlayerLimitationLockout() {
    }

    public PlayerLimitationLockout(@Nonnull final Player player, @Nonnull final BlockType blockType, @Nonnull final Date startDate, @Nonnull final Date endDate) {
        this.player = player;
        this.blockType = blockType;
        this.startDate = startDate;
        this.endDate = endDate;
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

    @Nonnull
    public BlockType getBlockType() {
        return blockType;
    }

    public void setBlockType(@Nonnull final BlockType blockType) {
        this.blockType = blockType;
    }

    @Nonnull
    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(@Nonnull final Date startDate) {
        this.startDate = startDate;
    }

    @Nonnull
    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(@Nonnull final Date endDate) {
        this.endDate = endDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final PlayerLimitationLockout that = (PlayerLimitationLockout) o;

        return Objects.equal(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .addValue(id)
                .addValue(player)
                .addValue(blockType)
                .addValue(startDate)
                .addValue(endDate)
                .toString();
    }
}
