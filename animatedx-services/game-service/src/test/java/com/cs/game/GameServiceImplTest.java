package com.cs.game;

import com.cs.audit.AuditService;
import com.cs.avatar.Avatar;
import com.cs.avatar.AvatarBaseType;
import com.cs.avatar.Level;
import com.cs.payment.Currency;
import com.cs.payment.Money;
import com.cs.persistence.Country;
import com.cs.player.Address;
import com.cs.player.BlockType;
import com.cs.player.BlockedPlayerException;
import com.cs.player.Player;
import com.cs.player.PlayerLimitation;
import com.cs.player.Verification;
import com.cs.player.Wallet;
import com.cs.user.User;

import com.casinomodule.api.CasinoMCService;
import com.casinomodule.api.GameGroupData;

import org.springframework.test.util.ReflectionTestUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import javax.xml.rpc.ServiceException;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.cs.persistence.Language.ENGLISH;
import static com.cs.persistence.Language.SWEDISH;
import static com.cs.persistence.Status.ACTIVE;
import static com.cs.persistence.Status.INACTIVE;
import static java.util.Calendar.MAY;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.isA;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * @author Hadi Movaghar
 */
public class GameServiceImplTest {
    @InjectMocks
    private GameServiceImpl gameServiceImpl;

    @Mock
    private GameSessionRepository gameSessionRepository;
    @Mock
    private CasinoModuleConnectionService connectionService;
    @Mock
    private CasinoMCService casinoMCService;
    @Mock
    private PlayerLimitationRepository playerLimitationRepository;
    @Mock
    private AuditService auditService;
    @Mock
    private GameRepository gameRepository;
    @Mock
    private GameInfoRepository gameInfoRepository;

    private Level createLevel() {
        final Level level = new Level();
        level.setMoneyCreditRate(0.5D);
        level.setBonusCreditRate(1.5D);
        level.setLevel(5L);
        return level;
    }

    private Wallet createWallet() {
        final Wallet wallet = new Wallet();
        wallet.setMoneyBalance(new Money(1000L));
        wallet.setCreditsBalance(0);
        return wallet;
    }

    private Avatar createAvatar() {
        final Avatar avatar = new Avatar();
        avatar.setLevel(new Level(1L));
        avatar.setId(1L);
        avatar.setStatus(ACTIVE);
        avatar.setAvatarBaseType(new AvatarBaseType());
        return avatar;
    }

    private Address createAddress() {
        final Address address = new Address();
        address.setStreet("Street");
        address.setCity("City");
        address.setZipCode("123456");
        address.setCountry(Country.SWEDEN);
        return address;
    }

    private Player createPlayerWithWalletAndLevelAndAvatar() {
        final Player player = new Player();
        player.setId(1L);
        player.setEmailAddress("exist@test.com");
        player.setStatus(ACTIVE);
        player.setAddress(createAddress());
        player.setFirstName("First");
        player.setLastName("Last");
        player.setNickname("Nick");
        player.setEmailVerification(Verification.VERIFIED);
        player.setPlayerVerification(Verification.VERIFIED);
        player.setBlockType(BlockType.UNBLOCKED);
        player.setCurrency(Currency.EUR);
        player.setAvatar(createAvatar());
        player.setWallet(createWallet());
        player.setLevel(createLevel());
        player.setPhoneNumber("123-456-789");
        player.setLanguage(ENGLISH);

        final Calendar calendar = Calendar.getInstance();
        calendar.set(1970, MAY, 1);
        player.setBirthday(calendar.getTime());

        return player;
    }

    private List<GameSession> createGameSession() {
        final List<GameSession> gameSessions = new ArrayList<>();
        final String oldSessionId = "old-session-id";
        final GameSession gameSession = new GameSession(createPlayerWithWalletAndLevelAndAvatar(), new Date(19970101));
        gameSession.setSessionId(oldSessionId);
        gameSession.setSessionLength(10);
        gameSessions.add(gameSession);
        return gameSessions;
    }

    private User createUser() {
        final User user = new User();
        user.setId(1L);
        user.setStatus(ACTIVE);
        return user;
    }

    private PlayerLimitation createPlayerLimitation() {
        final PlayerLimitation playerLimitation = new PlayerLimitation();
        playerLimitation.setId(1L);
        playerLimitation.setSessionLength(1);
        return playerLimitation;
    }

    @Before
    public void setup()
            throws MalformedURLException, ServiceException {
        initMocks(this);
        when(connectionService.getCasinoMCService()).thenReturn(casinoMCService);
    }

    @SuppressWarnings("SpellCheckingInspection")
    private List<GameInfo> createGamesInfosInDatabase() {
        final List<Game> games = createGamesInDatabase();
        final List<GameInfo> gameInfos = new ArrayList<>();

        final GameInfo gameInfo1 = new GameInfo();
        gameInfo1.setGame(games.get(0));

        final GameInfo gameInfo2 = new GameInfo();
        gameInfo2.setGame(games.get(1));

        gameInfos.add(gameInfo1);
        gameInfos.add(gameInfo2);
        return gameInfos;
    }

    @SuppressWarnings("SpellCheckingInspection")
    private List<Game> createGamesInDatabase() {
        final List<Game> games = new ArrayList<>();

        final Game game1 = new Game();
        game1.setGameId("blackjackdblex_sw");
        game1.setFullName("Alien Robots");
        game1.setName("Alien Robots");
        game1.setCategory(GameCategory.VIDEO_POKER);
        game1.setStatus(ACTIVE);

        final Game game2 = new Game();
        game2.setGameId("allamerican25_sw");
        game2.setFullName("1H All American (NC)");
        game2.setName("1H All American (NC)");
        game2.setCategory(GameCategory.VIDEO_POKER);
        game2.setStatus(ACTIVE);

        games.add(game1);
        games.add(game2);
        return games;
    }

    @SuppressWarnings("SpellCheckingInspection")
    private GameGroupData[] NetEntGameGroups() {
        final com.casinomodule.api.Game[] games = new com.casinomodule.api.Game[2];
        games[0] = new com.casinomodule.api.Game();
        games[0].setGameId("allamerican25_sw");

        games[1] = new com.casinomodule.api.Game();
        games[1].setGameId("blackjackdblex_sw");

        final GameGroupData[] gameGroups = new GameGroupData[1];
        gameGroups[0] = new GameGroupData();
        gameGroups[0].setGroupName("Video Poker");
        gameGroups[0].setGame(games);

        return gameGroups;
    }

    @Test
    public void loginPlayer_ShouldNewReturnGameSession()
            throws RemoteException {
        final GameSession gameSession = createGameSession().get(0);
        final Player player = createPlayerWithWalletAndLevelAndAvatar();

        when(gameSessionRepository.findByPlayer(player)).thenReturn(gameSession);
        when(casinoMCService.loginUserDetailed(anyString(), anyString(), any(String[].class), anyString(), anyString(), anyString())).thenReturn("Session");

        gameServiceImpl.loginPlayer(player, Channel.FLASH);
        assertThat(gameSession.getSessionId(), is("Session"));
        assertThat(gameSession.getStartDate(), is(notNullValue()));
    }

    @Test
    public void loginPlayer_ShouldReturnExistingGameSession()
            throws RemoteException {
        final GameSession gameSession = createGameSession().get(0);
        gameSession.setChannel(Channel.FLASH);
        gameSession.setStartDate(new Date());
        final Player player = createPlayerWithWalletAndLevelAndAvatar();

        when(gameSessionRepository.findByPlayer(player)).thenReturn(gameSession);
        when(casinoMCService.loginUserDetailed(anyString(), anyString(), any(String[].class), anyString(), anyString(), anyString())).thenReturn("Session");

        gameServiceImpl.loginPlayer(player, Channel.FLASH);
        assertThat(gameSession.getSessionId(), is("old-session-id"));
        assertThat(gameSession.getStartDate(), is(notNullValue()));
    }

    @Test(expected = PlayerVerificationException.class)
    public void loginPlayer_ShouldThrowPlayerVerificationException()
            throws RemoteException, ParseException {
        final Player player = createPlayerWithWalletAndLevelAndAvatar();
        player.setEmailVerification(Verification.UNVERIFIED);

        gameServiceImpl.loginPlayer(player, Channel.FLASH);
    }

    @Test(expected = BlockedPlayerException.class)
    public void loginPlayer_ShouldThrowBlockedPlayerException()
            throws RemoteException {
        final Player player = createPlayerWithWalletAndLevelAndAvatar();
        player.setBlockType(BlockType.BET_LIMIT);
        player.setBlockEndDate(new Date(12220227200000L));
        gameServiceImpl.loginPlayer(player, Channel.FLASH);
    }

    @Test
    public void logoutPlayer_setEndDateAfterLogout()
            throws RemoteException, MalformedURLException, ServiceException {
        final List<GameSession> gameSessions = createGameSession();
        final Player player = createPlayerWithWalletAndLevelAndAvatar();

        when(gameSessionRepository.findByPlayer(player)).thenReturn(gameSessions.get(0));

        gameServiceImpl.logoutPlayer(player);
        assertThat(gameSessions.get(0).getEndDate(), isA(Date.class));
    }

    @SuppressWarnings("SpellCheckingInspection")
    @Test
    public void getGames_ShouldReturnSetContainingGames()
            throws MalformedURLException, ServiceException, RemoteException {
        ReflectionTestUtils.setField(gameServiceImpl, "idOnNetEntCasinoModule", "id");
        ReflectionTestUtils.setField(gameServiceImpl, "passwordOnNetEntCasinoModule", "password");

        final Game game1 = new Game();
        game1.setGameId("blackjackdblex_sw");
        final Game game2 = new Game();
        game2.setGameId("allamerican25_sw");
        final Set<Game> expectedGames = Sets.newHashSet(game1, game2);

        when(casinoMCService.getGameGroups("id", "password")).thenReturn(NetEntGameGroups());
        final Set<Game> games = gameServiceImpl.getGamesFromNetEnt();
        assertThat(games, is(equalTo(expectedGames)));
    }

    @Test
    public void getGameInfoFromNetEnt_ShouldReturnExpectedGameInfo()
            throws MalformedURLException, ServiceException, RemoteException {
        ReflectionTestUtils.setField(gameServiceImpl, "idOnNetEntCasinoModule", "id");
        ReflectionTestUtils.setField(gameServiceImpl, "passwordOnNetEntCasinoModule", "password");
        final String[] gameInfosFromNetEnt = new String[4];
        gameInfosFromNetEnt[0] = "WIDTH";
        gameInfosFromNetEnt[1] = "561";
        gameInfosFromNetEnt[2] = "HEIGHT";
        gameInfosFromNetEnt[3] = "250";

        final GameInfo gameInfoEnglish = new GameInfo();
        gameInfoEnglish.setGame(new Game("GameIdEnglish"));
        final GameInfo gameInfoSwedish = new GameInfo();
        gameInfoSwedish.setGame(new Game("GameIdSwedish"));
        final List<GameInfo> expectedGameInfo = Lists.newArrayList(gameInfoEnglish, gameInfoSwedish);

        when(gameInfoRepository.findByGameAndLanguage(new Game("GameIdEnglish"), ENGLISH)).thenReturn(null);
        when(gameInfoRepository.findByGameAndLanguage(new Game("GameIdSwedish"), SWEDISH)).thenReturn(null);
        when(casinoMCService.getGameInfo("GameId", "en", "id", "password")).thenReturn(gameInfosFromNetEnt);
        when(casinoMCService.getGameInfo("GameId", "sv", "id", "password")).thenReturn(gameInfosFromNetEnt);

        final List<GameInfo> gameInfoList = gameServiceImpl.getGameInfoFromNetEnt(new Game("GameId"));

        assertThat(gameInfoList, is(equalTo(expectedGameInfo)));
    }

    @SuppressWarnings("SpellCheckingInspection")
    @Test
    public void refreshGames_ShouldReturnGamesInDataBase()
            throws MalformedURLException, ServiceException, RemoteException {
        final List<Game> gamesInDatabase = createGamesInDatabase();

        ReflectionTestUtils.setField(gameServiceImpl, "idOnNetEntCasinoModule", "id");
        ReflectionTestUtils.setField(gameServiceImpl, "passwordOnNetEntCasinoModule", "password");
        final String[] gameInfosFromNetEnt = new String[4];
        gameInfosFromNetEnt[0] = "WIDTH";
        gameInfosFromNetEnt[1] = "561";
        gameInfosFromNetEnt[2] = "HEIGHT";
        gameInfosFromNetEnt[3] = "250";

        final Set<Game> expectedGames = new HashSet<>(gamesInDatabase);

        when(gameRepository.findAllWithInfos()).thenReturn(gamesInDatabase);
        when(casinoMCService.getGameGroups("id", "password")).thenReturn(NetEntGameGroups());
        when(casinoMCService.getGameInfo("blackjackdblex_sw", "en", "id", "password")).thenReturn(gameInfosFromNetEnt);
        when(casinoMCService.getGameInfo("blackjackdblex_sw", "sv", "id", "password")).thenReturn(gameInfosFromNetEnt);
        when(casinoMCService.getGameInfo("allamerican25_sw", "en", "id", "password")).thenReturn(gameInfosFromNetEnt);
        when(casinoMCService.getGameInfo("allamerican25_sw", "sv", "id", "password")).thenReturn(gameInfosFromNetEnt);
        final Set<Game> games = gameServiceImpl.refreshGames(new User());
        assertThat(games, is(equalTo(expectedGames)));
    }

    @SuppressWarnings("SpellCheckingInspection")
    @Test
    public void updateGame_ShouldReturnUpdatedGame() {
        final User user = createUser();

        final Game gameInDatabase = new Game("blackjackdblex_sw");
        gameInDatabase.setStatus(ACTIVE);

        when(gameRepository.findOne("blackjackdblex_sw")).thenReturn(gameInDatabase);

        final Game game = new Game("blackjackdblex_sw");
        game.setStatus(INACTIVE);

        final Game updatedGame = gameServiceImpl.updateGame(game, user);
        assertThat(updatedGame.getModifiedBy(), is(user));
        assertThat(updatedGame.getStatus(), is(game.getStatus()));
        assertThat(updatedGame.getModifiedDate(), is(notNullValue()));
    }

    @Test
    public void getActiveGames_ShouldReturnActiveGames() {
        final Player player = createPlayerWithWalletAndLevelAndAvatar();
        final List<GameInfo> gamesInDatabase = createGamesInfosInDatabase();

        when(gameInfoRepository.findGamesForLobby(ACTIVE, GameClient.FLASH.getClient(), player.getLanguage(), GameCategory.OTHER)).thenReturn(gamesInDatabase);

        final List<GameInfo> expectedGames = gameServiceImpl.getActiveGames(player.getLanguage());

        assertThat(expectedGames, is(gamesInDatabase));
    }

    @Test
    public void isSessionTimeOver_ShouldReturnTrue() {
        final List<GameSession> gameSessions = createGameSession();
        final Player player = createPlayerWithWalletAndLevelAndAvatar();
        final PlayerLimitation playerLimitation = createPlayerLimitation();

        when(gameSessionRepository.findByPlayer(player)).thenReturn(gameSessions.get(0));
        when(playerLimitationRepository.findByPlayer(player)).thenReturn(playerLimitation);

        final boolean isSessionTimeOver = gameServiceImpl.isSessionTimeOver(player);
        assertThat(isSessionTimeOver, is(true));
    }

    @Test
    public void isSessionTimeOver_ShouldReturnFalse() {
        final List<GameSession> gameSessions = createGameSession();
        gameSessions.get(0).setSessionLength(0);
        final Player player = createPlayerWithWalletAndLevelAndAvatar();
        final PlayerLimitation playerLimitation = createPlayerLimitation();
        playerLimitation.setSessionLength(0);

        when(gameSessionRepository.findByPlayer(player)).thenReturn(gameSessions.get(0));
        when(playerLimitationRepository.findByPlayer(player)).thenReturn(playerLimitation);

        final boolean isSessionTimeOver = gameServiceImpl.isSessionTimeOver(player);
        assertThat(isSessionTimeOver, is(false));
    }
}
