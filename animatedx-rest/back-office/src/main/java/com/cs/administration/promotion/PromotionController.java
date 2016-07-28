package com.cs.administration.promotion;

import com.cs.administration.security.BackOfficeUser;
import com.cs.administration.security.CurrentUser;
import com.cs.audit.AuditService;
import com.cs.audit.UserActivityType;
import com.cs.player.Player;
import com.cs.player.PlayerService;
import com.cs.promotion.Promotion;
import com.cs.promotion.PromotionService;
import com.cs.promotion.PromotionTrigger;
import com.cs.user.User;
import com.cs.user.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Date;

import static com.cs.persistence.Constants.BO_DEFAULT_SIZE;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

/**
 * @author Hadi Movaghar
 */
@RestController
@RequestMapping(value = "/api/promotions", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
public class PromotionController {
    private final UserService userService;
    private final PromotionService promotionService;
    private final AuditService auditService;
    private final PlayerService playerService;

    @Autowired
    public PromotionController(final UserService userService, final PromotionService promotionService, final AuditService auditService, final PlayerService playerService) {
        this.userService = userService;
        this.promotionService = promotionService;
        this.auditService = auditService;
        this.playerService = playerService;
    }

    @RequestMapping(method = GET)
    @ResponseStatus(OK)
    public PromotionPageableDto getPromotions(@CurrentUser final BackOfficeUser currentUser,
                                      @RequestParam(value = "validFrom", required = true) @DateTimeFormat(iso = ISO.DATE) final Date validFrom,
                                      @RequestParam(value = "validTo", required = true) @DateTimeFormat(iso = ISO.DATE) final Date validTo,
                                      @RequestParam(value = "name", required = false) final String name,
                                      @RequestParam(value = "promotionTrigger", required = false) final PromotionTrigger promotionTrigger,
                                      @RequestParam(value = "page", required = false, defaultValue = "0") final Integer page,
                                      @RequestParam(value = "size", required = false, defaultValue = BO_DEFAULT_SIZE) final Integer size) {
        final User user = userService.getUser(currentUser.getId());
        return new PromotionPageableDto(promotionService.getPromotions(user, validFrom, validTo, name, promotionTrigger, page, size));
    }

    @RequestMapping(method = GET, value = "/{promotionId}")
    @ResponseStatus(OK)
    public PromotionDto getPromotionDetails(@CurrentUser final BackOfficeUser currentUser, @PathVariable("promotionId") final Long promotionId) {
        final User user = userService.getUser(currentUser.getId());
        return new PromotionDto(promotionService.getPromotionDetails(promotionId));
    }

    @RequestMapping(method = POST, value = "/create")
    @ResponseStatus(OK)
    public PromotionDto createPromotion(@Valid @RequestBody(required = true) final PromotionDto promotionDto,
                                        @CurrentUser final BackOfficeUser currentUser) {
        final User user = userService.getUser(currentUser.getId());
        auditService.trackUserActivity(user, UserActivityType.CREATE_PROMOTION);
        final Promotion promotion = promotionService.createPromotion(promotionDto.asPromotion(), promotionDto.getLevel(), user);
        return new PromotionDto(promotion);
    }

    @RequestMapping(method = PUT, value = "/update")
    @ResponseStatus(OK)
    public PromotionDto updatePromotion(@Valid @RequestBody(required = true) final UpdatePromotionDto promotionDto,
                                    @CurrentUser final BackOfficeUser currentUser) {
        final User user = userService.getUser(currentUser.getId());
        auditService.trackUserActivity(user, UserActivityType.UPDATE_PROMOTION);
        final Promotion promotion = promotionService.updatePromotion(promotionDto.asPromotion(), user);
        return new PromotionDto(promotion);
    }

    @RequestMapping(method = GET, value = "/{playerId}/give/{promotionId}")
    @ResponseStatus(OK)
    public PromotionDto assignPromotionToPlayer(@PathVariable("playerId") final Long playerId,
                                                @PathVariable("promotionId") final Long promotionId,
                                                @CurrentUser final BackOfficeUser currentUser) {
        final User user = userService.getUser(currentUser.getId());
        auditService.trackUserActivity(user, UserActivityType.GIVE_PROMOTION_TO_PLAYER);
        final Player player = playerService.getPlayer(playerId);
        return new PromotionDto(promotionService.grantPromotionManually(player, promotionId).getPk().getPromotion());
    }
}
