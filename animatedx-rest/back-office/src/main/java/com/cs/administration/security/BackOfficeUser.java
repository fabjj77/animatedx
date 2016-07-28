package com.cs.administration.security;

import com.cs.user.Role;
import com.cs.user.SecurityRole.Access;
import com.cs.user.User;
import com.cs.user.UserRole;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.google.common.base.Objects;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static com.cs.persistence.Status.ACTIVE;

/**
 * @author Joakim Gottzén
 */
public class BackOfficeUser implements UserDetails {

    private static final long serialVersionUID = 1L;

    private final String username;
    private final String password;
    private final boolean accountNonExpired;
    private final boolean accountNonLocked;
    private final boolean credentialsNonExpired;
    private final boolean enabled;
    private final List<SimpleGrantedAuthority> authorities = new ArrayList<>(10);

    private final Long id;

    public BackOfficeUser(final User user, final Integer maximumFailedLoginAttempts, final Integer badCredentialsLockoutTime) {
        username = user.getEmailAddress();
        password = user.getPassword();
        accountNonExpired = user.getStatus() == ACTIVE;

        if (user.getFailedLoginAttempts() >= maximumFailedLoginAttempts) {
            final Calendar calendar = Calendar.getInstance();
            calendar.setTime(user.getLastFailedLoginDate());
            calendar.add(Calendar.MINUTE, badCredentialsLockoutTime);
            accountNonLocked = !calendar.getTime().after(new Date());
        } else {
            accountNonLocked = true;
        }

        credentialsNonExpired = true;
        enabled = user.getStatus() == ACTIVE;

        id = user.getId();
        for (final UserRole securityRole : user.getRoles()) {
            final Role role = securityRole.getPk().getRole();
            for (final Access access : role.getName().getAccesses()) {
                authorities.add(new SimpleGrantedAuthority(access.name()));
            }
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.unmodifiableCollection(authorities);
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public Long getId() {
        return id;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final BackOfficeUser that = (BackOfficeUser) o;

        return Objects.equal(getUsername().toLowerCase(), that.getUsername().toLowerCase());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getUsername().toLowerCase());
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(super.toString()).append(": ");
        sb.append("Username: ").append(username).append("; ");
        sb.append("Password: [PROTECTED]; ");
        sb.append("Enabled: ").append(enabled).append("; ");
        sb.append("AccountNonExpired: ").append(accountNonExpired).append("; ");
        sb.append("credentialsNonExpired: ").append(credentialsNonExpired).append("; ");
        sb.append("AccountNonLocked: ").append(accountNonLocked).append("; ");

        if (!authorities.isEmpty()) {
            sb.append("Granted Authorities: ");

            @SuppressWarnings("BooleanVariableAlwaysNegated") boolean first = true;
            for (final GrantedAuthority auth : authorities) {
                if (!first) {
                    sb.append(",");
                }
                first = false;

                sb.append(auth);
            }
        } else {
            sb.append("Not granted any authorities");
        }

        return sb.toString();
    }
}
