package com.cs.messaging.email;

import com.cs.user.User;

import com.google.common.base.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

import static javax.persistence.GenerationType.IDENTITY;
import static javax.persistence.TemporalType.TIMESTAMP;

/**
 * @author Omid Alaepour
 */
@Entity
@Table(name = "bronto_lists")
public class BrontoList implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    @Nonnull
    private Long id;

    @Column(name = "list_id", nullable = false, length = 200)
    @Nonnull
    private String listId;

    @Column(name = "name", nullable = false, length = 100)
    @Nonnull
    private String name;

    @Column(name = "label", nullable = false, length = 100)
    @Nonnull
    private String label;

    @Column(name = "active_count", nullable = false)
    @Nonnull
    private BigInteger activeCount;

    @Column(name = "status", nullable = false)
    @Nonnull
    private BrontoListStatus status;

    @Column(name = "created_date", nullable = false)
    @Temporal(TIMESTAMP)
    @Nonnull
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

    public void setId(@Nonnull final Long id) {
        this.id = id;
    }

    @Nonnull
    public String getListId() {
        return listId;
    }

    public void setListId(@Nonnull final String listId) {
        this.listId = listId;
    }

    @Nonnull
    public String getName() {
        return name;
    }

    public void setName(@Nonnull final String name) {
        this.name = name;
    }

    @Nonnull
    public String getLabel() {
        return label;
    }

    public void setLabel(@Nonnull final String label) {
        this.label = label;
    }

    @Nonnull
    public BigInteger getActiveCount() {
        return activeCount;
    }

    public void setActiveCount(@Nonnull final BigInteger activeCount) {
        this.activeCount = activeCount;
    }

    @Nonnull
    public BrontoListStatus getStatus() {
        return status;
    }

    public void setStatus(@Nonnull final BrontoListStatus status) {
        this.status = status;
    }

    @Nonnull
    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(@Nonnull final Date createdDate) {
        this.createdDate = createdDate;
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
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final BrontoList that = (BrontoList) o;

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
                .addValue(listId)
                .addValue(name)
                .addValue(label)
                .addValue(activeCount)
                .addValue(status)
                .addValue(createdDate)
                .addValue(modifiedBy != null ? modifiedBy.getId() : null)
                .addValue(modifiedDate)
                .toString();
    }
}
