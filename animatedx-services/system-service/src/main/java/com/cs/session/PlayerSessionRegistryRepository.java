package com.cs.session;

import com.cs.player.Player;
import com.cs.system.PlayerSessionRegistry;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

/**
 * @author Hadi Movaghar
 */
public interface PlayerSessionRegistryRepository extends JpaRepository<PlayerSessionRegistry, Long>, QueryDslPredicateExecutor<PlayerSessionRegistry> {

    PlayerSessionRegistry findBySessionId(final String sessionId);

    PlayerSessionRegistry findByPlayer(final Player player);
}
