package com.cs.whitelist;

import com.cs.player.Player;
import com.cs.user.User;

import org.springframework.data.domain.Page;

/**
 * @author Hadi Movaghar
 */
public interface WhiteListService {

    boolean isWhiteListedOnLogin(final Player player, final String stringIpAddress);

    Page<Player> getWhiteListedPlayers(final Integer page, final Integer size);

    Player getWhiteListedPlayer(final Long playerId);

    WhiteListedPlayer addPlayerToWhiteList(final User user, final Player player);

    void removePlayerFromWhiteList(final User user, final Player player);

    WhiteListedIpAddress getWhiteListedIpAddress(final String ipAddress);

    Page<WhiteListedIpAddress> getWhiteListedIpAddresses(final Integer page, final Integer size);

    WhiteListedIpAddress addIpAddressToWhiteList(final User user, final String fromIpAddress, final String toIpAddress);

    void removeIpAddressToWhiteList(final User user, final Long id);
}
