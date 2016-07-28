package com.cs.casino.credit;

import com.cs.audit.AuditService;
import com.cs.audit.PlayerActivity;
import com.cs.audit.PlayerActivityType;
import com.cs.casino.payment.CreditsDto;
import com.cs.casino.payment.CreditsRateDto;
import com.cs.casino.player.CasinoWalletDto;
import com.cs.casino.security.CurrentPlayer;
import com.cs.casino.security.PlayerUser;
import com.cs.payment.credit.CreditService;
import com.cs.player.Player;
import com.cs.player.PlayerService;
import com.cs.player.Wallet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Date;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * @author Hadi Movaghar
 */
@RestController
@RequestMapping(value = "/api/credits", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
public class CreditController {

    private final AuditService auditService;
    private final CreditService creditService;
    private final PlayerService playerService;

    @Autowired
    public CreditController(final AuditService auditService, final CreditService creditService, final PlayerService playerService) {
        this.auditService = auditService;

        this.creditService = creditService;
        this.playerService = playerService;
    }

    @RequestMapping(method = POST, value = "/convert")
    @ResponseStatus(OK)
    public CasinoWalletDto convertCredits(@CurrentPlayer final PlayerUser currentPlayer, @RequestBody(required = true) @Valid final CreditsDto creditsDto,
                                          @RequestHeader("X-Forwarded-For") final String host) {
        final Player player = playerService.getPlayer(currentPlayer.getId());
        final PlayerActivity playerActivity = new PlayerActivity();
        playerActivity.setLevel(player.getLevel().getLevel());
        playerActivity.setPlayer(player);
        playerActivity.setActivityDate(new Date());
        playerActivity.setIpAddress(host);
        final Wallet wallet;
        switch (creditsDto.getConversionType()) {
            case REAL_MONEY:
                wallet = creditService.convertCreditsToRealMoney(player, creditsDto.getCreditAmount());
                playerActivity.setActivity(PlayerActivityType.REQ_CONVERT_CREDITS_TO_MONEY);
                playerActivity.setDescription("Converted " + creditsDto.getCreditAmount() + " to " + wallet);
                auditService.trackPlayerActivity(playerActivity);
                break;
            case BONUS_MONEY:
                wallet = creditService.convertCreditsToBonusMoney(player, creditsDto.getCreditAmount());
                playerActivity.setActivity(PlayerActivityType.REQ_CONVERT_CREDITS_TO_BONUS);
                playerActivity.setDescription("Converted " + creditsDto.getCreditAmount() + " to " + wallet);
                auditService.trackPlayerActivity(playerActivity);
                break;

            default:
                throw new RuntimeException("Unknown conversion type");
        }
        return new CasinoWalletDto(wallet);
    }

    @RequestMapping(method = GET, value = "/rates")
    @ResponseStatus(OK)
    public CreditsRateDto getCreditConversionRates(@CurrentPlayer final PlayerUser currentPlayer, @RequestHeader("X-Forwarded-For") final String host) {
        final Player player = playerService.getPlayer(currentPlayer.getId());
        auditService.trackPlayerActivityWithIpAddress(player, PlayerActivityType.REQ_GET_CREDITS_CONVERSION_RATE, host);
        return new CreditsRateDto(player.getLevel().getMoneyCreditRate(), player.getLevel().getBonusCreditRate());
    }
}
