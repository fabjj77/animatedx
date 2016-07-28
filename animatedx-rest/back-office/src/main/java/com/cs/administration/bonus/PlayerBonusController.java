package com.cs.administration.bonus;

import com.cs.administration.security.BackOfficeUser;
import com.cs.administration.security.CurrentUser;
import com.cs.audit.AuditService;
import com.cs.audit.UserActivityType;
import com.cs.bonus.Bonus;
import com.cs.bonus.BonusService;
import com.cs.bonus.PlayerBonus;
import com.cs.payment.Money;
import com.cs.payment.PaymentService;
import com.cs.player.Player;
import com.cs.player.PlayerService;
import com.cs.user.User;
import com.cs.user.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

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
@RequestMapping(value = "/api/player/bonuses", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
public class PlayerBonusController {

    private final AuditService auditService;
    private final BonusService bonusService;
    private final PaymentService paymentService;
    private final PlayerService playerService;
    private final UserService userService;

    @Autowired
    public PlayerBonusController(final AuditService auditService, final BonusService bonusService, final PaymentService paymentService, final PlayerService playerService,
                                 final UserService userService) {
        this.auditService = auditService;
        this.bonusService = bonusService;
        this.paymentService = paymentService;
        this.playerService = playerService;
        this.userService = userService;
    }

    @RequestMapping(method = PUT, value = "/{playerBonusId}")
    @ResponseStatus(OK)
    public BackOfficePlayerBonusDto updatePlayerBonus(@PathVariable("playerBonusId") final Long playerBonusId,
                                                      @Valid @RequestBody(required = true) final UpdatePlayerBonusDto updatePlayerBonusDto,
                                                      @CurrentUser final BackOfficeUser currentUser) {
        final User user = userService.getUser(currentUser.getId());
        auditService.trackUserActivity(user, UserActivityType.UPDATE_BONUS);
        final PlayerBonus playerBonus = paymentService.updatePlayerBonus(playerBonusId, user, new Money(updatePlayerBonusDto.getAmount()));
        return new BackOfficePlayerBonusDto(playerBonus);
    }

    @RequestMapping(method = GET, value = "/{playerBonusId}")
    @ResponseStatus(OK)
    public BackOfficePlayerBonusDto getPlayerBonus(@PathVariable("playerBonusId") final Long playerBonusId) {
        final PlayerBonus playerBonus = bonusService.getPlayerBonus(playerBonusId);
        return new BackOfficePlayerBonusDto(playerBonus);
    }

    @RequestMapping(method = GET, value = "/all/{playerId}")
    @ResponseStatus(OK)
    public PlayerBonusPageableDto getAllPlayerBonuses(@PathVariable("playerId") final Long playerId,
                                                      @RequestParam(value = "page", required = false, defaultValue = "0") final Integer page,
                                                      @RequestParam(value = "size", required = false, defaultValue = BO_DEFAULT_SIZE) final Integer size) {
        final Player player = playerService.getPlayer(playerId);
        final Page<PlayerBonus> playerBonuses = bonusService.getAllPlayerBonuses(player, page, size);
        return new PlayerBonusPageableDto(playerBonuses);
    }

    @RequestMapping(method = POST, value = "/cancel/{playerBonusId}")
    @ResponseStatus(OK)
    public BackOfficePlayerBonusDto cancelPlayerBonus(@PathVariable("playerBonusId") final Long playerBonusId,
                                                      @CurrentUser final BackOfficeUser currentUser) {
        final User user = userService.getUser(currentUser.getId());
        final String message = String.format("Cancelling player bonus id %d", playerBonusId);
        auditService.trackUserActivity(user, UserActivityType.CANCEL_PLAYER_BONUS, message);
        final PlayerBonus playerBonus = bonusService.cancelPlayerBonus(playerBonusId);
        return new BackOfficePlayerBonusDto(playerBonus);
    }

    @RequestMapping(method = POST, value = "{bonusId}/grant/{playerId}")
    @ResponseStatus(OK)
    public BackOfficePlayerBonusDto grantManualBonus(@PathVariable("bonusId") final Long bonusId,
                                                     @PathVariable("playerId") final Long playerId,
                                                     @Valid @RequestBody(required = true) final AdjustablePlayerBonusDto dto,
                                                     @CurrentUser final BackOfficeUser currentUser) {
        final User user = userService.getUser(currentUser.getId());
        final Player player = playerService.getPlayer(playerId);
        if (bonusId.equals(Bonus.CUSTOMER_SUPPORT_ADJUSTABLE_BONUS_MONEY)) {
            return grantAdjustableBonus(player, user, dto);
        }
        return grantBackOfficeBonus(player, user, bonusId, dto);
    }

    private BackOfficePlayerBonusDto grantAdjustableBonus(final Player player, final User user, final AdjustablePlayerBonusDto dto) {
        final String message = String.format("Bonus money of amount of %f and maximum redemption of %f", dto.getAmount(), dto.getMaxAmount());
        auditService.trackUserActivity(user, UserActivityType.ADD_ADJUSTABLE_BONUS_MONEY, message);
        final PlayerBonus playerBonus = paymentService.createAdjustableBonus(player, user, new Money(dto.getAmount()), new Money(dto.getMaxAmount()),
                                                                             dto.getValidFrom(), dto.getValidTo());
        return new BackOfficePlayerBonusDto(playerBonus);
    }

    private BackOfficePlayerBonusDto grantBackOfficeBonus(final Player player, final User user, final Long bonusId, final AdjustablePlayerBonusDto dto) {
        final PlayerBonus playerBonus = bonusService.grantBackOfficeBonus(player, user, bonusId, new Money(dto.getAmount()), new Money(dto.getMaxAmount()),
                                                                          dto.getValidFrom(), dto.getValidTo());
        return new BackOfficePlayerBonusDto(playerBonus);
    }
}
