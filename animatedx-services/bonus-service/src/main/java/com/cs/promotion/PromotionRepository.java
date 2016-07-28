package com.cs.promotion;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * @author Hadi Movaghar
 */
@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Long>, QueryDslPredicateExecutor<Promotion> {

    @Query("select p from Promotion p where p.validFrom <= ?1 and p.validTo >= ?1")
    List<Promotion> findActivePromotions(final Date now);
}
