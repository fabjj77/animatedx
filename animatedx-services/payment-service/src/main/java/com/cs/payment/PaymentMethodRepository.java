package com.cs.payment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Hadi Movaghar
 */
@Repository
public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, Long>, QueryDslPredicateExecutor<PaymentMethod> {

    List<PaymentMethod> findByEnabled(final Boolean enabled);

    PaymentMethod findByMethodName(final String methodName);
}
