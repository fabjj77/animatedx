package com.cs.game;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Omid Alaepour
 */
@Repository
public interface GameRepository extends JpaRepository<Game, String>, QueryDslPredicateExecutor<Game> {

    @Query("select g from Game g left join fetch g.gameInfos")
    List<Game> findAllWithInfos();
}
