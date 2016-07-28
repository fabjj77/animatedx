package com.cs.casino.item;

import com.cs.audit.AuditService;
import com.cs.audit.PlayerActivityType;
import com.cs.casino.security.CurrentPlayer;
import com.cs.casino.security.PlayerUser;
import com.cs.item.ItemService;
import com.cs.item.ItemState;
import com.cs.item.PlayerItem;
import com.cs.player.PlayerService;

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
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

/**
 * @author Omid Alaepour
 */
@RestController
@RequestMapping(value = "/api/items", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
public class ItemController {
    private final PlayerService playerService;
    private final ItemService itemService;
    private final AuditService auditService;

    @Autowired
    public ItemController(final PlayerService playerService, final ItemService itemService, final AuditService auditService) {
        this.playerService = playerService;
        this.itemService = itemService;
        this.auditService = auditService;
    }

    @RequestMapping(method = PUT, value = "/{itemId}")
    @ResponseStatus(OK)
    public ItemDto useItem(@CurrentPlayer final PlayerUser currentPlayer, @PathVariable("itemId") final Long itemId,
                           @RequestHeader("X-Forwarded-For") final String host) {
        auditService.trackPlayerActivityWithIpAddress(playerService.getPlayer(currentPlayer.getId()), PlayerActivityType.REQ_USE_ITEM, host);
        final PlayerItem playerItem = itemService.useItem(currentPlayer.getId(), itemId);
        return new ItemDto(playerItem);
    }

    @RequestMapping(method = GET)
    @ResponseStatus(OK)
    public ItemsDto getPlayerItems(@CurrentPlayer final PlayerUser currentPlayer, @RequestHeader("X-Forwarded-For") final String host) {
        final List<PlayerItem> unusedItems = itemService.getPlayerItems(currentPlayer.getId(), ItemState.UNUSED);
        final List<PlayerItem> usedItems = itemService.getPlayerItems(currentPlayer.getId(), ItemState.USED);
        auditService.trackPlayerActivityWithIpAddress(playerService.getPlayer(currentPlayer.getId()), PlayerActivityType.REQ_GET_PLAYER_ITEMS, host);
        return new ItemsDto(unusedItems, usedItems);
    }
}
