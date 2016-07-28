package com.cs.avatar;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.stereotype.Repository;

/**
 * @author Omid Alaepour
 */
@Repository
public interface AvatarHistoryRepository extends JpaRepository<AvatarHistory, Long>, QueryDslPredicateExecutor<AvatarHistory> {
}
