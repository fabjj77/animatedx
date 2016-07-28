package com.cs.avatar;

import com.cs.persistence.Status;
import com.cs.user.User;

import com.google.common.base.Objects;
import org.hibernate.validator.constraints.NotEmpty;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

import static javax.persistence.ConstraintMode.CONSTRAINT;
import static javax.persistence.EnumType.ORDINAL;
import static javax.persistence.EnumType.STRING;
import static javax.persistence.GenerationType.IDENTITY;
import static javax.persistence.TemporalType.TIMESTAMP;

/**
 * @author Joakim Gottzén
 */
@Entity
@Table(name = "avatars")
//@Converts(value = {
//        @Convert(attributeName = "skinColor", converter = SkinColorConverter.class),
//        @Convert(attributeName = "hairColor", converter = HairColorConverter.class)
//})
public class Avatar implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "avatar_base_type_id", nullable = true, foreignKey = @ForeignKey(CONSTRAINT))
    @Nonnull
    @NotNull(message = "avatar.avatarBaseType.notNull")
    private AvatarBaseType avatarBaseType;

    @ManyToOne
    @JoinColumn(name = "level", nullable = false, foreignKey = @ForeignKey(CONSTRAINT))
    @Nonnull
    @NotNull(message = "avatar.level.notNull")
    private Level level;

    @Column(name = "skin", nullable = false)
    @Enumerated(ORDINAL)
    @Nonnull
    @NotNull(message = "avatar.skinColor.notNull")
    private SkinColor skinColor;

    @Column(name = "hair", nullable = false)
    @Enumerated(ORDINAL)
    @Nonnull
    @NotNull(message = "avatar.hairColor.notNull")
    private HairColor hairColor;

    @Column(name = "picture_url", nullable = false, length = 120)
    @Nonnull
    @NotEmpty(message = "avatar.pictureUrl.notEmpty")
    private String pictureUrl;

    @Column(name = "night_background_url", nullable = false, length = 120)
    @Nonnull
    @NotEmpty(message = "avatar.nightBackgroundUrl.notEmpty")
    private String nightBackgroundUrl;

    @Column(name = "day_background_url", nullable = false, length = 120)
    @Nonnull
    @NotEmpty(message = "avatar.dayBackgroundUrl.notEmpty")
    private String dayBackgroundUrl;

    @Column(name = "status", nullable = false)
    @Enumerated(STRING)
    @Nonnull
    @NotNull(message = "avatar.status.notNull")
    private Status status;

    @Column(name = "text_id", nullable = false)
    @Nonnull
    @NotNull(message = "avatar.textId.notNull")
    private Integer textId;

    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false)
    @Nonnull
    @NotNull(message = "avatar.createdBy.notNull")
    private User createdBy;

    @Column(name = "created_date", nullable = false)
    @Temporal(TIMESTAMP)
    @Nonnull
    @NotNull(message = "avatar.createdDate.notNull")
    private Date createdDate;

    @ManyToOne
    @JoinColumn(name = "modified_by")
    @Nullable
    private User modifiedBy;

    @Column(name = "modified_date")
    @Temporal(TIMESTAMP)
    @Nullable
    private Date modifiedDate;

    public Avatar() {}

    public Avatar(@Nonnull final Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    @Nonnull
    public AvatarBaseType getAvatarBaseType() {
        return avatarBaseType;
    }

    public void setAvatarBaseType(@Nonnull final AvatarBaseType avatarBaseType) {
        this.avatarBaseType = avatarBaseType;
    }

    @Nonnull
    public Level getLevel() {
        return level;
    }

    public void setLevel(@Nonnull final Level level) {
        this.level = level;
    }

    @Nonnull
    public SkinColor getSkinColor() {
        return skinColor;
    }

    public void setSkinColor(@Nonnull final SkinColor skinColor) {
        this.skinColor = skinColor;
    }

    @Nonnull
    public HairColor getHairColor() {
        return hairColor;
    }

    public void setHairColor(@Nonnull final HairColor hairColor) {
        this.hairColor = hairColor;
    }

    @Nonnull
    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(@Nonnull final String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    @Nonnull
    public Status getStatus() {
        return status;
    }

    public void setStatus(@Nonnull final Status status) {
        this.status = status;
    }

    @Nonnull
    public User getCreatedBy() {
        return createdBy;
    }

    @Nonnull
    public Date getCreatedDate() {
        return createdDate;
    }

    @Nullable
    public User getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(@Nullable final User modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    @Nullable
    public Date getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(@Nullable final Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    @Nonnull
    public Integer getTextId() {
        return textId;
    }

    public void setTextId(@Nonnull final Integer textId) {
        this.textId = textId;
    }

    @Nonnull
    public String getNightBackgroundUrl() { return nightBackgroundUrl; }

    public void setNightBackgroundUrl(@Nonnull final String nightBackgroundUrl) {
        this.nightBackgroundUrl = nightBackgroundUrl;
    }

    @Nonnull
    public String getDayBackgroundUrl() {
        return dayBackgroundUrl;
    }

    public void setDayBackgroundUrl(@Nonnull final String dayBackgroundUrl) {
        this.dayBackgroundUrl = dayBackgroundUrl;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final Avatar that = (Avatar) o;

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
                .addValue(avatarBaseType.getId())
                .addValue(level.getLevel())
                .addValue(skinColor)
                .addValue(hairColor)
                .addValue(pictureUrl)
                .addValue(dayBackgroundUrl)
                .addValue(nightBackgroundUrl)
                .addValue(status)
                .addValue(textId)
                .toString();
    }
}
