package com.cs.administration.bonus;

import com.cs.administration.security.BackOfficeUser;
import com.cs.administration.security.CurrentUser;
import com.cs.audit.AuditService;
import com.cs.audit.UserActivityType;
import com.cs.bonus.Bonus;
import com.cs.bonus.BonusRepository;
import com.cs.bonus.BonusService;
import com.cs.player.PlayerService;
import com.cs.user.User;
import com.cs.user.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static org.springframework.http.HttpStatus.NOT_IMPLEMENTED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

/**
 * @author Hadi Movaghar
 */
@RestController
@RequestMapping(value = "/api/bonuses", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
public class BonusController {
    private final UserService userService;
    private final BonusService bonusService;
    private final AuditService auditService;
    private final PlayerService playerService;
    private final BonusRepository bonusRepository;

    @Autowired
    public BonusController(final UserService userService, final BonusService bonusService, final AuditService auditService, final PlayerService playerService,
                           final BonusRepository bonusRepository) {
        this.userService = userService;
        this.bonusService = bonusService;
        this.auditService = auditService;
        this.playerService = playerService;
        this.bonusRepository = bonusRepository;
    }

    @RequestMapping(method = POST, value = "/create")
    @ResponseStatus(OK)
    public BonusDto createBonus(@Valid @RequestBody(required = true) final BonusDto bonusDto,
                                @CurrentUser final BackOfficeUser currentUser) {
        final User user = userService.getUser(currentUser.getId());
        auditService.trackUserActivity(user, UserActivityType.CREATE_BONUS);
        final Bonus bonus = bonusService.createBonus(bonusDto.asBonus(), bonusDto.getPromotionId(), bonusDto.getCriteria().getLevel(), user);
        return new BonusDto(bonus);
    }

    @RequestMapping(method = PUT, value = "/update")
    @ResponseStatus(OK)
    public BonusDto updateBonus(@Valid @RequestBody(required = true) final UpdateBonusDto updateBonusDto,
                                @CurrentUser final BackOfficeUser currentUser) {
        final User user = userService.getUser(currentUser.getId());
        auditService.trackUserActivity(user, UserActivityType.UPDATE_BONUS);
        final Bonus bonus = bonusService.updateBonus(updateBonusDto.asBonus(), updateBonusDto.getPromotionId(), user);
        return new BonusDto(bonus);
    }

    @RequestMapping(method = GET, value = "/{playerId}/activate/{bonusId}")
    @ResponseStatus(NOT_IMPLEMENTED)
    public void giveBonusToPlayer(@PathVariable("playerId") final Long playerId,
                                  @PathVariable("bonusId") final Long bonusId,
                                  @CurrentUser final BackOfficeUser currentUser) {
//        auditService.trackUserActivity(userService.getUser(currentUser.getId()), UserActivityType.GIVE_BONUS_TO_PLAYER);
//        final Player player = playerService.getPlayer(playerId);
//        bonusService.useBonus(player, bonusRepository.findOne(bonusId), new PlayerCriteria());
    }

    //Todo comment out and correct when needed
//    @RequestMapping(method = GET, value = "/used/{playerId}")
//    @ResponseStatus(OK)
//    public List<BonusDto> getPlayerUsedBonuses(@PathVariable("playerId") final Long playerId, @CurrentUser final BackOfficeUser currentUser) {
////        auditService.trackUserActivity();
//        return BonusDto.getBonusDtoList(bonusService.getUsedPlayerBonuses(playerId));
//    }
//
//    @RequestMapping(method = GET, value = "/unused/{playerId}")
//    @ResponseStatus(OK)
//    public List<BonusDto> getPlayerNotUsedBonuses(@PathVariable("playerId") final Long playerId, @CurrentUser final BackOfficeUser currentUser) {
//        //        auditService.trackUserActivity();
//        return BonusDto.getBonusDtoList(bonusService.getNotUsedPlayerBonuses(playerId));
//    }
//
//    @RequestMapping(method = GET, value = "active/{playerId}")
//    @ResponseStatus(OK)
//    public BonusDto getActiveBonus(@PathVariable("playerId") final Long playerId) {
//        final Player player = playerService.getPlayer(playerId);
//        return BonusDto.getBonusDto(bonusService.getActivePlayerBonus(player));
//    }
}
