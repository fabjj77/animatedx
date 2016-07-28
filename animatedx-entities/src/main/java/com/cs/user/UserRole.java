package com.cs.user;

import com.google.common.base.Objects;

import javax.annotation.Nonnull;
import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @author Joakim Gottzén
 */
@Entity
@Table(name = "user_roles")
@AssociationOverrides(value = {
        @AssociationOverride(name = "pk.user", joinColumns = @JoinColumn(name = "user_id")),
        @AssociationOverride(name = "pk.role", joinColumns = @JoinColumn(name = "role_id"))
})
public class UserRole implements Serializable {
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    @Nonnull
    private UserRoleId pk;

    public UserRole() {
    }

    public UserRole(@Nonnull final UserRoleId userRoleId) {
        pk = userRoleId;
    }

    @Nonnull
    public UserRoleId getPk() {
        return pk;
    }

    public void setPk(@Nonnull final UserRoleId pk) {
        this.pk = pk;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final UserRole that = (UserRole) o;

        return Objects.equal(pk, that.pk);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(pk);
    }
}
