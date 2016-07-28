package com.cs.casino.promotion;

import com.cs.audit.AuditService;
import com.cs.audit.PlayerActivityType;
import com.cs.casino.security.CurrentPlayer;
import com.cs.casino.security.PlayerUser;
import com.cs.player.PlayerService;
import com.cs.promotion.PlayerPromotion;
import com.cs.promotion.Promotion;
import com.cs.promotion.PromotionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * @author Hadi Movaghar
 */
@RestController
@RequestMapping(value = "/api/promotions", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
public class PromotionController {
    private final PlayerService playerService;
    private final PromotionService promotionService;
    private final AuditService auditService;

    @Autowired
    public PromotionController(final PlayerService playerService, final PromotionService promotionService, final AuditService auditService) {
        this.playerService = playerService;
        this.promotionService = promotionService;
        this.auditService = auditService;
    }

    @RequestMapping(method = GET, value = "/list")
    @ResponseStatus(OK)
    public List<PromotionDto> getPlayerPromotions(@CurrentPlayer final PlayerUser currentPlayer, @RequestHeader("X-Forwarded-For") final String host) {
        final Map<PlayerPromotion, Promotion> playerPromotions = promotionService.getPlayerPromotions(currentPlayer.getId());
        auditService.trackPlayerActivityWithIpAddress(playerService.getPlayer(currentPlayer.getId()), PlayerActivityType.REQ_GET_PLAYER_PROMOTIONS, host);
        return getAllPromotions(playerPromotions);
    }

    private List<PromotionDto> getAllPromotions(final Map<PlayerPromotion, Promotion> playerPromotions) {
        final List<PromotionDto> bonusList = new ArrayList<>();
        for (final Entry<PlayerPromotion, Promotion> entry : playerPromotions.entrySet()) {
            final PromotionDto promotionDto = new PromotionDto();
            promotionDto.setName(entry.getValue().getName());
            promotionDto.setValidTo(entry.getValue().getValidTo());
            promotionDto.setActivationDate(entry.getKey().getActivationDate());
            promotionDto.setBonusList(entry.getValue().getBonusList());
            bonusList.add(promotionDto);
        }
        return bonusList;
    }
}
