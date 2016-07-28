package com.cs.affiliate;

import com.cs.messaging.sftp.PlayerRegistration;
import com.cs.player.Player;

import java.util.Date;
import java.util.List;

/**
 * @author Joakim Gottzén
 */
public interface AffiliateService {

    PlayerAffiliate createPlayerAffiliate(final Player player, final String bTag, final String ipAddress);

    List<PlayerRegistration> getPlayerRegistrations(final Date startDate, final Date endDate);

    List<Player> getAllPlayers();
}
