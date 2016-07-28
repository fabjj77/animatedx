package com.cs.game;

import com.cs.persistence.Language;
import com.cs.persistence.Status;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Omid Alaepour
 */
@Repository
public interface GameInfoRepository extends JpaRepository<GameInfo, Long>, QueryDslPredicateExecutor<GameInfo> {

    GameInfo findByGameAndLanguage(final Game game, final Language language);

    GameInfo findByGameAndLanguageAndClient(final Game game, final Language language, final String client);

    @Query("select g from GameInfo g where g.game.status = ?1 and g.client = ?2 and g.language = ?3 and g.game.category != ?4")
    List<GameInfo> findGamesForLobby(final Status status, final String client, final Language language, final GameCategory gameCategory);
}
