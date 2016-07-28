package com.cs.promotion;

import com.cs.audit.AuditService;
import com.cs.audit.PlayerActivityType;
import com.cs.avatar.Level;
import com.cs.bonus.Bonus;
import com.cs.bonus.BonusRepository;
import com.cs.bonus.BonusService;
import com.cs.bonus.PlayerBonus;
import com.cs.persistence.AlreadyAssignedException;
import com.cs.persistence.ExpiredEntityException;
import com.cs.persistence.InvalidArgumentException;
import com.cs.persistence.NotFoundException;
import com.cs.player.Player;
import com.cs.security.AccessDeniedException;
import com.cs.user.SecurityRole;
import com.cs.user.SecurityRole.Access;
import com.cs.user.User;
import com.cs.util.CalendarUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.querydsl.QSort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.mysema.query.types.Order;
import com.mysema.query.types.OrderSpecifier;
import com.mysema.query.types.expr.BooleanExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

/**
 * @author Hadi Movaghar
 */
@Service
@Transactional(isolation = Isolation.READ_COMMITTED)
public class PromotionServiceImpl implements PromotionService {

    private static final Logger logger = LoggerFactory.getLogger(PromotionServiceImpl.class);

    private final AuditService auditService;
    private final BonusService bonusService;
    private final PlayerPromotionRepository playerPromotionRepository;
    private final PromotionRepository promotionRepository;
    private final BonusRepository bonusRepository;

    @Autowired
    public PromotionServiceImpl(final AuditService auditService, final BonusService bonusService, final PlayerPromotionRepository playerPromotionRepository,
                                final PromotionRepository promotionRepository, final BonusRepository bonusRepository) {
        this.auditService = auditService;
        this.bonusService = bonusService;
        this.playerPromotionRepository = playerPromotionRepository;
        this.promotionRepository = promotionRepository;
        this.bonusRepository = bonusRepository;
    }

    @Override
    public Promotion createPromotion(final Promotion promotion, final Long levelId, final User user) {
        promotion.setLevel(new Level(levelId));
        promotion.setCreatedBy(user);
        promotion.setCreatedDate(new Date());
        return promotionRepository.save(promotion);
    }

    @Override
    public Promotion updatePromotion(final Promotion promotion, final User user) {
        final Promotion savedPromotion = promotionRepository.findOne(promotion.getId());

        if (savedPromotion == null) {
            logger.error("Promotion with id {} was not found to be updated.", promotion.getId());
            throw new NotFoundException("Promotion with id " + promotion.getId() + " was not found to be updated.");
        }

        if (savedPromotion.getValidTo().before(new Date())) {
            logger.warn("User {} tried to update expired promotion {}", user.getId(), promotion.getId());
            throw new ExpiredEntityException("Promotion " + promotion.getId() + " has been expired");
        }

        if (savedPromotion.getValidFrom().before(new Date())) {
            logger.warn("User {} tried to update an running promotion {}", user.getId(), promotion.getId());
            return updateRunningPromotion(savedPromotion, promotion, user);
        }

        // if (savedPromotion.getValidFrom().after(new Date()))
        logger.warn("User {} tried to update a future bonus {}", user.getId(), promotion.getId());
        return updateFuturePromotion(savedPromotion, promotion, user);
    }

    private Promotion updateRunningPromotion(final Promotion savedPromotion, final Promotion promotion, final User user) {
        boolean updated = false;

        // TODO which fields to be able to update

        if (promotion.getValidTo() != null && savedPromotion.getValidFrom().before(promotion.getValidTo())) {
            for (final Bonus bonus : savedPromotion.getBonusList() != null ? savedPromotion.getBonusList() : new ArrayList<Bonus>()) {
                if (bonus.getValidTo().after(promotion.getValidTo())) {
                    logger.error("Bonus {} validTo {} must be before new Promotion validTo {}", bonus.getId(), bonus.getValidTo(), promotion.getValidTo());
                    throw new InvalidArgumentException("Bonus " + bonus.getId() + " validTo " + bonus.getValidTo() +
                                                       " must be before new Promotion validTo " + promotion.getValidTo());
                }
            }
            savedPromotion.setValidTo(promotion.getValidTo());
            updated = true;
        }

        if (promotion.getName() != null) {
            savedPromotion.setName(promotion.getName());
            updated = true;
        }

        if (updated) {
            savedPromotion.setModifiedBy(user);
            savedPromotion.setModifiedDate(new Date());
            logger.info("User {} updated running promotion {}", user.getId(), savedPromotion.getId());
        }

        return promotionRepository.save(savedPromotion);
    }

    private Promotion updateFuturePromotion(final Promotion savedPromotion, final Promotion promotion, final User user) {
        updateRunningPromotion(savedPromotion, promotion, user);
        boolean updated = false;

        // TODO which fields to update?
        if (promotion.getValidFrom() != null && promotion.getValidFrom().after(new Date())) {
            for (final Bonus bonus : savedPromotion.getBonusList() != null ? savedPromotion.getBonusList() : new ArrayList<Bonus>()) {
                if (bonus.getValidFrom().before(promotion.getValidFrom())) {
                    logger.error("Bonus {} validFrom {} must be after new Promotion validFrom {}", bonus.getId(), bonus.getValidFrom(), promotion.getValidFrom());
                    throw new InvalidArgumentException("Bonus " + bonus.getId() + " validFrom " + bonus.getValidFrom() +
                                                       " must be after new Promotion validFrom " + promotion.getValidFrom());
                }
            }
            savedPromotion.setValidFrom(promotion.getValidFrom());
            updated = true;
        }

        if (updated) {
            savedPromotion.setModifiedBy(user);
            savedPromotion.setModifiedDate(new Date());
            logger.info("User {} updated future promotion {}", user.getId(), savedPromotion.getId());
        }

        return promotionRepository.save(savedPromotion);
    }

    @Override
    public Map<PlayerPromotion, Promotion> getPlayerPromotions(final Long playerId) {
        final List<PlayerPromotion> playerPromotions = playerPromotionRepository.findPlayerAllPromotions(new Player(playerId));
        final Map<PlayerPromotion, Promotion> playerPromotionDateMap = new HashMap<>();
        for (final PlayerPromotion playerPromotion : playerPromotions) {
            playerPromotionDateMap.put(playerPromotion, promotionRepository.getOne(playerPromotion.getPk().getPromotion().getId()));
        }
        return playerPromotionDateMap;
    }

    @Nullable
    @Override
    public PlayerPromotion grantPromotionManually(final Player player, final Long promotionId) {
        final Promotion promotion = promotionRepository.findOne(promotionId);
        if (promotion == null) {
            logger.error("Promotion {} was not found to give to Player {}", promotionId, player.getId());
            throw new NotFoundException("Promotion was not found with id " + promotionId);
        }

        if (promotion.getValidTo().before(new Date())) {
            logger.error("Expired promotion with id {} can not to be granted to Player {}", promotion.getId(), player.getId());
            throw new ExpiredEntityException("Promotion " + promotionId + " is expired");
        }

        if (playerPromotionRepository.findPlayerPromotion(player, promotion) != null) {
            logger.error("Promotion {} was already granted to Player {}", promotion.getId(), player.getId());
            throw new AlreadyAssignedException("Promotion " + promotionId + " was already assigned to Player " + player.getId());
        }

        auditService.trackPlayerActivityWithDescription(player, PlayerActivityType.ENROLLED_IN_PROMOTION,
                                                        String.format("Player %d was enrolled in Promotion %s with id %d ",
                                                                      player.getId(), promotion.getName(), promotion.getId())
        );

        return grantPromotion(player, promotion, new PlayerCriteria());
    }

    @Nullable
    private PlayerPromotion grantPromotion(final Player player, final Promotion promotion, final PlayerCriteria playerCriteria) {
        if (promotion.getValidTo().before(new Date())) {
            logger.error("Expired promotion with id {} can not to be granted to Player {}", promotion.getId(), player.getId());
            return null;
        }

        if (playerPromotionRepository.findPlayerPromotion(player, promotion) != null) {
            logger.error("Promotion {} was already granted to Player {}", promotion.getId(), player.getId());
            return null;
        }

        logger.info("Promotion {} was granted to the Player {}", promotion.getId(), player.getId());

        grantPromotionBonuses(player, promotion, playerCriteria);

        final String message = String.format("Player %d was enrolled in Promotion %s with id %d ", player.getId(), promotion.getName(), promotion.getId());
        auditService.trackPlayerActivityWithDescription(player, PlayerActivityType.ENROLLED_IN_PROMOTION, message);

        final PlayerPromotion playerPromotion = new PlayerPromotion(new PlayerPromotionId(player, promotion), new Date());
        return playerPromotionRepository.save(playerPromotion);
    }

    private void grantPromotionBonuses(final Player player, final Promotion promotion, final PlayerCriteria playerCriteria) {
        if (!promotion.isAutoGrantBonuses()) {
            logger.warn("Bonuses of Promotion {}: {} can not be auto granted to Player {}",
                        promotion.getId(), promotion.getName(), player.getId());
            return;
        }

        final List<Bonus> bonusList = bonusRepository.findByPromotion(promotion);

        if (bonusList == null || bonusList.isEmpty()) {
            logger.warn("Promotion {}: {}  contains no Bonuses to be granted to the Player {}", promotion.getId(), promotion.getName(), player.getId());
        }

        if (bonusList != null && !bonusList.isEmpty()) {
            if (promotion.getPromotionTriggers().contains(PromotionTrigger.SCHEDULE)) {
                grantGroupedBonuses(player, promotion, bonusList, playerCriteria);
                return;
            }
            grantAllBonuses(player, bonusList);
        }
    }

    private void grantAllBonuses(final Player player, final List<Bonus> bonusList) {
        for (final Bonus bonus : bonusList) {
            bonusService.grantBonusToPlayer(player, bonus);
        }
    }

    private void grantGroupedBonuses(final Player player, final Promotion promotion, final List<Bonus> bonusList, final PlayerCriteria playerCriteria) {
        for (final Bonus bonus : bonusList) {
            if (bonus.getBonusGroup() == null) {
                logger.error("Bonus {} of Promotion {} with grouped bonuses has no bonus group.", bonus.getId(), promotion.getId());
                return;
            }
        }

        final Map<Integer, List<Bonus>> bonusesByGroup = new TreeMap<>();
        for (final Bonus bonus : bonusList) {
            List<Bonus> groupedBonuses = bonusesByGroup.get(bonus.getBonusGroup());
            if (groupedBonuses == null) {
                groupedBonuses = new ArrayList<Bonus>();
                bonusesByGroup.put(bonus.getBonusGroup(), groupedBonuses);
            }
            groupedBonuses.add(bonus);
        }

        // smaller bonus group number will be granted first
        for (final Entry<Integer, List<Bonus>> integerListEntry : bonusesByGroup.entrySet()) {
            if (grantOneGroupOfBonuses(player, integerListEntry.getValue(), playerCriteria)) {
                logger.info("Bonus group {} of Promotion {} was granted to player {}", integerListEntry.getKey(), promotion.getId(), player.getId());
                return;
            }
        }
    }

    private boolean grantOneGroupOfBonuses(final Player player, final List<Bonus> bonusList, final PlayerCriteria playerCriteria) {
        boolean groupedBonusesGranted = false;
        for (final Bonus bonus : bonusList) {
            final List<PlayerBonus> playerBonuses = bonusService.grantScheduledBonusesByCriteria(player, bonus, playerCriteria);
            if (playerBonuses != null && !playerBonuses.isEmpty()) {
                groupedBonusesGranted = true;
            }
        }
        return groupedBonusesGranted;
    }

    @Override
    public List<PlayerPromotion> assignPromotions(final Player player, final PromotionTrigger promotionTrigger, final PlayerCriteria playerCriteria) {
        final List<Promotion> promotions = promotionRepository.findActivePromotions(new Date());

        final List<PlayerPromotion> playerPromotions = new ArrayList<>();
        for (final Promotion promotion : promotions) {
            if (promotion.getPromotionTriggers().contains(promotionTrigger) && meetCriteria(player, promotion)) {
                final PlayerPromotion playerPromotion = grantPromotion(player, promotion, playerCriteria);
                playerPromotions.add(playerPromotion);
            }
        }

        return playerPromotions;
    }

    private boolean meetCriteria(final Player player, final Promotion promotion) {
        if (player.getLevel().getLevel() < promotion.getLevel().getLevel()) {
            logger.info("Player {} level {} does not match promotion {} level {}",
                        player.getId(), player.getLevel().getLevel(), promotion.getId(), promotion.getLevel());
            return false;
        }
        return true;
    }

    @Override
    public Page<Promotion> getPromotions(final User user, final Date startDate, final Date endDate, final String name, final PromotionTrigger promotionTrigger,
                                         final Integer page, final Integer size) {
        if (!assertUserPromotionRead(user)) {
            logger.info("User: {} tried to get promotions", user.getId());
            throw new AccessDeniedException("User not allowed to execute this action");
        }

        final Calendar instance = Calendar.getInstance();
        final Date startDateTrimmed = CalendarUtils.startOfDay(instance, startDate);
        final Date endDateTrimmed = CalendarUtils.endOfDay(instance, endDate);

        BooleanExpression booleanExpression = QPromotion.promotion.createdDate.between(startDateTrimmed, endDateTrimmed);

        if (name != null) {
            booleanExpression = booleanExpression.and(QPromotion.promotion.name.contains(name));
        }

        //Todo commented out until we fix it in DB
//        if (promotionTrigger != null) {
//            booleanExpression = booleanExpression.and(QPromotion.promotion.promotionTriggers.contains(promotionTrigger));
//        }

        final PageRequest pageRequest = new PageRequest(page, size, new QSort(new OrderSpecifier<>(Order.DESC, QPromotion.promotion.createdDate)));

        final Page<Promotion> promotions = promotionRepository.findAll(booleanExpression, pageRequest);

        if (!promotions.hasContent()) {
            logger.warn("No promotions found");
            throw new NotFoundException("No promotions found");
        }
        return promotions;
    }

    @Override
    public Promotion getPromotionDetails(final Long promotionId) {
        final Promotion promotion = promotionRepository.findOne(promotionId);
        if (promotion == null) {
            logger.warn("Promotion with id {} not found.", promotionId);
            throw new NotFoundException(String.format("Promotion with id %d not found", promotionId));
        }
        return promotion;
    }

    private Boolean assertUserPromotionRead(final User user) {
        final SecurityRole securityRole = user.getRoles().get(0).getPk().getRole().getName();
        return securityRole.getAccesses().contains(Access.PROMOTION_CAMPAIGNS_ADMIN) || securityRole.getAccesses().contains(Access.PROMOTION_CAMPAIGNS_READ);
    }
}
