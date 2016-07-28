package com.cs.player;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerTermsAndConditionsVersionRepository extends JpaRepository<PlayerTermsAndConditionsVersion, PlayerTermsAndConditionsVersionId>,
        QueryDslPredicateExecutor<PlayerTermsAndConditionsVersion> {
}
