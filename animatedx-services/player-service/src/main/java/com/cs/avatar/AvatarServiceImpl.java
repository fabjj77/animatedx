package com.cs.avatar;

import com.cs.persistence.NotFoundException;
import com.cs.persistence.Status;
import com.cs.player.Player;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mysema.query.types.Order;
import com.mysema.query.types.OrderSpecifier;
import com.mysema.query.types.Predicate;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.cs.persistence.Status.ACTIVE;
import static org.springframework.transaction.annotation.Isolation.READ_COMMITTED;
import static org.springframework.transaction.annotation.Propagation.SUPPORTS;

/**
 * @author Omid Alaepour
 */
@Service
@Transactional(isolation = READ_COMMITTED)
public class AvatarServiceImpl implements AvatarService {
    private final AvatarBaseTypeRepository avatarBaseTypeRepository;
    private final AvatarRepository avatarRepository;
    private final AvatarHistoryRepository avatarHistoryRepository;

    @Autowired
    public AvatarServiceImpl(final AvatarBaseTypeRepository avatarBaseTypeRepository, final AvatarRepository avatarRepository,
                             final AvatarHistoryRepository avatarHistoryRepository) {
        this.avatarBaseTypeRepository = avatarBaseTypeRepository;
        this.avatarRepository = avatarRepository;
        this.avatarHistoryRepository = avatarHistoryRepository;
    }

    @Transactional(propagation = SUPPORTS, readOnly = true)
    @Override
    public List<AvatarBaseType> getAvatarBaseTypes(final Status status) {
        final List<AvatarBaseType> avatarBaseTypes = avatarBaseTypeRepository.findByStatus(status);
        if (avatarBaseTypes.isEmpty()) {
            throw new NotFoundException(status);
        }
        return avatarBaseTypes;
    }

    @Transactional(propagation = SUPPORTS, readOnly = true)
    @Override
    public List<Avatar> getAvatars(final Integer avatarBaseTypeId, final Long level) {
        final List<Avatar> avatars = avatarRepository.findByAvatarBaseTypeAndLevel(new AvatarBaseType(avatarBaseTypeId), new Level(level));
        if (avatars.isEmpty()) {
            throw new NotFoundException("No Avatar with avatarBaseType id: " + avatarBaseTypeId + " and level: " + level + " found.");
        }
        return avatars;
    }

    @Transactional(propagation = SUPPORTS, readOnly = true)
    @Override
    public List<Avatar> getActiveAvatars(final Long level) {
        final List<Avatar> avatars = avatarRepository.findByLevelAndStatus(new Level(level), ACTIVE);
        if (avatars.isEmpty()) {
            throw new NotFoundException("No active Avatar with level: " + level + " found.");
        }
        return avatars;
    }

    @Transactional(propagation = SUPPORTS, readOnly = true)
    @Override
    public Iterable<Avatar> getActiveAvatars(final Player player) {
        if (!validateChangeableAvatar(player)) {
            throw new AvatarChangeException("Player " + player.getId() + " is not allowed to change avatar.");
        }

        final QAvatar qAvatar = QAvatar.avatar;
        final Predicate predicate = qAvatar.level.eq(player.getAvatar().getLevel()).and(qAvatar.status.eq(ACTIVE));

        final Iterable<Avatar> avatars = avatarRepository.findAll(predicate);
        if (!avatars.iterator().hasNext()) {
            throw new NotFoundException("Active avatar not found with type: " + player.getAvatar().getAvatarBaseType() + " at level " + player.getAvatar().getLevel());
        }
        return avatars;
    }

    @Transactional(propagation = SUPPORTS, readOnly = true)
    @Override
    public Boolean validateChangeableAvatar(final Player player) {
        final Long avatarHistoryCount = avatarHistoryRepository.count(QAvatarHistory.avatarHistory.player.eq(player));
        return avatarHistoryCount < 1;
    }

    @Transactional(propagation = SUPPORTS, readOnly = true)
    @Override
    public Avatar getAvatar(final Long avatarId) {
        final Avatar avatar = avatarRepository.findOne(avatarId);
        if (avatar == null) {
            throw new NotFoundException(avatarId);
        }
        return avatar;
    }

    @Transactional(propagation = SUPPORTS, readOnly = true)
    @Override
    public Avatar getAvatarForLevel(final Avatar avatar, final Level level) {
        return getAvatar(avatar.getAvatarBaseType(), avatar.getHairColor(), avatar.getSkinColor(), level);
    }

    private Avatar getAvatar(final AvatarBaseType avatarBaseType, final HairColor hairColor, final SkinColor skinColor, final Level level) {
        final Avatar avatar = avatarRepository.findByAvatarBaseTypeAndHairColorAndSkinColorAndLevel(avatarBaseType, hairColor, skinColor, level);
        if (avatar == null) {
            throw new NotFoundException("No Avatar with avatarBaseType id: " + avatarBaseType.getId() + ", hairColor: " + hairColor + ", skinColor: " + skinColor +
                                        " and level: " + level.getLevel() + " found.");
        }
        return avatar;
    }

    @Override
    public AvatarHistory insertChangedAvatarHistory(final Player player) {
        final AvatarHistory avatarHistory = new AvatarHistory();
        avatarHistory.setPlayer(player);
        avatarHistory.setAvatar(player.getAvatar());
        avatarHistory.setCreatedDate(new Date());
        return avatarHistoryRepository.save(avatarHistory);
    }

    @Override
    public Iterable<Avatar> getLevelUpAvatarHistory(final Player player)
    {
        final QAvatar qAvatar = QAvatar.avatar;
        final List<Long> levels = Arrays.asList(1L, 2L, 5L, 8L, 10L, 12L, 15L, 18L, 20L, 22L, 25L, 28L, 30L, 32L, 35L, 38L, 40L, 42L, 45L, 48L);

        final Predicate predicate = qAvatar.avatarBaseType.eq(player.getAvatar().getAvatarBaseType())
                .and(qAvatar.skinColor.eq(player.getAvatar().getSkinColor()))
                .and(qAvatar.hairColor.eq(player.getAvatar().getHairColor()))
                .and(qAvatar.level.level.loe(player.getLevel().getLevel())
                .and(qAvatar.level.level.in(levels)));

        final Iterable<Avatar> avatars = avatarRepository.findAll(predicate, new OrderSpecifier<>(Order.DESC, qAvatar.level.level));

        if (!avatars.iterator().hasNext()) {
            throw new NotFoundException(String.format("Avatars not found for player: %d", player.getId()));
        }

        return avatars;
    }
}
