package com.cs.administration.whitelist;

import com.cs.administration.player.PlayerPageableDto;
import com.cs.administration.security.BackOfficeUser;
import com.cs.administration.security.CurrentUser;
import com.cs.player.Player;
import com.cs.player.PlayerService;
import com.cs.user.User;
import com.cs.user.UserService;
import com.cs.whitelist.WhiteListService;
import com.cs.whitelist.WhiteListedIpAddress;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Collections;

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
@RequestMapping(value = "/api/whitelist", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
public class WhiteListController {

    private final PlayerService playerService;
    private final UserService userService;
    private final WhiteListService whiteListService;

    @Autowired
    public WhiteListController(final PlayerService playerService, final UserService userService, final WhiteListService whiteListService) {
        this.playerService = playerService;
        this.userService = userService;
        this.whiteListService = whiteListService;
    }

    @RequestMapping(method = GET, value = "/player")
    @ResponseStatus(OK)
    public PlayerPageableDto getWhiteListedPlayer(@RequestParam(value = "playerId", required = false) final Long playerId,
                                                  @RequestParam(value = "page", required = false, defaultValue = "0") final Integer page,
                                                  @RequestParam(value = "size", required = false, defaultValue = BO_DEFAULT_SIZE) final Integer size) {
        if (playerId != null) {
            return new PlayerPageableDto(new PageImpl<>(Collections.singletonList(whiteListService.getWhiteListedPlayer(playerId))));
        }

        final Page<Player> players = whiteListService.getWhiteListedPlayers(page, size);
        return new PlayerPageableDto(players);
    }

    @RequestMapping(method = PUT, value = "/{playerId}/player/add")
    @ResponseStatus(OK)
    public void addPlayerToWhiteList(@PathVariable("playerId") final Long playerId, @CurrentUser final BackOfficeUser currentUser) {
        final User user = userService.getUser(currentUser.getId());
        final Player player = playerService.getPlayer(playerId);
        whiteListService.addPlayerToWhiteList(user, player);
    }

    @RequestMapping(method = PUT, value = "/{playerId}/player/remove")
    @ResponseStatus(OK)
    public void removePlayerFromWhiteList(@PathVariable("playerId") final Long playerId, @CurrentUser final BackOfficeUser currentUser) {
        final User user = userService.getUser(currentUser.getId());
        final Player player = playerService.getPlayer(playerId);
        whiteListService.removePlayerFromWhiteList(user, player);
    }

    @RequestMapping(method = GET, value = "/ip")
    @ResponseStatus(OK)
    public WhiteListedIpAddressPageableDto getWhiteListedIpAddresses(@RequestParam(value = "ipAddress", required = false) final String ipAddress,
                                                                     @RequestParam(value = "page", required = false, defaultValue = "0") final Integer page,
                                                                     @RequestParam(value = "size", required = false, defaultValue = BO_DEFAULT_SIZE) final Integer size) {
        if (ipAddress != null) {
            return new WhiteListedIpAddressPageableDto(new PageImpl<>(Collections.singletonList(whiteListService.getWhiteListedIpAddress(ipAddress))));
        }

        final Page<WhiteListedIpAddress> ipAddresses = whiteListService.getWhiteListedIpAddresses(page, size);
        return new WhiteListedIpAddressPageableDto(ipAddresses);
    }

    @RequestMapping(method = POST, value = "/ip/add")
    @ResponseStatus(OK)
    public WhiteListedIpAddressDto addIpAddressToWhiteList(@Valid @RequestBody(required = true) final WhiteListedIpAddressDto whiteListedIpAddressDto,
                                                           @CurrentUser final BackOfficeUser currentUser) {
        final User user = userService.getUser(currentUser.getId());
        final WhiteListedIpAddress whiteListedIpAddress = whiteListService.addIpAddressToWhiteList(user, whiteListedIpAddressDto.getFromIpAddress(),
                                                                                                   whiteListedIpAddressDto.getToIpAddress());
        return new WhiteListedIpAddressDto(whiteListedIpAddress);
    }

    @RequestMapping(method = PUT, value = "/{id}/ip/remove")
    @ResponseStatus(OK)
    public void removeIpAddressFromWhiteList(@PathVariable("id") final Long id, @CurrentUser final BackOfficeUser currentUser) {
        final User user = userService.getUser(currentUser.getId());
        whiteListService.removeIpAddressToWhiteList(user, id);
    }
}
