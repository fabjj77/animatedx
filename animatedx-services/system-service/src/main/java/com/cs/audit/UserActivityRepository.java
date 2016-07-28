package com.cs.audit;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.stereotype.Repository;

/**
 * @author Hadi Movaghar
 */
@Repository
public interface UserActivityRepository extends JpaRepository<UserActivity, Long>, QueryDslPredicateExecutor<UserActivity> {
}
