package com.cs.avatar;

import com.cs.persistence.Status;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Omid Alaepour
 */
@Repository
public interface AvatarRepository extends JpaRepository<Avatar, Long>, QueryDslPredicateExecutor<Avatar> {

    List<Avatar> findByAvatarBaseTypeAndLevel(final AvatarBaseType avatarBaseType, final Level level);

    List<Avatar> findByLevelAndStatus(final Level level, final Status status);

    Avatar findByAvatarBaseTypeAndHairColorAndSkinColorAndLevel(final AvatarBaseType avatarBaseType, final HairColor hairColor, final SkinColor skinColor,
                                                                final Level level);
}
