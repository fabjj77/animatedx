package com.cs.audit;

import com.cs.player.Player;
import com.cs.user.User;

import org.springframework.data.domain.Page;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Date;
import java.util.List;

/**
 * @author Hadi Movaghar
 */
public interface AuditService {
    void trackPlayerActivity(@Nonnull final Player player, @Nonnull final PlayerActivityType playerActivityType);

    void trackPlayerActivityWithIpAddress(@Nonnull final Player player, @Nonnull final PlayerActivityType playerActivityType, @Nonnull final String ipAddress);

    void trackPlayerActivityWithSessionId(@Nonnull final Player player, @Nonnull final PlayerActivityType playerActivityType, @Nonnull final String sessionId);

    void trackPlayerActivityWithDescription(@Nonnull final Player player, @Nonnull final PlayerActivityType playerActivityType, @Nonnull final String description);

    void trackPlayerActivity(@Nonnull PlayerActivity playerActivity);

    Date getPlayerLastLoginDate(@Nonnull final Player player);

    void trackUserActivity(@Nonnull final User user, @Nonnull final UserActivityType userActivityType);

    void trackUserActivity(@Nonnull final User user, @Nonnull final UserActivityType userActivityType, @Nonnull final String description);

    Page<UserActivity> getUserActivities(@Nullable final Long userId, @Nullable UserActivityType userActivityType, @Nonnull Date startDate, @Nonnull Date endDate,
                                         @Nonnull final Integer page, @Nonnull final Integer size);

    Page<PlayerActivity> getPlayerActivities(@Nullable final Long playerId, @Nullable PlayerActivityType playerActivityType, @Nonnull Date startDate,
                                             @Nonnull Date endDate, @Nonnull final Integer page, @Nonnull final Integer size);

    List<PlayerActivity> getPlayerActivities(@Nullable final Long playerId, @Nullable final PlayerActivityType playerActivity, @Nonnull Date startDate,
                                             @Nonnull Date endDate);
}
