package com.cs.game;

import com.cs.audit.AuditService;
import com.cs.audit.PlayerActivityType;
import com.cs.bonus.Bonus;
import com.cs.persistence.CommunicationException;
import com.cs.persistence.Language;
import com.cs.persistence.NotFoundException;
import com.cs.player.BlockedPlayerException;
import com.cs.player.Player;
import com.cs.player.PlayerLimitation;
import com.cs.user.User;

import com.casinomodule.api.BonusActivationDetails;
import com.casinomodule.api.CasinoMCService;
import com.casinomodule.api.CasinoSOAPException;
import com.casinomodule.api.GameGroupData;
import com.casinomodule.api.LoginInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.querydsl.QSort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Iterables;
import com.mysema.query.BooleanBuilder;
import com.mysema.query.JoinExpression;
import com.mysema.query.JoinType;
import com.mysema.query.types.Order;
import com.mysema.query.types.OrderSpecifier;
import com.mysema.query.types.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.rpc.ServiceException;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.cs.audit.PlayerActivityType.END_GAME;
import static com.cs.audit.PlayerActivityType.LOGOUT;
import static com.cs.persistence.Status.ACTIVE;
import static com.cs.persistence.Status.DELETED;
import static com.cs.persistence.Status.INACTIVE;
import static org.springframework.transaction.annotation.Isolation.READ_COMMITTED;
import static org.springframework.transaction.annotation.Propagation.SUPPORTS;

/**
 * @author Hadi Movaghar
 */
@Service
@Transactional(isolation = READ_COMMITTED)
public class GameServiceImpl implements GameService {

    private final Logger logger = LoggerFactory.getLogger(GameServiceImpl.class);

    @Value("${game.casino-soap-multi-currency-service-url}")
    private String casinoSoapMultiCurrencyServiceUrl;
    @Value("${game.id-on-netEnt-casino-module}")
    private String idOnNetEntCasinoModule;
    @Value("${game.password-on-netEnt-casino-module}")
    private String passwordOnNetEntCasinoModule;

    private final AuditService auditService;
    private final CasinoModuleConnectionService connectionService;

    private final GameInfoRepository gameInfoRepository;
    private final GameRepository gameRepository;
    private final GameSessionRepository gameSessionRepository;
    private final PlayerLimitationRepository playerLimitationRepository;

    @Autowired
    public GameServiceImpl(final AuditService auditService, final CasinoModuleConnectionService connectionService, final GameInfoRepository gameInfoRepository,
                           final GameRepository gameRepository, final GameSessionRepository gameSessionRepository,
                           final PlayerLimitationRepository playerLimitationRepository) {
        this.auditService = auditService;
        this.connectionService = connectionService;
        this.gameInfoRepository = gameInfoRepository;
        this.gameRepository = gameRepository;
        this.gameSessionRepository = gameSessionRepository;
        this.playerLimitationRepository = playerLimitationRepository;
    }

    @Override
    public void registerPlayer(final Player player, final Channel channel) {
        //noinspection OverlyBroadCatchBlock
        try {
            final CasinoMCService casinoMCService = connectionService.getCasinoMCService();
            casinoMCService.registerUser(CasinoModuleConstants.PLAYER_ID_PREFIX.concat(player.getId().toString()), CasinoModuleConstants.PLAYER_PASSWORD,
                                         getPlayerExtraInformation(player, channel), idOnNetEntCasinoModule, passwordOnNetEntCasinoModule, player.getCurrency().name());
            logger.info("Registered player with id {} into NetEnt.", player.getId());
        } catch (MalformedURLException | ServiceException | RemoteException e) {
            logger.error("Error while registering player with id {} on NetEnt.", player.getId(), e);
            throw new CommunicationException("Error while registering player on NetEnt.", e);
        }
    }

    private String[] getPlayerExtraInformation(final Player player, final Channel channel) {
        final ArrayList<String> extraInfoList = new ArrayList<>();
        extraInfoList.add(CasinoModuleConstants.PHONE);
        extraInfoList.add(getSubstring(player.getPhoneNumber(), 20));
        extraInfoList.add(CasinoModuleConstants.FIRST_NAME);
        extraInfoList.add(getSubstring(player.getFirstName(), 20));
        extraInfoList.add(CasinoModuleConstants.LAST_NAME);
        extraInfoList.add(getSubstring(player.getLastName(), 20));
        extraInfoList.add(CasinoModuleConstants.EMAIL);
        extraInfoList.add(getSubstring(player.getEmailAddress(), 70));
        extraInfoList.add(CasinoModuleConstants.BIRTH_DATE);
        extraInfoList.add(new SimpleDateFormat(CasinoModuleConstants.BIRTH_DATE_FORMAT).format(player.getBirthday()));
        extraInfoList.add(CasinoModuleConstants.CITY);
        extraInfoList.add(getSubstring(player.getAddress().getCity(), 20));
        extraInfoList.add(CasinoModuleConstants.STREET);
        extraInfoList.add(getSubstring(player.getAddress().getStreet(), 90));
        extraInfoList.add(CasinoModuleConstants.ZIP);
        extraInfoList.add(getSubstring(player.getAddress().getZipCode(), 10));
        if (player.getAddress().getState() != null) {
            extraInfoList.add(CasinoModuleConstants.STATE);
            extraInfoList.add(getSubstring(player.getAddress().getState(), 20));
        }
        extraInfoList.add(CasinoModuleConstants.COUNTRY);
        extraInfoList.add(getSubstring(player.getAddress().getCountry().toIso(), 60));
        extraInfoList.add(CasinoModuleConstants.DISPLAY_NAME);
        extraInfoList.add(getSubstring(player.getNickname(), 50));
        extraInfoList.add(CasinoModuleConstants.CHANNEL);
        extraInfoList.add(getSubstring(channel.getChannel(), 5));
        // TODO check affiliate get(0)
        if (player.getPlayerAffiliate() != null) {
            extraInfoList.add(CasinoModuleConstants.AFFILIATE_CODE);
            extraInfoList.add(getSubstring(player.getPlayerAffiliate().getAffiliateId(), 50));
        }
        return extraInfoList.toArray(new String[extraInfoList.size()]);
    }

    private String getSubstring(final String token, final int length) {
        return token.substring(0, Math.min(token.length(), length));
    }

    @Override
    public String loginPlayer(final Player player, final Channel channel) {

        //noinspection OverlyBroadCatchBlock
        try {
            if (!player.isEmailVerified()) {
                throw new PlayerVerificationException(player.getId());
            }

            if (player.getBlockType().isLimited(player.getBlockEndDate())) {
                logger.info("Player {} tried to start a game while was blocked: {}", player.getId(), player.getBlockType());
                throw new BlockedPlayerException("Player is blocked " + player.getBlockType());
            }

            final GameSession gameSession = gameSessionRepository.findByPlayer(player);
            final CasinoMCService casinoMCService = connectionService.getCasinoMCService();
            final Date currentDate = new Date();
            logger.info("Checking the game session of  player: {} to see if it already exists.", player.getId());
            if (gameSession.getSessionId() != null && gameSession.getEndDate() == null && gameSession.getChannel() == channel &&
                currentDate.getTime() - gameSession.getStartDate().getTime() < CasinoModuleConstants.SESSION_LENGTH) {
                auditService.trackPlayerActivityWithSessionId(player, PlayerActivityType.START_GAME, gameSession.getSessionId());
                logger.info("Game session of  player: {} already exists, returns without logging into NetEnt", player.getId());
                return gameSession.getSessionId();
            }
            logger.info("Attempting to login player: {} into NetEnt", player.getId());
            final String sessionId =
                    casinoMCService.loginUserDetailed(CasinoModuleConstants.PLAYER_ID_PREFIX.concat(player.getId().toString()), CasinoModuleConstants.PLAYER_PASSWORD,
                                                      getPlayerExtraInformation(player, channel), idOnNetEntCasinoModule, passwordOnNetEntCasinoModule,
                                                      player.getCurrency().name());

            logger.info("Logged in player with id {} into NetEnt.", player.getId());

            gameSession.setSessionId(sessionId);
            gameSession.setChannel(channel);
            gameSession.setStartDate(currentDate);
            // the session row in table is updated so end date have to be reset on login
            gameSession.setEndDate(null);
            gameSessionRepository.save(gameSession);

            auditService.trackPlayerActivityWithSessionId(player, PlayerActivityType.START_GAME, sessionId);

            return sessionId;
        } catch (MalformedURLException | ServiceException | RemoteException e) {
            logger.error("Error while logging in the player with id {} on NetEnt.", player.getId(), e);
            throw new CommunicationException("Error while logging in the player on NetEnt.", e);
        }
    }

    @Override
    public void logoutPlayer(final Player player) {
        //noinspection OverlyBroadCatchBlock
        final GameSession gameSession = gameSessionRepository.findByPlayer(player);

        if (gameSession == null || gameSession.getEndDate() != null || gameSession.getSessionId() == null) {
            logger.info("logoutPlayer: No active GameSession for Player {} found. Exiting method.", player.getId());
            return;
        }

        logoutPlayerFromNetent(gameSession.getSessionId());

        logger.info("Logged out player with id {} from NetEnt.", player.getId());
        gameSession.setEndDate(new Date());
        gameSessionRepository.save(gameSession);
        auditService.trackPlayerActivityWithSessionId(player, END_GAME, gameSession.getSessionId());
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public boolean activateBonus(final Player player, final Bonus bonus) {
        //noinspection TryWithIdenticalCatches
        logger.debug("Trying to activate bonus {} for player {}", bonus.getId(), player.getId());

        //Initially logs out a player if player has an active session
        logoutPlayer(player);

        //Login player to Netent if account exist. If account does not exist create one. Activate bonus program based on promotion code.
        final LoginInfo loginInfo = loginPlayerInNetentWithPromotionCode(player, bonus.getNetEntBonusCode());

        //Logout player from Netent only
        logoutPlayerFromNetent(loginInfo.getSessionId());

        for (final BonusActivationDetails bonusActivationDetails : loginInfo.getBonuspromotionCodeActivationDetails().getBonusActivationDetailsArr()) {
            logger.info("Activated bonus {} for player {}: {activated: {}, bonusProgramId: {}, reason: {}}", bonus.getNetEntBonusCode(), player.getId(),
                        bonusActivationDetails.isActivated(), bonusActivationDetails.getBonusProgramId(), bonusActivationDetails.getReason());
            if (!bonusActivationDetails.isActivated()) {
                return false;
            }
        }

        return true;
    }

    @Transactional(propagation = SUPPORTS)
    @Override
    public List<String> getActiveFreeRounds(final Player player) {
        //noinspection TryWithIdenticalCatches
        try {
            final CasinoMCService casinoMCService = connectionService.getCasinoMCService();
            return Arrays.asList(casinoMCService.getUserFreeRoundGames(CasinoModuleConstants.PLAYER_ID_PREFIX + player.getId(), CasinoModuleConstants.PLAYER_PASSWORD,
                                                                       idOnNetEntCasinoModule, passwordOnNetEntCasinoModule));
        } catch (final CasinoSOAPException e) {
            logger.error("Error while getting active free rounds for player", e);
            throw new CommunicationException("Error while getting active free rounds for player", e);
        } catch (MalformedURLException | ServiceException | RemoteException e) {
            logger.error("Error while getting active free rounds for player", e);
            throw new CommunicationException("Error while getting active free rounds for player", e);
        }
    }

    private LoginInfo loginPlayerInNetentWithPromotionCode(final Player player, final String bonusCode) {
        //noinspection TryWithIdenticalCatches
        try {
            final CasinoMCService casinoMCService = connectionService.getCasinoMCService();
            final LoginInfo loginInfo = casinoMCService.loginUserWithPromotionCode(CasinoModuleConstants.PLAYER_ID_PREFIX + player.getId(),
                                                                                   CasinoModuleConstants.PLAYER_PASSWORD, idOnNetEntCasinoModule,
                                                                                   passwordOnNetEntCasinoModule, player.getCurrency().name(), bonusCode);
            logger.info("Logging in player {} with bonus code {} into NetEnt", player.getId(), bonusCode);
            return loginInfo;
        } catch (final CasinoSOAPException e) {
            logger.error("Error while logging in player on NetEnt with promotion code.", e);
            throw new CommunicationException("Error while logging in player on NetEnt with promotion code.", e);
        } catch (MalformedURLException | ServiceException | RemoteException e) {
            logger.error("Error while logging in player on NetEnt with promotion code.", e);
            throw new CommunicationException("Error while logging in player on NetEnt with promotion code.", e);
        }
    }

    private void logoutPlayerFromNetent(final String sessionId) {
        //noinspection TryWithIdenticalCatches
        try {
            final CasinoMCService casinoMCService = connectionService.getCasinoMCService();
            casinoMCService.logoutUser(sessionId, idOnNetEntCasinoModule, passwordOnNetEntCasinoModule);
            logger.info("Logged out player with Session id {} from NetEnt", sessionId);
        } catch (final CasinoSOAPException e) {
            logger.error("Error while logging out player on NetEnt", e);
            throw new CommunicationException("Error while logging out player on NetEnt", e);
        } catch (MalformedURLException | ServiceException | RemoteException e) {
            logger.error("Error while logging out player on NetEnt", e);
            throw new CommunicationException("Error while logging out player on NetEnt", e);
        }
    }

    @Override
    public Set<Game> refreshGames(final User user) {
        //Get current games in database
        final Set<Game> gamesInDatabase = new HashSet<>(gameRepository.findAll());

        //Add available games to a new set
        final Set<Game> availableGames = new HashSet<>(getGamesFromNetEnt());

        //Copy available games to a new set and get all new games
        final Set<Game> newGames = new HashSet<>(availableGames);
        newGames.removeAll(gamesInDatabase);

        final Date currentDate = new Date();

        //If new games exist, loop through newGames and set User, CreatedDate and Status
        //Fetch game info for new games.
        final List<GameInfo> newGameInfos = new ArrayList<>();
        if (!newGames.isEmpty()) {
            for (final Game newGame : newGames) {
                newGame.setCategory(GameCategory.OTHER);
                newGame.setFeatured(Boolean.FALSE);
                newGame.setCreatedBy(user);
                newGame.setCreatedDate(currentDate);
                newGame.setStatus(INACTIVE);
                newGameInfos.addAll(getGameInfoFromNetEnt(newGame));
            }
        }

        //Copy available games to a new set and get all removed games
        final Set<Game> removedGames = new HashSet<>(gamesInDatabase);
        removedGames.removeAll(availableGames);

        //Loop through removed games and set as deleted
        for (final Game removedGame : removedGames) {
            removedGame.setStatus(DELETED);
            removedGame.setModifiedBy(user);
            removedGame.setModifiedDate(currentDate);
        }

        // Get the games that were re-added by NetEnt
        final Set<Game> deletedGames = new HashSet<>(gamesInDatabase);
        deletedGames.retainAll(availableGames);

        //Loop through existing games and set to Inactive if they are deleted
        //Fetch game info for previously deleted games
        final List<GameInfo> reAddedGameInfosFromNetEnt = new ArrayList<>();
        for (final Game game : deletedGames) {
            if (game.getStatus() == DELETED) {
                game.setStatus(INACTIVE);
                game.setModifiedBy(user);
                game.setModifiedDate(currentDate);
                reAddedGameInfosFromNetEnt.addAll(getGameInfoFromNetEnt(game));
            }
        }

        gameRepository.save(Iterables.concat(newGames, removedGames, gamesInDatabase));
        gameInfoRepository.save(Iterables.concat(newGameInfos, reAddedGameInfosFromNetEnt));

        return new HashSet<>(gameRepository.findAllWithInfos());
    }

    @VisibleForTesting
    Set<Game> getGamesFromNetEnt() {
        //noinspection OverlyBroadCatchBlock
        try {
            final Set<Game> netEntGames = new HashSet<>();

            final GameGroupData[] gameGroups = connectionService.getCasinoMCService().getGameGroups(idOnNetEntCasinoModule, passwordOnNetEntCasinoModule);
            for (final GameGroupData gameGroup : gameGroups) {

                final com.casinomodule.api.Game[] games = gameGroup.getGame();
                for (final com.casinomodule.api.Game game : games) {

                    final Game netEntGame = new Game();
                    netEntGame.setGameId(game.getGameId());
                    netEntGame.setFullName(game.getFullName());
                    netEntGame.setName(game.getName());

                    netEntGames.add(netEntGame);
                }
            }

            return netEntGames;
        } catch (RemoteException | MalformedURLException | ServiceException e) {
            logger.error("Error while getting the games from NetEnt.", e);
            throw new CommunicationException("Error while getting the games from NetEnt.", e);
        }
    }

    private Game findGameByGameId(final String gameId) {
        final Game gameInDatabase = gameRepository.findOne(gameId);
        if (gameInDatabase == null) {
            logger.error("Game with game id {} not found", gameId);
            throw new NotFoundException("Game with game id: " + gameId + " not found");
        }
        return gameInDatabase;
    }

    @Override
    public void refreshGamesInfos(final User user) {
        gameInfoRepository.deleteAll();

        final Set<Game> gamesInDatabase = new HashSet<>(gameRepository.findAll());
        final List<GameInfo> newGameInfos = new ArrayList<>();
        for (final Game game : gamesInDatabase) {
            final List<GameInfo> gameInfoFromNetEnt = getGameInfoFromNetEnt(game);
            newGameInfos.addAll(gameInfoFromNetEnt);
            logger.info("{}", gameInfoFromNetEnt);
        }

        gameInfoRepository.save(newGameInfos);
    }

    @Override
    public GameInfo getGameInfo(final Game game, final Language language) {
        return getGameInfoInternal(game, language, GameClient.FLASH);
    }

    private GameInfo getGameInfoInternal(final Game game, final Language language, final GameClient gameClient) {
        GameInfo gameInfo = gameInfoRepository.findByGameAndLanguageAndClient(game, language, gameClient.getClient());
        if (gameInfo == null) {
            gameInfo = gameInfoRepository.findByGameAndLanguageAndClient(game, Language.ENGLISH, gameClient.getClient());
            if (gameInfo == null) {
                throw new NotFoundException("No game found with game id: " + game.getGameId() + " and language " + Language.ENGLISH + " and client " + gameClient);
            }
        }
        return gameInfo;
    }

    @Override
    public GameInfo getTouchGameInfo(final Game game, final Language language) {
        return getGameInfoInternal(game, language, GameClient.TOUCH);
    }

    @Override
    public Page<Game> getGames(@Nonnull final User user, @Nullable final String gameId, @Nullable final String name, @Nullable final GameCategory category,
                               @Nullable final Boolean featured, @Nonnull final Integer page, @Nonnull final Integer size) {
        final QGame Q = QGame.game;
        final BooleanBuilder query = new BooleanBuilder();
        if (gameId != null) {
            query.and(Q.gameId.like(gameId));
        }
        if (name != null) {
            query.and(Q.name.like(name));
        }
        if (category != null) {
            query.and(Q.category.eq(category));
        }
        if (featured != null) {
            query.and(Q.featured.eq(featured));
        }

        final Predicate predicate = new JoinExpression(JoinType.LEFTJOIN, query).getCondition();
        final QSort sort = new QSort(new OrderSpecifier<>(Order.ASC, Q.gameId));
        return gameRepository.findAll(predicate, new PageRequest(page, size, sort));
    }

    @Override
    public Game updateGame(final Game game, final User user) {
        final Game gameInDatabase = findGameByGameId(game.getGameId());
        gameInDatabase.updateFromGame(game);
        gameInDatabase.setModifiedBy(user);
        gameInDatabase.setModifiedDate(new Date());
        gameRepository.save(gameInDatabase);

        return gameInDatabase;
    }

    @Override
    public List<GameInfo> getActiveGames(final Language language) {
        return gameInfoRepository.findGamesForLobby(ACTIVE, GameClient.FLASH.getClient(), language, GameCategory.OTHER);
    }

    @Override
    public List<GameInfo> getActiveTouchGames(final Language language) {
        return gameInfoRepository.findGamesForLobby(ACTIVE, GameClient.TOUCH.getClient(), language, GameCategory.OTHER);
    }

    @VisibleForTesting
    List<GameInfo> getGameInfoFromNetEnt(final Game game) {
        final List<GameInfo> gameInfos = new ArrayList<>();
        for (final Language language : Language.values()) {
            //noinspection OverlyBroadCatchBlock
            try {
                final String[] gameInfosFromNetEnt = connectionService.getCasinoMCService().getGameInfo(game.getGameId(), language.toIso(),
                                                                                                        idOnNetEntCasinoModule, passwordOnNetEntCasinoModule);
                GameInfo gameInfo = gameInfoRepository.findByGameAndLanguage(game, language);

                if (gameInfo == null) {
                    gameInfo = new GameInfo();
                    gameInfo.setGame(new Game(game.getGameId()));
                    gameInfo.setLanguage(language);
                }

                for (int i = 0; i < gameInfosFromNetEnt.length; i += 2) {
                    setGameInfoParameter(gameInfosFromNetEnt, i, gameInfo);
                }

                gameInfos.add(gameInfo);
            } catch (RemoteException | MalformedURLException | ServiceException e) {
                logger.error("Error while getting game info from NetEnt.", e);
                game.setStatus(INACTIVE);
                gameRepository.save(game);
            }
        }
        return gameInfos;
    }

    private GameInfo setGameInfoParameter(final String[] gameInfo, final int i, final GameInfo netEntGameInfo) {
        switch (gameInfo[i]) {
            case CasinoModuleConstants.WIDTH:
                netEntGameInfo.setWidth(gameInfo[i + 1]);
                break;
            case CasinoModuleConstants.HEIGHT:
                netEntGameInfo.setHeight(gameInfo[i + 1]);
                break;
            case CasinoModuleConstants.HELP_FILE:
                netEntGameInfo.setHelpFile(gameInfo[i + 1]);
                break;
            case CasinoModuleConstants.CLIENT:
                netEntGameInfo.setClient(gameInfo[i + 1]);
                break;
            case CasinoModuleConstants.STATIC_URL:
                netEntGameInfo.setStaticUrl(gameInfo[i + 1]);
                break;
            case CasinoModuleConstants.GAME_SERVER_URL:
                netEntGameInfo.setGameServerUrl(gameInfo[i + 1]);
                break;
            case CasinoModuleConstants.MOBILE_GAME_URL:
                netEntGameInfo.setMobileGameUrl(gameInfo[i + 1]);
                break;
            case CasinoModuleConstants.BASE:
                netEntGameInfo.setBase(gameInfo[i + 1]);
                break;
            case CasinoModuleConstants.VARS:
                netEntGameInfo.setVars(gameInfo[i + 1]);
                break;
            case CasinoModuleConstants.ALLOW_SCRIPT_ACCESS:
                netEntGameInfo.setAllowScriptAccess(gameInfo[i + 1]);
                break;
            case CasinoModuleConstants.FLASH_VERSION:
                netEntGameInfo.setFlashVersion(gameInfo[i + 1]);
                break;
            case CasinoModuleConstants.WINDOW_MODE:
                netEntGameInfo.setWindowMode(gameInfo[i + 1]);
                break;
            default:
                logger.warn("GameInfo variable not found for {}", gameInfo[i]);
        }
        return netEntGameInfo;
    }

    @Override
    public void resetSessionTime(final Player player) {
        final GameSession gameSession = gameSessionRepository.findByPlayer(player);
        gameSession.setSessionLength(0);
        gameSessionRepository.save(gameSession);
    }

    @Override
    public boolean isSessionTimeOver(final Player player) {
        final PlayerLimitation playerLimitation = playerLimitationRepository.findByPlayer(player);
        final GameSession gameSession = gameSessionRepository.findByPlayer(player);
        gameSession.setSessionLength(gameSession.getSessionLength() + 1);
        gameSessionRepository.save(gameSession);
        // TODO check and push if is close to limit?
        return playerLimitation.isSessionTimeOver(gameSession.getSessionLength());
    }

    @Override
    public void logoutOverTimedPlayer(final Player player) {
        try {
            logoutPlayer(player);
        } catch (final CommunicationException ignore) {
            // Doesn't really matter if the logout succeeds or not
        } finally {
            resetSessionTime(player);
            auditService.trackPlayerActivity(player, LOGOUT);
        }
    }

    @Transactional(propagation = SUPPORTS, readOnly = true)
    @Nullable
    @Override
    public Game getGame(@Nullable final String gameId) {
        if (gameId == null) {
            return null;
        }
        return gameRepository.findOne(gameId);
    }
}
