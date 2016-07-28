package com.cs.agreement;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TermsAndConditionsVersionRepository extends JpaRepository<TermsAndConditionsVersion, Long>, QueryDslPredicateExecutor<TermsAndConditionsVersion> {

    TermsAndConditionsVersion findByActiveTrue();

    TermsAndConditionsVersion findByVersion(final String version);
}
