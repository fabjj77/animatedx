package com.cs.bonus;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * @author Hadi Movaghar
 */
public enum BonusStatus {
    UNUSED,
    ACTIVE,
    INACTIVE,
    RESERVED,
    VOIDED,
    GOAL_CONVERTED,
    LOST,
    NEGLIGIBLE_CONVERTED,
    MOVED_TO_NEXT_BONUS,
    EXPIRED,
    CANCELLED,
    COMPLETED, // for free spins

    /**
     * Special status used for bonuses that have been migrated by the system.
     */
    MIGRATED,
    SCHEDULED;

    private static final Collection<BonusStatus> currentBonuses = Collections.unmodifiableCollection(Arrays.asList(ACTIVE, INACTIVE, RESERVED));
    private static final Collection<BonusStatus> cancellableBonuses = Collections.unmodifiableCollection(Arrays.asList(ACTIVE, INACTIVE, UNUSED));
    private static final Collection<BonusStatus> completedStatuses =
            Collections.unmodifiableCollection(Arrays.asList(VOIDED, GOAL_CONVERTED, LOST, NEGLIGIBLE_CONVERTED, MOVED_TO_NEXT_BONUS, EXPIRED, CANCELLED, MIGRATED));

    public boolean isCompleted() {
        return completedStatuses.contains(this);
    }

    public boolean isCancellable() {
        return cancellableBonuses.contains(this);
    }

    public static Collection<BonusStatus> currentBonusStatuses() {
        return currentBonuses;
    }
}
