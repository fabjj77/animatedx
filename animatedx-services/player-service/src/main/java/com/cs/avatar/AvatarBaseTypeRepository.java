package com.cs.avatar;

import com.cs.persistence.Status;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Omid Aleapour
 */
@Repository
public interface AvatarBaseTypeRepository extends JpaRepository<AvatarBaseType, Long>, QueryDslPredicateExecutor<AvatarBaseType> {

    List<AvatarBaseType> findByStatus(final Status status);
}
