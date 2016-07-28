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
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

import static javax.persistence.EnumType.STRING;
import static javax.persistence.GenerationType.IDENTITY;
import static javax.persistence.TemporalType.TIMESTAMP;

/**
 * @author Joakim Gottzén
 */
@Entity
@Table(name = "avatar_base_types")
public class AvatarBaseType implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Nonnull
    private Integer id;

    @Column(name = "name", nullable = false, length = 50)
    @Nonnull
    @NotEmpty(message = "avatarBaseType.name.notEmpty")
    private String name;

    @Column(name = "status", nullable = false, length = 50)
    @Enumerated(STRING)
    @Nonnull
    @NotNull(message = "avatarBaseType.status.notNull")
    private Status status;

    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false)
    @Nonnull
    @NotNull(message = "avatarBaseType.createdBy.notNull")
    private User createdBy;

    @Column(name = "created_date", nullable = false)
    @Temporal(TIMESTAMP)
    @Nonnull
    @NotNull(message = "avatarBaseType.createdDate.notNull")
    private Date createdDate;

    @ManyToOne
    @JoinColumn(name = "modified_by")
    @Nullable
    private User modifiedBy;

    @Column(name = "modified_date")
    @Temporal(TIMESTAMP)
    @Nullable
    private Date modifiedDate;

    public AvatarBaseType() {}

    public AvatarBaseType(@Nonnull final Integer AvatarBaseTypeId) {
        this.id = AvatarBaseTypeId;
    }

    public AvatarBaseType(@Nonnull final String name, @Nonnull final Status status, @Nonnull final User createdBy, @Nonnull final Date createdDate) {
        this.name = name;
        this.status = status;
        this.createdBy = createdBy;
        this.createdDate = createdDate;
    }

    @Nonnull
    public Integer getId() {
        return id;
    }

    public void setId(@Nonnull final Integer id) {
        this.id = id;
    }

    @Nonnull
    public String getName() {
        return name;
    }

    public void setName(@Nonnull final String name) {
        this.name = name;
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

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AvatarBaseType that = (AvatarBaseType) o;

        return Objects.equal(this.id, that.id);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .addValue(id)
                .addValue(name)
                .addValue(status)
                .addValue(createdBy != null ? createdBy.getId() : null)
                .addValue(createdDate)
                .addValue(modifiedBy != null ? modifiedBy.getId() : null)
                .addValue(modifiedDate)
                .toString();
    }
}
