package com.cs.bonus;

import com.cs.payment.DCPaymentTransaction;
import com.cs.payment.PaymentTransaction;
import com.cs.player.Player;
import com.cs.promotion.Promotion;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * @author Hadi Movaghar
 */
@Repository
interface PlayerBonusRepository extends JpaRepository<PlayerBonus, Long>, QueryDslPredicateExecutor<PlayerBonus> {

    @Query("select p from PlayerBonus p where p.pk.player = ?1 and p.pk.bonus = ?2")
    List<PlayerBonus> findPlayerBonuses(final Player player, final Bonus bonus);

    @Query("select p from PlayerBonus p where p.pk.player = ?1 and p.pk.bonus = ?2 and p.usedDate is null and p.status = 'UNUSED'")
    List<PlayerBonus> findPlayerNotUsedBonuses(final Player player, final Bonus bonus);

    @Query("select p from PlayerBonus p where p.pk.player = ?1 and p.usedDate is null and p.status = 'UNUSED'")
    List<PlayerBonus> findPlayerNotUsedBonuses(final Player player);

    @Query("select p from PlayerBonus p where p.pk.player = ?1 and p.status =?2 order by p.usedDate desc")
    List<PlayerBonus> findByStatusAndDescDate(final Player player, final BonusStatus bonusStatus);

    @Query("select p from PlayerBonus p where p.pk.player = ?1 and p.status =?2 and p.usedDate <= ?3")
    List<PlayerBonus> findByStatusBefore(final Player player, final BonusStatus bonusStatus, final Date date);

    @Query("select p from PlayerBonus p where p.pk.player = ?1 and p.status =?2")
    PlayerBonus findPlayerBonus(final Player player, final BonusStatus bonusStatus);

    @Query("select p from PlayerBonus p where p.pk.player = ?1 and p.status in ?2 and p.pk.bonus.bonusType in ?3")
    List<PlayerBonus> findPlayerBonuses(final Player player, final Collection<BonusStatus> bonusStatuses, final Collection<BonusType> bonusTypes);

    @Query("select p from PlayerBonus p where p.pk.player = ?1 and p.paymentTransaction = ?2 and p.status in ?3")
    List<PlayerBonus> findByPaymentTransaction(final Player player, final PaymentTransaction paymentTransaction, final BonusStatus bonusStatus);

    @Query("select p from PlayerBonus p where p.pk.player = ?1 and p.dcPaymentTransaction = ?2 and p.status in ?3")
    List<PlayerBonus> findByPaymentTransaction(final Player player, final DCPaymentTransaction dcPaymentTransaction, final BonusStatus bonusStatus);

    @Query(value = "select player_id, sum(inactive), sum(reserved) from (select player_id, if(status = 'INACTIVE',  sum(current_balance), 0) inactive, " +
                   "if(status = 'RESERVED',  sum(current_balance), 0) reserved from players_bonuses where status in ('INACTIVE' , 'RESERVED') group by player_id, " +
                   "status) a group by player_id",
           nativeQuery = true)
    List<Object[]> getPlayersInactiveReservedBonusBalances();

    @Query("select p from PlayerBonus p where p.pk.player = ?1 and p.pk.bonus.promotion = ?2")
    List<PlayerBonus> findByPromotion(final Player player, final Promotion promotion);

    @Query(value = "select player_id, sum(current_balance) " +
                   "FROM players_bonuses " +
                   "where player_id in (select player_id from players_affiliates) and status in ('ACTIVE', 'INACTIVE') " +
                   "group by player_id", nativeQuery = true)
    List<Object[]> getAffiliatePlayersBonusBalances();

    @Query("select p from PlayerBonus p where p.pk.player = ?1 and p.pk.bonus.netentBonusId =?2 and p.status = 'COMPLETED'")
    List<PlayerBonus> findFreeRoundPlayerBonus(final Player player, final Long netentBonusId);

    @Query("select p from PlayerBonus p where p.pk.player = ?1 and p.activationDate <= ?2 and p.status = 'SCHEDULED'")
    List<PlayerBonus> findScheduledBonuses(Player player, Date date);
}
