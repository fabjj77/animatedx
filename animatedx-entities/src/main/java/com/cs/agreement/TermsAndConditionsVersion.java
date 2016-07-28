package com.cs.agreement;

import com.google.common.base.Objects;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.NotEmpty;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

import static javax.persistence.GenerationType.IDENTITY;
import static javax.persistence.TemporalType.TIMESTAMP;

@Entity
@Table(name = "terms_and_conditions_versions")
public class TermsAndConditionsVersion implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    @Nonnull
    private Long id;

    @Column(name = "version", nullable = false, length = 10)
    @Nonnull
    @NotEmpty(message = "termsAndConditionsVersion.version.notEmpty")
    private String version;

    @Column(name = "active", nullable = false)
    @Type(type = "org.hibernate.type.NumericBooleanType")
    @Nonnull
    private Boolean active;

    @Column(name = "created_date", nullable = false)
    @Temporal(TIMESTAMP)
    @Nonnull
    @NotNull(message = "termsAndConditionsVersion.createdDate.notNull")
    private Date createdDate;

    @Column(name = "activated_date")
    @Temporal(TIMESTAMP)
    @Nullable
    private Date activatedDate;

    @Nonnull
    public Long getId() {
        return id;
    }

    public void setId(@Nonnull final Long id) {
        this.id = id;
    }

    @Nonnull
    public String getVersion() {
        return version;
    }

    public void setVersion(@Nonnull final String version) {
        this.version = version;
    }

    @Nonnull
    public Boolean isActive() {
        return active;
    }

    public void setActive(@Nonnull final Boolean active) {
        this.active = active;
    }

    @Nonnull
    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(@Nonnull final Date createdDate) {
        this.createdDate = createdDate;
    }

    @Nullable
    public Date getActivatedDate() {
        return activatedDate;
    }

    public void setActivatedDate(@Nullable final Date activatedDate) {
        this.activatedDate = activatedDate;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final TermsAndConditionsVersion that = (TermsAndConditionsVersion) o;

        return Objects.equal(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, version);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("id", id)
                .add("version", version)
                .add("createdDate", createdDate)
                .add("active", active)
                .add("activatedDate", activatedDate)
                .toString();
    }
}
