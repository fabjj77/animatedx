package com.cs.level;

import com.cs.avatar.Level;
import com.cs.payment.Money;
import com.cs.persistence.Status;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Joakim Gottzén
 */
@Repository
public interface LevelRepository extends JpaRepository<Level, Long>, QueryDslPredicateExecutor<Level> {

    List<Level> findByStatus(final Status status);

    @Query("select l from Level l where l.turnover <= ?1 and l.status = 'ACTIVE' order by l.level desc")
    Page<Level> findLevelByTurnover(final Money turnover, final Pageable pageable);
}
