package com.cs.user;

import com.google.common.base.Objects;

import javax.annotation.Nonnull;
import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;
import java.io.Serializable;

/**
 * @author Joakim Gottzén
 */
@Embeddable
public class UserRoleId implements Serializable {
    private static final long serialVersionUID = 1L;

    @ManyToOne
    @Nonnull
    private User user;

    @ManyToOne
    @Nonnull
    private Role role;

    public UserRoleId() {}

    public UserRoleId(@Nonnull final User user, @Nonnull final Role role) {
        this.user = user;
        this.role = role;
    }

    @Nonnull
    public User getUser() {
        return user;
    }

    public void setUser(@Nonnull final User user) {
        this.user = user;
    }

    @Nonnull
    public Role getRole() {
        return role;
    }

    public void setRole(@Nonnull final Role role) {
        this.role = role;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final UserRoleId that = (UserRoleId) o;

        return Objects.equal(user, that.user) &&
               Objects.equal(role, that.role);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(user, role);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .addValue(user != null ? user.getId() : null)
                .addValue(role != null ? role.getId() : null)
                .toString();
    }
}
