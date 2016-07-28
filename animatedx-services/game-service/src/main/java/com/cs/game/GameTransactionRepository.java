package com.cs.game;

import com.cs.game.GameTransaction;
import com.cs.player.Player;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.stereotype.Repository;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Date;
import java.util.List;

/**
 * @author Hadi Movaghar
 */
@Repository
interface GameTransactionRepository extends JpaRepository<GameTransaction, Long>, QueryDslPredicateExecutor<GameTransaction> {

    GameTransaction findByTransactionRef(final String transactionRef);

    @Query("select n from GameTransaction n where player =?1 and gameRoundRef =?2")
    List<GameTransaction> findPlayerGameRound(final Player player, final String gameRoundRef);

    @Query(value = "select n from GameTransaction  n where n.player = ?1 and n.createdDate between ?2 and ?3")
    Page<GameTransaction> findByPlayerBetweenDates(final Player player, @Nullable final Date fromDate, @Nullable final Date toDate, final Pageable pageable);

    @Query(value = "select n from GameTransaction  n where n.player = ?1 and n.createdDate between ?2 and ?3")
    List<GameTransaction> findByPlayerBetweenDates(final Player player, @Nullable final Date fromDate, @Nullable final Date toDate);

    @Query(value = "select player_id, sum(money_withdraw + bonus_withdraw) FROM netent_transactions where created_date >= ?1 and created_date <= ?2 group by " +
                   "player_id", nativeQuery = true)
    List<Object[]> getTotalBetByPlayerBetweenDates(@Nullable final Date fromDate, @Nullable final Date  toDate);

    @Query(value = "select n.game_id, n.game_round_ref, p.avatar_id, a.avatar_base_type_id, p.level, a.picture_url, p.nickname, ng.slug, ng.name, " +
                   "SUM(bonus_deposit + money_deposit) as biggest_win, " +
                   " (Select b.quantity from players_bonuses pb, bonuses b where b.id = pb.bonus_id and pb.player_id = n.player_bonus_id and b.type = \"FREE_ROUND\") " +
                   "as free_spins " +
                   "from netent_transactions n, players p, netent_games ng, avatars a " +
                   "where n.player_id = p.id and n.game_id = ng.game_id and  a.id = p.avatar_id and n.created_date between ?1 and ?2 " +
                   "group by n.game_id, n.game_round_ref, p.avatar_id, a.avatar_base_type_id, p.level, a.picture_url, p.nickname, ng.slug, ng.name " +
                   "order by biggest_win desc " +
                   "limit ?3 ", nativeQuery = true)
    List<Object[]> getLeaderPlayersByWeek(@Nonnull final Date startDate, @Nonnull final Date endDate, @Nonnull Integer pageSize);

    @Query(value = "select player_id, count(id), " +
                   "sum(money_deposit), sum(money_withdraw)," +
                   "sum(bonus_deposit), sum(bonus_withdraw)" +
                   "FROM netent_transactions " +
                   "where created_date >= ?1 and created_date <= ?2 and player_id in (select player_id from players_affiliates)" +
                   "group by player_id", nativeQuery = true)
    List<Object[]> getAffiliatePlayersGameTransactionSummary(@Nullable final Date fromDate, @Nullable final Date toDate);
}
