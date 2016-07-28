package com.cs.item;

import com.cs.player.Player;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Joakim Gottzén
 */
@Repository
public interface PlayerItemRepository extends JpaRepository<PlayerItem, PlayerItemId>, QueryDslPredicateExecutor<PlayerItem> {

    @Query("select p from PlayerItem p where p.pk.player = ?1 and p.itemState = ?2")
    List<PlayerItem> findPlayerItemsByState(final Player player, final ItemState itemState);

    @Query("select p from PlayerItem p where p.pk.player = ?1 and p.pk.item = ?2")
    PlayerItem findPlayerItem(final Player player, final Item item);
}
