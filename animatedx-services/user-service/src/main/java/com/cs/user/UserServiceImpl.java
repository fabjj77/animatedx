package com.cs.user;

import com.cs.audit.AuditService;
import com.cs.audit.UserActivityType;
import com.cs.persistence.NotFoundException;
import com.cs.persistence.Status;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.querydsl.QSort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mysema.query.BooleanBuilder;
import com.mysema.query.types.Order;
import com.mysema.query.types.OrderSpecifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static com.cs.audit.UserActivityType.FAILED_LOGIN;
import static com.cs.persistence.Status.ACTIVE;
import static com.cs.persistence.Status.DELETED;
import static org.springframework.transaction.annotation.Isolation.READ_COMMITTED;
import static org.springframework.transaction.annotation.Propagation.SUPPORTS;

/**
 * @author Omid Alaepour
 */
@Service
@Transactional(isolation = READ_COMMITTED)
public class UserServiceImpl implements UserService {
    private final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final AuditService auditService;

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(final AuditService auditService, final RoleRepository roleRepository, final UserRepository userRepository,
                           final UserRoleRepository userRoleRepository, final PasswordEncoder passwordEncoder) {
        this.auditService = auditService;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User getUser(@Nonnull final Long userId) {
        return findActiveUserById(userId);
    }

    private User findActiveUserById(final Long userId) {
        final User user = userRepository.findOne(userId);
        if (user == null || user.getStatus() != ACTIVE) {
            throw new NotFoundException(userId);
        }
        return user;
    }

    private User findUserById(final Long userId) {
        final User user = userRepository.findOne(userId);
        if (user == null) {
            throw new NotFoundException(userId);
        }
        return user;
    }

    @Override
    public User getUser(@Nonnull final String emailAddress) {
        final User user = userRepository.findByEmailAddress(emailAddress);
        if (user == null || user.getStatus() != ACTIVE) {
            throw new NotFoundException("User not found");
        }
        auditService.trackUserActivity(user, UserActivityType.LOGIN, null);
        return user;
    }

    /**
     * This method should only be used when fetching players for authentication, by the Spring Security framework, since it doesn't check if the user is active or not!
     */
    @Override
    public User getUserForAuthentication(@Nonnull final String emailAddress) {
        final User user = userRepository.findByEmailAddress(emailAddress);
        if (user == null) {
            throw new NotFoundException("User not found");
        }
        return user;
    }

    @Override
    public User createUser(@Nonnull final User user, @Nonnull final SecurityRole role, @Nonnull final Long creatorId) {
        final User createdBy = getUser(creatorId);

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setStatus(ACTIVE);
        user.setCreatedBy(createdBy);
        user.setCreatedDate(new Date());
        user.setFailedLoginAttempts(0);
        final User newUser = userRepository.save(user);
        final UserRole userRole = new UserRole(new UserRoleId(user, roleRepository.findByName(role)));
        newUser.setRoles(Collections.singletonList(userRoleRepository.save(userRole)));

        logger.info("User {} created new user {}", createdBy.getEmailAddress(), newUser.getEmailAddress());
        auditService.trackUserActivity(createdBy, UserActivityType.CREATE_USER, "User " + newUser.getId() + " was created.");
        return newUser;
    }

    @Override
    public User updateUser(final Long userId, @Nonnull final User user, @Nullable final String password, @Nullable final String newPassword,
                           final SecurityRole role, @Nonnull final Long updatingUserId) {
        final User existingUser = findUserById(userId);
        final User updatingUser = findActiveUserById(updatingUserId);
        logger.info("User {} updates user {}", updatingUser, existingUser);

        if (password != null) {
            existingUser.setPassword(passwordEncoder.encode(password));
        }

        existingUser.updateFromUser(user);
        existingUser.setModifiedDate(new Date());
        existingUser.setModifiedBy(updatingUser);
        final User updatedUser = userRepository.save(existingUser);
        if (role != null) {
            final List<UserRole> roles = existingUser.getRoles();
            logger.info("Removing roles '{}' from user '{}'", roles, existingUser.getEmailAddress());
            userRoleRepository.delete(roles);
            final UserRole userRole = new UserRole(new UserRoleId(existingUser, roleRepository.findByName(role)));
            logger.info("Assigning roles '{}' to user '{}'", userRole, existingUser.getEmailAddress());
            final UserRole saved = userRoleRepository.save(userRole);
            existingUser.setRoles(Collections.singletonList(saved));
        }

        logger.info("User {} updated user {}", updatingUser.getEmailAddress(), existingUser.getEmailAddress());
        auditService.trackUserActivity(updatingUser, UserActivityType.UPDATE, null);
        return updatedUser;
    }

    @Override
    public User deleteUser(@Nonnull final Long userId, @Nonnull final Long updaterUserId) {
        auditService.trackUserActivity(getUser(updaterUserId), UserActivityType.UPDATE_ANOTHER_USER, "User's " + userId + " was deleted.");
        return updateUserStatus(userId, updaterUserId, DELETED);
    }

    private User updateUserStatus(@Nonnull final Long userId, @Nonnull final Long updaterUserId, @Nonnull final Status status) {
        final User user = findActiveUserById(userId);
        final User updaterUser = findActiveUserById(updaterUserId);

        user.setStatus(status);
        user.setModifiedBy(updaterUser);
        user.setModifiedDate(new Date());

        logger.info("User {} updated the user's {} status as {}", updaterUser, userId, status);
        auditService.trackUserActivity(updaterUser, UserActivityType.UPDATE_ANOTHER_USER, "User's " + userId + " status was updated to " + status);
        return userRepository.save(user);
    }

    @Override
    public void resetLoginFailureCounter(final Long id) {
        final User user = findActiveUserById(id);
        user.setFailedLoginAttempts(0);
        userRepository.save(user);
    }

    @Override
    public void recordLoginFailure(final Long id) {
        final User user = findActiveUserById(id);

        auditService.trackUserActivity(user, FAILED_LOGIN);

        final Integer currentFailedLoginAttempts = user.getFailedLoginAttempts();
        if (currentFailedLoginAttempts < Integer.MAX_VALUE) {
            user.setFailedLoginAttempts(currentFailedLoginAttempts + 1);
        }
        user.setLastFailedLoginDate(new Date());
        userRepository.save(user);
    }

    @Transactional(propagation = SUPPORTS, readOnly = true)
    @Override
    public Page<User> searchUsers(final String emailAddress, final String nickname, final String firstName, final String lastName, final Integer page,
                                  final Integer size) {
        final BooleanBuilder searchUsersQuery = searchUsersQuery(emailAddress, nickname, firstName, lastName);
        final QSort qSort = new QSort(new OrderSpecifier<>(Order.DESC, QUser.user.id));
        return userRepository.findAll(searchUsersQuery, new PageRequest(page, size, qSort));
    }

    private BooleanBuilder searchUsersQuery(final String emailAddress, final String nickname, final String firstName, final String lastName) {
        final QUser Q = QUser.user;
        final BooleanBuilder builder = new BooleanBuilder();
        if (emailAddress != null) {
            builder.and(Q.emailAddress.contains(emailAddress));
        }
        if (nickname != null) {
            builder.and(Q.nickname.contains(nickname));
        }
        if (firstName != null) {
            builder.and(Q.firstName.contains(firstName));
        }
        if (lastName != null) {
            builder.and(Q.lastName.contains(lastName));
        }
        return builder;
    }
}
