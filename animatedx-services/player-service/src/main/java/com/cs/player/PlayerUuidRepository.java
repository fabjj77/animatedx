package com.cs.player;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Omid Alaepour
 */
@Repository
public interface PlayerUuidRepository extends JpaRepository<PlayerUuid, String>, QueryDslPredicateExecutor<PlayerUuid> {

    List<PlayerUuid> findByPlayerAndUuidTypeAndUsedDateIsNull(final Player player, final UuidType uuidType);

    PlayerUuid findByUuidAndUuidTypeAndUsedDateIsNull(final String uuid, final UuidType uuidType);
}
