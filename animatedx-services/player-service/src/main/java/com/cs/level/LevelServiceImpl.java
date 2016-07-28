package com.cs.level;

import com.cs.avatar.Level;
import com.cs.avatar.QLevel;
import com.cs.payment.Money;
import com.cs.persistence.Status;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mysema.query.types.Order;
import com.mysema.query.types.OrderSpecifier;
import com.mysema.query.types.expr.BooleanExpression;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

import static org.springframework.transaction.annotation.Isolation.READ_COMMITTED;
import static org.springframework.transaction.annotation.Propagation.SUPPORTS;

/**
 * @author Joakim Gottzén
 */
@Service
@Transactional(isolation = READ_COMMITTED)
public class LevelServiceImpl implements LevelService {
    final LevelRepository levelRepository;

    @Autowired
    public LevelServiceImpl(final LevelRepository levelRepository) {
        this.levelRepository = levelRepository;
    }

    @Transactional(propagation = SUPPORTS, readOnly = true)
    @Nullable
    @Override
    public Level getLevel(final Long level) {
        final Level l = levelRepository.findOne(level);
        if (l != null && l.getStatus() != Status.ACTIVE) {
            return null;
        }
        return l;
    }

    @Transactional(propagation = SUPPORTS, readOnly = true)
    @Nullable
    @Override
    public Level getLevelByTurnover(final Money turnover) {
        final Page<Level> level = levelRepository.findLevelByTurnover(turnover, new PageRequest(0, 1));
        if (!level.hasContent()) {
            return null;
        }
        return level.iterator().next();
    }

    @Transactional(propagation = SUPPORTS, readOnly = true)
    @Override
    public Iterable<Level> getLevelsWithItems(@Nonnull final Long startLevel, @Nonnull final Long endLevel) {
        final QLevel q = QLevel.level1;
        final BooleanExpression expression = q.level.between(startLevel, endLevel).and(q.item.isNotNull()).and(q.status.eq(Status.ACTIVE));
        return levelRepository.findAll(expression, new OrderSpecifier<>(Order.DESC, q.level));
    }

    @Transactional(propagation = SUPPORTS, readOnly = true)
    @Override
    public List<Level> getAllLevels() {
        return levelRepository.findByStatus(Status.ACTIVE);
    }
}
