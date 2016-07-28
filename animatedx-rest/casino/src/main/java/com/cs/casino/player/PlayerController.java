package com.cs.casino.player;

import com.cs.agreement.TermsAndConditionsVersionService;
import com.cs.audit.AuditService;
import com.cs.audit.PlayerActivityType;
import com.cs.bonus.BonusService;
import com.cs.casino.security.CasinoCookieContent;
import com.cs.casino.security.CurrentPlayer;
import com.cs.casino.security.LogoutHelper;
import com.cs.casino.security.PlayerUser;
import com.cs.common.EmailAddressDto;
import com.cs.control.SystemControlService;
import com.cs.persistence.IpBlockedException;
import com.cs.player.BlockType;
import com.cs.player.Player;
import com.cs.player.PlayerLimitation;
import com.cs.player.PlayerLimitationsDto;
import com.cs.player.PlayerRegisterTrack;
import com.cs.player.PlayerRegisterTrackDto;
import com.cs.player.PlayerService;
import com.cs.player.UpdatePlayerLimitationsDto;
import com.cs.player.Wallet;
import com.cs.promotion.PlayerCriteria;
import com.cs.promotion.PromotionService;
import com.cs.promotion.PromotionTrigger;
import com.cs.rest.status.BlockedResponseMessage;
import com.cs.rest.status.ValidationResponseMessage;
import com.cs.security.AccessDeniedException;
import com.cs.security.InvalidSessionException;
import com.cs.security.SessionExpiredException;
import com.cs.session.PlayerSessionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.MediaType.ALL_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

/**
 * @author Joakim Gottzén
 */
@RestController
@RequestMapping(value = "/api/players", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
public class PlayerController {

    private static final String BTAG = "bc_btag";

    private final AuditService auditService;
    private final BonusService bonusService;
    private final PlayerService playerService;
    private final PlayerSessionService playerSessionService;
    private final PromotionService promotionService;
    private final SystemControlService systemControlService;
    private final TermsAndConditionsVersionService termsAndConditionsVersionService;

    private final LogoutHelper logoutHelper;

    @Autowired
    public PlayerController(final AuditService auditService, final BonusService bonusService, final PlayerService playerService,
                            final PlayerSessionService playerSessionService, final PromotionService promotionService, final SystemControlService systemControlService,
                            final TermsAndConditionsVersionService termsAndConditionsVersionService, final LogoutHelper logoutHelper) {
        this.auditService = auditService;
        this.bonusService = bonusService;
        this.playerService = playerService;
        this.playerSessionService = playerSessionService;
        this.promotionService = promotionService;
        this.systemControlService = systemControlService;
        this.termsAndConditionsVersionService = termsAndConditionsVersionService;
        this.logoutHelper = logoutHelper;
    }

    @RequestMapping(method = GET, value = "/login", consumes = ALL_VALUE)
    @ResponseStatus(OK)
    public void login() {
    }

    @RequestMapping(method = GET, value = "/login/success", consumes = ALL_VALUE)
    @ResponseStatus(OK)
    public CasinoPlayerDto loginSuccess(@CurrentPlayer final PlayerUser currentPlayer, @RequestHeader("X-Forwarded-For") final String ipAddress,
                                        final HttpServletRequest request,final HttpServletResponse response) {
        if (!systemControlService.validateLoginAllowed(currentPlayer.getUsername())) {
            // Programmatically log the player out
            logoutHelper.logout(currentPlayer, request, response);
            throw new AccessDeniedException("Login is disabled");
        }

        final String sanitizedIpAddress = sanitizeIpAddress(ipAddress);
        final Player player = playerService.getPlayer(currentPlayer.getId());
        if (playerService.isIpBlocked(player, sanitizedIpAddress)) {
            // Programmatically log the player out
            logoutHelper.logout(currentPlayer, request, response);
            throw new IpBlockedException("IP address "+ sanitizedIpAddress + " is blocked");
        }

        promotionService.assignPromotions(player, PromotionTrigger.LOGIN, new PlayerCriteria());
        bonusService.handleScheduledBonuses(player);
        // Player might actually have levelled up "offline"
        playerService.checkLevelByProgressTurnover(player);
        auditService.trackPlayerActivityWithIpAddress(player, PlayerActivityType.LOGIN, sanitizedIpAddress);
        response.addCookie(getCasinoCookie(player));

        return new CasinoPlayerDto(player, termsAndConditionsVersionService.hasAcceptedLatestTermsAndConditions(player));
    }

    private String sanitizeIpAddress(final String ipAddress) {
        String correctedIpAddress = ipAddress;
        if (correctedIpAddress.contains(",")) {
            final int commaIndex = ipAddress.indexOf(',');
            correctedIpAddress = ipAddress.substring(0, commaIndex - 1);
        }
        return correctedIpAddress;
    }

    private Cookie getCasinoCookie(final Player player) {
        final CasinoCookieContent cookieContent = new CasinoCookieContent(playerSessionService.getPaymentUuid(player));
        final Cookie cookie = new Cookie(CasinoCookieContent.COOKIE_NAME, cookieContent.getContent());
        cookie.setPath("/");
        return cookie;
    }

    @RequestMapping(method = GET, value = "/login/failure", consumes = ALL_VALUE)
    @ResponseStatus(UNAUTHORIZED)
    public void loginFailure() {
    }

    @RequestMapping(method = POST, value = "/logout", consumes = ALL_VALUE)
    @ResponseStatus(OK)
    public void logout() {
    }

    @RequestMapping(method = GET, value = "/logout/success", consumes = ALL_VALUE)
    @ResponseStatus(OK)
    public void logoutSuccess(final HttpServletResponse response) {
        response.addCookie(getDeletedCasinoCookie());
    }

    private Cookie getDeletedCasinoCookie() {
        final Cookie cookie = new Cookie(CasinoCookieContent.COOKIE_NAME, null);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        return cookie;
    }

    @RequestMapping(method = GET, value = "/session/expired", consumes = ALL_VALUE)
    @ResponseStatus(UNAUTHORIZED)
    public String sessionExpired(final HttpServletResponse response) {
        response.addCookie(getDeletedCasinoCookie());
        throw new SessionExpiredException("Your session has expired");
    }

    @RequestMapping(method = GET, value = "/session/invalid", consumes = ALL_VALUE)
    @ResponseStatus(UNAUTHORIZED)
    public String invalidSession(final HttpServletResponse response) {
        response.addCookie(getDeletedCasinoCookie());
        throw new InvalidSessionException("Your session is invalid");
    }

    @RequestMapping(method = GET)
    @ResponseStatus(OK)
    public CasinoPlayerDto getCurrentPlayer(@CurrentPlayer final PlayerUser currentPlayer) {
        final Player player = playerService.getPlayer(currentPlayer.getId());
        return new CasinoPlayerDto(player);
    }

    @RequestMapping(method = PUT)
    @ResponseStatus(OK)
    public CasinoPlayerDto updatePlayer(@CurrentPlayer final PlayerUser currentPlayer, @Valid @RequestBody(required = true) final CasinoUpdatePlayerDto player,
                                        @RequestHeader("X-Forwarded-For") final String host) {
        final Player updatedPlayer = playerService.updatePlayer(currentPlayer.getId(), player.asPlayer(), player.getPassword(), player.getNewPassword());
        auditService.trackPlayerActivityWithIpAddress(updatedPlayer, PlayerActivityType.REQ_UPDATE_PLAYER, host);
        return new CasinoPlayerDto(updatedPlayer);
    }

    @RequestMapping(method = DELETE)
    @ResponseStatus(OK)
    public CasinoPlayerDto deletePlayer(@CurrentPlayer final PlayerUser currentPlayer, @RequestHeader("X-Forwarded-For") final String host) {
        final Player inactivatedPlayer = playerService.inactivatePlayer(currentPlayer.getId());
        auditService.trackPlayerActivityWithIpAddress(inactivatedPlayer, PlayerActivityType.REQ_DELETE_PLAYER, host);
        return new CasinoPlayerDto(inactivatedPlayer);
    }

    @RequestMapping(method = POST)
    @ResponseStatus(CREATED)
    public CasinoPlayerDto createPlayer(@RequestBody(required = true) @Valid final CasinoPlayerDto playerDto, @RequestHeader("X-Forwarded-For") final String ipAddress,
                                        @CookieValue(value = BTAG, required = false) final String bTag, final HttpServletResponse response) {
        systemControlService.validateRegistrationAllowed(playerDto.getEmailAddress());

        final PlayerRegisterTrackDto tracking = playerDto.getTracking();
        final PlayerRegisterTrack playerRegisterTrack = tracking != null ? tracking.asPlayerRegisterTrack() : null;
        final Player player = playerService.createPlayer(playerDto.asPlayer(), playerRegisterTrack, bTag, sanitizeIpAddress(ipAddress));

        final PlayerActivityType activityType = tracking != null ? PlayerActivityType.REQ_CREATE_PLAYER_DURING_CAMPAIGN : PlayerActivityType.REQ_CREATE_PLAYER;
        auditService.trackPlayerActivityWithIpAddress(player, activityType, ipAddress);
        response.addCookie(getCasinoCookie(player));
        return new CasinoPlayerDto(playerService.getPlayer(player.getId()));
    }

    @RequestMapping(method = GET, value = "/address")
    @ResponseStatus(OK)
    public CasinoAddressDto getPlayerAddress(@CurrentPlayer final PlayerUser currentPlayer, @RequestHeader("X-Forwarded-For") final String host) {
        auditService.trackPlayerActivityWithIpAddress(playerService.getPlayer(currentPlayer.getId()), PlayerActivityType.REQ_GET_PLAYER_ADDRESS, host);
        return new CasinoAddressDto(playerService.getPlayerAddress(currentPlayer.getId()));
    }

    @RequestMapping(method = PUT, value = "/address")
    @ResponseStatus(OK)
    public CasinoAddressDto updatePlayerAddress(@CurrentPlayer final PlayerUser currentPlayer, @Valid @RequestBody(required = true) final CasinoAddressDto addressDto,
                                                @RequestHeader("X-Forwarded-For") final String host) {
        auditService.trackPlayerActivityWithIpAddress(playerService.getPlayer(currentPlayer.getId()), PlayerActivityType.REQ_UPDATE_PLAYER_ADDRESS, host);
        return new CasinoAddressDto(playerService.updatePlayerAddress(currentPlayer.getId(), addressDto.asAddress()));
    }

    @RequestMapping(method = GET, value = "/wallet")
    @ResponseStatus(OK)
    public CasinoWalletDto getPlayerWallet(@CurrentPlayer final PlayerUser currentPlayer) {
        final Wallet wallet = playerService.getPlayerWallet(currentPlayer.getId());
        return new CasinoWalletDto(wallet);
    }

    @RequestMapping(method = POST, value = "/password/reset/create")
    @ResponseStatus(OK)
    public void createResetPassword(@RequestBody(required = true) final EmailAddressDto emailAddress, @RequestHeader("X-Forwarded-For") final String host) {
        auditService.trackPlayerActivityWithIpAddress(playerService.getPlayer(emailAddress.getEmailAddress()), PlayerActivityType.REQ_CREATE_RESET_PASSWORD,
                                                      host);
        playerService.createResetPassword(emailAddress.getEmailAddress());
    }

    @RequestMapping(method = POST, value = "/password/reset")
    @ResponseStatus(OK)
    public void resetPassword(@RequestBody(required = true) final ResetPasswordDto resetPasswordDto, @RequestHeader("X-Forwarded-For") final String host) {
        final Player player = playerService.resetPassword(resetPasswordDto.getUuid(), resetPasswordDto.getPassword());
        auditService.trackPlayerActivityWithIpAddress(player, PlayerActivityType.REQ_RESET_PASSWORD, host);
    }

    @RequestMapping(method = GET, value = "/verify/{uuid}")
    @ResponseStatus(OK)
    public void verifyPlayerEmail(@PathVariable("uuid") final String uuidString, @RequestHeader("X-Forwarded-For") final String host) {
        final Player player = playerService.verifyEmail(uuidString);
        auditService.trackPlayerActivityWithIpAddress(player, PlayerActivityType.REQ_VERIFY_PLAYER_EMAIL, host);
    }

    @RequestMapping(method = POST, value = "/validate/email")
    @ResponseStatus(OK)
    public ValidationResponseMessage validateEmail(@Valid @RequestBody(required = true) final EmailAddressDto emailAddressDto) {
        return new ValidationResponseMessage(playerService.isEmailAddressUnregistered(emailAddressDto.getEmailAddress()));
    }

    @RequestMapping(method = POST, value = "/validate/nickname")
    @ResponseStatus(OK)
    public ValidationResponseMessage validateNickname(@Valid @RequestBody(required = true) final NicknameDto nicknameDto) {
        return new ValidationResponseMessage(playerService.isNicknameUnregistered(nicknameDto.getNickname()));
    }

    @RequestMapping(method = GET, value = "/limit")
    @ResponseStatus(OK)
    public PlayerLimitationsDto getPlayerLimit(@CurrentPlayer final PlayerUser currentPlayer, @RequestHeader("X-Forwarded-For") final String host) {
        final PlayerLimitation playerLimitation = playerService.getPlayerLimitation(currentPlayer.getId());
        auditService.trackPlayerActivityWithIpAddress(playerService.getPlayer(currentPlayer.getId()), PlayerActivityType.REQ_GET_PLAYER_LIMITS, host);
        return new PlayerLimitationsDto(playerLimitation);
    }

    @RequestMapping(method = PUT, value = "/limit")
    @ResponseStatus(OK)
    public PlayerLimitationsDto updatePlayerLimit(@CurrentPlayer final PlayerUser currentPlayer,
                                                  @RequestBody(required = true) @Valid final UpdatePlayerLimitationsDto playerLimitationsDto,
                                                  @RequestHeader("X-Forwarded-For") final String host) {
        final PlayerLimitation playerLimitation = playerService.updatePlayerLimitation(currentPlayer.getId(), playerLimitationsDto.getLimitsAsList(),
                                                                                       playerLimitationsDto.getSessionLength(), playerLimitationsDto.getPassword());
        auditService.trackPlayerActivityWithIpAddress(playerService.getPlayer(currentPlayer.getId()), PlayerActivityType.REQ_UPDATE_PLAYER_LIMITS, host);
        return new PlayerLimitationsDto(playerLimitation);
    }

    @RequestMapping(method = POST, value = "/block")
    @ResponseStatus(NO_CONTENT)
    public void selfExcludePlayer(@CurrentPlayer final PlayerUser currentPlayer, @RequestParam(value = "days", required = true) final Integer days,
                                  @RequestHeader("X-Forwarded-For") final String host) {
        auditService.trackPlayerActivityWithIpAddress(playerService.getPlayer(currentPlayer.getId()), PlayerActivityType.REQ_SELF_EXCLUSION, host);
        playerService.selfExcludePlayer(currentPlayer.getId(), BlockType.DEFINITE_SELF_EXCLUSION, days);
    }

    @RequestMapping(method = PUT, value = "/sessionTime")
    @ResponseStatus(OK)
    public BlockedResponseMessage updateSessionTime(@CurrentPlayer final PlayerUser currentPlayer) {
        return new BlockedResponseMessage(playerService.checkSessionTime(currentPlayer.getId()));
    }

    @RequestMapping(method = POST, value = "/acceptTermsAndConditions")
    @ResponseStatus(OK)
    public void acceptTermsByPlayer(@CurrentPlayer final PlayerUser currentPlayer, @RequestHeader("X-Forwarded-For") final String host) {
        final Player player = playerService.getPlayer(currentPlayer.getId());
        auditService.trackPlayerActivityWithIpAddress(player, PlayerActivityType.REQ_ACCEPT_TERMS_AND_CONDITIONS, host);
        termsAndConditionsVersionService.acceptLatestTermsAndConditions(player);
    }
}
