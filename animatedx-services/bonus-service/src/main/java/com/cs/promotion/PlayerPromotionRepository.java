package com.cs.promotion;

import com.cs.player.Player;

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
public interface PlayerPromotionRepository extends JpaRepository<PlayerPromotion, PlayerPromotionId>, QueryDslPredicateExecutor<PlayerPromotion> {

    @Query("select p from PlayerPromotion p where p.pk.player = ?1")
    List<PlayerPromotion> findPlayerAllPromotions(final Player player);

    @Query("select p from PlayerPromotion p where p.pk.player = ?1 and p.pk.promotion = ?2")
    PlayerPromotion findPlayerPromotion(final Player player, final Promotion promotion);

    @Query("select p from PlayerPromotion p where p.pk.player = ?1 and p.pk.promotion.promotionTriggers like '%SCHEDULE%' and p.pk.promotion.validTo > ?2")
    List<PlayerPromotion> findScheduledPlayerPromotions(final Player player, final Date validTo);
}
