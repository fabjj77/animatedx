package com.cs.comment;

import com.cs.player.PlayerComment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.stereotype.Repository;

/**
 * @author Hadi Movaghar
 */
@Repository
public interface PlayerCommentRepository extends JpaRepository<PlayerComment, Long>, QueryDslPredicateExecutor<PlayerComment> {
}
