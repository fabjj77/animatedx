package com.cs.item;

import com.cs.avatar.Level;
import com.cs.bonus.Bonus;
import com.cs.bonus.BonusService;
import com.cs.bonus.BonusType;
import com.cs.persistence.NotFoundException;
import com.cs.player.Player;
import com.cs.promotion.PlayerCriteria;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.springframework.transaction.annotation.Isolation.READ_COMMITTED;
import static org.springframework.transaction.annotation.Propagation.SUPPORTS;

/**
 * @author Omid Alaepour.
 */
@Service
@Transactional(isolation = READ_COMMITTED)
public class ItemServiceImpl implements ItemService {
    private static final Logger logger = LoggerFactory.getLogger(ItemServiceImpl.class);

    private final BonusService bonusService;
    private final PlayerItemRepository playerItemRepository;

    @Autowired
    public ItemServiceImpl(final BonusService bonusService, final PlayerItemRepository playerItemRepository) {
        this.bonusService = bonusService;
        this.playerItemRepository = playerItemRepository;
    }

    @Nonnull
    @Transactional(propagation = SUPPORTS, readOnly = true)
    @Override
    public List<PlayerItem> getPlayerItems(@Nonnull final Long playerId, @Nonnull final ItemState itemState) {
        return playerItemRepository.findPlayerItemsByState(new Player(playerId), itemState);
    }

    @Override
    public PlayerItem useItem(final Long playerId, final Long itemId) {
        final PlayerItem playerItem = playerItemRepository.findPlayerItem(new Player(playerId), new Item(itemId));

        if (playerItem == null) {
            logger.error("Item with id {} for player with id {} was not found", itemId, playerId);
            throw new NotFoundException("Item with id " + itemId + " not found for user with playerId " + playerId);
        }

        if (playerItem.getItemState() != ItemState.UNUSED) {
            logger.error("Item with id {} was already used for player with id {}", itemId, playerId);
            throw new IllegalStateException("Unused Item with id " + itemId + " not found for user with playerId " + playerId);
        }

        final Bonus bonus = playerItem.getItem().getBonus();
        // First grant the bonus to the player
        // We only grant item bonuses on use
        bonusService.grantItemBonus(playerItem.getPlayer(), bonus);

        // Items that gives a deposit bonus has have to have special handling,
        // using the item doesn't actually grant the bonus, but makes it visible to the user.
        if (bonus.getBonusType() == BonusType.DEPOSIT_BONUS) {
            return setItemAsUsed(playerId, itemId, playerItem);
        } else if (bonusService.useBonus(playerItem.getPlayer(), bonus, new PlayerCriteria()) != null) {
            return setItemAsUsed(playerId, itemId, playerItem);
        } else {
            logger.error("Bonus {} for item {} already used for user with playerId {}", bonus.getId(), itemId, playerId);
            throw new IllegalStateException("Bonus " + bonus.getId() + " for item " + itemId + " already used for user with playerId " + playerId);
        }
    }

    private PlayerItem setItemAsUsed(final Long playerId, final Long itemId, final PlayerItem playerItem) {
        logger.info("Player {} used item with id {}", playerId, itemId);
        playerItem.setItemState(ItemState.USED);
        playerItem.setUsedDate(new Date());
        return playerItemRepository.save(playerItem);
    }

    @Nonnull
    @Override
    public List<PlayerItem> assignItems(@Nonnull final Player player, @Nonnull final Iterable<Level> levels) {
        final List<PlayerItem> playerItems = new ArrayList<>();
        final List<PlayerItem> alreadyAssignedItems = player.getPlayerItems();
        for (final Level level : levels) {
            final Item item = level.getItem();
            if (item != null) {
                final PlayerItemId id = new PlayerItemId(player, item);
                if (!alreadyAssignedItems.contains(new PlayerItem(id))) {
                    final PlayerItem playerItem = playerItemRepository.findOne(id);
                    // Only assign an item if the player doesn't already have it
                    if (playerItem == null) {
                        final PlayerItem newPlayerItem = new PlayerItem(id, ItemState.UNUSED);
                        playerItems.add(newPlayerItem);
                        logger.info("Player {}({}) awarded with item {}", player.getId(), player.getEmailAddress(), item);
                    }
                }
            }
        }

        // Do not assign the bonus to the player on assigning the item(s).
        // This way, we don't have to deal with the logic for whether the bonus is visible or not

        if (!playerItems.isEmpty()) {
            return playerItemRepository.save(playerItems);
        } else {
            return playerItems;
        }
    }
}
