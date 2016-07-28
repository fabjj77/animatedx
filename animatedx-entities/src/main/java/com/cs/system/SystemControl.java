package com.cs.system;

import com.cs.user.User;

import com.google.common.base.Objects;
import org.hibernate.annotations.Type;

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
import java.util.Date;

import static javax.persistence.GenerationType.IDENTITY;
import static javax.persistence.TemporalType.TIMESTAMP;

/**
 * @author Omid Alaepour
 */
@Entity
@Table(name = "system_control")
public class SystemControl implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final Long ID = 1L;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    @Nonnull
    private Long id;

    @Column(name = "logins_enabled", nullable = false)
    @Type(type = "org.hibernate.type.NumericBooleanType")
    @Nonnull
    private Boolean loginEnabled;

    @Column(name = "registrations_enabled", nullable = false)
    @Type(type = "org.hibernate.type.NumericBooleanType")
    @Nonnull
    private Boolean registrationEnabled;

    @Column(name = "bronto_enabled", nullable = false)
    @Type(type = "org.hibernate.type.NumericBooleanType")
    @Nonnull
    private Boolean brontoEnabled;

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
    public Boolean getLoginEnabled() {
        return loginEnabled;
    }

    public void setLoginEnabled(@Nonnull final Boolean loginEnabled) {
        this.loginEnabled = loginEnabled;
    }

    @Nonnull
    public Boolean getRegistrationEnabled() {
        return registrationEnabled;
    }

    public void setRegistrationEnabled(@Nonnull final Boolean registrationEnabled) {
        this.registrationEnabled = registrationEnabled;
    }

    @Nonnull
    public Boolean getBrontoEnabled() {
        return brontoEnabled;
    }

    public void setBrontoEnabled(@Nonnull final Boolean brontoEnabled) {
        this.brontoEnabled = brontoEnabled;
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

        final SystemControl that = (SystemControl) o;

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
                .add("loginEnabled", loginEnabled)
                .add("registrationEnabled", registrationEnabled)
                .add("brontoEnabled", brontoEnabled)
                .add("modifiedBy", modifiedBy != null ? modifiedBy.getId() : null)
                .add("modifiedDate", modifiedDate)
                .toString();
    }
}
