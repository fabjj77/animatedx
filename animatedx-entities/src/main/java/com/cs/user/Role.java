package com.cs.user;

import com.cs.persistence.Status;

import com.google.common.base.Objects;

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
@Table(name = "roles")
public class Role implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    @Nonnull
    private Long id;

    @Column(name = "name", nullable = false)
    @Enumerated(STRING)
    @Nonnull
    @NotNull(message = "role.name.notNull")
    private SecurityRole name;

    @Column(name = "status", nullable = false, length = 8)
    @Enumerated(STRING)
    @Nonnull
    @NotNull(message = "role.status.notNull")
    private Status status;

    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false)
    @Nonnull
    @NotNull(message = "role.createdBy.notNull")
    private User createdBy;

    @Column(name = "created_date", nullable = false)
    @Temporal(TIMESTAMP)
    @Nonnull
    @NotNull(message = "role.createdDate.notNull")
    private Date createdDate;

    @ManyToOne
    @JoinColumn(name = "modified_by")
    @Nullable
    private User modifiedBy;

    @Column(name = "modified_date")
    @Temporal(TIMESTAMP)
    @Nullable
    private Date modifiedDate;

    @Nonnull
    public Long getId() {
        return id;
    }

    @Nonnull
    public SecurityRole getName() {
        return name;
    }

    @Nonnull
    public Status getStatus() {
        return status;
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

    @Nullable
    public Date getModifiedDate() {
        return modifiedDate;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final Role that = (Role) o;

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
                .addValue(name)
                .addValue(status)
                .addValue(createdBy.getId())
                .addValue(createdDate)
                .addValue(modifiedBy != null ? modifiedBy.getId() : null)
                .addValue(modifiedDate)
                .toString();
    }
}
