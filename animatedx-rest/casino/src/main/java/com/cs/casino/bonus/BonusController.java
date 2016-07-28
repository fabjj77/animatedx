package com.cs.casino.bonus;

import com.cs.audit.AuditService;
import com.cs.audit.PlayerActivityType;
import com.cs.bonus.Bonus;
import com.cs.bonus.BonusService;
import com.cs.bonus.BonusType;
import com.cs.bonus.PlayerBonus;
import com.cs.casino.security.CurrentPlayer;
import com.cs.casino.security.PlayerUser;
import com.cs.player.Player;
import com.cs.player.PlayerService;
import com.cs.promotion.PlayerCriteria;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * @author Hadi Movaghar
 */
@RestController
@RequestMapping(value = "/api/bonuses", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
public class BonusController {

    private final AuditService auditService;
    private final BonusService bonusService;
    private final PlayerService playerService;

    @Autowired
    public BonusController(final AuditService auditService, final BonusService bonusService, final PlayerService playerService) {
        this.auditService = auditService;
        this.bonusService = bonusService;
        this.playerService = playerService;
    }

    @RequestMapping(method = GET, value = "/deposit")
    @ResponseStatus(OK)
    public List<BonusDto> getAvailableDepositBonuses(@CurrentPlayer final PlayerUser currentPlayer, @RequestHeader("X-Forwarded-For") final String host) {
        final Player player = playerService.getPlayer(currentPlayer.getId());
        auditService.trackPlayerActivityWithIpAddress(player, PlayerActivityType.REQ_GET_AVAILABLE_DEPOSIT_BONUSES, host);
        final List<Bonus> bonusList = bonusService.getAvailableBonuses(player, BonusType.DEPOSIT_BONUS);
        if (player.getLevel().hasDepositBonus()) {
            bonusList.add(player.getLevel().getDepositBonus());
        }
        return BonusDto.getBonusList(bonusList);
    }

    @RequestMapping(method = GET)
    @ResponseStatus(OK)
    public CasinoPlayerBonusesDto getCurrentPlayerBonuses(@CurrentPlayer final PlayerUser currentPlayer, @RequestHeader("X-Forwarded-For") final String host) {
        final Player player = playerService.getPlayer(currentPlayer.getId());
        auditService.trackPlayerActivityWithIpAddress(player, PlayerActivityType.REQ_GET_CURRENT_BONUSES, host);
        final List<PlayerBonus> currentPlayerBonuses = bonusService.getCurrentPlayerBonuses(player);
        return new CasinoPlayerBonusesDto(currentPlayerBonuses);
    }

    @RequestMapping(method = POST, value = "/cancel/{playerBonusId}")
    @ResponseStatus(OK)
    public void cancelPlayerBonus(@CurrentPlayer final PlayerUser currentPlayer, @PathVariable("playerBonusId") final Long playerBonusId,
                                  @RequestHeader("X-Forwarded-For") final String host) {
        final Player player = playerService.getPlayer(currentPlayer.getId());
        auditService.trackPlayerActivityWithIpAddress(player, PlayerActivityType.REQ_CANCEL_BONUS, host);
        bonusService.cancelPlayerBonus(playerBonusId);
    }

    @RequestMapping(method = POST, value = "/activate/{bonusCode}")
    @ResponseStatus(OK)
    public void activateLinkBonus(@CurrentPlayer final PlayerUser currentPlayer, @PathVariable("bonusCode") final String bonusCode,
                                  @RequestHeader("X-Forwarded-For") final String host) {
        final Player player = playerService.getPlayer(currentPlayer.getId());
        auditService.trackPlayerActivityWithIpAddress(player, PlayerActivityType.REQ_ACTIVATE_LINK_BONUS, host);
        final PlayerBonus playerBonus = bonusService.grantLinkBonus(player, bonusCode);
        bonusService.useBonus(player, playerBonus.getPk().getBonus(), new PlayerCriteria());
    }

    @RequestMapping(method = GET, value = "/freeround")
    @ResponseStatus(OK)
    public FreeRoundDto getCurrentPlayerFreeRounds(@CurrentPlayer final PlayerUser currentPlayer){
        final Player player = playerService.getPlayer(currentPlayer.getId());
        return new FreeRoundDto(bonusService.getActiveFreeRounds(player));
    }
}
