package com.cs.player;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.stereotype.Repository;

/**
 * @author Hadi Movaghar
 */
@Repository
public interface PlayerRegisterTrackRepository extends JpaRepository<PlayerRegisterTrack, Long>, QueryDslPredicateExecutor<PlayerRegisterTrack> {
}
