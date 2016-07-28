package com.cs.level;

import com.cs.avatar.Level;
import com.cs.payment.Money;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * @author Joakim Gottzén
 */
public interface LevelService {

    @Nullable
    Level getLevel(final Long level);

    @Nullable
    Level getLevelByTurnover(final Money turnover);

    Iterable<Level> getLevelsWithItems(@Nonnull final Long startLevel, @Nonnull final Long endLevel);

    List<Level> getAllLevels();
}
