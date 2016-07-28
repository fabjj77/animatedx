package com.cs.bonus;

import com.cs.audit.AuditService;
import com.cs.audit.PlayerActivityType;
import com.cs.audit.UserActivityType;
import com.cs.avatar.Level;
import com.cs.game.GameService;
import com.cs.payment.DCPaymentTransaction;
import com.cs.payment.EventCode;
import com.cs.payment.Money;
import com.cs.payment.PaymentTransaction;
import com.cs.persistence.AlreadyAssignedException;
import com.cs.persistence.BonusException;
import com.cs.persistence.CommunicationException;
import com.cs.persistence.Country;
import com.cs.persistence.ExpiredEntityException;
import com.cs.persistence.IllegalOperationException;
import com.cs.persistence.InvalidArgumentException;
import com.cs.persistence.NotFoundException;
import com.cs.player.Player;
import com.cs.player.Wallet;
import com.cs.player.WalletRepository;
import com.cs.promotion.Criteria;
import com.cs.promotion.PlayerCriteria;
import com.cs.promotion.Promotion;
import com.cs.promotion.PromotionRepository;
import com.cs.promotion.PromotionTrigger;
import com.cs.promotion.RecurringTimeUnit;
import com.cs.promotion.TimeCriteria;
import com.cs.security.AccessDeniedException;
import com.cs.user.SecurityRole;
import com.cs.user.SecurityRole.Access;
import com.cs.user.User;
import com.cs.util.CalendarUtils;
import com.cs.util.Pair;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.querydsl.QSort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.mysema.query.types.Order;
import com.mysema.query.types.OrderSpecifier;
import com.mysema.query.types.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.springframework.transaction.annotation.Propagation.REQUIRED;
import static org.springframework.transaction.annotation.Propagation.SUPPORTS;

/**
 * @author Hadi Movaghar
 */
@Service
@Transactional(isolation = Isolation.READ_COMMITTED)
public class BonusServiceImpl implements BonusService {

    private static final Logger logger = LoggerFactory.getLogger(BonusServiceImpl.class);

    @Value("${bonus.bonus-conversion-goal-multiplier}")
    private Double bonusConversionGoalMultiplier;
    @Value("${game.id-on-netEnt-casino-module}")
    private String idOnNetEntCasinoModule;
    @Value("${game.password-on-netEnt-casino-module}")
    private String passwordOnNetEntCasinoModule;

    private final AuditService auditService;
    private final BonusRepository bonusRepository;
    private final GameService gameService;
    private final PlayerBonusRepository playerBonusRepository;
    private final PromotionRepository promotionRepository;
    private final WalletRepository walletRepository;

    @Autowired
    public BonusServiceImpl(final AuditService auditService, final BonusRepository bonusRepository, final GameService gameService,
                            final PlayerBonusRepository playerBonusRepository, final PromotionRepository promotionRepository, final WalletRepository walletRepository) {
        this.auditService = auditService;
        this.bonusRepository = bonusRepository;
        this.gameService = gameService;
        this.playerBonusRepository = playerBonusRepository;
        this.promotionRepository = promotionRepository;
        this.walletRepository = walletRepository;
    }

    @Override
    public Bonus getBonus(final Long bonusId) {
        return bonusRepository.findOne(bonusId);
    }

    @Override
    public Bonus getCreditsConversionBonus() {
        return bonusRepository.findOne(Bonus.CREDITS_TO_BONUS_MONEY_CONVERSION_BONUS);
    }

    @Override
    public List<Bonus> getAvailableBonuses(final Player player, final TriggerEvent triggerEvent) {
        final List<Bonus> triggeredBonuses = new ArrayList<>();

        for (final BonusType bonusType : triggerEvent.getBonusList()) {
            final List<Bonus> availableBonuses = getAvailableBonuses(player, bonusType);
            triggeredBonuses.addAll(availableBonuses);
        }

        logger.info("There are {} number of available Bonuses for Trigger Event {} for Player {}", triggeredBonuses.size(), triggerEvent, player.getId());
        return triggeredBonuses;
    }

    @Override
    public List<Bonus> getAvailableBonuses(final Player player, final BonusType bonusType) {
        final List<PlayerBonus> playerBonuses = playerBonusRepository.findPlayerNotUsedBonuses(player);
        final List<Bonus> availableBonuses = new ArrayList<>();

        for (final PlayerBonus playerBonus : playerBonuses) {
            final Bonus bonus = playerBonus.getPk().getBonus();
            if (bonus.getBonusType() == bonusType && meetGrantingCriteria(player, bonus)) {
                availableBonuses.add(playerBonus.getPk().getBonus());
            }
        }

        logger.info("There are {} number of available {} Bonuses for Player {}", availableBonuses.size(), bonusType, player.getId());
        return availableBonuses;
    }

    @Override
    public Bonus createBonus(final Bonus bonus, final Long promotionId, final Long levelId, final User user) {
        final Promotion promotion = promotionRepository.findOne(promotionId);

        if (promotion == null) {
            logger.error("User {} tried to create bonus with invalid promotion {}", user.getId(), promotionId);
            throw new NotFoundException("Promotion was not found with id: " + promotionId);
        }

        if (!bonus.isBonusPeriodValidAgainstPromotion(promotion)) {
            logger.error("Bonus period must be between promotion period from {} to {}", promotion.getValidFrom(), promotion.getValidTo());
            throw new InvalidArgumentException("Bonus period must be between promotion period from " + promotion.getValidFrom() + " to " + promotion.getValidTo());
        }

        final Criteria criteria = new Criteria();
        criteria.setLevel(new Level(levelId));
        bonus.setCriteria(criteria);
        bonus.setPromotion(promotion);
        bonus.setCreatedDate(new Date());
        bonus.setCreatedBy(user);
        return bonusRepository.save(bonus);
    }

    @Override
    public Bonus updateBonus(final Bonus bonus, final Long promotionId, final User user) {
        final Promotion promotion = promotionRepository.findOne(promotionId);

        if (promotion == null) {
            throw new NotFoundException("Promotion was not found with id: " + promotionId);
        }

        final Bonus bonusInDb = bonusRepository.findOne(bonus.getId());

        if (bonusInDb == null) {
            throw new NotFoundException("Bonus with id: " + bonus.getId() + " was not found to be updated");
        }

        if (bonusInDb.getValidTo().before(new Date())) {
            logger.warn("User {} tried to update expired bonus {}", user.getId(), bonus.getId());
            throw new ExpiredEntityException("Bonus " + bonus.getId() + " is expired.");
        }

        if (bonusInDb.getValidFrom().before(new Date())) {
            logger.warn("User {} tried to update an running bonus {}", user.getId(), bonus.getId());
            return updateRunningBonus(bonusInDb, bonus, user);
        }

        logger.warn("User {} tried to update a future bonus {}", user.getId(), bonus.getId());
        return updateFutureBonus(bonusInDb, bonus, promotionId, user);
    }

    private Bonus updateRunningBonus(final Bonus savedBonus, final Bonus bonus, final User user) {
        final boolean updated = savedBonus.updateRunningBonusFromBonus(bonus);
        if (updated) {
            savedBonus.setModifiedBy(user);
            savedBonus.setModifiedDate(new Date());
            logger.info("User {} updated future bonus {}", user.getId(), savedBonus.getId());
            return bonusRepository.save(savedBonus);
        } else {
            return savedBonus;
        }
    }

    private Bonus updateFutureBonus(final Bonus savedBonus, final Bonus bonus, final Long promotionId, final User user) {
        final Promotion promotion = promotionRepository.findOne(promotionId);
        final boolean updated = savedBonus.updateFutureBonusFromBonus(bonus, promotion);

        if (updated) {
            savedBonus.setModifiedBy(user);
            savedBonus.setModifiedDate(new Date());
            logger.info("User {} updated future bonus {}", user.getId(), savedBonus.getId());
            return bonusRepository.save(savedBonus);
        } else {
            return savedBonus;
        }
    }

    @Override
    public List<PlayerBonus> getCurrentPlayerBonuses(final Player player) {
        return playerBonusRepository.findPlayerBonuses(player, BonusStatus.currentBonusStatuses(), BonusType.currentBonusTypes());
    }

    @Override
    public Page<PlayerBonus> getAllPlayerBonuses(final Player player, final Integer page, final Integer size) {
        final Predicate predicate = QPlayerBonus.playerBonus.pk.player.eq(player);
        final PageRequest pageRequest = new PageRequest(page, size, new QSort(new OrderSpecifier<>(Order.DESC, QPlayerBonus.playerBonus.createdDate)));
        return playerBonusRepository.findAll(predicate, pageRequest);
    }

    @Override
    public PlayerBonus getPlayerBonus(final Long playerBonusId) {
        final PlayerBonus playerBonus = playerBonusRepository.findOne(playerBonusId);

        if (playerBonus == null) {
            logger.warn("Player-Bonus with id {} not found", playerBonusId);
            throw new NotFoundException("PlayerBonus not found with id : " + playerBonusId);
        }

        return playerBonus;
    }

    @Override
    public List<PlayerBonus> getPlayerBonusesTiedToPayment(final Player player, final PaymentTransaction payment) {
        return playerBonusRepository.findByPaymentTransaction(player, payment, BonusStatus.RESERVED);
    }

    @Override
    public List<PlayerBonus> getPlayerBonusesTiedToPayment(final Player player, final DCPaymentTransaction payment) {
        return playerBonusRepository.findByPaymentTransaction(player, payment, BonusStatus.RESERVED);
    }

    @Override
    public List<PlayerBonus> getOldReservedPlayerBonuses(final Player player, final Date date) {
        return playerBonusRepository.findByStatusBefore(player, BonusStatus.RESERVED, date);
    }

    @Nonnull
    @Override
    public PlayerBonus updatePlayerBonus(@Nonnull final PlayerBonus playerBonus) {
        return playerBonusRepository.save(playerBonus);
    }

    @Nullable
    @Override
    public PlayerBonus grantBonusToPlayer(final Player player, final Bonus bonus) {
        if (!isBonusValid(bonus)) {
            return null;
        }

        logger.info("Granting Bonus {} to the Player {}", bonus.getId(), player.getId());
        // make sure there is always one instance of granted bonus
        final List<PlayerBonus> notUsedBonuses = playerBonusRepository.findPlayerNotUsedBonuses(player, bonus);
        if (!notUsedBonuses.isEmpty()) {
            logger.error("Bonus with id {} was already granted to Player {}", bonus.getId(), player.getId());
            return null;
        }

        if (bonus.getPromotion().getId().equals(Promotion.LEVEL_PROMOTION)) {
            logger.warn("Bonus with id {} of type {} linked to LEVEL_PROMOTION will be granted to Player {} after Item usage.",
                        bonus.getId(), bonus.getBonusType(), player.getId());
            return null;
        }

        if (bonus.getPromotion().getPromotionTriggers().contains(PromotionTrigger.LINK)) {
            logger.info("Bonus {} of type {} can not be granted to Player {} automatically for next time usage; It must be activated by a Link",
                        bonus.getId(), bonus.getBonusType(), player.getId());
            return null;
        }

        auditService.trackPlayerActivityWithDescription(player, PlayerActivityType.GRANTED_BONUS, String.format("Bonus %d of type %s granted to Player %d",
                                                                                                                bonus.getId(), bonus.getBonusType(), player.getId()));

        final PlayerBonus playerBonus = new PlayerBonus(new PlayerBonusPair(player, bonus));
        return playerBonusRepository.save(playerBonus);
    }

    private boolean isBonusValid(final Bonus bonus) {
        if (bonus == null) {
            logger.error("Bonus  was not found to be granted.");
            return false;
        }

        if (bonus.isExpired()) {
            logger.error("Expired Bonus {} was of type {} can not be granted. Expired on {}", bonus.getId(), bonus.getBonusType(), bonus.getValidTo());
            return false;
        }

        return true;
    }

    @Override
    @Nullable
    public List<PlayerBonus> grantScheduledBonusesByCriteria(final Player player, final Bonus bonus, final PlayerCriteria playerCriteria) {
        if (!meetUsageCriteria(player, bonus, playerCriteria)) {
            logger.error("Bonus {} can not be used by Player {} ", bonus.getId(), player.getId());
            return null;
        }

        return grantScheduledBonusesToPlayer(player, bonus);
    }

    @Nullable
    private List<PlayerBonus> grantScheduledBonusesToPlayer(final Player player, final Bonus bonus) {
        if (!isScheduledBonusValid(bonus)) {
            return null;
        }

        logger.info("Granting Scheduled Bonus {} to the Player {} for {} number of times", bonus.getId(), player.getId(), bonus.getMaxGrantNumbers());

        final List<PlayerBonus> grantedBonuses = new ArrayList<>();
        Long grantTime = new Date().getTime();

        if (bonus.getInitialGrantDelayHours() == 0) {
            grantedBonuses.add(grantBonusToPlayer(player, bonus));
        } else {
            grantTime = getNextGrantTime(grantTime, bonus.getInitialGrantDelayHours());
            grantedBonuses.add(grantScheduledBonusToPlayer(player, grantTime, bonus));
        }

        for (int i = 1; i <= bonus.getMaxGrantNumbers() - 1; i++) {
            grantTime = getNextGrantTime(grantTime, bonus.getNextGrantIntervalHours());
            grantedBonuses.add(grantScheduledBonusToPlayer(player, grantTime, bonus));
        }

        auditService.trackPlayerActivityWithDescription(player, PlayerActivityType.GRANTED_BONUS,
                                                        String.format("Scheduled Bonus %d of type %s granted to Player %d for %d number of times",
                                                                      bonus.getId(), bonus.getBonusType(), player.getId(), bonus.getMaxGrantNumbers())
        );

        return grantedBonuses;
    }

    private Long getNextGrantTime(final Long previousGrantTime, final Integer nextGrantIntervalHours) {
        return previousGrantTime + nextGrantIntervalHours * CalendarUtils.MILLISECOND_IN_AN_HOUR;
    }

    private PlayerBonus grantScheduledBonusToPlayer(final Player player, final Long nextGrantTime, final Bonus bonus) {
        final PlayerBonus playerBonus = new PlayerBonus(new PlayerBonusPair(player, bonus), new Date(nextGrantTime));
        return playerBonusRepository.save(playerBonus);
    }

    private boolean isScheduledBonusValid(final Bonus bonus) {
        if (!isBonusValid(bonus)) {
            return false;
        }

        if (!bonus.getPromotion().getPromotionTriggers().contains(PromotionTrigger.SCHEDULE)) {
            logger.error("Scheduled Bonus {} must corresponds to a {} Promotion", bonus.getId(), PromotionTrigger.SCHEDULE);
            return false;
        }

        if (bonus.getBonusGroup() == null) {
            logger.error("Scheduled Bonus {} must contain a group number", bonus.getId());
            return false;
        }

        if (bonus.getMaxGrantNumbers() == null) {
            logger.error("Scheduled Bonus {} must contain max grant numbers", bonus.getId());
            return false;
        }

        if (bonus.getInitialGrantDelayHours() == null) {
            logger.error("Scheduled Bonus {} must contain an initial grant delay in hours", bonus.getId());
            return false;
        }

        if (bonus.getNextGrantIntervalHours() == null) {
            logger.error("Scheduled Bonus {} must contain  next grant interval in hours", bonus.getId());
            return false;
        }

        return true;
    }

    @Override
    public void grantItemBonus(@Nonnull final Player player, @Nonnull final Bonus bonus) {
        if (!bonus.getPromotion().getId().equals(Promotion.CUSTOMER_SUPPORT_PROMOTION) &&
            !bonus.getPromotion().getId().equals(Promotion.LEVEL_PROMOTION)) {
            logger.error("Bonus with id {} can not be granted to Player {} ", bonus.getId(), player.getId());
        }

        if (bonus.getValidTo().before(new Date())) {
            logger.error("Expired Bonus {} was of type {} can not be granted to Player {} : Expired on {}",
                         bonus.getId(), bonus.getBonusType(), player.getId(), bonus.getValidTo());
            return;
        }

        // make sure there is always one instance of granted bonus
        final List<PlayerBonus> notUsedBonuses = playerBonusRepository.findPlayerNotUsedBonuses(player, bonus);
        if (!notUsedBonuses.isEmpty()) {
            logger.error("Bonus with id {} was already granted to Player {}", bonus.getId(), player.getId());
        } else {
            final String message = String.format("Item Bonus %d of type %s granted to Player %d", bonus.getId(), bonus.getBonusType(), player.getId());
            auditService.trackPlayerActivityWithDescription(player, PlayerActivityType.GRANTED_BONUS, message);

            final PlayerBonus playerBonus = new PlayerBonus(new PlayerBonusPair(player, bonus));
            playerBonusRepository.save(playerBonus);
            logger.info("Bonus {} linked to Item was granted to Player {}", bonus.getId(), player.getId());
        }
    }

    @Override
    public PlayerBonus grantLinkBonus(final Player player, final String bonusCode) {
        final Bonus bonus = bonusRepository.findByBonusCode(bonusCode);

        if (bonus == null) {
            logger.error("Bonus with code {} was not found to be granted to Player {}", bonusCode, player.getId());
            throw new NotFoundException("Bonus with code " + bonusCode + " was not found to be granted to Player " + player.getId());
        }

        if (bonus.getValidTo().before(new Date())) {
            logger.error("Expired Bonus {} was of type {} can not be granted to Player {} : Expired on {}",
                         bonus.getId(), bonus.getBonusType(), player.getId(), bonus.getValidTo());
            throw new ExpiredEntityException("Bonus was expired on " + bonus.getValidTo());
        }

        if (!bonus.getPromotion().getPromotionTriggers().contains(PromotionTrigger.LINK)) {
            logger.error("Bonus with code {} can not be granted to Player {} by link since Promotion Trigger is not {}",
                         bonusCode, player.getId(), PromotionTrigger.LINK);
            throw new IllegalOperationException("Bonus can not be granted via link");
        }

        // make sure there is always one instance of granted bonus
        final List<PlayerBonus> notUsedBonuses = playerBonusRepository.findPlayerNotUsedBonuses(player, bonus);
        if (!notUsedBonuses.isEmpty()) {
            logger.error("Bonus with id {} was already granted to Player {}", bonus.getId(), player.getId());
            throw new AlreadyAssignedException("Bonus with id " + bonus.getId() + " was already granted to Player " + player.getId());
        }

        auditService.trackPlayerActivityWithDescription(player, PlayerActivityType.GRANTED_BONUS, String.format("Link Bonus %d of type %s granted to Player %d",
                                                                                                                bonus.getId(), bonus.getBonusType(), player.getId()));

        final PlayerBonus playerBonus = new PlayerBonus(new PlayerBonusPair(player, bonus));
        playerBonusRepository.save(playerBonus);
        logger.info("Bonus {} with bonus code {} was granted to Player {}", bonus.getId(), bonusCode, player.getId());
        return playerBonus;
    }

    @Override
    public PlayerBonus handleFreeRoundsCompletionBonus(final Player player, final Money amount, final Long netentBonusId) {
        final List<PlayerBonus> playerBonuses = playerBonusRepository.findFreeRoundPlayerBonus(player, netentBonusId);
        if (playerBonuses.isEmpty()) {
            logger.error("Could not find free round player-bonus corresponding to netent bonus id {} for player {}", netentBonusId, player.getId());
            return null;
        }

        final PlayerBonus playerBonus = playerBonuses.get(0);
        playerBonus.setUsedAmount(amount);
        playerBonus.setCurrentBalance(amount);
        playerBonus.setBonusConversionProgress(Money.ZERO);
        playerBonus.setBonusConversionGoal(amount.multiply(bonusConversionGoalMultiplier));
        playerBonus.setMaxRedemptionAmount(playerBonus.getPk().getBonus().getMaxRedemptionAmount());
        playerBonus.setStatus(BonusStatus.INACTIVE);
        playerBonus.setValidFrom(playerBonus.getPk().getBonus().getValidFrom());
        playerBonus.setValidTo(playerBonus.getPk().getBonus().getValidTo());

        playerBonusRepository.save(playerBonus);

        // activate player-bonus if needed
        getActivePlayerBonus(player);
        return playerBonus;
    }

    private PlayerBonus createBonusMoneyPlayerBonus(final Player player, final Money amount, @Nullable final Money maxRedemptionAmount, final Date validFrom,
                                                    @Nullable final Date validTo, final Bonus bonus) {
        final PlayerBonus grantedBonusToPlayer = grantBonusToPlayer(player, bonus);
        if (grantedBonusToPlayer == null) {
            throw new BonusException("Bonus " + bonus.getId() + " is already granted and unused");
        }
        final PlayerBonus playerBonus = useBonus(player, bonus, new PlayerCriteria());
        if (playerBonus == null) {
            throw new BonusException("No bonus could be created for bonus id " + bonus.getId());
        }

        playerBonus.setUsedAmount(amount);
        playerBonus.setCurrentBalance(amount);
        playerBonus.setBonusConversionProgress(Money.ZERO);
        playerBonus.setBonusConversionGoal(amount.multiply(bonusConversionGoalMultiplier));
        playerBonus.setMaxRedemptionAmount(maxRedemptionAmount);
        playerBonus.setStatus(BonusStatus.INACTIVE);
        playerBonus.setValidFrom(validFrom);
        playerBonus.setValidTo(validTo);

        playerBonusRepository.save(playerBonus);

        // activate player-bonus if needed
        getActivePlayerBonus(player);
        return playerBonus;
    }

    @Override
    public void useBonuses(final Player player, final List<Bonus> bonuses, final PlayerCriteria playerCriteria) {
        for (final Bonus bonus : bonuses) {
            try {
                useBonus(player, bonus, playerCriteria);
            } catch (final RuntimeException ex) {
                logger.error("Can not use bonus {} for player {} because of: {}", bonus.getId(), player.getId(), ex.getMessage());
            }
        }
    }

    @Nullable
    @Override
    public PlayerBonus useBonus(@Nonnull final Player player, @Nonnull final Bonus bonus, @Nonnull final PlayerCriteria playerCriteria) {
        final List<PlayerBonus> playerBonuses = playerBonusRepository.findPlayerNotUsedBonuses(player, bonus);

        if (playerBonuses.isEmpty()) {
            logger.error("Could not find an granted and not used Bonus {} to Player {} to be used", bonus.getId(), player.getId());
            return null;
        } else if (playerBonuses.size() > 1) {
            logger.error("Found the same unused Bonus {} granted to Player {} {} times}", bonus.getId(), player.getId(), playerBonuses.size());
            return null;
        }

        return usePlayerBonusWithCriteriaCheck(player, playerBonuses.get(0), playerCriteria);
    }

    @Nullable
    private PlayerBonus usePlayerBonusWithCriteriaCheck(@Nonnull final Player player, @Nonnull final PlayerBonus playerBonus,
                                                        @Nonnull final PlayerCriteria playerCriteria) {
        final Bonus bonus = playerBonus.getPk().getBonus();

        if (!meetUsageCriteria(player, bonus, playerCriteria)) {
            logger.error("Bonus {} can not be used by Player {} ", bonus.getId(), player.getId());
            return null;
        }

        return usePlayerBonus(player, playerBonus, playerCriteria, bonus);
    }

    @Nullable
    private PlayerBonus usePlayerBonus(final Player player, final PlayerBonus playerBonus, final PlayerCriteria playerCriteria, final Bonus bonus) {
        PlayerBonus usedPlayerBonus = null;

        switch (bonus.getBonusType()) {
            case REAL_MONEY:
            case BONUS_MONEY:
                usedPlayerBonus = useMoneyBonus(player, bonus, playerBonus);
                break;
            case DEPOSIT_BONUS:
                if (playerCriteria == null || playerCriteria.getAmount() == null) {
                    logger.error("Bonus {} of type {} can not be assigned without a deposit amount", bonus.getId(), bonus.getBonusType());
                    return null;
                }
                usedPlayerBonus = useDepositBonus(player, bonus, playerBonus, playerCriteria.getAmount());
                break;
            case CREDITS:
                break;
            case FREE_ROUND:
                usedPlayerBonus = useFreeRoundBonus(player, bonus, playerBonus);
                break;
            case CREDITS_MULTIPLIER_X2:
                break;
            case CREDITS_MULTIPLIER_X3:
                break;
        }

        grantNextBonus(player, bonus);
        return usedPlayerBonus;
    }

    @Nullable
    private PlayerBonus grantNextBonus(final Player player, final Bonus bonus) {
        if (!bonus.isAutoGrant()) {
            logger.info("Bonus {} of type {} can not be granted to Player {} for next time, it is NOT auto-granted",
                        bonus.getId(), bonus.getBonusType(), player.getId());
            return null;
        }

        final List<PlayerBonus> playerBonuses = playerBonusRepository.findPlayerBonuses(player, bonus);

        // TODO : level deposits are not granted next time?? if so disable autoGrant field instead
        // if bonus == level deposit bonus then it will be granted before usage not here
        if (bonus.isLevelDepositBonus(player)) {
            logger.info("Bonus {} of type {} can not be granted to Player {} since is Level Deposit Bonus",
                        bonus.getId(), bonus.getBonusType(), player.getId());
            return null;
        }

        if (bonus.getMaxGrantNumbers() != null && playerBonuses.size() >= bonus.getMaxGrantNumbers()) {
            logger.info("Bonus {} of type {} can not be granted to Player {}; Granted times: {}, Bonus max grant numbers: {} ", bonus.getId(), bonus.getBonusType(),
                        player.getId(), playerBonuses.size(), bonus.getMaxGrantNumbers());
            return null;
        }

        return grantBonusToPlayer(player, bonus);
    }

    @Nullable
    private PlayerBonus useMoneyBonus(final Player player, final Bonus bonus, final PlayerBonus playerBonus) {
        final Money amount = bonus.getAmount();
        if (amount == null) {
            logger.error("Bonus {} of type {} must contain Amount value", bonus.getId(), bonus.getBonusType());
            return null;
        }

        final Wallet wallet = player.getWallet();
        if (bonus.getBonusType() == BonusType.REAL_MONEY) {
            logger.debug("Adding {} to player {} real money balance", amount, player.getId());
            wallet.setMoneyBalance(wallet.getMoneyBalance().add(amount));
            final String message = String.format("Real Money Bonus %d of amount %s used by Player %d", bonus.getId(), amount, player.getId());
            auditService.trackPlayerActivityWithDescription(player, PlayerActivityType.USED_MONEY_BONUS, message);
            walletRepository.save(wallet);
        } else if (bonus.getBonusType() == BonusType.BONUS_MONEY) {
            logger.debug("Adding {} to player {} bonus money balance in Player-Bonus {}", amount, player.getId(), playerBonus.getId());
            playerBonus.setCurrentBalance(amount);
            final Money goal = amount.multiply(bonusConversionGoalMultiplier);
            logger.debug("Adding {} to player {} bonus conversion goal in Player-Bonus {}", goal, player.getId(), playerBonus.getId());
            playerBonus.setBonusConversionGoal(goal);
            playerBonus.setBonusConversionProgress(Money.ZERO);
            final String message = String.format("Bonus Money (as Bonus) %d of amount %s used by Player %d in Player-Bonus %d",
                                                 bonus.getId(), amount, player.getId(), playerBonus.getId());
            auditService.trackPlayerActivityWithDescription(player, PlayerActivityType.USED_BONUS_AMOUNT, message);
        }

        playerBonus.setUsedAmount(amount);
        playerBonus.setUsedDate(new Date());
        playerBonus.setStatus(BonusStatus.INACTIVE);
        final PlayerBonus updatedBonus = playerBonusRepository.save(playerBonus);

        logger.info("Player {} used {} Bonus {} of amount {} in Player-Bonus {}", player.getId(), bonus.getBonusType(), bonus.getId(), amount, updatedBonus.getId());
        activateBonus(player, updatedBonus);
        return updatedBonus;
    }

    @Nullable
    private PlayerBonus useDepositBonus(final Player player, final Bonus bonus, final PlayerBonus playerBonus, final Money depositAmount) {
        if (bonus.isLevelDepositBonus(player)) {
            return useLevelDepositBonus(player, bonus, playerBonus, depositAmount);
        }

        if (bonus.getPercentage() == null || bonus.getMaxAmount() == null) {
            logger.error("Bonus {} of type {} must contain Percentage and Max Amount values", bonus.getId(), bonus.getBonusType());
            return null;
        }

        final Money bonusMoneyFromPercentage = depositAmount.multiply(BigDecimal.valueOf(bonus.getPercentage() / 100.0D));
        final Money depositBonus = bonusMoneyFromPercentage.min(bonus.getMaxAmount());

        return addDepositBonus(player, bonus, playerBonus, depositAmount, depositBonus);
    }

    @Nullable
    private PlayerBonus useLevelDepositBonus(final Player player, final Bonus bonus, final PlayerBonus playerBonus, final Money depositAmount) {
        if (bonus.getPercentage() == null) {
            logger.error("Bonus {} of type {} must contain Percentage", bonus.getId(), bonus.getBonusType());
            return null;
        }

        final Money depositBonus = depositAmount.multiply(BigDecimal.valueOf(bonus.getPercentage() / 100.0D));

        return addDepositBonus(player, bonus, playerBonus, depositAmount, depositBonus);
    }

    private PlayerBonus addDepositBonus(final Player player, final Bonus bonus, final PlayerBonus playerBonus, final Money depositAmount, final Money depositBonus) {
        logger.debug("Adding {} to player {} bonus money balance to player-bonus {}", depositBonus, player.getId(), playerBonus.getId());
        playerBonus.setCurrentBalance(depositBonus);

        playerBonus.setBonusConversionProgress(Money.ZERO);
        final Money goal = depositBonus.multiply(bonusConversionGoalMultiplier);
        logger.debug("Adding {} to player {} bonus conversion goal to player-bonus {}", goal, player.getId(), playerBonus.getId());
        playerBonus.setBonusConversionGoal(goal);

        playerBonus.setStatus(BonusStatus.INACTIVE);
        playerBonus.setUsedAmount(depositBonus);
        playerBonus.setUsedDate(new Date());
        playerBonusRepository.save(playerBonus);

        final String message = String.format("Deposit Money Bonus %d of amount %s used by Player %d the new turn-over goal: %s in Player-Bonus %d", bonus.getId(),
                                             depositBonus, player.getId(), playerBonus.getBonusConversionGoal(), playerBonus.getId());
        auditService.trackPlayerActivityWithDescription(player, PlayerActivityType.USED_DEPOSIT_BONUS, message);

        logger.info("Player {} used {} Bonus {} ; deposit amount {} and deposit bonus {} in Player-Bonus {}",
                    player.getId(), bonus.getBonusType(), bonus.getId(), depositAmount, depositBonus, playerBonus.getId());

        activateBonus(player, playerBonus);
        return playerBonus;
    }

    private void activateBonus(final Player player, final PlayerBonus playerBonus) {
        final PlayerBonus activePlayerBonus = playerBonusRepository.findPlayerBonus(player, BonusStatus.ACTIVE);

        if (activePlayerBonus == null) {
            playerBonus.setStatus(BonusStatus.ACTIVE);
            playerBonusRepository.save(playerBonus);
            logger.info("Bonus {} for Player {} is activated since there is no active bonus available (Player-Bonus {})",
                        playerBonus.getPk().getBonus().getId(), player.getId(), playerBonus.getId());
            updateActivePlayerBonusInWallet(player, playerBonus);
        }
    }

    @Nullable
    private PlayerBonus useFreeRoundBonus(final Player player, final Bonus bonus, final PlayerBonus playerBonus) {
        try {
            final boolean activated = gameService.activateBonus(player, bonus);
            if (activated) {
                playerBonus.setUsedAmount(bonus.getAmount());
                playerBonus.setUsedDate(new Date());
                playerBonus.setStatus(BonusStatus.COMPLETED);
                playerBonusRepository.save(playerBonus);

                final String message = String.format("Free Round Bonus %d used by Player %d", bonus.getId(), player.getId());
                auditService.trackPlayerActivityWithDescription(player, PlayerActivityType.USED_FREE_ROUND_BONUS, message);

                logger.info("Player {} used {} Bonus {} of amount {}", player.getId(), bonus.getBonusType(), bonus.getId(), bonus.getAmount());
                return playerBonus;
            } else {
                logger.error("Bonus {} of type {} failed to be activated on netent for Player {}", bonus.getId(), bonus.getBonusType(), player.getId());
                throw new BonusException(String.format("Free round Bonus %d failed to be activated on netent for Player %d", bonus.getId(), player.getId()));
            }
        } catch (final CommunicationException ex) {
            logger.error("Bonus {} of type {} failed to be used by Player {} because of {}", bonus.getId(), bonus.getBonusType(), player.getId(), ex.getMessage());
            throw new BonusException(String.format("Free round Bonus %d failed to be activated on netent for Player %d because of %s",
                                                   bonus.getId(), player.getId(), ex.getMessage()));
        }
    }

    private boolean meetGrantingCriteria(final Player player, final Bonus bonus) {
        if (!bonus.isBonusPeriodValidForUsage()) {
            logger.error("Bonus {} can not be granted to Player {} because of bonus validation period from {} to {}",
                         bonus.getId(), player.getId(), bonus.getValidFrom(), bonus.getValidTo());
            return false;
        }

        if (bonus.getCurrency() != player.getCurrency()) {
            logger.error("Bonus {} can not be used by Player {} because of different Currencies: player {} Bonus {}",
                         bonus.getId(), player.getId(), player.getCurrency(), bonus.getCurrency());
            return false;
        }

        final Criteria criteria = bonus.getCriteria();
        if (criteria != null) {
            final Set<Country> allowedCountries = criteria.getAllowedCountries();
            if (!allowedCountries.isEmpty() && !allowedCountries.contains(player.getAddress().getCountry())) {
                logger.info("Player {} does not meet criteria for bonus {} : Criteria allowed countries doesn't contain Player's country: {}",
                            player.getId(), bonus.getId(), player.getAddress().getCountry());
                return false;
            }

            if (criteria.getLevel() != null && player.getLevel().getLevel() < criteria.getLevel().getLevel()) {
                logger.info("Player {} does not meet criteria for bonus {} : Criteria level: {} , Player level: {}",
                            player.getId(), bonus.getId(), criteria.getLevel(), player.getLevel().getLevel());
                return false;
            }

            if (!meetTimeCriteria(player, criteria.getTimeCriteria())) {
                return false;
            }
        }

        logger.info("Player {} met granting criteria for bonus {}", player.getId(), bonus.getId());
        return true;
    }

    private boolean meetTimeCriteria(final Player player, @Nullable final TimeCriteria bonusTimeCriteria) {
        final Calendar calendar = Calendar.getInstance();

        if (bonusTimeCriteria != null) {
            if (bonusTimeCriteria.getRecurringTime() != null && bonusTimeCriteria.getRecurringTimeUnit() == RecurringTimeUnit.WEEK
                && calendar.get(Calendar.DAY_OF_WEEK) != bonusTimeCriteria.getRecurringTime()) {
                logger.info("Player {} does not meet time criteria for bonus: Current day of week: {} , Criteria day of week: {}",
                            player.getId(), calendar.get(Calendar.DAY_OF_WEEK), bonusTimeCriteria.getRecurringTime());
                return false;
            } else if (bonusTimeCriteria.getRecurringTimeUnit() == RecurringTimeUnit.MONTH
                       && calendar.get(Calendar.DAY_OF_MONTH) != bonusTimeCriteria.getRecurringTime()) {
                logger.info("Player {} does not meet time criteria for bonus: Current day of month: {} , Criteria day of month: {}",
                            player.getId(), calendar.get(Calendar.DAY_OF_MONTH), bonusTimeCriteria.getRecurringTime());
                return false;
            } else if (bonusTimeCriteria.getRecurringTimeUnit() == RecurringTimeUnit.BIRTHDAY
                       && !CalendarUtils.isToday(player.getBirthday())) {
                logger.info("Player {} does not meet time criteria for bonus: Player birthday is not today", player.getId());
                return false;
            }
            logger.info("Player {} meets time criteria for bonus: Time unit: {} , Recurring time: {}",
                        player.getId(), bonusTimeCriteria.getRecurringTimeUnit(), bonusTimeCriteria.getRecurringTime());
        }

        return true;
    }

    private boolean meetUsageCriteria(@Nonnull final Player player, @Nonnull final Bonus bonus, @Nonnull final PlayerCriteria playerCriteria) {
        if (!meetGrantingCriteria(player, bonus)) {
            return false;
        }

        final Criteria criteria = bonus.getCriteria();
        if (criteria != null) {
            final Money amount = criteria.getAmount();
            if (amount != null) {
                final Money criteriaAmount = playerCriteria.getAmount();
                if (criteriaAmount == null || amount.isGreaterThan(criteriaAmount)) {
                    logger.info("Player {} does not meet criteria for bonus {} : Criteria Amount: {} , Player Amount: {}",
                                player.getId(), bonus.getId(), amount, criteriaAmount);
                    return false;
                }
            }

            if (criteria.getRepetition() != null) {
                if (playerCriteria.getRepetition() == null || criteria.getRepetition() > playerCriteria.getRepetition()) {
                    logger.info("Player {} does not meet criteria for bonus {} : Criteria repetition: {} , Player repetition: {}",
                                player.getId(), bonus.getId(), criteria.getLevel(), player.getLevel().getLevel());
                    return false;
                }
            }
        }

        logger.info("Player {} met usage criteria for bonus {}", player.getId(), bonus.getId());
        return true;
    }

    @Nullable
    @Override
    public PlayerBonus getActivePlayerBonus(final Player player) {
        final PlayerBonus activeBonus = playerBonusRepository.findPlayerBonus(player, BonusStatus.ACTIVE);

        if (activeBonus != null && activeBonus.isExpired()) {
            activeBonus.setStatus(BonusStatus.EXPIRED);
            activeBonus.setCompletionDate(new Date());
            playerBonusRepository.save(activeBonus);
            logger.info("Player {} active player-bonus {} with bonus balance {}, bonus conversion progress {} and bonus conversion goal {} was expired",
                        player.getId(), activeBonus.getId(), activeBonus.getCurrentBalance(), activeBonus.getBonusConversionProgress(),
                        activeBonus.getBonusConversionGoal());
            updateActivePlayerBonusInWallet(player, null);
        } else if (activeBonus != null) {
            return activeBonus;
        }

        return activateNextPlayerBonus(player);
    }

    @Override
    public List<PlayerBonus> getVoidablePlayerBonuses(final Player player) {
        final List<PlayerBonus> voidablePlayerBonuses = new ArrayList<>();

        final PlayerBonus activePlayerBonus = getActivePlayerBonus(player);
        final List<PlayerBonus> inactivePlayerBonuses = getInactivePlayerBonuses(player);

        if (!inactivePlayerBonuses.isEmpty()) {
            voidablePlayerBonuses.addAll(inactivePlayerBonuses);
        }

        if (activePlayerBonus != null) {
            voidablePlayerBonuses.add(activePlayerBonus);
        }

        return voidablePlayerBonuses;
    }

    @Nullable
    private PlayerBonus activateNextPlayerBonus(final Player player) {
        // get inactive list desc based on used date
        final List<PlayerBonus> playerBonuses = playerBonusRepository.findByStatusAndDescDate(player, BonusStatus.INACTIVE);
        final List<PlayerBonus> expiredPlayerBonuses = new ArrayList<>();
        PlayerBonus firstInactivePlayerBonus = null;

        for (final PlayerBonus playerBonus : playerBonuses) {
            if (playerBonus.getPk().getBonus().isExpired()) {
                expiredPlayerBonuses.add(playerBonus);
            } else {
                firstInactivePlayerBonus = playerBonus;
            }
        }

        expirePlayerBonuses(expiredPlayerBonuses);
        // the last inactive in list (oldest) is served
        if (firstInactivePlayerBonus != null) {
            firstInactivePlayerBonus.setStatus(BonusStatus.ACTIVE);
            playerBonusRepository.save(firstInactivePlayerBonus);
            logger.info("Player {} next player-bonus with id {} bonus balance {}, bonus conversion progress {} and bonus conversion goal {} activated.",
                        player.getId(), firstInactivePlayerBonus.getId(), firstInactivePlayerBonus.getCurrentBalance(),
                        firstInactivePlayerBonus.getBonusConversionProgress(), firstInactivePlayerBonus.getBonusConversionGoal());

            updateActivePlayerBonusInWallet(player, firstInactivePlayerBonus);
        }
        return firstInactivePlayerBonus;
    }

    private void expirePlayerBonuses(final List<PlayerBonus> expiredPlayerBonuses) {
        for (final PlayerBonus expiredPlayerBonus : expiredPlayerBonuses) {
            expiredPlayerBonus.setStatus(BonusStatus.EXPIRED);
            expiredPlayerBonus.setCompletionDate(new Date());
            logger.info("Expiring bonus {} for player {}", expiredPlayerBonus.getId(), expiredPlayerBonus.getPk().getPlayer().getId());
            playerBonusRepository.save(expiredPlayerBonus);
        }
    }

    @Nullable
    @Override
    public PlayerBonus finishCurrentAndActivateNextBonus(final Player player, final PlayerBonus current, final BonusStatus status) {
        current.setStatus(status);
        current.setCompletionDate(new Date());
        playerBonusRepository.save(current);
        updateActivePlayerBonusInWallet(player, null);
        logger.info("Finishing bonus {} for player {}, setting to status {}", current.getId(), player.getId(), status);
        return activateNextPlayerBonus(player);
    }

    private List<PlayerBonus> getInactivePlayerBonuses(final Player player) {
        return playerBonusRepository.findByStatusAndDescDate(player, BonusStatus.INACTIVE);
    }

    private void updateActivePlayerBonusInWallet(final Player player, @Nullable final PlayerBonus playerBonus) {
        final Wallet wallet = player.getWallet();
        wallet.setActivePlayerBonus(playerBonus);
        walletRepository.save(wallet);
    }

    @Override
    public PlayerBonus cancelPlayerBonus(final Long playerBonusId) {
        final PlayerBonus playerBonus = playerBonusRepository.findOne(playerBonusId);

        if (playerBonus == null) {
            throw new NotFoundException("PlayerBonus not found with id : " + playerBonusId);
        }

        final BonusStatus status = playerBonus.getStatus();

        if (!status.isCancellable()) {
            throw new IllegalStateException("PlayerBonus has invalid status : " + playerBonus.getStatus());
        }

        final Player player = playerBonus.getPk().getPlayer();
        final Bonus bonus = playerBonus.getPk().getBonus();

        playerBonus.setStatus(BonusStatus.CANCELLED);
        playerBonus.setCompletionDate(new Date());
        playerBonusRepository.save(playerBonus);

        final Money currentBalance = playerBonus.getCurrentBalance();
        final String euroValue = currentBalance != null ? currentBalance.getEuroValueInBigDecimal().toPlainString() : "";
        final String message = String.format("Canceled Bonus %d of type %s and balance %s with status %s", bonus.getId(), bonus.getBonusType(),
                                             euroValue, playerBonus.getStatus());
        auditService.trackPlayerActivityWithDescription(player, PlayerActivityType.CANCELLED_BONUS, message);

        if (status == BonusStatus.ACTIVE) {
            updateActivePlayerBonusInWallet(player, null);
            activateNextPlayerBonus(player);
        }

        return playerBonus;
    }

    @Transactional(propagation = REQUIRED, noRollbackFor = {AccessDeniedException.class})
    @Override
    public Pair<Pair<Money, EventCode>, PlayerBonus> updatePlayerBonus(final Long playerBonusId, final User user, final Money requestedBonusBalance) {
        final PlayerBonus playerBonus = playerBonusRepository.findOne(playerBonusId);

        if (playerBonus == null) {
            throw new NotFoundException("PlayerBonus not found with id : " + playerBonusId);
        }

        final BonusStatus status = playerBonus.getStatus();

        if (!playerBonus.hasValidStatusToUpdate()) {
            logger.error("User {} tried to update player-bonus {} for player {} with status {}",
                         user.getId(), playerBonusId, playerBonus.getPk().getPlayer().getId(), status);
            throw new IllegalStateException("PlayerBonus has invalid status : " + playerBonus.getStatus());
        }

        if (!playerBonus.hasValidMonetaryFieldsToUpdate()) {
            logger.error("Player-bonus {} with status {} has null monetary fields", playerBonusId, status);
            throw new IllegalStateException("PlayerBonus has null monetary fields : " + playerBonus.getStatus());
        }

        final SecurityRole securityRole = user.getRoles().get(0).getPk().getRole().getName();
        final Player player = playerBonus.getPk().getPlayer();

        @SuppressWarnings("ConstantConditions") final Money bonusDelta = requestedBonusBalance.subtract(playerBonus.getCurrentBalance());
        if (!bonusDelta.isZero()) {
            if (securityRole.getAccesses().contains(Access.PLAYER_PAYMENTS_ADMIN) || securityRole.getAccesses().contains(Access.PLAYER_PAYMENTS_WRITE)) {
                playerBonus.setCurrentBalance(requestedBonusBalance);
                @SuppressWarnings("ConstantConditions") final Money add = playerBonus.getBonusConversionGoal().add(bonusDelta.multiply(bonusConversionGoalMultiplier));
                playerBonus.setBonusConversionGoal(add);

                final String message = String.format("Updated Bonus balance for player: %s from user: %s with bonus amount: %s Player-Bonus : %s",
                                                     player.getId(), user.getId(), bonusDelta, playerBonusId);
                auditService.trackUserActivity(user, UserActivityType.UPDATE_PLAYER_BONUS, message);
                logger.info("Updated Bonus balance for player: {} from user: {} with bonus amount: {}", player.getId(), user.getId(), bonusDelta);
            } else {
                logger.warn("User: {} tried to update player: {} with bonus amount: {}", player.getId(), user.getId(), bonusDelta);
                final String message = String.format("User: %s tried to update player: %s with bonus amount: %s Player-Bonus : %s",
                                                     player.getId(), user.getId(), requestedBonusBalance, playerBonusId);
                auditService.trackUserActivity(user, UserActivityType.UPDATE_PLAYER_BONUS, message);
                throw new AccessDeniedException("User not allowed to execute this action");
            }
        }

        if (requestedBonusBalance.isLessOrEqualThan(Money.ZERO)) {
            if (securityRole.getAccesses().contains(Access.PLAYER_PAYMENTS_ADMIN) || securityRole.getAccesses().contains(Access.PLAYER_PAYMENTS_WRITE)) {
                logger.info("Requested bonus balance is {}, setting bonus conversion goal to {} for player {}", requestedBonusBalance, Money.ZERO, player.getId());

                playerBonus.setCurrentBalance(Money.ZERO);
                playerBonus.setBonusConversionGoal(Money.ZERO);
            }
        }

        playerBonusRepository.save(playerBonus);
        final EventCode eventCode = bonusDelta.isPositive() ? EventCode.BACK_OFFICE_BONUS_DEPOSIT : EventCode.BACK_OFFICE_BONUS_WITHDRAW;
        return new Pair<>(new Pair<>(bonusDelta.abs(), eventCode), playerBonus);
    }

    @Transactional(propagation = REQUIRED, noRollbackFor = {AccessDeniedException.class})
    @Override
    public PlayerBonus addAdjustableBonus(final Player player, final User user, final Money amount, final Money maxRedemptionAmount, final Date validFrom,
                                          final Date validTo) {
        if (!amount.isPositive() || maxRedemptionAmount != null && !maxRedemptionAmount.isPositive()) {
            throw new InvalidArgumentException("Bonus amount and max redemption must be positive");
        }

        final Bonus bonus = bonusRepository.findOne(Bonus.CUSTOMER_SUPPORT_ADJUSTABLE_BONUS_MONEY);
        final SecurityRole securityRole = user.getRoles().get(0).getPk().getRole().getName();

        if (securityRole.getAccesses().contains(Access.PLAYER_PAYMENTS_ADMIN) || securityRole.getAccesses().contains(Access.PLAYER_PAYMENTS_WRITE)) {
            final PlayerBonus playerBonus = createBonusMoneyPlayerBonus(player, amount, maxRedemptionAmount, validFrom, validTo, bonus);

            logger.info("User {} added bonus money for player {} with amount {} added as player-bonus {}", user.getId(), player.getId(), amount, playerBonus.getId());
            final String message = String.format("User %d added bonus money for player %d with amount %s added as player-bonus %d", user.getId(), player.getId(), amount,
                                                 playerBonus.getId());
            auditService.trackUserActivity(user, UserActivityType.CREATE_ADJUSTABLE_BONUS, message);
            return playerBonus;
        } else {
            logger.warn("User: {} tried to give player {} {} in bonus money", user.getId(), player.getId(), amount);
            final String message = String.format("User: %s tried to update player: %s with money amount: %s", user.getId(), player.getId(), amount);
            auditService.trackUserActivity(user, UserActivityType.CREATE_ADJUSTABLE_BONUS, message);
            throw new AccessDeniedException("User not allowed to execute this action");
        }
    }

    @Override
    public PlayerBonus grantBackOfficeBonus(final Player player, final User user, final Long bonusId, final Money amount, @Nullable final Money maxRedemptionAmount,
                                            final Date validFrom, @Nullable final Date validTo) {
        final Bonus bonus = getBonus(bonusId);
        if (bonus == null) {
            logger.error("Could not find bonus with id {} to grant to Player {}", bonusId, player.getId());
            throw new NotFoundException("Could not find bonus with id " + bonusId);
        }

        if (!bonus.getPromotion().getPromotionTriggers().contains(PromotionTrigger.MANUAL)) {
            logger.warn("User: {} failed to give bonus {} (not Manual) to player {}", user.getId(), bonus.getId(), player.getId());
            final String message = String.format("User: %s failed to grant bonus %s (not Manual) to player: %s.", user.getId(), bonus.getId(), player.getId());
            auditService.trackUserActivity(user, UserActivityType.GRANT_MANUAL_BONUS_FAILED, message);
            throw new BonusException("Only bonus of type " + PromotionTrigger.MANUAL + " can be granted by BO");
        }

        if (!checkUserRole(user)) {
            logger.warn("User: {} tried to give bonus {} to player {}", user.getId(), bonus.getId(), player.getId());
            final String message = String.format("User: %s failed to grant bonus %s to player: %s for security role check.", user.getId(), bonus.getId(), player.getId());
            auditService.trackUserActivity(user, UserActivityType.GRANT_MANUAL_BONUS_FAILED, message);
            throw new AccessDeniedException("User not allowed to execute this action");
        }

        if (bonus.getBonusType() == BonusType.FREE_ROUND) {
            final PlayerBonus grantedBonusToPlayer = grantBonusToPlayer(player, bonus);
            if (grantedBonusToPlayer == null) {
                throw new BonusException("Bonus " + bonus.getId() + " is already granted and unused");
            }

            return useBonus(player, bonus, new PlayerCriteria());
        }

        final PlayerBonus playerBonus = createBonusMoneyPlayerBonus(player, amount, maxRedemptionAmount, validFrom, validTo, bonus);
        logger.info("User {} granted bonus {} to player {} added as player-bonus {}", user.getId(), bonus.getId(), player.getId(), playerBonus.getId());
        final String message = String
                .format("User %d granted bonus %d to player %d added as player-bonus %d", user.getId(), bonus.getId(), player.getId(), playerBonus.getId());
        auditService.trackUserActivity(user, UserActivityType.GRANT_MANUAL_BONUS, message);

        return playerBonus;
    }

    private boolean checkUserRole(final User user) {
        final SecurityRole securityRole = user.getRoles().get(0).getPk().getRole().getName();
        return securityRole.getAccesses().contains(Access.PLAYER_PAYMENTS_ADMIN) || securityRole.getAccesses().contains(Access.PLAYER_PAYMENTS_WRITE);
    }

    @Transactional(propagation = SUPPORTS, readOnly = true)
    @Override
    public Map<BigInteger, Pair<Money, Money>> getPlayersInactiveReservedBonusBalances() {
        final Map<BigInteger, Pair<Money, Money>> map = new HashMap<>();
        final List<Object[]> list = playerBonusRepository.getPlayersInactiveReservedBonusBalances();
        for (final Object[] tuple : list) {
            map.put((BigInteger) tuple[0], new Pair<>(new Money((BigDecimal) tuple[1]), new Money((BigDecimal) tuple[2])));
        }
        return map;
    }

    @Override
    public List<String> getActiveFreeRounds(final Player player) {
        return gameService.getActiveFreeRounds(player);
    }

    @Override
    public Map<BigInteger, Money> getAffiliatePlayersBonusBalances() {
        final Map<BigInteger, Money> map = new HashMap<>();
        final List<Object[]> list = playerBonusRepository.getAffiliatePlayersBonusBalances();
        for (final Object[] tuple : list) {
            final BigInteger playerId = (BigInteger) tuple[0];
            final Money currentBalance = Money.getMoneyFromCents((BigDecimal) tuple[1]);

            map.put(playerId, currentBalance);
        }
        return map;
    }

    @Override
    public void handleScheduledBonuses(final Player player) {
        final List<PlayerBonus> scheduledBonuses = playerBonusRepository.findScheduledBonuses(player, new Date());
        logger.info("Handling {} scheduled bonuses for player {} on login.", scheduledBonuses.size(), player.getId());

        for (final PlayerBonus scheduledBonus : scheduledBonuses) {
            scheduledBonus.setStatus(BonusStatus.UNUSED);
            playerBonusRepository.save(scheduledBonus);
            try {
                usePlayerBonus(player, scheduledBonus, new PlayerCriteria(), scheduledBonus.getPk().getBonus());
            } catch (final RuntimeException ex) {
                logger.error("Can not use scheduled player-bonus {} of bonus {} for player {} on login.",
                             scheduledBonus.getId(), scheduledBonus.getPk().getBonus().getId(), player.getId());
            }
        }
    }
}
