package com.cs.messaging.bronto;

import com.cs.messaging.email.BrontoContact;
import com.cs.player.Player;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.stereotype.Repository;

/**
 * @author Omid Alaepour
 */
@Repository
public interface EmailContactRepository extends JpaRepository<BrontoContact, Long>, QueryDslPredicateExecutor<BrontoContact> {

    BrontoContact findByPlayer(final Player player);
}
