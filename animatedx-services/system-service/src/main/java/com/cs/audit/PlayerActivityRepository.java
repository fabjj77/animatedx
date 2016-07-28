package com.cs.audit;

import com.cs.player.Player;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Hadi Movaghar
 */
@Repository
public interface PlayerActivityRepository extends JpaRepository<PlayerActivity, Long>, QueryDslPredicateExecutor<PlayerActivity> {

    @Query("select pa from PlayerActivity pa where pa.id = (select max(id) from PlayerActivity where player =?1 and pa.activity =?2)")
    PlayerActivity findLastActivity(final Player player, final PlayerActivityType playerActivityType);

    @Query(value = "select player_id, ip_address from player_activities where activity_type in ('REQ_CREATE_PLAYER', 'REQ_CREATE_PLAYER_DURING_CAMPAIGN')",
           nativeQuery = true)
    List<Object[]> getPlayersSignUpIPs();
}
