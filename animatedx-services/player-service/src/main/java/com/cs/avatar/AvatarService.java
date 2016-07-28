package com.cs.avatar;

import com.cs.persistence.Status;
import com.cs.player.Player;

import java.util.List;

/**
 * @author Omid Alaepour
 */
public interface AvatarService {
    List<AvatarBaseType> getAvatarBaseTypes(final Status status);

    List<Avatar> getAvatars(final Integer avatarBaseTypeId, final Long level);

    List<Avatar> getActiveAvatars(final Long level);

    Avatar getAvatar(final Long avatarId);

    Avatar getAvatarForLevel(final Avatar avatar, final Level level);

    Iterable<Avatar> getActiveAvatars(final Player player);

    Boolean validateChangeableAvatar(final Player player);

    AvatarHistory insertChangedAvatarHistory(final Player player);

    Iterable<Avatar> getLevelUpAvatarHistory(final Player player);
}
