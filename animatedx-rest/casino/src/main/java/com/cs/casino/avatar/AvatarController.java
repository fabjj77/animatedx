package com.cs.casino.avatar;

import com.cs.avatar.Avatar;
import com.cs.avatar.AvatarBaseType;
import com.cs.avatar.AvatarBaseTypeDto;
import com.cs.avatar.AvatarDto;
import com.cs.avatar.AvatarService;
import com.cs.casino.security.CurrentPlayer;
import com.cs.casino.security.PlayerUser;
import com.cs.persistence.Status;
import com.cs.player.Player;
import com.cs.player.PlayerService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * @author Omid Aleapour
 */
@RestController
@RequestMapping(value = "/api/avatars", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
public class AvatarController {
    private final AvatarService avatarService;
    private final PlayerService playerService;

    @Autowired
    public AvatarController(final AvatarService avatarService, final PlayerService playerService) {
        this.avatarService = avatarService;
        this.playerService = playerService;
    }

    @RequestMapping(method = GET, value = "/{id}")
    @ResponseStatus(OK)
    public AvatarDto getAvatar(@PathVariable("id") final Long id) {
        final Avatar avatar = avatarService.getAvatar(id);
        return new AvatarDto(avatar);
    }

    @RequestMapping(method = GET, value = "/status/{status}")
    @ResponseStatus(OK)
    public List<AvatarBaseTypeDto> getAvatarBaseTypes(@PathVariable("status") final Status status) {
        return convertAvatarBaseType(avatarService.getAvatarBaseTypes(status));
    }

    @RequestMapping(method = GET, value = "/{avatarBaseTypeId}/{level}")
    @ResponseStatus(OK)
    public List<AvatarDto> getAvatars(@PathVariable("avatarBaseTypeId") final Integer avatarBaseTypeId, @PathVariable("level") final Long level) {
        final List<Avatar> avatars = avatarService.getAvatars(avatarBaseTypeId, level);
        return convertAvatar(avatars);
    }

    @RequestMapping(method = GET, value = "/signup")
    @ResponseStatus(OK)
    public List<AvatarDto> getSignupAvatars() {
        final List<Avatar> avatars = avatarService.getActiveAvatars(1L);
        return convertAvatar(avatars);
    }

    @RequestMapping(method = GET, value = "/changeable")
    @ResponseStatus(OK)
    public List<AvatarDto> getChangeableAvatar(@CurrentPlayer final PlayerUser currentPlayer) {
        final Iterable<Avatar> avatars = avatarService.getActiveAvatars(playerService.getPlayer(currentPlayer.getId()));
        return convertAvatar(avatars);
    }

    private List<AvatarDto> convertAvatar(final Iterable<Avatar> avatars) {
        final List<AvatarDto> converted = new ArrayList<>();
        for (final Avatar avatar : avatars) {
            converted.add(new AvatarDto(avatar));
        }
        return converted;
    }

    private List<AvatarBaseTypeDto> convertAvatarBaseType(final List<AvatarBaseType> avatarBaseTypes) {
        final List<AvatarBaseTypeDto> converted = new ArrayList<>();
        for (final AvatarBaseType avatarBaseType : avatarBaseTypes) {
            converted.add(new AvatarBaseTypeDto(avatarBaseType));
        }
        return converted;
    }

    @RequestMapping(method = GET, value = "/history")
    @ResponseStatus(OK)
    public List<AvatarDto> getAvatarHistory(@CurrentPlayer final PlayerUser currentPlayer, @RequestParam(value = "night", required = true) final Boolean night) {
        final Player player = playerService.getPlayer(currentPlayer.getId());
        return convertAvatar(avatarService.getLevelUpAvatarHistory(player), night);
    }

    private List<AvatarDto> convertAvatar(final Iterable<Avatar> avatars, final Boolean night) {
        final List<AvatarDto> converted = new ArrayList<>();
        for (final Avatar avatar : avatars) {
            converted.add(new AvatarDto(avatar, night));
        }
        return converted;
    }
}
