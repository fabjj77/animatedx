package com.cs.affiliate;

import com.cs.messaging.sftp.PlayerRegistration;
import com.cs.player.Player;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.springframework.transaction.annotation.Isolation.READ_COMMITTED;

/**
 * @author Joakim Gottzén
 */
@Service
@Transactional(isolation = READ_COMMITTED)
public class AffiliateServiceImpl implements AffiliateService {

    private final Logger logger = LoggerFactory.getLogger(AffiliateServiceImpl.class);

    private static final String BTAG_PATTERN = "(\\d{6})_(\\w{32})";
    private static final String UNKNOWN_IP_ADDRESS = "UNKNOWN";

    private final PlayerAffiliateRepository playerAffiliateRepository;

    private final Pattern bTagPattern;

    @Autowired
    public AffiliateServiceImpl(final PlayerAffiliateRepository playerAffiliateRepository) {
        this.playerAffiliateRepository = playerAffiliateRepository;

        bTagPattern = Pattern.compile(BTAG_PATTERN);
    }

    @Override
    public PlayerAffiliate createPlayerAffiliate(final Player player, final String bTag, final String ipAddress) {
        final Matcher matcher = bTagPattern.matcher(bTag);
        final String affiliateId;
        if (matcher.matches()) {
            affiliateId = matcher.group(1);
        } else {
            logger.info("No affiliate id found in the pattern for btag '{}'", bTag);
            affiliateId = "";
        }
        final PlayerAffiliate playerAffiliate = playerAffiliateRepository.save(new PlayerAffiliate(player, affiliateId, bTag, new Date(), ipAddress));
        logger.debug("Creating player affiliate {}", playerAffiliate);
        return playerAffiliate;
    }

    @Override
    public List<PlayerRegistration> getPlayerRegistrations(final Date startDate, final Date endDate) {
        final List<PlayerAffiliate> playerAffiliates = playerAffiliateRepository.getNewRegisteredPlayers(startDate, endDate);
        final List<PlayerRegistration> playerRegistrations = new ArrayList<>();
        final Date reportedDate = new Date();
        for (final PlayerAffiliate playerAffiliate : playerAffiliates) {
            final PlayerRegistration playerRegistration =
                    new PlayerRegistration(playerAffiliate.getPlayer().getId(), playerAffiliate.getPlayer().getAddress().getCountry().toString(),
                                           playerAffiliate.getIpAddress() != null ? playerAffiliate.getIpAddress() : UNKNOWN_IP_ADDRESS,
                                           playerAffiliate.getBTag(), playerAffiliate.getCreatedDate());
            playerRegistrations.add(playerRegistration);
            playerAffiliate.setReportedDate(reportedDate);
        }
        playerAffiliateRepository.save(playerAffiliates);
        return playerRegistrations;
    }

    @Override
    public List<Player> getAllPlayers() {
        return playerAffiliateRepository.getAllPlayers();
    }
}
