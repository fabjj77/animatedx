package com.cs.messaging.bronto;

import com.cs.messaging.email.BrontoWorkflow;
import com.cs.messaging.email.BrontoWorkflowName;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.stereotype.Repository;

/**
 * @author Omid Alaepour
 */
@Repository
public interface EmailWorkflowRepository extends JpaRepository<BrontoWorkflow, Long>, QueryDslPredicateExecutor<BrontoWorkflow> {

    BrontoWorkflow findByName(final BrontoWorkflowName brontoWorkflowName);
}
