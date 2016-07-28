package com.cs.game;

import com.cs.player.Player;
import com.cs.player.PlayerLimitation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.stereotype.Repository;

/**
 * @author Hadi Movaghar
 */
@Repository
public interface PlayerLimitationRepository extends JpaRepository<PlayerLimitation, Long>, QueryDslPredicateExecutor<PlayerLimitation> {

    PlayerLimitation findByPlayer(final Player player);
}
