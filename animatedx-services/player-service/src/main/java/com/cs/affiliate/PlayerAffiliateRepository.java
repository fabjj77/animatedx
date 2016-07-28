package com.cs.affiliate;

import com.cs.player.Player;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * @author Joakim Gottzén
 */
@Repository
interface PlayerAffiliateRepository extends JpaRepository<PlayerAffiliate, Long>, QueryDslPredicateExecutor<PlayerAffiliate> {

    @Query("select pa from PlayerAffiliate pa where pa.createdDate between ?1 and ?2")
    List<PlayerAffiliate> getNewRegisteredPlayers(final Date startDate, final Date endDate);

    @Query(value = "select pa.player from PlayerAffiliate pa")
    List<Player> getAllPlayers();
}
