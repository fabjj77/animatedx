package com.cs.messaging.email;

import com.cs.persistence.Country;
import com.cs.player.Player;

/**
 * @author Omid Alaepour.
 */
public interface EmailService {

    void sendIpCountryMismatchEmail(final Player player, final String ipAddress, final Country country);
}
