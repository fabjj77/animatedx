package com.cs.avatar;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.stereotype.Repository;

/**
 * @author Hadi Movaghar
 */
@Repository
public interface ChapterTranslationRepository extends JpaRepository<ChapterTranslation, Long>, QueryDslPredicateExecutor<ChapterTranslation> {
}
