package com.cs.administration.player;

import com.cs.administration.security.BackOfficeUser;
import com.cs.administration.security.CurrentUser;
import com.cs.comment.CommentService;
import com.cs.game.Channel;
import com.cs.game.GameService;
import com.cs.persistence.Country;
import com.cs.player.BlockDto;
import com.cs.player.BlockType;
import com.cs.player.LimitationStatus;
import com.cs.player.Player;
import com.cs.player.PlayerComment;
import com.cs.player.PlayerLimitation;
import com.cs.player.PlayerLimitationsDto;
import com.cs.player.PlayerService;
import com.cs.player.TrustLevel;
import com.cs.user.User;
import com.cs.user.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.cs.persistence.Constants.BO_DEFAULT_SIZE;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

/**
 * @author Joakim Gottzén
 */
@RestController
@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.SUPPORTS, readOnly = true)
@RequestMapping(value = "/api/players", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
public class PlayerController {

    private final CommentService commentService;
    private final GameService gameService;
    private final PlayerService playerService;
    private final UserService userService;

    @Autowired
    public PlayerController(final CommentService commentService, final GameService gameService, final PlayerService playerService, final UserService userService) {
        this.commentService = commentService;
        this.gameService = gameService;
        this.playerService = playerService;
        this.userService = userService;
    }

    @RequestMapping(method = GET, value = "/{id}")
    @ResponseStatus(OK)
    public BackOfficePlayerDto getPlayer(@PathVariable("id") final Long id) {
        final Player player = playerService.getPlayer(id);
        player.getPlayerAffiliate();
        return new BackOfficePlayerDto(player);
    }

    @RequestMapping(method = PUT, value = "/{playerId}")
    @ResponseStatus(OK)
    public BackOfficePlayerDto updatePlayer(@PathVariable("playerId") final Long playerId, @Valid @RequestBody(required = true) final BackOfficeUpdatePlayerDto player) {
        final Player updatedPlayer = playerService.updatePlayerFromBackOffice(playerId, player.asPlayer());
        return new BackOfficePlayerDto(updatedPlayer);
    }

    @RequestMapping(method = POST, value = "/{playerId}/resend")
    @ResponseStatus(OK)
    public void resendUuidEmail(@PathVariable("playerId") final Long playerId) {
        /** Todo Fix resending UUID mail. This method re-adds the contact to bronto which is wrong.
         Should only add the contact to bronto workflow if an active UUID with
         a specific type exists **/
        //playerService.sendPlayerRegistrationEmail(playerId);
    }

    /**
     * Search for customers (a player is a playing player on the site), call service as following example: {@code /api/players?
     * playerId=1&emailAddress=anima&nickname='PAPI'&firstname=Ma&lastname=Wa}
     *
     * @param playerId     If a playerId is passed in the method will try to get that player
     * @param emailAddress Like search (%value%)
     * @param nickname     Like search (%value%)
     * @param firstName    Like search (%value%)
     * @param lastName     Like search (%value%)
     */
    @RequestMapping(method = GET)
    @ResponseStatus(OK)
    public PlayerPageableDto searchPlayers(@RequestParam(value = "playerId", required = false) final Long playerId,
                                           @RequestParam(value = "emailAddress", required = false) final String emailAddress,
                                           @RequestParam(value = "nickname", required = false) final String nickname,
                                           @RequestParam(value = "firstName", required = false) final String firstName,
                                           @RequestParam(value = "lastName", required = false) final String lastName,
                                           @RequestParam(value = "blockType", required = false) final BlockType blockType,
                                           @RequestParam(value = "limitStatus", required = false) final LimitationStatus limitationStatus,
                                           @RequestParam(value = "page", required = false, defaultValue = "0") final Integer page,
                                           @RequestParam(value = "size", required = false, defaultValue = BO_DEFAULT_SIZE) final Integer size) {
        if (playerId != null) {
            return new PlayerPageableDto(new PageImpl<>(Collections.singletonList(playerService.getPlayer(playerId))));
        }

        final Page<Player> players = playerService.searchPlayers(emailAddress, nickname, firstName, lastName, blockType, limitationStatus, page, size);
        return new PlayerPageableDto(players);
    }

    @RequestMapping(method = POST, value = "/{playerId}/netent/register")
    @ResponseStatus(NO_CONTENT)
    public void registerCasinoUser(@PathVariable("playerId") final Long playerId) {
        final Player player = playerService.getPlayer(playerId);
        gameService.registerPlayer(player, Channel.FLASH);
    }

    @RequestMapping(method = POST, value = "/{playerId}/netent/login")
    @ResponseStatus(NO_CONTENT)
    public String loginUserOnCasino(@PathVariable("playerId") final Long playerId) {
        final Player player = playerService.getPlayer(playerId);
        return gameService.loginPlayer(player, Channel.FLASH);
    }

    @RequestMapping(method = POST, value = "/{playerId}/netent/logout")
    @ResponseStatus(NO_CONTENT)
    public void logoutOnCasino(@PathVariable("playerId") final Long playerId) {
        final Player player = playerService.getPlayer(playerId);
        gameService.logoutPlayer(player);
    }

    @RequestMapping(method = GET, value = "/{playerId}/limit")
    @ResponseStatus(OK)
    public PlayerLimitationsDto getPlayerLimit(@PathVariable("playerId") final Long playerId) {
        final PlayerLimitation playerLimitation = playerService.backOfficeGetPlayerLimitation(playerId);
        return new PlayerLimitationsDto(playerLimitation);
    }

    @RequestMapping(method = PUT, value = "/{playerId}/limit")
    @ResponseStatus(OK)
    public PlayerLimitationsDto updatePlayerLimit(@PathVariable("playerId") final Long playerId,
                                                  @Valid @RequestBody(required = true) final PlayerLimitationsDto playerLimitationsDto,
                                                  @CurrentUser final BackOfficeUser currentUser) {
        final User user = userService.getUser(currentUser.getId());
        final PlayerLimitation playerLimitation = playerService
                .backOfficeUpdatePlayerLimitations(user, playerId, playerLimitationsDto.getLimitsAsList(), playerLimitationsDto.getSessionLength());
        return new PlayerLimitationsDto(playerLimitation);
    }

    @RequestMapping(method = PUT, value = "/{playerId}/limit/force")
    @ResponseStatus(OK)
    public PlayerLimitationsDto forceUpdatePlayerLimit(@PathVariable("playerId") final Long playerId,
                                                       @Valid @RequestBody(required = true) final PlayerLimitationsDto playerLimitationsDto,
                                                       @CurrentUser final BackOfficeUser currentUser) {
        final User user = userService.getUser(currentUser.getId());
        final PlayerLimitation playerLimitation =
                playerService.forceUpdatePlayerLimitations(user, playerId, playerLimitationsDto.getLimitsAsList(), playerLimitationsDto.getSessionLength());
        return new PlayerLimitationsDto(playerLimitation);
    }

    @RequestMapping(method = PUT, value = "/{playerId}/block")
    @ResponseStatus(NO_CONTENT)
    public void blockSelfExcludedPlayer(@PathVariable("playerId") final Long playerId, @Valid @RequestBody(required = true) final BlockDto blockDto,
                                        @CurrentUser final BackOfficeUser currentUser) {
        final User user = userService.getUser(currentUser.getId());
        playerService.blockPlayerSelfExcluded(user, playerId, blockDto.getBlockType(), blockDto.getDays());
    }

    @RequestMapping(method = PUT, value = "/{playerId}/unblock")
    @ResponseStatus(NO_CONTENT)
    public void unblockSelfExcludedPlayer(@PathVariable("playerId") final Long playerId, @CurrentUser final BackOfficeUser currentUser) {
        final User user = userService.getUser(currentUser.getId());
        playerService.unblockSelfExcludedPlayer(user, playerId);
    }

    @RequestMapping(method = PUT, value = "/{playerId}/unblock/force")
    @ResponseStatus(NO_CONTENT)
    public void forceUnblockSelfExcludedPlayer(@PathVariable("playerId") final Long playerId, @CurrentUser final BackOfficeUser currentUser) {
        final User user = userService.getUser(currentUser.getId());
        playerService.forceUnblockSelfExcludedPlayer(user, playerId);
    }

    @RequestMapping(method = POST, value = "/{playerId}/comment")
    @ResponseStatus(OK)
    public PlayerCommentDto addPlayerComment(@PathVariable("playerId") final Long playerId, @Valid @RequestBody(required = true) final PlayerCommentDto playerCommentDto,
                                             @CurrentUser final BackOfficeUser currentUser) {
        final User user = userService.getUser(currentUser.getId());
        final PlayerComment playerComment = commentService.addPlayerComment(playerId, playerCommentDto.getComment(), user);
        return new PlayerCommentDto(playerComment);
    }

    @RequestMapping(method = GET, value = "/{playerId}/comment")
    @ResponseStatus(OK)
    public PlayerCommentsPageableDto getPlayerComments(@PathVariable("playerId") final Long playerId,
                                                       @RequestParam(value = "page", required = false, defaultValue = "0") final Integer page,
                                                       @RequestParam(value = "size", required = false, defaultValue = BO_DEFAULT_SIZE) final Integer size) {
        final Page<PlayerComment> playerComments = commentService.getPlayerComments(playerId, page, size);
        return new PlayerCommentsPageableDto(playerComments);
    }

    @RequestMapping(method = GET, value = "/flags")
    @ResponseStatus(OK)
    public List<TrustLevel> getTrustLevels() {
        return Arrays.asList(TrustLevel.values());
    }

    @RequestMapping(method = GET, value = "/countries")
    @ResponseStatus(OK)
    public List<Country> getCountries() {
        return Arrays.asList(Country.values());
    }
}
