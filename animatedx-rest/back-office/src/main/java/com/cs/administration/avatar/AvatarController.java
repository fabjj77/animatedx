package com.cs.administration.avatar;

import com.cs.avatar.Avatar;
import com.cs.avatar.AvatarService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * @author Marcus Wass
 */
@RestController
@RequestMapping(value = "/api/avatar", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
public class AvatarController {
    private final AvatarService avatarService;

    @Autowired
    public AvatarController(final AvatarService avatarService) {
        this.avatarService = avatarService;
    }

    @RequestMapping(method = GET, value = "/{id}")
    @ResponseStatus(OK)
    public Avatar getAvatar(@PathVariable("id") final Long id) {
        return avatarService.getAvatar(id);
    }
}
