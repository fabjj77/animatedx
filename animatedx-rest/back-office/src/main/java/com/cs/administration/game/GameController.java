package com.cs.administration.game;

import com.cs.administration.security.BackOfficeUser;
import com.cs.administration.security.CurrentUser;
import com.cs.game.Game;
import com.cs.game.GameCategory;
import com.cs.game.GameService;
import com.cs.game.GameSessionPageableDto;
import com.cs.game.GameTransaction;
import com.cs.game.GameTransactionService;
import com.cs.player.Player;
import com.cs.player.PlayerService;
import com.cs.security.AccessDeniedException;
import com.cs.user.SecurityRole;
import com.cs.user.SecurityRole.Access;
import com.cs.user.User;
import com.cs.user.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Nullable;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static com.cs.persistence.Constants.BO_DEFAULT_SIZE;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

/**
 * @author Omid Alaepour
 */
@RestController
@RequestMapping(value = "/api/games", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
public class GameController {

    private final GameService gameService;
    private final GameTransactionService gameTransactionService;
    private final PlayerService playerService;
    private final UserService userService;

    @Autowired
    public GameController(final GameService gameService, final GameTransactionService gameTransactionService, final PlayerService playerService,
                          final UserService userService) {
        this.gameService = gameService;
        this.gameTransactionService = gameTransactionService;
        this.playerService = playerService;
        this.userService = userService;
    }

    @RequestMapping(method = GET, value = "/categories")
    @ResponseStatus(OK)
    public List<GameCategory> getCategories() {
        return Arrays.asList(GameCategory.values());
    }

    @RequestMapping(method = POST, value = "/refresh")
    @ResponseStatus(OK)
    public List<BackOfficeGameDto> refreshGames(@CurrentUser final BackOfficeUser currentUser) {
        final User user = userService.getUser(currentUser.getId());
        if (user.getRoles().get(0).getPk().getRole().getName() != SecurityRole.SUPER_USER) {
            throw new AccessDeniedException("User not allowed");
        }

        final Set<Game> games = gameService.refreshGames(user);
        return convert(games);
    }

    @RequestMapping(method = POST, value = "/refreshInfos")
    @ResponseStatus(NO_CONTENT)
    public void refreshGameInfos(@CurrentUser final BackOfficeUser currentUser) {
        final User user = userService.getUser(currentUser.getId());
        if (user.getRoles().get(0).getPk().getRole().getName() != SecurityRole.SUPER_USER) {
            throw new AccessDeniedException("User not allowed");
        }

        gameService.refreshGamesInfos(user);
    }

    private List<BackOfficeGameDto> convert(final Set<Game> games) {
        final List<BackOfficeGameDto> gameDtos = new ArrayList<>();
        if (games != null) {
            for (final Game game : games) {
                gameDtos.add(new BackOfficeGameDto(game));
            }
        }
        return gameDtos;
    }

    @RequestMapping(method = GET)
    @ResponseStatus(OK)
    public GamePageableDto getGames(@RequestParam(value = "id", required = false) @Nullable final String gameId,
                                    @RequestParam(value = "name", required = false) @Nullable final String name,
                                    @RequestParam(value = "category", required = false) @Nullable final GameCategory category,
                                    @RequestParam(value = "featured", required = false) @Nullable final Boolean featured,
                                    @RequestParam(value = "page", required = false, defaultValue = "0") final Integer page,
                                    @RequestParam(value = "size", required = false, defaultValue = BO_DEFAULT_SIZE) final Integer size,
                                    @CurrentUser final BackOfficeUser currentUser) {
        final User user = userService.getUser(currentUser.getId());
        final Page<Game> games = gameService.getGames(user, gameId, name, category, featured, page, size);
        return new GamePageableDto(games);
    }

    @RequestMapping(method = PUT, value = "{gameId}")
    @ResponseStatus(OK)
    public BackOfficeGameDto updateGame(@PathVariable final String gameId, @Valid @RequestBody(required = true) final GameUpdateDto gameUpdateDto,
                                        @CurrentUser final BackOfficeUser currentUser) {
        final User user = userService.getUser(currentUser.getId());
        final SecurityRole securityRole = user.getRoles().get(0).getPk().getRole().getName();
        if (!(securityRole.getAccesses().contains(Access.GAME_ADMIN) || securityRole.getAccesses().contains(Access.GAME_WRITE))) {
            throw new AccessDeniedException("User not allowed");
        }

        gameUpdateDto.setGameId(gameId);
        final Game game = gameService.updateGame(gameUpdateDto.asGame(), user);
        return new BackOfficeGameDto(game);

    }

    @RequestMapping(method = GET, value = "/session")
    @ResponseStatus(OK)
    public GameSessionPageableDto getGameSessionHistory(@RequestParam(value = "playerId", required = true) final Long playerId,
                                                        @RequestParam(value = "startDate", required = true) @DateTimeFormat(iso = ISO.DATE) final Date startDate,
                                                        @RequestParam(value = "endDate", required = true) @DateTimeFormat(iso = ISO.DATE) final Date endDate,
                                                        @RequestParam(value = "page", required = false, defaultValue = "0") final Integer page,
                                                        @RequestParam(value = "size", required = false, defaultValue = BO_DEFAULT_SIZE) final Integer size) {
        final Player player = playerService.getPlayer(playerId);
        final Page<GameTransaction> gameTransactions = gameTransactionService.getGameTransactions(player, startDate, endDate, page, size);
        return new GameSessionPageableDto(gameTransactions);
    }
}
