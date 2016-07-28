package com.cs.casino.security;

import com.cs.persistence.Status;
import com.cs.player.Player;
import com.cs.player.Verification;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Objects;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static com.cs.persistence.Status.ACTIVE;
import static com.cs.persistence.Status.BAD_CREDENTIALS_LOCKED;
import static com.cs.player.TrustLevel.BLACK;

/**
 * @author Joakim Gottzén
 */
public class PlayerUser implements UserDetails {
    private static final long serialVersionUID = 1L;

    private final String username;
    private final String password;
    private final boolean accountNonExpired;
    private final boolean accountNonLocked;
    private final boolean credentialsNonExpired;
    private final boolean enabled;
    private final List<GrantedAuthority> authorities;

    private final Long id;

    public PlayerUser(final Player player) {
        username = player.getEmailAddress();
        password = player.getPassword();
        final Status status = player.getStatus();
        accountNonExpired = status == ACTIVE;
        accountNonLocked = status != BAD_CREDENTIALS_LOCKED;
        credentialsNonExpired = player.getEmailVerification() == Verification.VERIFIED;
        enabled = isAccountNonLocked(player);

        id = player.getId();
        authorities = AuthorityUtils.createAuthorityList("PLAYER");
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

    @VisibleForTesting
    static boolean isAccountNonLocked(final Player player) {
        return player.getTrustLevel() != BLACK && !player.getBlockType().isBlocked(player.getBlockEndDate());
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final PlayerUser that = (PlayerUser) o;

        return Objects.equal(username.toLowerCase(), that.username.toLowerCase());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(username.toLowerCase());
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
