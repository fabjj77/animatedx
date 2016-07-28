package com.cs.player;

import com.cs.validation.ValidationProperties;

import com.google.common.base.Objects;
import org.hibernate.annotations.GenericGenerator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.Date;

import static javax.persistence.TemporalType.TIMESTAMP;

/**
 * @author Omid Alaepour
 */
@Entity
@Table(name = "player_uuids")
public class PlayerUuid implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    @Pattern(regexp = ValidationProperties.UUID_PATTERN, message = "playerUuid.uuid.notValid")
    private String uuid;

    @OneToOne
    @JoinColumn(name = "player_id", nullable = false)
    @Nonnull
    @Valid
    private Player player;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    @Nonnull
    @NotNull(message = "playerUuid.uuidType.notNull")
    private UuidType uuidType;

    @Column(name = "created_date", nullable = false)
    @Temporal(TIMESTAMP)
    @Nonnull
    @NotNull(message = "playerUuid.createdBy.notNull")
    private Date createdDate;

    @Column(name = "used_date")
    @Temporal(TIMESTAMP)
    @Nullable
    private Date usedDate;

    @Column(name = "bonus_id")
    @Nullable
    private Long bonusId;

    @Column(name = "data")
    @Nullable
    private String data;

    public PlayerUuid() {}

    public PlayerUuid(@Nonnull final Player player, @Nonnull final Date createdDate) {
        this.player = player;
        this.createdDate = createdDate;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(final String uuid) {
        this.uuid = uuid;
    }

    @Nonnull
    public Player getPlayer() {
        return player;
    }

    public void setPlayer(@Nonnull final Player player) {
        this.player = player;
    }

    @Nonnull
    public UuidType getUuidType() {
        return uuidType;
    }

    public void setUuidType(@Nonnull final UuidType uuidType) {
        this.uuidType = uuidType;
    }

    @Nonnull
    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(@Nonnull final Date createdDate) {
        this.createdDate = createdDate;
    }

    @Nullable
    public Date getUsedDate() {
        return usedDate;
    }

    public void setUsedDate(@Nullable final Date usedDate) {
        this.usedDate = usedDate;
    }

    @Nullable
    public String getData() {
        return data;
    }

    public void setData(@Nullable final String data) {
        this.data = data;
    }

    @Nullable
    public Long getBonusId() {
        return bonusId;
    }

    public void setBonusId(@Nullable final Long bonusId) {
        this.bonusId = bonusId;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final PlayerUuid that = (PlayerUuid) o;

        return Objects.equal(uuid, that.uuid);
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .addValue(uuid)
                .addValue(player.getId())
                .addValue(createdDate)
                .addValue(usedDate)
                .addValue(data)
                .addValue(bonusId)
                .toString();
    }
}
