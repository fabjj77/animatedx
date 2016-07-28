package com.cs.agreement;

import com.cs.audit.AuditService;
import com.cs.audit.PlayerActivityType;
import com.cs.audit.UserActivityType;
import com.cs.persistence.InvalidArgumentException;
import com.cs.persistence.NotFoundException;
import com.cs.player.Player;
import com.cs.player.PlayerTermsAndConditionsVersion;
import com.cs.player.PlayerTermsAndConditionsVersionId;
import com.cs.player.PlayerTermsAndConditionsVersionRepository;
import com.cs.user.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.Date;

import static org.springframework.transaction.annotation.Isolation.READ_COMMITTED;
import static org.springframework.transaction.annotation.Propagation.SUPPORTS;

@Service
@Transactional(isolation = READ_COMMITTED)
public class TermsAndConditionsVersionServiceImpl implements TermsAndConditionsVersionService {

    private final Logger logger = LoggerFactory.getLogger(TermsAndConditionsVersionServiceImpl.class);

    private final AuditService auditService;
    private final PlayerTermsAndConditionsVersionRepository playerTermsAndConditionsVersionRepository;
    private final TermsAndConditionsVersionRepository termsAndConditionsVersionRepository;

    @Autowired
    public TermsAndConditionsVersionServiceImpl(final AuditService auditService, final TermsAndConditionsVersionRepository termsAndConditionsVersionRepository,
                                                final PlayerTermsAndConditionsVersionRepository playerTermsAndConditionsVersionRepository) {
        this.auditService = auditService;
        this.termsAndConditionsVersionRepository = termsAndConditionsVersionRepository;
        this.playerTermsAndConditionsVersionRepository = playerTermsAndConditionsVersionRepository;
    }

    @Nonnull
    @Override
    public TermsAndConditionsVersion createTermsAndConditions(@Nonnull final String version, final User user) {
        if (termsAndConditionsVersionRepository.findByVersion(version) != null) {
            logger.error("Terms and conditions version {} is already exist.", version);
            throw new InvalidArgumentException(String.format("Terms and conditions version %s is already exist.", version));
        }

        final TermsAndConditionsVersion termsAndConditionsVersion = new TermsAndConditionsVersion();
        termsAndConditionsVersion.setVersion(version);
        termsAndConditionsVersion.setCreatedDate(new Date());
        termsAndConditionsVersion.setActive(false);
        final TermsAndConditionsVersion savedTermsAndConditionsVersion = termsAndConditionsVersionRepository.save(termsAndConditionsVersion);

        auditService.trackUserActivity(user, UserActivityType.CREATED_TERMS_AND_CONDITIONS_VERSION,
                                       String.format("Terms and Conditions with version %s and id %d", version, savedTermsAndConditionsVersion .getId()));
        return savedTermsAndConditionsVersion;
    }

    @Nonnull
    @Override
    public TermsAndConditionsVersion activateTermsAndConditions(@Nonnull final Long id, final User user) {
        final TermsAndConditionsVersion termsAndConditionsVersion = termsAndConditionsVersionRepository.findOne(id);
        if (termsAndConditionsVersion == null) {
            logger.error("Terms and Conditions with id {} not found.", id);
            throw new NotFoundException("No Terms and Conditions with id " + id);
        }

        final TermsAndConditionsVersion activeTermsAndConditionsVersion = termsAndConditionsVersionRepository.findByActiveTrue();
        if (activeTermsAndConditionsVersion != null) {
            activeTermsAndConditionsVersion.setActive(false);
            termsAndConditionsVersionRepository.save(activeTermsAndConditionsVersion);
        }

        auditService.trackUserActivity(user, UserActivityType.ACTIVATED_TERMS_AND_CONDITIONS_VERSION,
                                       String.format("Terms and Conditions with version %s and id %d", termsAndConditionsVersion.getVersion(), id));

        termsAndConditionsVersion.setActive(true);
        termsAndConditionsVersion.setActivatedDate(new Date());
        return termsAndConditionsVersionRepository.save(termsAndConditionsVersion);
    }

    @Transactional(propagation = SUPPORTS)
    @Override
    public void acceptLatestTermsAndConditions(@Nonnull final Player player) {
        final TermsAndConditionsVersion activeVersion = getActiveTermsAndConditionsVersion();

        if (hasAcceptedLatestTermsAndConditions(player, activeVersion)) {
            logger.warn("Player {} already has accepted active terms and conditions.", player.getId());
            return;
        }

        final PlayerTermsAndConditionsVersion playerTermsAndConditionsVersion =
                new PlayerTermsAndConditionsVersion(new PlayerTermsAndConditionsVersionId(player, activeVersion));
        playerTermsAndConditionsVersion.setAcceptedDate(new Date());

        playerTermsAndConditionsVersionRepository.save(playerTermsAndConditionsVersion);

        auditService.trackPlayerActivityWithDescription(player, PlayerActivityType.ACCEPTED_TERRMS_AND_CONDITIONS,
                                                        String.format("Terms and Conditions with version %s and id %d", activeVersion.getVersion(), activeVersion.getId()));
    }

    @Transactional(propagation = SUPPORTS)
    @Override
    public boolean hasAcceptedLatestTermsAndConditions(final Player player) {
        return hasAcceptedLatestTermsAndConditions(player, getActiveTermsAndConditionsVersion());
    }

    @Transactional(propagation = SUPPORTS)
    private boolean hasAcceptedLatestTermsAndConditions(final Player player, final TermsAndConditionsVersion activeTermsAndConditionsVersion) {
        return playerTermsAndConditionsVersionRepository.findOne(new PlayerTermsAndConditionsVersionId(player, activeTermsAndConditionsVersion)) != null;
    }

    @Transactional(propagation = SUPPORTS)
    private TermsAndConditionsVersion getActiveTermsAndConditionsVersion() {
        final TermsAndConditionsVersion activeTermsAndConditionsVersion = termsAndConditionsVersionRepository.findByActiveTrue();
        if (activeTermsAndConditionsVersion == null) {
            logger.error("There is no active Terms and Conditions.");
            throw new NotFoundException("There is no active Terms and Conditions.");
        }
        return activeTermsAndConditionsVersion;
    }
}
