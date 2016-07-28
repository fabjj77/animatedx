package com.cs.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.stereotype.Repository;

/**
 * @author Joakim Gottzén
 */
@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, UserRoleId>, QueryDslPredicateExecutor<UserRole> {
}
