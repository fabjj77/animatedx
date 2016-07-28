package com.cs.player;

import com.cs.affiliate.AffiliateService;
import com.cs.audit.AuditService;
import com.cs.audit.PlayerActivityRepository;
import com.cs.audit.PlayerActivityType;
import com.cs.audit.UserActivityType;
import com.cs.avatar.Avatar;
import com.cs.avatar.AvatarChangeException;
import com.cs.avatar.AvatarService;
import com.cs.avatar.Level;
import com.cs.bonus.Bonus;
import com.cs.bonus.BonusService;
import com.cs.bonus.TriggerEvent;
import com.cs.comment.PlayerCommentRepository;
import com.cs.game.CasinoModuleConstants;
import com.cs.game.GameService;
import com.cs.game.GameSession;
import com.cs.game.GameSessionRepository;
import com.cs.game.PlayerLimitationLockoutRepository;
import com.cs.game.PlayerLimitationRepository;
import com.cs.item.ItemService;
import com.cs.job.ScheduledJob;
import com.cs.level.LevelService;
import com.cs.messaging.bronto.BrontoGateway;
import com.cs.messaging.bronto.InternalBrontoContact;
import com.cs.messaging.bronto.UpdateContactFieldMessage;
import com.cs.messaging.email.BrontoFieldName;
import com.cs.messaging.email.BrontoWorkflowName;
import com.cs.messaging.email.EmailService;
import com.cs.messaging.websocket.LevelUpMessage;
import com.cs.messaging.websocket.WebSocketGateway;
import com.cs.payment.Money;
import com.cs.persistence.CommunicationException;
import com.cs.persistence.Country;
import com.cs.persistence.InvalidPasswordException;
import com.cs.persistence.NotFoundException;
import com.cs.persistence.ValidationException;
import com.cs.promotion.PlayerCriteria;
import com.cs.promotion.PromotionService;
import com.cs.promotion.PromotionTrigger;
import com.cs.session.PlayerSessionRegistryRepository;
import com.cs.system.PlayerSessionRegistry;
import com.cs.user.User;
import com.cs.util.CalendarUtils;
import com.cs.whitelist.WhiteListService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.querydsl.QSort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Strings;
import com.maxmind.geoip2.WebServiceClient;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CountryResponse;
import com.mysema.query.BooleanBuilder;
import com.mysema.query.types.Order;
import com.mysema.query.types.OrderSpecifier;
import com.mysema.query.types.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.cs.audit.PlayerActivityType.FAILED_LOGIN;
import static com.cs.audit.PlayerActivityType.LOGIN;
import static com.cs.audit.PlayerActivityType.LOGOUT;
import static com.cs.payment.Currency.EUR;
import static com.cs.persistence.Status.ACTIVE;
import static com.cs.persistence.Status.BAD_CREDENTIALS_LOCKED;
import static com.cs.persistence.Status.INACTIVE;
import static com.cs.player.BlockType.BET_LIMIT;
import static com.cs.player.BlockType.DEFINITE_SELF_EXCLUSION;
import static com.cs.player.BlockType.INDEFINITE_SELF_EXCLUSION;
import static com.cs.player.BlockType.LOSS_LIMIT;
import static com.cs.player.BlockType.UNBLOCKED;
import static com.cs.player.LimitationStatus.APPLIED;
import static com.cs.player.LimitationType.BET_AMOUNT;
import static com.cs.player.LimitationType.LOSS_AMOUNT;
import static com.cs.player.TimeUnit.DAY;
import static com.cs.player.TrustLevel.GREEN;
import static com.cs.player.UuidType.PAYMENT;
import static com.cs.player.Verification.UNVERIFIED;
import static org.springframework.transaction.annotation.Isolation.READ_COMMITTED;
import static org.springframework.transaction.annotation.Propagation.SUPPORTS;

/**
 * @author Joakim Gottzén
 */
@Service
@Transactional(isolation = READ_COMMITTED)
public class PlayerServiceImpl implements PlayerService {

    private final Logger logger = LoggerFactory.getLogger(PlayerServiceImpl.class);

    private static final int MAX_CREDITS_PER_DICE = 6;
    @SuppressWarnings("SpellCheckingInspection")
    private static final String NICKNAME_LETTERS = "abcdefghjkmnpqrstuvwxyzABCDEFGHJKMNPQRSTUVWXYZ23456789-_";
    private static final Integer NICKNAME_LENGTH = 10;

    private static final Random random = new Random();

    private final AffiliateService affiliateService;
    private final AuditService auditService;
    private final AvatarService avatarService;
    private final BonusService bonusService;
    private final EmailService emailService;
    private final GameService gameService;
    private final ItemService itemService;
    private final LevelService levelService;
    private final PromotionService promotionService;
    private final WhiteListService whiteListService;

    private final GameSessionRepository gameSessionRepository;
    private final PlayerActivityRepository playerActivityRepository;
    private final PlayerCommentRepository playerCommentRepository;
    private final PlayerLimitationRepository playerLimitationRepository;
    private final PlayerLimitationLockoutRepository playerLimitationLockoutRepository;
    private final PlayerLimitationTraceRepository playerLimitationTraceRepository;
    private final PlayerRegisterTrackRepository playerRegisterTrackRepository;
    private final PlayerRepository playerRepository;
    private final PlayerSessionRegistryRepository playerSessionRegistryRepository;
    private final PlayerUuidRepository playerUuidRepository;
    private final WalletRepository walletRepository;

    private final BrontoGateway brontoGateway;
    private final WebSocketGateway webSocketGateway;

    private final PasswordEncoder passwordEncoder;

    private final Pattern netEntUsernamePattern = Pattern.compile("^" + CasinoModuleConstants.PLAYER_ID_PREFIX + "(\\d+)");

    @Value("${player.minimum-block-days}")
    private Integer minimumBlockDays;
    @Value("${player.time-to-inactive-in-month}")
    private Integer timeToInactivePlayer;
    @Value("${player.time-to-dormant-in-month}")
    private Integer timeToDormantPlayer;
    @Value("${player.lock-time-for-exceeding-session-length-in-minutes}")
    private Integer lockTimeForExceedingSessionLengthInMinutes;
    @Value("${player.maximum-failed-login-attempts}")
    private Integer maximumFailedLoginAttempts;
    @Value("${player.maxmind-user-id}")
    private Integer maxmindUserId;
    @Value("${player.maxmind-license-key}")
    private String maxmindLicenseKey;

    @Autowired
    public PlayerServiceImpl(final AffiliateService affiliateService, final AuditService auditService, final AvatarService avatarService, final BonusService bonusService,
                             final EmailService emailService, final GameService gameService, final ItemService itemService, final LevelService levelService,
                             final PromotionService promotionService, final WhiteListService whiteListService, final GameSessionRepository gameSessionRepository,
                             final PlayerActivityRepository playerActivityRepository, final PlayerCommentRepository playerCommentRepository,
                             final PlayerLimitationRepository playerLimitationRepository, final PlayerLimitationLockoutRepository playerLimitationLockoutRepository,
                             final PlayerLimitationTraceRepository playerLimitationTraceRepository, final PlayerRegisterTrackRepository playerRegisterTrackRepository,
                             final PlayerRepository playerRepository, final PlayerSessionRegistryRepository playerSessionRegistryRepository,
                             final PlayerUuidRepository playerUuidRepository, final WalletRepository walletRepository, final BrontoGateway brontoGateway,
                             final WebSocketGateway webSocketGateway, final PasswordEncoder passwordEncoder) {
        this.affiliateService = affiliateService;
        this.auditService = auditService;
        this.avatarService = avatarService;
        this.bonusService = bonusService;
        this.emailService = emailService;
        this.gameService = gameService;
        this.itemService = itemService;
        this.levelService = levelService;
        this.promotionService = promotionService;
        this.whiteListService = whiteListService;
        this.gameSessionRepository = gameSessionRepository;
        this.playerActivityRepository = playerActivityRepository;
        this.playerCommentRepository = playerCommentRepository;
        this.playerLimitationRepository = playerLimitationRepository;
        this.playerLimitationLockoutRepository = playerLimitationLockoutRepository;
        this.playerLimitationTraceRepository = playerLimitationTraceRepository;
        this.playerRegisterTrackRepository = playerRegisterTrackRepository;
        this.playerRepository = playerRepository;
        this.playerSessionRegistryRepository = playerSessionRegistryRepository;
        this.playerUuidRepository = playerUuidRepository;
        this.walletRepository = walletRepository;
        this.brontoGateway = brontoGateway;
        this.webSocketGateway = webSocketGateway;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(propagation = SUPPORTS, readOnly = true)
    @Override
    public Player getPlayer(final Long id) {
        final Player player = findPlayerById(id);
        updateWalletWithNextLevelTurnoverTarget(player);
        return player;
    }

    private Player findPlayerById(final Long id) {
        final Player player = playerRepository.findOne(id);
        if (player == null) {
            throw new NotFoundException(id);
        }
        return player;
    }

    @Override
    public Player getPlayer(@Nonnull final String emailAddress) {
        final Player player = playerRepository.findByEmailAddress(emailAddress);
        if (player == null || player.getStatus() != ACTIVE) {
            throw new NotFoundException("Player not found");
        }
        // TODO: Joakim, this is not where we login
        gameService.resetSessionTime(player);
        auditService.trackPlayerActivity(player, LOGIN);
        updateWalletWithNextLevelTurnoverTarget(player);
        return player;
    }

    private void updateWalletWithNextLevelTurnoverTarget(final Player player) {
        final Level nextLevel = levelService.getLevel(player.getLevel().getLevel() + 1);
        if (nextLevel != null) {
            final Long currentLevelTurnoverTarget = player.getLevel().getTurnover().getCents();
            final Long target = nextLevel.getTurnover().getCents() - currentLevelTurnoverTarget;
            final Long progress = player.getWallet().getLevelProgress().getCents() - currentLevelTurnoverTarget;
            player.getWallet().setNextLevelPercentage(new BigDecimal(progress).multiply(new BigDecimal(100)).divide(new BigDecimal(target), 2, BigDecimal.ROUND_DOWN));
        }
    }

    /**
     * This method should only be used when fetching players for authentication, by the Spring Security framework, since it doesn't check if the user is active or not!
     */
    @Override
    public Player getPlayerForAuthentication(@Nonnull final String emailAddress) {
        final Player player = playerRepository.findByEmailAddress(emailAddress);
        if (player == null) {
            throw new NotFoundException("Player not found");
        }
        return player;
    }

    @Transactional(propagation = SUPPORTS, readOnly = true)
    @Override
    public Player getPlayerByCasinoUsername(final String netEntUsername) {
        final Matcher matcher = netEntUsernamePattern.matcher(netEntUsername);
        if (!matcher.matches()) {
            throw new InvalidNetEntUsernameException("Invalid NetEnt username pattern " + netEntUsername);
        }
        final String id = matcher.group(1);

        try {
            return findPlayerById(Long.valueOf(id));
        } catch (final NumberFormatException e) {
            throw new InvalidNetEntUsernameException("No player with NetEnt username " + netEntUsername);
        }
    }

    @Transactional(propagation = SUPPORTS, readOnly = true)
    @Override
    public Page<Player> searchPlayers(@Nullable final String emailAddress, @Nullable final String nickname, @Nullable final String firstName,
                                      @Nullable final String lastName, @Nullable final BlockType blockType, @Nullable final LimitationStatus limitationStatus,
                                      @Nonnull final Integer page, final Integer size) {
        final Predicate expression = searchPlayersBuilder(emailAddress, nickname, firstName, lastName, blockType, limitationStatus);
        final PageRequest pageRequest = new PageRequest(page, size, new QSort(new OrderSpecifier<>(Order.DESC, QPlayer.player.id)));
        return playerRepository.findAll(expression, pageRequest);
    }

    @Transactional(propagation = SUPPORTS, readOnly = true)
    @Override
    public Iterable<Player> searchPlayers(@Nullable final String emailAddress, @Nullable final String nickname, @Nullable final String firstName,
                                          @Nullable final String lastName, @Nullable final BlockType blockType, @Nullable final LimitationStatus limitationStatus) {
        final Predicate expression = searchPlayersBuilder(emailAddress, nickname, firstName, lastName, blockType, limitationStatus);
        return playerRepository.findAll(expression);
    }

    private Predicate searchPlayersBuilder(@Nullable final String emailAddress, @Nullable final String nickname, @Nullable final String firstName,
                                           @Nullable final String lastName, @Nullable final BlockType blockType, @Nullable final LimitationStatus limitationStatus) {
        final QPlayer Q = QPlayer.player;
        final BooleanBuilder builder = new BooleanBuilder();
        if (emailAddress != null) {
            builder.and(Q.emailAddress.contains(emailAddress));
        }
        if (nickname != null) {
            builder.and(Q.nickname.contains(nickname));
        }
        if (firstName != null) {
            builder.and(Q.firstName.contains(firstName));
        }
        if (lastName != null) {
            builder.and(Q.lastName.contains(lastName));
        }
        if (blockType != null) {
            builder.and(Q.blockType.eq(blockType));
        }
        if (limitationStatus != null && limitationStatus == APPLIED) {
            builder.and(Q.playerLimitation.lossLimit.ne(Money.ZERO)).or(Q.playerLimitation.betLimit.ne(Money.ZERO).or(Q.playerLimitation.sessionLength.ne(0)));
        }
        return builder;
    }

    @Override
    public Player createPlayer(final Player player, @Nullable final PlayerRegisterTrack track, final String bTag, final String ipAddress) {
        if (emailExists(player.getEmailAddress())) {
            throw new PlayerCreationException("Duplicate e-mail for player with e-mail " + player.getEmailAddress());
        }
        if (playerExists(player)) {
            throw new PlayerCreationException("Player already exists.");
        }
        if (!isOldEnough(player)) {
            throw new PlayerCreationException("Player not old enough: " + player.getBirthday() + " " + player.getAddress().getCountry());
        }

        // Temporary fix for CAS-515
        //noinspection ConstantConditions
        if (player.getReceivePromotion() == null) {
            player.setReceivePromotion(ReceivePromotion.SUBSCRIBED);
        }

        final Level level = new Level(1L);
        setAvatar(player, level);
        player.setLevel(level);
        player.setStatus(ACTIVE);
        player.setPassword(passwordEncoder.encode(player.getPassword()));
        player.setCreatedDate(new Date());
        player.setTrustLevel(GREEN);
        player.setEmailVerification(UNVERIFIED);
        player.setPlayerVerification(UNVERIFIED);
        player.setBlockType(UNBLOCKED);
        player.setLanguage(player.getLanguage());
        player.setCurrency(EUR);
        player.setBlockEndDate(new Date());
        player.setFailedLoginAttempts(0);
        setRandomNickname(player);
        player.setTestAccount(false);
        final Player savedPlayer = playerRepository.save(player);
        createPlayerLimitations(savedPlayer);
        createPlayerGameSession(savedPlayer);
        createPlayerSessionRegistry(savedPlayer);
        savedPlayer.setWallet(walletRepository.save(new Wallet(savedPlayer)));

        checkIpAddressOnRegistration(savedPlayer, ipAddress);

        createPlayerTrack(savedPlayer, track);
        registerAffiliate(savedPlayer, bTag, ipAddress);

        promotionService.assignPromotions(savedPlayer, PromotionTrigger.SIGN_UP, new PlayerCriteria());
        final List<Bonus> availableBonuses = bonusService.getAvailableBonuses(savedPlayer, TriggerEvent.SIGN_UP);
        bonusService.useBonuses(savedPlayer, availableBonuses, new PlayerCriteria());

        sendPlayerRegistrationEmail(savedPlayer);

        logger.info("Created player: {}", savedPlayer);
        return savedPlayer;
    }

    private boolean emailExists(final String emailAddress) {
        final Player playerByEmailInDatabase = playerRepository.findByEmailAddress(emailAddress);
        return playerByEmailInDatabase != null;
    }

    private boolean playerExists(final Player player) {
        final List<Player> playersInDatabase = playerRepository.findByFirstNameAndLastName(player.getFirstName(), player.getLastName());

        for (final Player playerInDatabase : playersInDatabase) {
            if (player.getAddress().getStreet().equals(playerInDatabase.getAddress().getStreet())) {
                // Clear out all time fields before comparing
                final Calendar calendar = Calendar.getInstance();
                final Date playerBirthday = CalendarUtils.startOfDay(calendar, player.getBirthday());
                final Date playerInDatabaseBirthday = CalendarUtils.startOfDay(calendar, playerInDatabase.getBirthday());
                if (playerBirthday.equals(playerInDatabaseBirthday)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isOldEnough(final Player player) {
        return player.getAddress().getCountry().isAgeValid(player.getBirthday());
    }

    private void setAvatar(final Player player, final Level level) {
        //noinspection ConstantConditions
        if (player.getAvatar() != null) {
            player.setAvatar(avatarService.getAvatarForLevel(player.getAvatar(), level));
            return;
        }

        final List<Avatar> avatars = avatarService.getActiveAvatars(level.getLevel());
        player.setAvatar(avatars.get(random.nextInt(avatars.size())));
    }

    private void setRandomNickname(final Player player) {
        String nickname;
        Player existingPlayer;

        do {
            nickname = generateRandomNickname();
            existingPlayer = playerRepository.findByNickname(nickname);
        } while (existingPlayer != null);

        player.setNickname(nickname);
    }

    private String generateRandomNickname() {
        String nickname = "";
        for (int i = 0; i < NICKNAME_LENGTH; i++) {
            final int index = (int) (random.nextDouble() * NICKNAME_LETTERS.length());
            nickname = nickname.concat(NICKNAME_LETTERS.substring(index, index + 1));
        }
        return nickname;
    }

    private void createPlayerSessionRegistry(final Player savedPlayer) {
        final PlayerSessionRegistry playerSessionRegistry = new PlayerSessionRegistry();
        playerSessionRegistry.setActive(true);
        playerSessionRegistry.setExpired(true);
        playerSessionRegistry.setLastRequest(new Date());
        playerSessionRegistry.setUuid(UUID.randomUUID().toString());
        playerSessionRegistry.setSessionId(PlayerSessionRegistry.REGISTRATION_SESSION_ID);
        playerSessionRegistry.setPlayer(savedPlayer);
        playerSessionRegistryRepository.save(playerSessionRegistry);
    }

    private void checkIpAddressOnRegistration(final Player player, final String ipAddress) {
        final Country country = getCountryByIpAddress(ipAddress);
        if (country == null || Country.getBlockedCountries().contains(country)) {
            final PlayerComment playerComment = new PlayerComment();
            playerComment.setPlayer(player);
            playerComment.setCreatedDate(new Date());
            playerComment.setUser(new User(1L));
            final String comment = String.format("Player needs KYC review for IP address %s and corresponding country %s", ipAddress, country);
            playerComment.setComment(comment);
            playerCommentRepository.save(playerComment);
            if (country != null) {
                emailService.sendIpCountryMismatchEmail(player, ipAddress, country);
            }
        }
    }

    private void createPlayerTrack(final Player player, @Nullable final PlayerRegisterTrack track) {
        if (track != null) {
            track.setPlayer(player);
            playerRegisterTrackRepository.save(track);
            final String message = String.format("Player %d was created during %s Campaign (source: %s, content: %s, medium: %s, version: %s)",
                                                 player.getId(), track.getCampaign(), track.getSource(), track.getContent(), track.getMedium(), track.getVersion());
            auditService.trackPlayerActivityWithDescription(player, PlayerActivityType.CREATE_PLAYER_DURING_CAMPAIGN, message);
        }
    }

    private void registerAffiliate(final Player player, final String bTag, final String ipAddress) {
        if (!Strings.isNullOrEmpty(bTag)) {
            affiliateService.createPlayerAffiliate(player, bTag, ipAddress);
        }
    }

    private void createPlayerGameSession(final Player player) {
        final Date now = new Date();
        final GameSession gameSession = new GameSession(player, now);
        gameSession.setSessionLength(0);
        gameSession.setStartDate(now);
        gameSessionRepository.save(gameSession);
    }

    private void createPlayerLimitations(final Player player) {
        final PlayerLimitation playerLimit = new PlayerLimitation();
        playerLimit.setPlayer(player);
        playerLimit.setBetLimit(Money.ZERO);
        playerLimit.setBetTimeUnit(DAY);
        playerLimit.setLossLimit(Money.ZERO);
        playerLimit.setLossTimeUnit(DAY);
        playerLimit.setSessionLength(0);
        playerLimitationRepository.save(playerLimit);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public Player updatePlayer(final Long playerId, final Player player, @Nullable final String oldPassword, @Nullable final String newPassword) {
        final Player playerById = findPlayerById(playerId);

        if (oldPassword != null && newPassword != null) {
            if (!passwordEncoder.matches(oldPassword, playerById.getPassword())) {
                throw new InvalidPasswordException("Wrong password");
            }
            player.setPassword(passwordEncoder.encode(newPassword));
        }

        final String emailAddress = player.getEmailAddress();
        if (!isEmailAddressUnregistered(emailAddress)) {
            throw new ValidationException("emailAddress");
        }
        final String nickname = player.getNickname();
        if (!isNicknameUnregistered(nickname)) {
            throw new ValidationException("nickname");
        }

        //noinspection ConstantConditions
        if (emailAddress != null && !playerById.getEmailAddress().equals(emailAddress)) {
            updateEmailVerification(playerById);
        }

        if (shouldPlayerBeVerified(player, playerById)) {
            updatePlayerVerification(playerById);
        }

        // sanitize the address object, since the player isn't allowed to change all fields
        if (player.getAddress() != null) {
            player.getAddress().setCountry(null);
        }

        if (player.getAvatar() != null && player.getAvatar().getId() != null) {
            if (!avatarService.validateChangeableAvatar(playerById)) {
                throw new AvatarChangeException("Player " + playerById.getId() + " is not allowed to change avatar.");
            }
            avatarService.insertChangedAvatarHistory(playerById);
            playerById.setAvatar(avatarService.getAvatar(player.getAvatar().getId()));
        }

        playerById.updateFromPlayer(player);
        playerById.setModifiedDate(new Date());
        updateWalletWithNextLevelTurnoverTarget(playerById);
        updatePlayerBrontoFields(playerById);
        return playerRepository.save(playerById);
    }

    @SuppressWarnings("ConstantConditions")
    private void updatePlayerBrontoFields(final Player player) {
        final InternalBrontoContact internalBrontoContact = new InternalBrontoContact(player);
        Map<BrontoFieldName, String> brontoFieldNameMap = new EnumMap<>(BrontoFieldName.class);

        if (player.getFirstName() != null) {
            brontoFieldNameMap.put(BrontoFieldName.first_name, player.getFirstName());
        }
        if (player.getLastName() != null) {
            brontoFieldNameMap.put(BrontoFieldName.last_name, player.getLastName());
        }
        if (player.getEmailAddress() != null) {
            //Todo fix e-mail, not regular field
        }
        if (player.getNickname() != null) {
            brontoFieldNameMap.put(BrontoFieldName.nickname, player.getNickname());
        }
        if (player.getPhoneNumber() != null) {
            //Todo fix phone, not regular field
        }
        if (player.getStatus() != null) {
            brontoFieldNameMap.put(BrontoFieldName.status, player.getStatus().toString());
        }
        if (player.getPlayerVerification() != null) {
            brontoFieldNameMap.put(BrontoFieldName.email_verification, player.getEmailVerification().toString());
        }
        if (player.getAvatar() != null) {
            brontoFieldNameMap.put(BrontoFieldName.avatar_id, player.getAvatar().getId().toString());
        }

        if (player.getBirthday() != null) {
            brontoFieldNameMap.put(BrontoFieldName.birthday, player.getBirthday().toString());
        }
        if (player.getLanguage() != null) {
            brontoFieldNameMap.put(BrontoFieldName.language, player.getLanguage().toString());
        }
        if (player.getCurrency() != null) {
            brontoFieldNameMap.put(BrontoFieldName.currency, player.getCurrency().toString());
        }

        brontoFieldNameMap = populateBrontoAddress(brontoFieldNameMap, player.getAddress());

        if (!brontoFieldNameMap.isEmpty()) {
            final UpdateContactFieldMessage updateContactFieldMessage = new UpdateContactFieldMessage(internalBrontoContact, brontoFieldNameMap);
            brontoGateway.updateContactFields(updateContactFieldMessage);
        }
    }

    @Override
    public Player updatePlayerFromBackOffice(final Long id, final Player player) {
        final Player playerById = findPlayerById(id);

        logger.info("Updating player {}, with new values: {}", playerById.getEmailAddress(), player);

        playerById.updateFromPlayerBackOffice(player);
        playerById.setModifiedDate(new Date());
        updatePlayerBrontoFields(playerById);
        return playerRepository.save(playerById);
    }

    private void updateEmailVerification(final Player player) {
        //Set player column email_verification here to "Re-verify" if value is "Verified"
        if (player.getEmailVerification() == Verification.VERIFIED) {
            invalidatePlayerUuids(player, UuidType.VERIFY_EMAIL);

            final String uuid = createPlayerUuid(player, UuidType.VERIFY_EMAIL, player.getEmailAddress()).getUuid();

            final Map<BrontoFieldName, String> brontoFieldNameMap = new EnumMap<>(BrontoFieldName.class);
            brontoFieldNameMap.put(BrontoFieldName.uuid, uuid);
            brontoGateway.updateContactFields(new UpdateContactFieldMessage(new InternalBrontoContact(player), brontoFieldNameMap, BrontoWorkflowName.EMAILVERIFICATION));

            player.setEmailVerification(Verification.RE_VERIFY);
        }
    }

    private boolean shouldPlayerBeVerified(final Player player, final Player playerById) {
        return !playerById.getFirstName().equals(player.getFirstName()) ||
               !playerById.getLastName().equals(player.getLastName());
    }

    private void updatePlayerVerification(final Player player) {
        //Set player column email_verification here to "Re-verify" if value is "Verified"
        if (player.getPlayerVerification() == Verification.VERIFIED) {
            player.setPlayerVerification(Verification.RE_VERIFY);
        }
    }

    @Override
    public Player inactivatePlayer(final Long id) {
        final Player player = findPlayerById(id);
        player.setStatus(INACTIVE);
        player.setModifiedDate(new Date());

        //Update the status field in bronto
        final Map<BrontoFieldName, String> brontoFieldNameMap = new EnumMap<>(BrontoFieldName.class);
        brontoFieldNameMap.put(BrontoFieldName.status, INACTIVE.toString());
        brontoGateway.updateContactFields(new UpdateContactFieldMessage(new InternalBrontoContact(player), brontoFieldNameMap));

        return playerRepository.save(player);
    }

    @Transactional(propagation = SUPPORTS, readOnly = true)
    @Override
    public Address getPlayerAddress(final Long id) {
        return getPlayer(id).getAddress();
    }

    @Override
    public Address updatePlayerAddress(final Long id, final Address address) {
        final Player player = findPlayerById(id);
        player.setAddress(address);
        player.setModifiedDate(new Date());
        updatePlayerVerification(player);
        updateBrontoAddress(player, address);
        return playerRepository.save(player).getAddress();
    }

    private void updateBrontoAddress(final Player player, final Address address) {
        Map<BrontoFieldName, String> brontoFieldNameMap = new EnumMap<>(BrontoFieldName.class);
        brontoFieldNameMap = populateBrontoAddress(brontoFieldNameMap, address);
        brontoGateway.updateContactFields(new UpdateContactFieldMessage(new InternalBrontoContact(player), brontoFieldNameMap));
    }

    @SuppressWarnings("ConstantConditions")
    private Map<BrontoFieldName, String> populateBrontoAddress(final Map<BrontoFieldName, String> brontoFieldNameMap, final Address address) {
        if (address != null) {
            if (address.getStreet() != null) {
                brontoFieldNameMap.put(BrontoFieldName.street, address.getStreet());
            }
            if (address.getStreet2() != null) {
                brontoFieldNameMap.put(BrontoFieldName.street2, address.getStreet2());
            }
            if (address.getZipCode() != null) {
                brontoFieldNameMap.put(BrontoFieldName.zipcode, address.getZipCode());
            }
            if (address.getState() != null) {
                brontoFieldNameMap.put(BrontoFieldName.state, address.getState());
            }
            if (address.getCity() != null) {
                brontoFieldNameMap.put(BrontoFieldName.city, address.getCity());
            }
            if (address.getCountry() != null) {
                brontoFieldNameMap.put(BrontoFieldName.country, address.getCountry().toString());
            }
        }

        return brontoFieldNameMap;
    }

    @Transactional(propagation = SUPPORTS, readOnly = true)
    @Override
    public Wallet getPlayerWallet(final Long id) {
        final Player player = getPlayer(id);
        updateWalletWithNextLevelTurnoverTarget(player);
        return player.getWallet();
    }

    @Override
    public boolean checkLevelByProgressTurnover(final Player player) {
        final Money turnoverProgress = player.getWallet().getLevelProgress();
        final Level levelForTurnover = levelService.getLevelByTurnover(turnoverProgress);

        // If the player has leveled up we need to set the avatar to the one for the next level
        if (levelForTurnover != null && levelForTurnover.getLevel() > player.getLevel().getLevel()) {
            final Level oldLevel = player.getLevel();
            final Avatar nextLevelAvatar = avatarService.getAvatarForLevel(player.getAvatar(), levelForTurnover);
            player.setAvatar(nextLevelAvatar);
            player.setLevel(levelForTurnover);

            giveCreditsForLevelUp(player, oldLevel);
            assignItemsForLevelUp(player, oldLevel);

            final Player updatedPlayer = playerRepository.save(player);
            logger.info("Player {}({}) levelled up from level {} to level {}", player.getId(), player.getEmailAddress(), oldLevel.getLevel(),
                        levelForTurnover.getLevel());

            webSocketGateway.levelUp(new LevelUpMessage(updatedPlayer.getLevel().getLevel(), oldLevel.getLevel()));

            //Update bronto level field
            final Map<BrontoFieldName, String> brontoFieldNameMap = new EnumMap<>(BrontoFieldName.class);
            brontoFieldNameMap.put(BrontoFieldName.level, updatedPlayer.getLevel().getLevel().toString());
            brontoGateway.updateContactFields(new UpdateContactFieldMessage(new InternalBrontoContact(player), brontoFieldNameMap));

            promotionService.assignPromotions(player, PromotionTrigger.LEVEL_UP, new PlayerCriteria());

            return true;
        }
        return false;
    }

    private void giveCreditsForLevelUp(final Player player, final Level oldLevel) {
        final Level reachedLevel = player.getLevel();
        final Wallet wallet = player.getWallet();
        for (long currentLevel = oldLevel.getLevel() + 1; currentLevel <= reachedLevel.getLevel(); currentLevel++) {
            final Level nextLevel = levelService.getLevel(currentLevel);
            if (nextLevel != null) {
                final Integer credits = calculateCreditsToGive(nextLevel.getCreditDices());
                wallet.setCreditsBalance(wallet.getCreditsBalance() + credits);
                logger.info("Player {}({}) was awarded {} credits", player.getId(), player.getEmailAddress(), credits);
            }
        }
        walletRepository.save(wallet);
    }

    private void assignItemsForLevelUp(final Player player, final Level oldLevel) {
        final Level reachedLevel = player.getLevel();
        final Iterable<Level> levelsWithItems = levelService.getLevelsWithItems(oldLevel.getLevel() + 1, reachedLevel.getLevel());
        itemService.assignItems(player, levelsWithItems);
    }

    private Integer calculateCreditsToGive(final Short creditDices) {
        Integer creditsToGive = 0;
        for (int i = 0; i < creditDices; i++) {
            creditsToGive += random.nextInt(MAX_CREDITS_PER_DICE) + 1;
        }
        return creditsToGive;
    }

    @VisibleForTesting
    PlayerUuid createPlayerUuid(final Player player, final UuidType uuidType, final String data) {
        final PlayerUuid playerUuid = createPlayerUuid(player, uuidType);
        playerUuid.setData(data);
        playerUuidRepository.save(playerUuid);
        return playerUuid;
    }

    private PlayerUuid createPlayerUuid(final Player player, final UuidType uuidType) {
        if (uuidType == UuidType.RESET_PASSWORD || uuidType == UuidType.VERIFY_EMAIL) {
            invalidatePlayerUuids(player, uuidType);
        }

        final PlayerUuid playerUuid = new PlayerUuid();
        playerUuid.setPlayer(player);
        playerUuid.setUuidType(uuidType);
        playerUuid.setCreatedDate(new Date());
        playerUuidRepository.save(playerUuid);
        return playerUuid;
    }

    @Override
    public void sendPlayerRegistrationEmail(final Long playerId) {
        final Player player = findPlayerById(playerId);
        sendPlayerRegistrationEmail(player);
    }

    private void sendPlayerRegistrationEmail(final Player player) {
        final String uuid = createPlayerUuid(player, UuidType.VERIFY_EMAIL, player.getEmailAddress()).getUuid();
        brontoGateway.addContact(new InternalBrontoContact(player, BrontoWorkflowName.SIGNUP, uuid));
    }

    @Override
    public void createResetPassword(final String emailAddress) {
        final Player player = getPlayer(emailAddress);

        invalidatePlayerUuids(player, UuidType.RESET_PASSWORD);

        final String uuid = createPlayerUuid(player, UuidType.RESET_PASSWORD, player.getEmailAddress()).getUuid();

        final Map<BrontoFieldName, String> brontoFieldNameMap = new EnumMap<>(BrontoFieldName.class);
        brontoFieldNameMap.put(BrontoFieldName.uuid, uuid);
        brontoGateway.updateContactFields(new UpdateContactFieldMessage(new InternalBrontoContact(player), brontoFieldNameMap, BrontoWorkflowName.RESETPASSWORD));
    }

    @Override
    public Player resetPassword(@Nonnull final String uuidString, @Nonnull final String newPassword) {
        final PlayerUuid playerUuid = playerUuidRepository.findByUuidAndUuidTypeAndUsedDateIsNull(uuidString, UuidType.RESET_PASSWORD);

        if (playerUuid == null) {
            throw new NotFoundException("Uuid " + uuidString + " not found");
        }

        final Player player = findPlayerById(playerUuid.getPlayer().getId());

        final String uuidEmail = playerUuid.getData();

        if (!player.getEmailAddress().equals(uuidEmail)) {
            throw new NotFoundException("Valid e-mail not found");
        }

        playerUuid.setUsedDate(new Date());
        playerUuidRepository.save(playerUuid);

        player.setPassword(passwordEncoder.encode(newPassword));

        playerRepository.save(player);
        return player;
    }

    @Override
    public Player verifyEmail(@Nonnull final String uuidString) {
        final PlayerUuid playerUuid = playerUuidRepository.findByUuidAndUuidTypeAndUsedDateIsNull(uuidString, UuidType.VERIFY_EMAIL);

        if (playerUuid == null) {
            throw new NotFoundException("Uuid " + uuidString + " not found");
        }

        final Player player = findPlayerById(playerUuid.getPlayer().getId());

        final String uuidEmail = playerUuid.getData();

        if (!player.getEmailAddress().equals(uuidEmail)) {
            throw new NotFoundException("Valid e-mail not found");
        }

        playerUuid.setUsedDate(new Date());

        player.setEmailVerification(Verification.VERIFIED);

        final Map<BrontoFieldName, String> fieldNameMap = new EnumMap<>(BrontoFieldName.class);
        fieldNameMap.put(BrontoFieldName.email_verification, Verification.VERIFIED.toString());
        brontoGateway.updateContactFields(new UpdateContactFieldMessage(new InternalBrontoContact(player), fieldNameMap));

        playerRepository.save(player);
        return player;
    }

    @Override
    public boolean isEmailAddressUnregistered(@Nonnull final String emailAddress) {
        return playerRepository.findByEmailAddress(emailAddress) == null;
    }

    @Override
    public boolean isNicknameUnregistered(@Nonnull final String nickname) {
        return playerRepository.findByNickname(nickname) == null;
    }

    private void invalidatePlayerUuids(final Player player, final UuidType uuidType) {
        final List<PlayerUuid> playerUuidList = playerUuidRepository.findByPlayerAndUuidTypeAndUsedDateIsNull(player, uuidType);
        if (playerUuidList != null) {
            final Date date = new Date();
            for (final PlayerUuid playerUuid : playerUuidList) {
                playerUuid.setUsedDate(date);
            }
            playerUuidRepository.save(playerUuidList);
        }
    }

    private void setPlayerLimitation(final PlayerLimitation playerLimitation, final LimitationType limitationType, final TimeUnit newTimeUnit,
                                     final Money newLimit) {
        final Player player = playerLimitation.getPlayer();
        final Money currentLimit = playerLimitation.getLimitByLimitationType(limitationType);
        final TimeUnit currentTimeUnit = playerLimitation.getTimeUnitByLimitationType(limitationType);

        logger.info("Player {} trying to set responsible gaming limits for {} from {} per {} to {} per {}",
                    player.getId(), limitationType, currentLimit, currentTimeUnit, newLimit, newTimeUnit);

        // TODO validation if new limit is positive?
        if (newLimit.getCents() <= 0 || playerLimitation.isCurrentLimitHarsher(limitationType, newTimeUnit, newLimit)) {
            logger.info("Player {} current limits are harsher than new limits.", player.getId());
            throw new PlayerLimitationException("Player " + player.getId() + " current " + limitationType + " limit: " + currentLimit
                                                + " per " + currentTimeUnit + " is harsher than new limit: " + newLimit + " per " + newTimeUnit);
        }

        playerLimitation.setLimit(limitationType, newTimeUnit, newLimit);
        playerLimitation.setModifiedDate(new Date());
        playerLimitationRepository.save(playerLimitation);
    }

    @Override
    public PlayerLimitation getPlayerLimitation(@Nonnull final Long playerId) {
        return playerLimitationRepository.findByPlayer(getPlayer(playerId));
    }

    @VisibleForTesting
    PlayerLimitation getFullPlayerLimitation(final Player player) {
        final PlayerLimitation limitation = playerLimitationRepository.findByPlayer(player);
        limitation.setBetPercentage(limitation.calculatePercentage(BET_AMOUNT, player.getWallet().getAccumulatedBetAmountByTimeUnit(limitation.getBetTimeUnit())));
        limitation.setLossPercentage(limitation.calculatePercentage(LOSS_AMOUNT, player.getWallet().getAccumulatedLossAmountByTimeUnit(limitation.getLossTimeUnit())));
        return limitation;
    }

    @Override
    public PlayerLimitation updatePlayerLimitation(@Nonnull final Long playerId, @Nonnull final List<Limit> limits, @Nullable final Integer sessionLength,
                                                   @Nonnull final String password) {
        final Player player = getPlayer(playerId);

        if (!passwordEncoder.matches(password, player.getPassword())) {
            throw new InvalidPasswordException("Wrong password");
        }

        final PlayerLimitationTrace limitTrace = new PlayerLimitationTrace(player, new Date(), new Date(), APPLIED);
        final PlayerLimitation playerLimitation = playerLimitationRepository.findByPlayer(player);

        if (player.getBlockType().isBlocked(player.getBlockEndDate())) {
            logger.error("Blocked Player {} is trying to change player limitations : {} and session length limit : {}", playerId, limits, sessionLength);
            throw new BlockedPlayerException("Player is blocked.");
        }

        if (sessionLength != null) {
            setSessionLengthLimitation(playerLimitation, sessionLength);
            limitTrace.setSessionLength(sessionLength);
        }

        for (final Limit limit : limits) {
            setPlayerLimitation(playerLimitation, limit.getLimitationType(), limit.getTimeUnit(), limit.getAmount());
            limitTrace.setLimit(limit.getLimitationType(), limit.getTimeUnit(), limit.getAmount());
        }

        playerLimitationTraceRepository.save(limitTrace);
        return getFullPlayerLimitation(player);
    }

    @VisibleForTesting
    void setSessionLengthLimitation(final PlayerLimitation playerLimitation, final Integer newLength) {
        final Player player = playerLimitation.getPlayer();
        logger.info("Player {} tried to change session length limitation from {} to {}",
                    player.getId(), playerLimitation.getSessionLength(), newLength);

        if (newLength <= 0 || playerLimitation.isSessionLengthLimitHarsher(newLength)) {
            logger.warn("Player {} failed to change session length limitation from {} to {}",
                        player.getId(), playerLimitation.getSessionLength(), newLength);
            throw new PlayerLimitationException("Current session limitation length is less than new one.");
        }

        playerLimitation.setSessionLength(newLength);
        playerLimitationRepository.save(playerLimitation);
    }

    @Override
    public void logoffPlayer(@Nonnull final Long playerId) {
        final Player player = getPlayer(playerId);
        try {
            gameService.logoutPlayer(player);
        } catch (final CommunicationException ignore) {
            // Doesn't really matter if the logout succeeds or not
        } finally {
            gameService.resetSessionTime(player);
            auditService.trackPlayerActivity(player, LOGOUT);
        }
    }

    @Override
    public PlayerUuid createUuidForDeposit(final Player player) {
        if (player.getBlockType().isLimited(player.getBlockEndDate())) {
            logger.info("Player {} tried to deposit money while was blocked {}", player, player.getBlockType());
            throw new BlockedPlayerException("Player is blocked " + player.getBlockType());
        }

        return createPlayerUuid(player, PAYMENT);
    }

    @Override
    public boolean checkSessionTime(final Long playerId) {
        final Player player = getPlayer(playerId);

        if (!gameService.isSessionTimeOver(player)) {
            return false;
        }

        gameService.logoutOverTimedPlayer(player);

        logger.info("Player {} was logged out due to exceeding session length limit.", playerId);
        return true;
    }

    @Override
    public PlayerLimitation backOfficeGetPlayerLimitation(@Nonnull final Long playerId) {
        return playerLimitationRepository.findByPlayer(getPlayer(playerId));
    }

    @Override
    public PlayerLimitation backOfficeUpdatePlayerLimitations(@Nonnull final User user, @Nonnull final Long playerId, @Nonnull final List<Limit> limits,
                                                              @Nullable final Integer sessionLength) {
        final Player player = getPlayer(playerId);
        final Date currentDate = new Date();
        final PlayerLimitationTrace limitTrace = new PlayerLimitationTrace(player, user, currentDate, currentDate, APPLIED);
        final PlayerLimitation playerLimitation = playerLimitationRepository.findByPlayer(player);

        // TODO if player is blocked? should still be able to set?
        if (player.getBlockType().isBlocked(player.getBlockEndDate())) {
            logger.error("User {} is trying to change the BLOCKED player {} limitations : {} and session length limit : {}", user.getId(), playerId, limits,
                         sessionLength);
            throw new BlockedPlayerException("Player is blocked.");
        }

        if (sessionLength != null) {
            logger.info("User {} changed the player {} session length limitation from {} to {}", user.getId(), playerId, playerLimitation.getSessionLength(),
                        sessionLength);
            backOfficeSetSessionLengthLimitation(user, limitTrace, playerLimitation, sessionLength, currentDate);
        }

        for (final Limit limit : limits) {
            backOfficeSetPlayerLimitation(user, limitTrace, playerLimitation, limit.getLimitationType(), limit.getTimeUnit(), limit.getAmount(), currentDate);
        }

        playerLimitationTraceRepository.save(limitTrace);
        auditService.trackUserActivity(user, UserActivityType.UPDATE_PLAYER_LIMITATIONS, "Updated the limitation of the Player  " + playerId);
        return getFullPlayerLimitation(player);
    }

    private void backOfficeSetPlayerLimitation(final User user, final PlayerLimitationTrace limitTrace, final PlayerLimitation playerLimitation,
                                               final LimitationType limitationType, final TimeUnit newTimeUnit, final Money newLimit, final Date currentDate) {
        // TODO validation if new limit is positive?
        limitTrace.setLimit(limitationType, newTimeUnit, newLimit);

        if (newLimit.getCents() == 0 || playerLimitation.isCurrentLimitHarsher(limitationType, newTimeUnit, newLimit)) {
            limitTrace.setLimitationStatus(LimitationStatus.AWAITING);
            limitTrace.setApplyDate(getNewTime(currentDate, Calendar.DATE, minimumBlockDays));
            logger.info("[Will be applied in 7 days] User {} changed the player {} responsible gaming limits for {} from {} per {} to {} per {}",
                        user.getId(), playerLimitation.getPlayer().getId(), limitationType, playerLimitation.getLimitByLimitationType(limitationType),
                        playerLimitation.getTimeUnitByLimitationType(limitationType), newLimit, newTimeUnit);
        } else {
            playerLimitation.setLimit(limitationType, newTimeUnit, newLimit);
            playerLimitation.setModifiedDate(currentDate);
            playerLimitationRepository.save(playerLimitation);
            logger.info("[Applied] User {} changed the player {} responsible gaming limits for {} from {} per {} to {} per {}",
                        user.getId(), playerLimitation.getPlayer().getId(), limitationType, playerLimitation.getLimitByLimitationType(limitationType),
                        playerLimitation.getTimeUnitByLimitationType(limitationType), newLimit, newTimeUnit);
        }
        playerLimitationTraceRepository.save(limitTrace);
    }

    private void backOfficeSetSessionLengthLimitation(final User user, final PlayerLimitationTrace limitTrace, final PlayerLimitation playerLimitation,
                                                      final Integer newLength, final Date currentDate) {
        limitTrace.setSessionLength(newLength);

        if (newLength == 0 || playerLimitation.isSessionLengthLimitHarsher(newLength)) {
            limitTrace.setLimitationStatus(LimitationStatus.AWAITING);
            limitTrace.setApplyDate(getNewTime(currentDate, Calendar.DATE, minimumBlockDays));
            logger.info("[Will be applied in 7 days] User {} changed the player {} session length limit from {} to {}",
                        user.getId(), playerLimitation.getPlayer().getId(), playerLimitation.getSessionLength(), newLength);
        } else {
            playerLimitation.setSessionLength(newLength);
            playerLimitation.setModifiedDate(currentDate);
            playerLimitationRepository.save(playerLimitation);
            logger.info("[Applied] User {} changed the player {} session length limit from {} to {}",
                        user.getId(), playerLimitation.getPlayer().getId(), playerLimitation.getSessionLength(), newLength);
        }
        playerLimitationTraceRepository.save(limitTrace);
    }

    private Date getNewTime(final Date currentDate, final int timeUnit, final int amount) {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(timeUnit, amount);
        return calendar.getTime();
    }

    @Override
    public PlayerLimitation forceUpdatePlayerLimitations(@Nonnull final User user, @Nonnull final Long playerId, @Nonnull final List<Limit> limits,
                                                         @Nullable final Integer sessionLength) {
        final Player player = getPlayer(playerId);
        final Date currentDate = new Date();
        final PlayerLimitationTrace limitTrace = new PlayerLimitationTrace(player, user, currentDate, currentDate, APPLIED);
        final PlayerLimitation playerLimitation = playerLimitationRepository.findByPlayer(player);

        // TODO if player is blocked? should still be able to set?
        if (player.getBlockType().isBlocked(player.getBlockEndDate())) {
            logger.error("User {} is trying to force change the BLOCKED player {} limitations : {} and session length limit : {}",
                         user.getId(), playerId, limits, sessionLength);
            throw new BlockedPlayerException("Player is blocked.");
        }

        if (sessionLength != null) {
            logger.error("User {} forced to change the player {} session length limitation from {} to {}",
                         user.getId(), playerId, playerLimitation.getSessionLength(), sessionLength);
            limitTrace.setSessionLength(sessionLength);
            playerLimitation.setSessionLength(sessionLength);
            playerLimitation.setModifiedDate(currentDate);
            playerLimitationRepository.save(playerLimitation);
        }

        for (final Limit limit : limits) {
            limitTrace.setLimit(limit.getLimitationType(), limit.getTimeUnit(), limit.getAmount());
            playerLimitation.setLimit(limit.getLimitationType(), limit.getTimeUnit(), limit.getAmount());
            playerLimitation.setModifiedDate(currentDate);
            playerLimitationRepository.save(playerLimitation);
            logger.info("[Applied] User {} forced to change the player {} responsible gaming limits for {} from {} per {} to {} per {}",
                        user.getId(), playerId, limit.getLimitationType(), playerLimitation.getLimitByLimitationType(limit.getLimitationType()),
                        playerLimitation.getTimeUnitByLimitationType(limit.getLimitationType()), limit.getAmount(), limit.getTimeUnit());
        }

        playerLimitationTraceRepository.save(limitTrace);
        auditService.trackUserActivity(user, UserActivityType.UPDATE_PLAYER_LIMITATIONS, "Forced to update the limitation of the Player  " + playerId);
        return getFullPlayerLimitation(player);
    }

    @Override
    public void selfExcludePlayer(@Nonnull final Long playerId, @Nonnull final BlockType blockType, @Nullable final Integer days) {
        final Player player = getPlayer(playerId);
        final Date now = new Date();
        logger.info("player {} self blocking of type {} for {} days.", playerId, blockType, days);

        final PlayerLimitationLockout lockout;

        if (blockType == DEFINITE_SELF_EXCLUSION && days != null) {
            final Date blockEndTime = getNewTime(now, Calendar.DATE, days);
            player.setBlockEndDate(blockEndTime);
            lockout = new PlayerLimitationLockout(player, blockType, now, blockEndTime);
            logger.info("player {} self blocked of type {} for {} days.", playerId, blockType, days);
        } else {
            throw new IllegalArgumentException("Unsupported  BlockType " + blockType);
        }

        playerLimitationLockoutRepository.save(lockout);
        player.setBlockType(blockType);
        player.setModifiedDate(now);
        playerRepository.save(player);
        auditService.trackPlayerActivity(player, PlayerActivityType.SELF_BLOCK);
    }

    @Override
    public void blockPlayerSelfExcluded(@Nonnull final User user, @Nonnull final Long playerId, @Nonnull final BlockType blockType, @Nullable final Integer days) {
        final Player player = getPlayer(playerId);
        final Date now = new Date();
        logger.info("User {} tried to block the player {} of type {} for {} days.", user.getId(), playerId, blockType, days);

        final PlayerLimitationLockout lockout;

        if (blockType == DEFINITE_SELF_EXCLUSION && days != null) {
            final Date blockEndTime = getNewTime(now, Calendar.DATE, days);
            player.setBlockEndDate(blockEndTime);
            lockout = new PlayerLimitationLockout(player, blockType, now, blockEndTime);
            logger.info("User {} blocked the player {} of type {} for {} days.", user.getId(), playerId, blockType, days);
            auditService.trackUserActivity(user, UserActivityType.BLOCK_PLAYER, blockType + " block for the Player " + playerId + " of number of days " + days);
        } else if (blockType == INDEFINITE_SELF_EXCLUSION) {
            lockout = new PlayerLimitationLockout(player, blockType, now, now);
            logger.info("User {} blocked the player {} of type {}.", user.getId(), playerId, blockType);
            auditService.trackUserActivity(user, UserActivityType.BLOCK_PLAYER, blockType + " block for the Player " + playerId);
        } else {
            throw new IllegalArgumentException("Unsupported  BlockType " + blockType);
        }

        playerLimitationLockoutRepository.save(lockout);
        player.setBlockType(blockType);
        player.setModifiedDate(now);
        playerRepository.save(player);
    }

    @Override
    public void unblockSelfExcludedPlayer(@Nonnull final User user, @Nonnull final Long playerId) {
        final Player player = getPlayer(playerId);
        final Date now = new Date();

        if (!player.getBlockType().isBlocked(player.getBlockEndDate())) {
            logger.warn("User {} tried to unblock (self-excluded) player {} which had of block type {}", user.getId(), playerId, player.getBlockType());
            throw new PlayerLimitationException("Player " + playerId + " is not self excluded block");
        }

        final Date blockEndDate = getNewTime(now, Calendar.DATE, minimumBlockDays);
        player.setBlockType(DEFINITE_SELF_EXCLUSION);
        player.setBlockEndDate(blockEndDate);
        player.setModifiedDate(now);
        playerRepository.save(player);

        final PlayerLimitationLockout lockout = new PlayerLimitationLockout(player, DEFINITE_SELF_EXCLUSION, now, blockEndDate);
        playerLimitationLockoutRepository.save(lockout);

        logger.info("User {} unblocked the player {} : {} until {}", user.getId(), playerId, DEFINITE_SELF_EXCLUSION, blockEndDate);
        auditService.trackUserActivity(user, UserActivityType.UNBLOCK_PLAYER, "Unblocked self-excluded Player " + playerId);
    }

    @Override
    public void forceUnblockSelfExcludedPlayer(@Nonnull final User user, @Nonnull final Long playerId) {
        final Player player = getPlayer(playerId);
        final Date now = new Date();

        if (!player.getBlockType().isBlocked(player.getBlockEndDate())) {
            logger.warn("User {} tried to unblock (self-excluded) player {} which had of block type {}", user.getId(), playerId, player.getBlockType());
            throw new PlayerLimitationException("Player " + playerId + " is not self excluded block");
        }

        logger.info("User {} FORCED to unblock the player {} with block type {} and block end time {}",
                    user.getId(), playerId, player.getBlockType(), player.getBlockEndDate());

        player.setBlockType(UNBLOCKED);
        player.setBlockEndDate(now);
        player.setModifiedDate(now);
        playerRepository.save(player);

        final PlayerLimitationLockout lockout = new PlayerLimitationLockout(player, UNBLOCKED, now, now);
        playerLimitationLockoutRepository.save(lockout);
        auditService.trackUserActivity(user, UserActivityType.UNBLOCK_PLAYER, "Forced to unblock self-excluded Player " + playerId);
    }

    @Transactional(propagation = SUPPORTS, readOnly = true)
    @Override
    public void validatePassword(@Nonnull final Player player, @Nonnull final String password) {
        if (!passwordEncoder.matches(password, player.getPassword())) {
            throw new InvalidPasswordException("Wrong password");
        }
    }

    @Override
    public void resetLoginFailureCounter(final Long id) {
        final Player player = findPlayerById(id);
        player.setFailedLoginAttempts(0);
        playerRepository.save(player);
    }

    @Override
    public void recordLoginFailure(@Nonnull final Long id) {
        final Player player = findPlayerById(id);

        auditService.trackPlayerActivity(player, FAILED_LOGIN);

        final Integer currentFailedLoginAttempts = player.getFailedLoginAttempts();
        if (currentFailedLoginAttempts < Integer.MAX_VALUE) {
            player.setFailedLoginAttempts(currentFailedLoginAttempts + 1);
        }
        if (player.getFailedLoginAttempts() >= maximumFailedLoginAttempts) {
            player.setStatus(BAD_CREDENTIALS_LOCKED);
        }
        player.setLastFailedLoginDate(new Date());
        playerRepository.save(player);
    }

    @Override
    public Player savePlayer(final Player player) {
        return playerRepository.save(player);
    }

    @ScheduledJob
    @Override
    public void resetExpiredLimitBlocks() {
        final Integer count = playerRepository.resetExpiredLimitBlocks(UNBLOCKED, new Date(), Arrays.asList(BET_LIMIT, LOSS_LIMIT));
        logger.info("Reset block type of {} players", count);
    }

    @Override
    public List<Player> getPlayersRegisteredBefore(final Date date) {
        return playerRepository.getNonTestPlayersCreatedBefore(date);
    }

    @Transactional(propagation = SUPPORTS, readOnly = true)
    @Override
    public Map<BigInteger, String> getPlayerSignUpIp() {
        final Map<BigInteger, String> map = new HashMap<>();
        final List<Object[]> list = playerActivityRepository.getPlayersSignUpIPs();
        for (final Object[] tuple : list) {
            map.put((BigInteger) tuple[0], (String) tuple[1]);
        }
        return map;
    }

    @Override
    public boolean isIpBlocked(final Player player, final String ipAddress) {
        if (whiteListService.isWhiteListedOnLogin(player, ipAddress)) {
            return false;
        }
        final Country country = getCountryByIpAddress(ipAddress);
        return country != null && Country.getBlockedCountries().contains(country);
    }

    @Nullable
    private Country getCountryByIpAddress(final String ipAddress) {
        Country country = null;
        final WebServiceClient client = new WebServiceClient.Builder(maxmindUserId, maxmindLicenseKey).build();
        try {
            final CountryResponse response = client.country(InetAddress.getByName(ipAddress));
            country = Country.getCountryByIsoCode(response.getCountry().getIsoCode());
        } catch (final UnknownHostException e) {
            logger.error("UnknownHostException on connecting to max mined {}", e.getMessage());
        } catch (final IOException e) {
            logger.error("IOException on connecting to max mined {}", e.getMessage());
        } catch (final GeoIp2Exception e) {
            logger.error("GeoIp2Exception on connecting to max mined {}", e.getMessage());
        } catch (final NullPointerException | IllegalArgumentException e) {
            logger.error("Can not get country of ip address {}", ipAddress);
        }

        return country;
    }

    @ScheduledJob
    @Override
    public void applyPlayerLimitations() {
        final List<PlayerLimitationTrace> playerLimitationTraceList = playerLimitationTraceRepository.findAwaitingLimitations(new Date());
        for (final PlayerLimitationTrace playerLimitationTrace : playerLimitationTraceList) {
            applyPlayerLimitations(playerLimitationTrace);
        }
        logger.info("Applied player limitations of {} players", playerLimitationTraceList.size());
    }

    private void applyPlayerLimitations(final PlayerLimitationTrace playerLimitationTrace) {

        final PlayerLimitation playerLimitation = playerLimitationRepository.findByPlayer(playerLimitationTrace.getPlayer());

        if (playerLimitationTrace.getSessionLength() != null) {
            playerLimitation.setSessionLength(playerLimitationTrace.getSessionLength());
        }

        if (playerLimitationTrace.getBetLimit() != null && playerLimitationTrace.getBetTimeUnit() != null) {
            playerLimitation.setBetLimit(playerLimitationTrace.getBetLimit());
            playerLimitation.setBetTimeUnit(playerLimitationTrace.getBetTimeUnit());
        }

        if (playerLimitationTrace.getLossLimit() != null && playerLimitationTrace.getLossTimeUnit() != null) {
            playerLimitation.setLossLimit(playerLimitationTrace.getLossLimit());
            playerLimitation.setLossTimeUnit(playerLimitationTrace.getLossTimeUnit());
        }

        playerLimitation.setModifiedDate(new Date());
        playerLimitationRepository.save(playerLimitation);

        playerLimitationTrace.setLimitationStatus(APPLIED);
        playerLimitationTraceRepository.save(playerLimitationTrace);
    }

    @ScheduledJob
    @Override
    public void resetDailyAccumulatedLimits() {
        final Integer count = walletRepository.resetAccumulateDailyBetsAndLosses(Money.ZERO);
        logger.info("Reset accumulated daily bets and losses of {} players", count);
    }

    @ScheduledJob
    @Override
    public void resetWeeklyAccumulatedLimits() {
        final Integer count = walletRepository.resetAccumulateWeeklyBetsAndLosses(Money.ZERO);
        logger.info("Reset accumulated weekly bets and losses of {} players", count);
    }

    @ScheduledJob
    @Override
    public void resetMonthlyAccumulatedLimits() {
        final Integer count = walletRepository.resetAccumulateMonthlyBetsAndLosses(Money.ZERO);
        logger.info("Reset accumulated monthly bets and losses of {} players", count);
    }
}
