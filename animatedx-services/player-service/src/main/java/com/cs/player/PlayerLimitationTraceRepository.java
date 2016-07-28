package com.cs.player;

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
public interface PlayerLimitationTraceRepository extends JpaRepository<PlayerLimitationTrace, Long>, QueryDslPredicateExecutor<PlayerLimitationTrace> {

    @Query("select p from PlayerLimitationTrace p where p.limitationStatus = 'AWAITING' and p.applyDate <= ?1")
    List<PlayerLimitationTrace> findAwaitingLimitations(final Date date);
}
