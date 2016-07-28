package com.cs.control;

import com.cs.system.SystemControl;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.stereotype.Repository;

/**
 * @author Omid Alaepour
 */
@Repository
public interface SystemControlRepository extends JpaRepository<SystemControl, Long>, QueryDslPredicateExecutor<SystemControl> {
}
