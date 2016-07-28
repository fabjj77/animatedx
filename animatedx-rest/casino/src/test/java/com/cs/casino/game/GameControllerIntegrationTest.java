package com.cs.casino.game;

import com.cs.avatar.Avatar;
import com.cs.avatar.AvatarBaseType;
import com.cs.avatar.HairColor;
import com.cs.avatar.Level;
import com.cs.avatar.SkinColor;
import com.cs.casino.player.TestConfig;
import com.cs.game.Channel;
import com.cs.game.Game;
import com.cs.game.GameDto;
import com.cs.game.GameInfo;
import com.cs.game.GameService;
import com.cs.game.GameTransactionService;
import com.cs.game.LeaderboardEntry;
import com.cs.payment.Money;
import com.cs.persistence.Language;
import com.cs.player.Player;
import com.cs.player.PlayerService;
import com.cs.player.Wallet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.ContentResultMatchers;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.cs.persistence.Language.ENGLISH;
import static com.cs.persistence.Status.ACTIVE;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * @author Omid Alaepour
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {TestConfig.class, GameController.class})
public class GameControllerIntegrationTest {
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private FilterChainProxy springSecurityFilter;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private GameService gameService;

    @Autowired
    private PlayerService playerService;

    @Mock
    private GameTransactionService gameTransactionService;


    @Before
    public void setup() {
        mockMvc = webAppContextSetup(context).build();
    }

    private GameInfo createGameInfo() {
        final GameInfo gameInfo = new GameInfo();
        gameInfo.setId(1L);
        gameInfo.setGame(new Game("BlackJack"));
        gameInfo.setLanguage(ENGLISH);
        return gameInfo;
    }

    private GameInfoDto createGameInfoDto() {
        return new GameInfoDto(createGameInfo(), "Session-123", "BlingCity");
    }

    private Player createPlayerWithAvatarAndWallet() {
        final Player player = new Player();
        player.setId(0L);
        player.setWallet(new Wallet());
        player.setFirstName("First");
        player.setLastName("Last");
        player.setEmailAddress("email@email.com");
        player.setPassword("$2a$10$9UhOj9mS294sxq0NyIwMb.N5fYvJNRCyGDbI3RD0xF8W1AXs3aRyG");
        player.setNickname("Nick");
        player.setBirthday(new Date(20120101));
        player.setStatus(ACTIVE);

        final AvatarBaseType avatarBaseType = new AvatarBaseType();
        avatarBaseType.setId(1);

        final Avatar avatar = new Avatar(1L);
        avatar.setHairColor(HairColor.DARK);
        avatar.setSkinColor(SkinColor.CAUCASIAN);
        avatar.setPictureUrl("");
        avatar.setAvatarBaseType(avatarBaseType);
        avatar.setLevel(new Level(1L));

        final Level level = new Level();
        level.setLevel(1L);
        level.setCashbackPercentage(new BigDecimal("2"));
        level.setTurnover(new Money(3L));

        player.setAvatar(avatar);
        player.setLevel(level);
        return player;
    }

    private List<GameInfo> createActiveGames() {
        final Game game1 = new Game();
        game1.setGameId("BJ");
        game1.setStatus(ACTIVE);

        final List<GameInfo> games = new ArrayList<>();
        final GameInfo gameInfo = new GameInfo();
        gameInfo.setGame(game1);
        games.add(gameInfo);
        return games;
    }

    //CAS-553. Penthouse
    private List<LeaderboardEntry> getLeadersForTheWeek() {

        Object[] player = Arrays.asList(3655, 9, 29, "s1h3.png", "Alessandro", "wish-master", "The Wish Master", new BigDecimal(18200), 0).toArray();

        List<LeaderboardEntry> leaderboardEntryList = new ArrayList<>();

        LeaderboardEntry leaderboardEntry = new LeaderboardEntry(player);

        leaderboardEntryList.add(leaderboardEntry);

        return leaderboardEntryList;
    }
    //CAS-553. Penthouse
    private LeaderboardDto buildLeaderboardDto(List<LeaderboardEntry> playerList){

        LeaderboardDto leaderboardDto = new LeaderboardDto(playerList);

        return leaderboardDto;
    }

    private Set<GameDto> convert(final List<GameInfo> games) {
        final Set<GameDto> gameDtos = new HashSet<>();
        if (games != null) {
            for (final GameInfo game : games) {
                gameDtos.add(new GameDto(game));
            }
        }
        return gameDtos;
    }

    @Test
    @Ignore
    public void playGame_ShouldReturnGameInfoDtoWithPlayerIdAndSessionId()
            throws Exception {
        final Player player = createPlayerWithAvatarAndWallet();
        final GameInfoDto gameInfoDto = createGameInfoDto();
        final GameInfo gameInfo = createGameInfo();

        mockMvc = webAppContextSetup(context).addFilter(springSecurityFilter, "/*").build();

        when(playerService.getPlayer(anyLong())).thenReturn(player);
        when(gameService.getGameInfo(any(Game.class), any(Language.class))).thenReturn(gameInfo);
        when(gameService.loginPlayer(any(Player.class), any(Channel.class))).thenReturn("Session-123");

        final String sentContent = mapper.writeValueAsString(gameInfoDto);
        final String expectedContent = mapper.writeValueAsString(gameInfoDto);

        mockMvc.perform(
                post("/api/games/").content(sentContent).contentType(APPLICATION_JSON).accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(expectedContent));
    }

    @Test
    @Ignore
    public void activeGames_ShouldReturnActiveGames()
            throws Exception {
        final Player player = createPlayerWithAvatarAndWallet();
        final List<GameInfo> activeGames = createActiveGames();

        when(gameService.getActiveGames(player.getLanguage())).thenReturn(activeGames);

        final String expectedContent = mapper.writeValueAsString(convert(activeGames));
        mockMvc = webAppContextSetup(context).addFilter(springSecurityFilter, "/*").build();
        final ContentResultMatchers content = content();
        mockMvc.perform(
                get("/api/games/").contentType(APPLICATION_JSON).accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content.string(expectedContent));
    }

    @Test
    @Ignore
    public void shouldReturnPenthousePlayerList()
            throws Exception {
        List<LeaderboardEntry> playerList = getLeadersForTheWeek();

        when(gameTransactionService.getLeaderPlayersOfTheWeek((Date)any())).thenReturn(playerList);

        final String expectedContent = mapper.writeValueAsString(buildLeaderboardDto(playerList));
        mockMvc = webAppContextSetup(context).addFilter(springSecurityFilter, "/*").build();
        final ContentResultMatchers content = content();
        mockMvc.perform(
                get("/api/games/weekly").contentType(APPLICATION_JSON).accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content.string(expectedContent));
    }
}
