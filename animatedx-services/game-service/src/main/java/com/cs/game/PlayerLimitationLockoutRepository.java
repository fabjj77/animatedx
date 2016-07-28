package com.cs.game;

import com.cs.player.PlayerLimitationLockout;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.stereotype.Repository;

/**
 * @author Hadi Movaghar
 */
@Repository
public interface PlayerLimitationLockoutRepository extends JpaRepository<PlayerLimitationLockout, Long>, QueryDslPredicateExecutor<PlayerLimitationLockout> {
}
