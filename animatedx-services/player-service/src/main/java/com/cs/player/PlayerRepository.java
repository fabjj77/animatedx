package com.cs.player;

import com.cs.persistence.Status;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * @author Joakim Gottzén
 */
@Repository
public interface PlayerRepository extends JpaRepository<Player, Long>, QueryDslPredicateExecutor<Player> {

    Player findByEmailAddress(final String emailAddress);

    Player findByNickname(final String nickname);

    List<Player> findByFirstNameAndLastName(final String firstName, final String lastName);

    List<Player> findByStatusNot(final Status status);

    @Query("select p from Player p where p.createdDate < ?1 and p.testAccount = 'false'")
    List<Player> getNonTestPlayersCreatedBefore(final Date date);

    List<Player> findByTrustLevelAndStatusNot(final TrustLevel trustLevel, final Status status);

    @Modifying
    @Query("update Player p set p.blockType = ?1 where p.blockEndDate <= ?2 and p.blockType in (?3)")
    Integer resetExpiredLimitBlocks(final BlockType unblocked, final Date expiryDate, final List<BlockType> limitBlocks);
}
