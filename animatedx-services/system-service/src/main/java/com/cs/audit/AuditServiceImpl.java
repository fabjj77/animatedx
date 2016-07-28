package com.cs.audit;

import com.cs.player.Player;
import com.cs.user.User;
import com.cs.util.CalendarUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.querydsl.QSort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.mysema.query.types.Order;
import com.mysema.query.types.OrderSpecifier;
import com.mysema.query.types.expr.BooleanExpression;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.springframework.transaction.annotation.Isolation.READ_COMMITTED;
import static org.springframework.transaction.annotation.Propagation.SUPPORTS;

/**
 * @author Hadi Movaghar
 */
@Service
@Transactional(isolation = READ_COMMITTED)
public class AuditServiceImpl implements AuditService {
    private final PlayerActivityRepository playerActivityRepository;
    private final UserActivityRepository userActivityRepository;

    @Autowired
    public AuditServiceImpl(final PlayerActivityRepository playerActivityRepository, final UserActivityRepository userActivityRepository) {
        this.playerActivityRepository = playerActivityRepository;
        this.userActivityRepository = userActivityRepository;
    }

    @Override
    public void trackPlayerActivity(@Nonnull final Player player, @Nonnull final PlayerActivityType playerActivityType) {
        final PlayerActivity playerActivity = new PlayerActivity(player, playerActivityType, new Date());
        playerActivityRepository.save(playerActivity);
    }

    @Override
    public void trackPlayerActivityWithIpAddress(@Nonnull final Player player, @Nonnull final PlayerActivityType playerActivityType, @Nonnull final String ipAddress) {
        final PlayerActivity playerActivity = new PlayerActivity(player, playerActivityType, new Date());
        playerActivity.setIpAddress(ipAddress);
        playerActivityRepository.save(playerActivity);
    }

    @Override
    public void trackPlayerActivityWithSessionId(@Nonnull final Player player, @Nonnull final PlayerActivityType playerActivityType, @Nullable final String sessionId) {
        final PlayerActivity playerActivity = new PlayerActivity(player, playerActivityType, new Date());
        playerActivity.setSessionId(sessionId);
        playerActivityRepository.save(playerActivity);
    }

    @Override
    public void trackPlayerActivityWithDescription(@Nonnull final Player player, @Nonnull final PlayerActivityType playerActivityType, @Nonnull final String description) {
        final PlayerActivity playerActivity = new PlayerActivity(player, playerActivityType, new Date());
        playerActivity.setDescription(description);
        playerActivityRepository.save(playerActivity);
    }

    @Override
    public void trackPlayerActivity(@Nonnull final PlayerActivity playerActivity) {
        playerActivityRepository.save(playerActivity);
    }

    @Transactional(propagation = SUPPORTS)
    @Nullable
    @Override
    public Date getPlayerLastLoginDate(@Nonnull final Player player) {
        return playerActivityRepository.findLastActivity(player, PlayerActivityType.LOGIN).getActivityDate();
    }

    @Override
    public void trackUserActivity(@Nonnull final User user, @Nonnull final UserActivityType userActivityType) {
        userActivityRepository.save(new UserActivity(user, userActivityType, new Date()));
    }

    @Override
    public void trackUserActivity(@Nonnull final User user, @Nonnull final UserActivityType userActivityType, @Nonnull final String description) {
        userActivityRepository.save(new UserActivity(user, userActivityType, new Date(), description));
    }

    @Transactional(propagation = SUPPORTS)
    @Override
    public Page<UserActivity> getUserActivities(@Nullable final Long userId, @Nullable final UserActivityType userActivityType, @Nonnull final Date startDate,
                                                @Nonnull final Date endDate, @Nonnull final Integer page, @Nonnull final Integer size) {
        final Calendar instance = Calendar.getInstance();
        final Date startDateTrimmed = CalendarUtils.startOfDay(instance, startDate);
        final Date endDateTrimmed = CalendarUtils.endOfDay(instance, endDate);
        final QUserActivity userActivity = QUserActivity.userActivity;

        BooleanExpression expression = userActivity.activityDate.between(startDateTrimmed, endDateTrimmed);

        if (userId != null) {
            expression = expression.and(userActivity.user.id.eq(userId));
        }
        if (userActivityType != null) {
            expression = expression.and(userActivity.userActivityType.eq(userActivityType));
        }

        final PageRequest pageRequest = new PageRequest(page, size, new QSort(new OrderSpecifier<>(Order.DESC, userActivity.activityDate)));

        return userActivityRepository.findAll(expression, pageRequest);
    }

    @Transactional(propagation = SUPPORTS)
    @Override
    public Page<PlayerActivity> getPlayerActivities(@Nullable final Long playerId, @Nullable final PlayerActivityType playerActivityType, @Nonnull final Date startDate,
                                                    @Nonnull final Date endDate, @Nonnull final Integer page, @Nonnull final Integer size) {
        final Calendar instance = Calendar.getInstance();
        final Date startDateTrimmed = CalendarUtils.startOfDay(instance, startDate);
        final Date endDateTrimmed = CalendarUtils.endOfDay(instance, endDate);
        final QPlayerActivity playerActivity = QPlayerActivity.playerActivity;

        BooleanExpression expression = playerActivity.activityDate.between(startDateTrimmed, endDateTrimmed);

        if (playerId != null) {
            expression = expression.and(playerActivity.player.id.eq(playerId));
        }
        if (playerActivityType != null) {
            expression = expression.and(playerActivity.activity.eq(playerActivityType));
        }

        final PageRequest pageRequest = new PageRequest(page, size, new QSort(new OrderSpecifier<>(Order.DESC, playerActivity.activityDate)));

        return playerActivityRepository.findAll(expression, pageRequest);
    }

    @Transactional(propagation = SUPPORTS)
    @Override
    public List<PlayerActivity> getPlayerActivities(@Nullable final Long playerId, @Nullable final PlayerActivityType playerActivityType, @Nonnull final Date startDate,
                                                    @Nonnull final Date endDate) {
        final Calendar instance = Calendar.getInstance();
        final Date startDateTrimmed = CalendarUtils.startOfDay(instance, startDate);
        final Date endDateTrimmed = CalendarUtils.endOfDay(instance, endDate);
        final QPlayerActivity playerActivity = QPlayerActivity.playerActivity;

        BooleanExpression expression = playerActivity.activityDate.between(startDateTrimmed, endDateTrimmed);

        if (playerId != null) {
            expression = expression.and(playerActivity.player.id.eq(playerId));
        }
        if (playerActivityType != null) {
            expression = expression.and(playerActivity.activity.eq(playerActivityType));
        }

        return Lists.newArrayList(playerActivityRepository.findAll(expression));
    }
}
