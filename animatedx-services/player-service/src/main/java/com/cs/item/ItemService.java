package com.cs.item;

import com.cs.avatar.Level;
import com.cs.player.Player;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author Omid Alaepour.
 */
public interface ItemService {
    @Nonnull
    List<PlayerItem> getPlayerItems(@Nonnull final Long playerId, @Nonnull final ItemState itemState);

    PlayerItem useItem(final Long playerId, final Long itemId);

    @Nonnull
    List<PlayerItem> assignItems(@Nonnull final Player player, @Nonnull final Iterable<Level> levels);
}
