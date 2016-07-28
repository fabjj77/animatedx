package com.cs.messaging.email;

import com.cs.user.User;

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
import java.io.Serializable;
import java.util.Date;

import static javax.persistence.EnumType.STRING;
import static javax.persistence.GenerationType.IDENTITY;
import static javax.persistence.TemporalType.TIMESTAMP;

/**
 * @author Omid Alaepour
 */
@Entity
@Table(name = "bronto_fields")
public class BrontoField implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    @Nonnull
    private Long id;

    @Column(name = "field_id", nullable = false, length = 200)
    @Nonnull
    private String fieldId;

    @Column(name = "name", nullable = false, length = 100)
    @Nonnull
    @Enumerated(STRING)
    private BrontoFieldName name;

    @Column(name = "label", nullable = false, length = 100)
    @Nonnull
    private String label;

    @Column(name = "type", nullable = false)
    @Nonnull
    @Enumerated(STRING)
    private BrontoFieldType type;

    @Column(name = "visibility", nullable = false)
    @Nonnull
    @Enumerated(STRING)
    private BrontoFieldVisibility visibility;

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
    public String getFieldId() {
        return fieldId;
    }

    public void setFieldId(@Nonnull final String fieldId) {
        this.fieldId = fieldId;
    }

    @Nonnull
    public BrontoFieldName getName() {
        return name;
    }

    public void setName(@Nonnull final BrontoFieldName name) {
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
    public BrontoFieldType getType() {
        return type;
    }

    public void setType(@Nonnull final BrontoFieldType type) {
        this.type = type;
    }

    @Nonnull
    public BrontoFieldVisibility getVisibility() {
        return visibility;
    }

    public void setVisibility(@Nonnull final BrontoFieldVisibility visbility) {
        this.visibility = visbility;
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
    public String toString() {
        return Objects.toStringHelper(this)
                .addValue(id)
                .addValue(fieldId)
                .addValue(name)
                .addValue(label)
                .addValue(type)
                .addValue(visibility)
                .addValue(createdDate)
                .addValue(modifiedBy != null ? modifiedBy.getId() : null)
                .addValue(modifiedDate)
                .toString();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final BrontoField that = (BrontoField) o;

        return Objects.equal(fieldId, that.fieldId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(fieldId);
    }
}
