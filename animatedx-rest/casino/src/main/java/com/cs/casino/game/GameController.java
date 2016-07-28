package com.cs.casino.game;

import com.cs.audit.AuditService;
import com.cs.audit.PlayerActivityType;
import com.cs.casino.security.CurrentPlayer;
import com.cs.casino.security.PlayerUser;
import com.cs.game.Channel;
import com.cs.game.Game;
import com.cs.game.GameInfo;
import com.cs.game.GameService;
import com.cs.game.GameSessionPageableDto;
import com.cs.game.GameTransaction;
import com.cs.game.GameTransactionService;
import com.cs.game.LeaderboardEntry;
import com.cs.messaging.bronto.BrontoGateway;
import com.cs.messaging.bronto.InternalBrontoContact;
import com.cs.messaging.bronto.UpdateContactFieldMessage;
import com.cs.messaging.email.BrontoFieldName;
import com.cs.persistence.Language;
import com.cs.player.Player;
import com.cs.player.PlayerService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Calendar;
import java.util.Date;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static com.cs.persistence.Constants.CASINO_DEFAULT_SIZE;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * @author Omid Alaepour
 */

@RestController
@RequestMapping(value = "/api/games", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
public class GameController {

    private final AuditService auditService;
    private final BrontoGateway brontoGateway;
    private final GameService gameService;
    private final GameTransactionService gameTransactionService;
    private final PlayerService playerService;

    @Value("${game.net-net.operatorId}")
    private String operatorId;

    @Autowired
    public GameController(final AuditService auditService, final BrontoGateway brontoGateway, final GameService gameService,
                          final GameTransactionService gameTransactionService, final PlayerService playerService) {
        this.auditService = auditService;
        this.brontoGateway = brontoGateway;
        this.gameService = gameService;
        this.gameTransactionService = gameTransactionService;
        this.playerService = playerService;
    }

    @RequestMapping(method = POST)
    @ResponseStatus(OK)
    public GameInfoDto playGame(@RequestBody(required = true) @Valid final GameInfoDto gameInfoDto, @RequestHeader("X-Forwarded-For") final String host,
                                @CurrentPlayer final PlayerUser currentPlayer) {
        final Player player = playerService.getPlayer(currentPlayer.getId());
        auditService.trackPlayerActivityWithIpAddress(player, PlayerActivityType.REQ_PLAY_GAME, host);

        final GameInfo gameInfo = gameService.getGameInfo(new Game(gameInfoDto.getGameId()), player.getLanguage());
        final String sessionId = gameService.loginPlayer(player, Channel.FLASH);

        //Update wallet info in Bronto
        updateBrontoWallet(player);

        return new GameInfoDto(gameInfo, sessionId, operatorId);
    }

    private void updateBrontoWallet(final Player player) {
        final Map<BrontoFieldName, String> fieldNameMap = new EnumMap<>(BrontoFieldName.class);
        fieldNameMap.put(BrontoFieldName.money_balance, player.getWallet().getMoneyBalance().getEuroValueInBigDecimal().toString());
        fieldNameMap.put(BrontoFieldName.bonus_balance, player.getWallet().getBonusBalance().getEuroValueInBigDecimal().toString());
        brontoGateway.updateContactFields(new UpdateContactFieldMessage(new InternalBrontoContact(player), fieldNameMap));
    }

    @RequestMapping(method = POST, value = "/touch")
    @ResponseStatus(OK)
    public GameInfoDto playTouchGame(@RequestBody(required = true) @Valid final GameInfoDto gameInfoDto, @RequestHeader("X-Forwarded-For") final String host,
                                     @CurrentPlayer final PlayerUser currentPlayer) {
        final Player player = playerService.getPlayer(currentPlayer.getId());
        auditService.trackPlayerActivityWithIpAddress(player, PlayerActivityType.REQ_PLAY_GAME, host);

        final GameInfo gameInfo = gameService.getTouchGameInfo(new Game(gameInfoDto.getGameId()), player.getLanguage());
        final String sessionId = gameService.loginPlayer(player, Channel.TOUCH);
        return new GameInfoDto(gameInfo, sessionId, operatorId);
    }

    @RequestMapping(method = GET)
    @ResponseStatus(OK)
    public GamesDto getGames(@CurrentPlayer final PlayerUser currentPlayer) {
        final Language language;
        if (currentPlayer != null) {
            language = playerService.getPlayer(currentPlayer.getId()).getLanguage();
        } else {
            language = Language.ENGLISH;
        }
        final List<GameInfo> games = gameService.getActiveGames(language);
        return new GamesDto(games);
    }

    @RequestMapping(method = GET, value = "/touch")
    @ResponseStatus(OK)
    public GamesDto getTouchGames(@CurrentPlayer final PlayerUser currentPlayer) {
        final Language language;
        if (currentPlayer != null) {
            language = playerService.getPlayer(currentPlayer.getId()).getLanguage();
        } else {
            language = Language.ENGLISH;
        }
        final List<GameInfo> games = gameService.getActiveTouchGames(language);
        return new GamesDto(games);
    }

    @RequestMapping(method = GET, value = "/session")
    @ResponseStatus(OK)
    public GameSessionPageableDto getGameSessionHistory(@CurrentPlayer final PlayerUser currentPlayer,
                                                        @RequestParam(value = "startDate", required = true) @DateTimeFormat(iso = ISO.DATE) final Date startDate,
                                                        @RequestParam(value = "endDate", required = true) @DateTimeFormat(iso = ISO.DATE) final Date endDate,
                                                        @RequestParam(value = "page", required = false, defaultValue = "0") final Integer page,
                                                        @RequestParam(value = "size", required = false, defaultValue = CASINO_DEFAULT_SIZE) final Integer size,
                                                        @RequestHeader("X-Forwarded-For") final String host) {
        final Player player = playerService.getPlayer(currentPlayer.getId());
        auditService.trackPlayerActivityWithIpAddress(player, PlayerActivityType.REQ_GET_GAME_TRANSACTIONS, host);
        final Page<GameTransaction> gameTransactions = gameTransactionService.getGameTransactions(player, startDate, endDate, page, size);
        return new GameSessionPageableDto(gameTransactions);
    }

    @RequestMapping(method = GET, value = "/leaderboard/{week}")
    @ResponseStatus(OK)
    public LeaderboardDto getLeaderPlayersOfTheWeek(@PathVariable("week") final Integer week) {
        final Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.WEEK_OF_YEAR, week);
        calendar.set(Calendar.YEAR, calendar.getWeekYear());

        // Now get the first day of week.
        final Date date = calendar.getTime();
        final List<LeaderboardEntry> leaderboardEntryList = gameTransactionService.getLeaderPlayersOfTheWeek(date);
        return new LeaderboardDto(leaderboardEntryList);
    }

    @RequestMapping(method = GET, value = "/leaderboard")
    @ResponseStatus(OK)
    public LeaderboardDto getLeaderPlayersOfTheWeek() {
        final Date startWeekDate = new Date();
        final List<LeaderboardEntry> leaderboardEntryList = gameTransactionService.getLeaderPlayersOfTheWeek(startWeekDate);
        return new LeaderboardDto(leaderboardEntryList);
    }
}
