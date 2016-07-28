package com.cs.promotion;

import com.cs.player.Player;
import com.cs.user.User;

import org.springframework.data.domain.Page;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Hadi Movaghar
 */
public interface PromotionService {
    Promotion createPromotion(final Promotion promotion, final Long levelId, final User user);

    Promotion updatePromotion(final Promotion promotion, final User user);

    Map<PlayerPromotion, Promotion> getPlayerPromotions(final Long playerId);

    PlayerPromotion grantPromotionManually(final Player player, final Long promotionId);

    List<PlayerPromotion> assignPromotions(final Player player, final PromotionTrigger promotionTrigger, final PlayerCriteria playerCriteria);

    Page<Promotion> getPromotions(final User user, final Date startDate, final Date endDate, final String name, final PromotionTrigger promotionTrigger,
                                  final Integer page, final Integer size);

    Promotion getPromotionDetails(Long promotionId);
}
