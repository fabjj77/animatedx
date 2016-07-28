package com.cs.payment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.stereotype.Repository;

/**
 * @author Hadi Movaghar
 */
@Repository
public interface ProviderRepository extends JpaRepository<Provider, Integer>, QueryDslPredicateExecutor<Provider> {
}
