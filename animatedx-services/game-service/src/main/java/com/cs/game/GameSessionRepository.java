package com.cs.game;

import com.cs.player.Player;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.stereotype.Repository;

/**
 * @author Hadi Movaghar
 */
@Repository
public interface GameSessionRepository extends JpaRepository<GameSession, Long>, QueryDslPredicateExecutor<GameSession> {

    GameSession findByPlayer(final Player player);
}
