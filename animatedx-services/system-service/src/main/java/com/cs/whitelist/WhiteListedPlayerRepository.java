package com.cs.whitelist;

import com.cs.player.Player;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.stereotype.Repository;

/**
 * @author Hadi Movaghar
 */
@Repository
public interface WhiteListedPlayerRepository extends JpaRepository<WhiteListedPlayer, Long>, QueryDslPredicateExecutor<WhiteListedPlayer> {

    WhiteListedPlayer findByPlayer(final Player player);

    WhiteListedPlayer findByPlayerAndDeletedFalse(final Player player);

    @Query("select wp.player from WhiteListedPlayer wp where wp.deleted = 'false'")
    Page<Player> findWhiteListedPlayers(Pageable pageable);

    @Query("select wp.player from WhiteListedPlayer wp where wp.player.id = ?1 and wp.deleted = 'false'")
    Player findWhiteListedPlayer(final Long playerId);
}

