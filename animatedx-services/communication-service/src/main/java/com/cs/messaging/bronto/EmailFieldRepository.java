package com.cs.messaging.bronto;

import com.cs.messaging.email.BrontoField;
import com.cs.messaging.email.BrontoFieldName;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.stereotype.Repository;

/**
 * @author Omid Alaepour
 */
@Repository
public interface EmailFieldRepository extends JpaRepository<BrontoField, Long>, QueryDslPredicateExecutor<BrontoField> {

    BrontoField findByName(final BrontoFieldName brontoFieldName);
}
