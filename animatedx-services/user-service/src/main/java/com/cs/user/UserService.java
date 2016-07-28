package com.cs.user;

import org.springframework.data.domain.Page;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Omid Alaepour
 */
public interface UserService {
    User getUser(@Nonnull final Long userId);

    User getUser(@Nonnull final String emailAddress);

    /**
     * This method should only be used when fetching players for authentication, by the Spring Security framework, since it doesn't check if the user is active or not!
     */
    User getUserForAuthentication(@Nonnull final String emailAddress);

    User createUser(@Nonnull final User user, @Nonnull final SecurityRole role, @Nonnull final Long creatorId);

    User updateUser(final Long userId, @Nonnull final User user, @Nullable final String password, @Nullable final String newPassword,
                    final SecurityRole role, @Nonnull final Long updatingUserId);

    User deleteUser(@Nonnull final Long userId, @Nonnull final Long updaterUserId);

    void resetLoginFailureCounter(Long id);

    void recordLoginFailure(Long id);

    Page<User> searchUsers(String emailAddress, String nickname, String firstName, String lastName, Integer page, final Integer size);
}
