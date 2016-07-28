package com.cs.bonus;

import com.cs.promotion.Promotion;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Hadi Movaghar
 */
@Repository
public interface BonusRepository extends JpaRepository<Bonus, Long>, QueryDslPredicateExecutor<Bonus> {

    Bonus findByBonusCode(final String bonusCode);

    List<Bonus> findByPromotion(final Promotion promotion);
}
