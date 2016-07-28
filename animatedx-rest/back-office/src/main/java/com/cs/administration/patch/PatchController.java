package com.cs.administration.patch;

import com.cs.administration.security.BackOfficeUser;
import com.cs.administration.security.CurrentUser;
import com.cs.audit.AuditService;
import com.cs.audit.UserActivityType;
import com.cs.avatar.Level;
import com.cs.item.ItemService;
import com.cs.item.PlayerItem;
import com.cs.level.LevelService;
import com.cs.persistence.Constants;
import com.cs.player.Player;
import com.cs.player.PlayerService;
import com.cs.user.User;
import com.cs.user.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * @author Joakim Gottzén
 */
@RestController
@RequestMapping(value = "/api/patch", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
public class PatchController {
    final AuditService auditService;
    final ItemService itemService;
    final LevelService levelService;
    final PlayerService playerService;
    final UserService userService;
    private final Logger logger = LoggerFactory.getLogger(PatchController.class);

    @Autowired
    public PatchController(final AuditService auditService, final ItemService itemService, final LevelService levelService, final PlayerService playerService,
                           final UserService userService) {
        this.auditService = auditService;
        this.itemService = itemService;
        this.levelService = levelService;
        this.playerService = playerService;
        this.userService = userService;
    }

    @RequestMapping(method = POST, value = "/items/fix")
    @ResponseStatus(OK)
    public void fixItems(@CurrentUser final BackOfficeUser currentUser) {
        final User user = userService.getUser(currentUser.getId());
        logger.info("User {} starting to fix items", user.getId());
        auditService.trackUserActivity(user, UserActivityType.PATCH_CALL, "Fixing player items");

        final List<Long> playerIds = new ArrayList<>();
        int page = 0;
        boolean more;
        do {
            final Page<Player> players = playerService.searchPlayers(null, null, null, null, null, null, page, Constants.DEFAULT_PAGE_SIZE);
            page += 1;
            more = !players.isLastPage();
            for (final Player player : players) {
                final Iterable<Level> levelsWithItems = levelService.getLevelsWithItems(1L, player.getLevel().getLevel());
                final List<PlayerItem> playerItems = itemService.assignItems(player, levelsWithItems);
                if (!playerItems.isEmpty()) {
                    playerIds.add(player.getId());
                }
            }
        } while (more);
        logger.info("User {} fixed items for players {}", user.getId(), playerIds);
    }
}
