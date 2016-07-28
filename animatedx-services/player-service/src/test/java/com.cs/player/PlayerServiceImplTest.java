package com.cs.player;

import com.cs.affiliate.AffiliateService;
import com.cs.audit.AuditService;
import com.cs.avatar.Avatar;
import com.cs.avatar.AvatarService;
import com.cs.avatar.Level;
import com.cs.bonus.BonusService;
import com.cs.game.CasinoModuleConstants;
import com.cs.game.GameService;
import com.cs.game.GameSessionRepository;
import com.cs.game.PlayerLimitationRepository;
import com.cs.item.Item;
import com.cs.item.ItemService;
import com.cs.item.PlayerItem;
import com.cs.item.PlayerItemRepository;
import com.cs.level.LevelService;
import com.cs.messaging.bronto.BrontoGateway;
import com.cs.messaging.bronto.BrontoService;
import com.cs.messaging.email.EmailService;
import com.cs.messaging.websocket.WebSocketGateway;
import com.cs.payment.Currency;
import com.cs.payment.Money;
import com.cs.persistence.Constants;
import com.cs.persistence.Country;
import com.cs.persistence.InvalidPasswordException;
import com.cs.persistence.Language;
import com.cs.persistence.NotFoundException;
import com.cs.persistence.Status;
import com.cs.promotion.PromotionService;
import com.cs.session.PlayerSessionRegistryRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import com.mysema.query.types.Predicate;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static com.cs.persistence.Status.ACTIVE;
import static com.cs.persistence.Status.INACTIVE;
import static java.util.Calendar.MAY;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Omid Alaepour
 */
@RunWith(MockitoJUnitRunner.class)
public class PlayerServiceImplTest {

    @InjectMocks
    private PlayerServiceImpl playerServiceImpl;

    @Mock
    private AffiliateService affiliateService;
    @Mock
    private AuditService auditService;
    @Mock
    private AvatarService avatarService;
    @Mock
    private BonusService bonusService;
    @Mock
    private BrontoService brontoService;
    @Mock
    private BrontoGateway brontoGateway;
    @Mock
    private EmailService emailService;
    @Mock
    private GameService gameService;
    @Mock
    private GameSessionRepository gameSessionRepository;
    @Mock
    private ItemService itemService;
    @Mock
    private LevelService levelService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private PlayerItemRepository playerItemRepository;
    @Mock
    private PlayerLimitationRepository playerLimitationRepository;
    @Mock
    private PlayerLimitationTraceRepository playerLimitationTraceRepository;
    @Mock
    private PlayerRepository playerRepository;
    @Mock
    private PlayerUuidRepository playerUuidRepository;
    @Mock
    private PromotionService promotionService;
    @Mock
    private WalletRepository walletRepository;
    @Mock
    private WebSocketGateway webSocketGateway;
    @Mock
    private PlayerSessionRegistryRepository playerSessionRegistryRepository;

    @Captor
    private ArgumentCaptor<List<Level>> levelArgumentCaptor;

    private PlayerLimitation createPlayerLimitation(final Player player) {
        return new PlayerLimitation(player, new Money(2000L), TimeUnit.DAY, new Money(2000L), TimeUnit.DAY, 60);
    }

    private Level createLevel() {
        final Level newLevel = new Level();
        newLevel.setMoneyCreditRate(0.5D);
        newLevel.setBonusCreditRate(1.5D);
        newLevel.setLevel(5L);
        return newLevel;
    }

    private Wallet createWallet(final Player newPlayer) {
        final Wallet newWallet = new Wallet(newPlayer);
        newWallet.setMoneyBalance(new Money(1000L));
        newWallet.setCreditsBalance(0);
        return newWallet;
    }

    private Address createAddress() {
        final Address address = new Address();
        address.setStreet("Street");
        address.setCity("City");
        address.setZipCode("123456");
        address.setCountry(Country.SWEDEN);
        return address;
    }

    private Player createPlayerWithWalletAndLevel() {
        final Player newPlayer = new Player();
        newPlayer.setId(1L);
        newPlayer.setEmailAddress("exist@test.com");
        newPlayer.setStatus(ACTIVE);
        newPlayer.setAddress(createAddress());
        newPlayer.setFirstName("First");
        newPlayer.setLastName("Last");
        newPlayer.setNickname("Nick");
        newPlayer.setWallet(createWallet(newPlayer));
        newPlayer.setLevel(createLevel());
        newPlayer.setTrustLevel(TrustLevel.GREEN);
        newPlayer.setBlockType(BlockType.UNBLOCKED);
        newPlayer.setPhoneNumber("562-145-3625");
        newPlayer.setEmailVerification(Verification.VERIFIED);
        newPlayer.setCurrency(Currency.EUR);
        final Avatar avatar = new Avatar();
        avatar.setId(10L);
        newPlayer.setAvatar(avatar);
        newPlayer.setBirthday(new Date());

        newPlayer.setLanguage(Language.ENGLISH);
        final Calendar calendar = Calendar.getInstance();
        calendar.set(1970, MAY, 1);
        newPlayer.setBirthday(calendar.getTime());
        return newPlayer;
    }

    @Test
    public void getPlayer_ReturnsExistingPlayer() {
        final Player player = createPlayerWithWalletAndLevel();

        when(playerRepository.findByEmailAddress(anyString())).thenReturn(player);

        assertThat(playerServiceImpl.getPlayer(anyString()), is(notNullValue()));
    }

    @Test(expected = NotFoundException.class)
    public void getPlayer_ShouldThrowNotFoundExceptionIfPlayerDoesNotExist() {
        when(playerRepository.findByEmailAddress(anyString())).thenReturn(null);

        playerServiceImpl.getPlayer(anyString());
    }

    @Test(expected = NotFoundException.class)
    public void getPlayer_ShouldThrowNotFoundExceptionIfPlayerIsInactive() {
        final Player player = createPlayerWithWalletAndLevel();
        player.setStatus(Status.INACTIVE);

        when(playerRepository.findByEmailAddress(anyString())).thenReturn(player);

        playerServiceImpl.getPlayer(anyString());
    }

    @Test
    public void getPlayerByCasinoUsername_ShouldThrowInvalidNetEntUsernameExceptionWhenIncorrectNetEntUsername() {
        try {
            playerServiceImpl.getPlayerByCasinoUsername("");
            fail();
        } catch (final InvalidNetEntUsernameException ignored) {
        }

        try {
            playerServiceImpl.getPlayerByCasinoUsername(CasinoModuleConstants.PLAYER_ID_PREFIX);
            fail();
        } catch (final InvalidNetEntUsernameException ignored) {
        }

        try {
            playerServiceImpl.getPlayerByCasinoUsername(CasinoModuleConstants.PLAYER_ID_PREFIX + "player1");
            fail();
        } catch (final InvalidNetEntUsernameException ignored) {
        }

        try {
            playerServiceImpl.getPlayerByCasinoUsername("player1" + CasinoModuleConstants.PLAYER_ID_PREFIX);
            fail();
        } catch (final InvalidNetEntUsernameException ignored) {
        }
    }

    @Test(expected = NotFoundException.class)
    public void getPlayerByCasinoUsername_ShouldThrowNotFoundExceptionWhenUserDoesNotExist() {
        when(playerRepository.findOne(anyLong())).thenReturn(null);

        playerServiceImpl.getPlayerByCasinoUsername(CasinoModuleConstants.PLAYER_ID_PREFIX + "1");
    }

    @Test
    public void getPlayerByCasinoUsername_ShouldReturnPlayer() {
        final Player player = createPlayerWithWalletAndLevel();
        when(playerRepository.findOne(player.getId())).thenReturn(player);

        final Player result = playerServiceImpl.getPlayerByCasinoUsername(CasinoModuleConstants.PLAYER_ID_PREFIX + player.getId());

        assertThat(result, is(equalTo(player)));
    }

    @Test
    public void searchPlayer_ShouldReturnPlayers() {
        final List<Player> players = new ArrayList<>();
        final Player player = createPlayerWithWalletAndLevel();
        players.add(player);
        final Page<Player> pagedPlayers = new PageImpl<>(players);

        when(playerRepository.findAll(any(Predicate.class), any(Pageable.class))).thenReturn(pagedPlayers);

        final Page<Player> result = playerServiceImpl.searchPlayers(anyString(), anyString(), null, null, null, LimitationStatus.APPLIED, 0, Constants.DEFAULT_PAGE_SIZE);

        assertThat(result, is(equalTo(pagedPlayers)));
    }

    @Test
    public void createPlayer_ShouldReturnPlayersWithActiveAndPasswordAndCreatedDateAndWalletWithoutNull() {
        final Player player = createPlayerWithWalletAndLevel();

        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(playerRepository.save(any(Player.class))).thenReturn(player);
        when(walletRepository.save(any(Wallet.class))).thenReturn(player.getWallet());
        when(playerRepository.findOne(anyLong())).thenReturn(player);
        when(avatarService.getAvatarForLevel(player.getAvatar(), new Level(1L))).thenReturn(player.getAvatar());

        ReflectionTestUtils.setField(playerServiceImpl, "maxmindUserId", 91182);
        ReflectionTestUtils.setField(playerServiceImpl, "maxmindLicenseKey", "RSldVet134Cu");
        final Player createdPlayer = playerServiceImpl.createPlayer(player, null, "bTag", "109.228.157.98");

        assertThat(createdPlayer.getStatus(), is(equalTo(ACTIVE)));
        assertThat(createdPlayer.getPassword(), is(not(isEmptyOrNullString())));
        assertThat(createdPlayer.getCreatedDate(), is(notNullValue()));
        assertThat(createdPlayer.getWallet().getPlayer(), is(sameInstance(player)));
    }

    @Test(expected = PlayerCreationException.class)
    public void createPlayer_ShouldErrorWhenStreetAndDateOfBirthIsEqual() {
        final Player player = createPlayerWithWalletAndLevel();

        final Player existingPlayer = new Player();
        existingPlayer.setAddress(player.getAddress());
        existingPlayer.setBirthday(player.getBirthday());

        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(playerRepository.save(any(Player.class))).thenReturn(player);
        when(walletRepository.save(any(Wallet.class))).thenReturn(player.getWallet());
        when(playerRepository.findOne(anyLong())).thenReturn(player);
        when(playerRepository.findByFirstNameAndLastName(anyString(), anyString())).thenReturn(Collections.singletonList(existingPlayer));

        playerServiceImpl.createPlayer(player, null, "bTag", "109.228.157.98");
    }

    @Test
    @Ignore //Todo email test
    public void createPlayer_ShouldWorkWhenEmailFails() {
        final Player player = createPlayerWithWalletAndLevel();

        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(playerRepository.save(any(Player.class))).thenReturn(player);
        when(walletRepository.save(any(Wallet.class))).thenReturn(player.getWallet());
        when(playerRepository.findOne(anyLong())).thenReturn(player);
        //doThrow(new MailSendException("Failed")).when(emailService).sendEmail(any(Player.class), eq(EmailType.WELCOME_USER), eq(player.getLanguage()));

        playerServiceImpl.createPlayer(player, null, "bTag", "109.228.157.98");

        assertThat(player.getStatus(), is(equalTo(ACTIVE)));
        assertThat(player.getPassword(), is(not(isEmptyOrNullString())));
        assertThat(player.getCreatedDate(), is(notNullValue()));
        assertThat(player.getWallet().getPlayer(), is(sameInstance(player)));
    }

    @Test
    public void updatePlayer_ShouldReturnUpdatedPlayer() {
        final Player newPlayer = new Player();
        newPlayer.setFirstName("First name");
        newPlayer.setLastName("Last name");
        newPlayer.setEmailAddress("email@email.com");
        newPlayer.setNickname("Nick name");
        newPlayer.setPhoneNumber("newPhoneNumber");

        final Player originalPlayer = new Player();
        originalPlayer.setStatus(ACTIVE);
        originalPlayer.setFirstName(newPlayer.getFirstName() + "more");
        originalPlayer.setLastName(newPlayer.getLastName() + "more");
        originalPlayer.setEmailAddress(newPlayer.getEmailAddress() + "more");
        originalPlayer.setNickname(newPlayer.getNickname() + "more");
        originalPlayer.setPhoneNumber(newPlayer.getPhoneNumber() + "more");
        originalPlayer.setCurrency(Currency.EUR);
        originalPlayer.setAvatar(new Avatar(1L));
        originalPlayer.setBirthday(new Date());
        originalPlayer.setLevel(new Level(1L));
        final Wallet wallet = new Wallet();
        wallet.setMoneyBalance(new Money(1L));
        originalPlayer.setWallet(wallet);

        when(playerRepository.findOne(anyLong())).thenReturn(originalPlayer);

        playerServiceImpl.updatePlayer(anyLong(), newPlayer, null, null);

        assertThat(originalPlayer.getFirstName(), hasToString(newPlayer.getFirstName()));
        assertThat(originalPlayer.getLastName(), hasToString(newPlayer.getLastName()));
        assertThat(originalPlayer.getEmailAddress(), hasToString(newPlayer.getEmailAddress()));
        assertThat(originalPlayer.getNickname(), hasToString(newPlayer.getNickname()));
        assertThat(originalPlayer.getPhoneNumber(), hasToString(newPlayer.getPhoneNumber()));
    }

    @Test(expected = InvalidPasswordException.class)
    public void updatePlayer_ShouldFailOnPasswordMissMatch() {
        final Player newPlayer = new Player();
        newPlayer.setPassword("wrongPassword");

        final Player originalPlayer = new Player();
        originalPlayer.setStatus(ACTIVE);
        originalPlayer.setPassword("correctPassword");

        when(playerRepository.findOne(anyLong())).thenReturn(originalPlayer);

        playerServiceImpl.updatePlayer(anyLong(), newPlayer, newPlayer.getPassword(), "newPassword");
    }

    @Test
    public void updatePlayer_ShouldSetEmailVerificationOnEmailChange() {
        final Player newPlayer = new Player();
        newPlayer.setFirstName("firstName");
        newPlayer.setLastName("lastName");
        newPlayer.setEmailAddress("newEmail");

        final Player originalPlayer = new Player();
        originalPlayer.setStatus(ACTIVE);
        originalPlayer.setFirstName(newPlayer.getFirstName());
        originalPlayer.setLastName(newPlayer.getFirstName());
        originalPlayer.setEmailAddress("oldEmail");
        originalPlayer.setEmailVerification(Verification.VERIFIED);
        originalPlayer.setCurrency(Currency.EUR);
        originalPlayer.setAvatar(new Avatar(1L));
        originalPlayer.setBirthday(new Date());
        originalPlayer.setLevel(new Level(1L));
        final Wallet wallet = new Wallet();
        wallet.setMoneyBalance(new Money(1L));
        originalPlayer.setWallet(wallet);

        when(playerRepository.findOne(anyLong())).thenReturn(originalPlayer);
        when(playerUuidRepository.save(any(PlayerUuid.class))).thenReturn(new PlayerUuid());

        playerServiceImpl.updatePlayer(anyLong(), newPlayer, null, null);

        assertThat(originalPlayer.getEmailAddress(), hasToString(newPlayer.getEmailAddress()));
        assertThat(originalPlayer.getEmailVerification(), is(equalTo(Verification.RE_VERIFY)));
    }

    @Test
    public void updatePlayer_ShouldSetPlayerVerificationFirstNameChange() {
        final Player newPlayer = new Player();
        newPlayer.setFirstName("newFirstName");
        newPlayer.setLastName("lastName");

        final Player originalPlayer = new Player();
        originalPlayer.setStatus(ACTIVE);
        originalPlayer.setFirstName("firstName");
        originalPlayer.setLastName(newPlayer.getLastName());
        originalPlayer.setPlayerVerification(Verification.VERIFIED);
        originalPlayer.setLevel(new Level(1L));
        originalPlayer.setEmailVerification(Verification.VERIFIED);
        originalPlayer.setCurrency(Currency.EUR);
        originalPlayer.setAvatar(new Avatar(1L));
        originalPlayer.setBirthday(new Date());
        final Wallet wallet = new Wallet();
        wallet.setMoneyBalance(new Money(1L));
        originalPlayer.setWallet(wallet);

        when(playerRepository.findOne(anyLong())).thenReturn(originalPlayer);
        when(playerUuidRepository.save(any(PlayerUuid.class))).thenReturn(new PlayerUuid());

        playerServiceImpl.updatePlayer(anyLong(), newPlayer, null, null);

        assertThat(originalPlayer.getPlayerVerification(), is(equalTo(Verification.RE_VERIFY)));
    }

    @Test
    public void updatePlayer_ShouldSetPlayerVerificationLastNameChange() {
        final Player newPlayer = new Player();
        newPlayer.setFirstName("firstName");
        newPlayer.setLastName("newLastName");

        final Player originalPlayer = new Player();
        originalPlayer.setStatus(ACTIVE);
        originalPlayer.setFirstName(newPlayer.getFirstName());
        originalPlayer.setLastName("lastName");
        originalPlayer.setAddress(createAddress());
        originalPlayer.setPlayerVerification(Verification.VERIFIED);
        originalPlayer.setLevel(new Level(1L));
        originalPlayer.setEmailVerification(Verification.VERIFIED);
        originalPlayer.setCurrency(Currency.EUR);
        originalPlayer.setAvatar(new Avatar(1L));
        originalPlayer.setBirthday(new Date());

        final Wallet wallet = new Wallet();
        wallet.setMoneyBalance(new Money(10L));

        originalPlayer.setWallet(wallet);
        originalPlayer.setLanguage(Language.ENGLISH);

        when(playerRepository.findOne(anyLong())).thenReturn(originalPlayer);
        when(playerUuidRepository.save(any(PlayerUuid.class))).thenReturn(new PlayerUuid());

        playerServiceImpl.updatePlayer(anyLong(), newPlayer, null, null);

        assertThat(originalPlayer.getPlayerVerification(), is(equalTo(Verification.RE_VERIFY)));
    }

    @Test(expected = NotFoundException.class)
    public void updatePlayer_ShouldThrowNotFoundExceptionIfPlayerDoesNotExist() {
        when(playerRepository.findOne(anyLong())).thenReturn(null);

        playerServiceImpl.updatePlayer(anyLong(), new Player(), null, null);
    }

    @Test
    public void inactivatePlayer_ShouldReturnInactiveAndDateNotNull() {
        final Player player = createPlayerWithWalletAndLevel();

        when(playerRepository.findOne(anyLong())).thenReturn(player);

        playerServiceImpl.inactivatePlayer(anyLong());

        assertThat(player.getStatus(), is(equalTo(INACTIVE)));
        assertThat(player.getStatus(), is(notNullValue()));
    }

    @Test(expected = NotFoundException.class)
    public void inactivatePlayer_ShouldThrowNotFoundExceptionIfPlayerDoesNotExist() {
        when(playerRepository.findOne(anyLong())).thenReturn(null);

        playerServiceImpl.inactivatePlayer(anyLong());
    }

    @Test(expected = NotFoundException.class)
    public void getPlayerAddress_ShouldThrowNotFoundExceptionIfPlayerDoesNotExist() {
        when(playerRepository.findOne(anyLong())).thenReturn(null);

        playerServiceImpl.getPlayerAddress(anyLong());
    }

    @Test
    public void getUpdatePlayerAddress_ShouldSetModifiedDate() {
        final Player player = createPlayerWithWalletAndLevel();
        final Address address = player.getAddress();

        when(playerRepository.findOne(anyLong())).thenReturn(player);
        when(playerRepository.save(any(Player.class))).thenReturn(player);

        playerServiceImpl.updatePlayerAddress(anyLong(), address);

        assertThat(player.getModifiedDate(), is(notNullValue()));
    }

    @Test(expected = NotFoundException.class)
    public void getUpdatePlayerAddress_ShouldThrowNotFoundExceptionIfPlayerDoesNotExist() {
        when(playerRepository.findOne(anyLong())).thenReturn(null);

        playerServiceImpl.updatePlayerAddress(anyLong(), new Address());
    }

    @Test(expected = NotFoundException.class)
    public void getPlayerWallet_ShouldThrowNotFoundExceptionIfPlayerDoesNotExist() {
        when(playerRepository.findOne(anyLong())).thenReturn(null);

        playerServiceImpl.getPlayerWallet(anyLong());
    }

    @Test
    public void checkLevelByProgressTurnover_ShouldLevelUpWhenAboveThreshold() {
        final Player player = createPlayerWithWalletAndLevel();

        final Wallet wallet = player.getWallet();
        wallet.setLevelProgress(new Money(100L));

        final Level level10 = new Level();
        level10.setLevel(10L);
        player.setLevel(level10);
        player.setAvatar(new Avatar());

        final Level level11 = new Level();
        level11.setLevel(11L);
        level11.setTurnover(new Money(50L));
        level11.setCreditDices((short) 1);

        final Avatar nextAvatar = new Avatar();

        when(levelService.getLevelByTurnover(wallet.getLevelProgress())).thenReturn(level11);
        when(levelService.getLevel(eq(level11.getLevel()))).thenReturn(level11);
        when(avatarService.getAvatarForLevel(any(Avatar.class), eq(level11))).thenReturn(nextAvatar);
        when(playerRepository.save(any(Player.class))).thenReturn(player);

        final Iterable<Level> levels = new ArrayList<>();
        when(levelService.getLevelsWithItems(level10.getLevel() + 1, level11.getLevel())).thenReturn(levels);

        final boolean leveledUp = playerServiceImpl.checkLevelByProgressTurnover(player);

        assertThat(leveledUp, is(true));
        assertThat(player.getLevel(), is(sameInstance(level11)));
        assertThat(player.getAvatar(), is(sameInstance(nextAvatar)));
        assertThat(player.getWallet(), is(sameInstance(wallet)));
    }

    @Test
    public void checkLevelByProgressTurnover_ShouldReceiveItemWhenLevelHasItem() {
        final Player player = createPlayerWithWalletAndLevel();

        final Wallet wallet = player.getWallet();
        wallet.setLevelProgress(new Money(100L));

        final Level level10 = new Level();
        level10.setLevel(10L);
        player.setLevel(level10);
        player.setAvatar(new Avatar());

        final Level level11 = new Level();
        level11.setLevel(11L);
        level11.setTurnover(new Money(50L));
        level11.setCreditDices((short) 1);
        final Item item = new Item(1L);
        level11.setItem(item);

        final Avatar nextAvatar = new Avatar();

        final Iterable<Level> levels = Collections.singletonList(level11);
        final List<PlayerItem> playerItems = Collections.singletonList(new PlayerItem());

        when(levelService.getLevelByTurnover(wallet.getLevelProgress())).thenReturn(level11);
        when(levelService.getLevel(eq(level11.getLevel()))).thenReturn(level11);
        when(avatarService.getAvatarForLevel(any(Avatar.class), eq(level11))).thenReturn(nextAvatar);
        when(playerRepository.save(any(Player.class))).thenReturn(player);
        when(levelService.getLevelsWithItems(level10.getLevel() + 1, level11.getLevel())).thenReturn(levels);
        when(itemService.assignItems(eq(player), eq(levels))).thenReturn(playerItems);

        playerServiceImpl.checkLevelByProgressTurnover(player);

        final ArgumentCaptor<Player> playerArgumentCaptor = ArgumentCaptor.forClass(Player.class);
        verify(itemService, times(1)).assignItems(playerArgumentCaptor.capture(), levelArgumentCaptor.capture());
        assertThat(levelArgumentCaptor.getValue().get(0).getItem(), is(equalTo(item)));
        assertThat(playerArgumentCaptor.getValue(), is(equalTo(player)));
    }

    @Test
    public void checkLevelByProgressTurnover_ShouldNotLevelUpWhenBelowThreshold() {
        final Player player = createPlayerWithWalletAndLevel();

        final Wallet wallet = player.getWallet();
        wallet.setLevelProgress(new Money(100L));

        final Level initialLevel = new Level();
        initialLevel.setLevel(10L);
        player.setLevel(initialLevel);
        final Avatar avatar = new Avatar();
        player.setAvatar(avatar);

        when(levelService.getLevelByTurnover(wallet.getLevelProgress())).thenReturn(initialLevel);
        when(playerRepository.findOne(anyLong())).thenReturn(player);

        final boolean leveledUp = playerServiceImpl.checkLevelByProgressTurnover(player);

        assertThat(leveledUp, is(false));
        assertThat(player.getLevel(), is(sameInstance(initialLevel)));
        assertThat(player.getAvatar(), is(sameInstance(avatar)));
        assertThat(player.getWallet(), is(sameInstance(wallet)));
    }

    @Test
    public void createPlayerUuid_ShouldReturnSameInstanceOfPlayerAndCreatedDateNotNull() {
        final Player player = createPlayerWithWalletAndLevel();

        when(playerRepository.findOne(anyLong())).thenReturn(player);

        final PlayerUuid playerUuid = playerServiceImpl.createPlayerUuid(any(Player.class), UuidType.PAYMENT, "null");

        assertThat(playerUuid.getCreatedDate(), is(not(nullValue())));
    }

    private PlayerLimitation beforeTest_updatePlayerLimitation(final Player player) {
        final PlayerLimitation playerLimitation = new PlayerLimitation(player, new Money(1000L), TimeUnit.DAY, new Money(1000L), TimeUnit.DAY, 60);

        when(playerRepository.findOne(player.getId())).thenReturn(player);
        when(playerLimitationRepository.findByPlayer(player)).thenReturn(playerLimitation);

        return playerLimitation;
    }

    @Test
    public void updatePlayerLimitation_fromDayToDay() {
        final Player player = createPlayerWithWalletAndLevel();
        final String password = "test";
        player.setPassword(password);
        final Money tenMonies = new Money(1000L);
        final Wallet wallet = new Wallet(player);
        player.setWallet(wallet);
        final PlayerLimitation playerLimitation = beforeTest_updatePlayerLimitation(player);

        final Limit limit = new Limit(LimitationType.BET_AMOUNT, TimeUnit.DAY, tenMonies);
        final List<Limit> limits = new ArrayList<>();
        limits.add(limit);

        when(passwordEncoder.matches(password, player.getPassword())).thenReturn(true);

        playerServiceImpl.updatePlayerLimitation(player.getId(), limits, null, password);

        assertThat(playerLimitation.getBetLimit().getCents(), is(tenMonies.getCents()));
        assertThat(playerLimitation.getBetTimeUnit(), is(TimeUnit.DAY));
    }

    @Test(expected = PlayerLimitationException.class)
    public void updatePlayerLimitation_fromDayToDayHarsher() {
        final Player player = createPlayerWithWalletAndLevel();
        final String password = "test";
        player.setPassword(password);
        beforeTest_updatePlayerLimitation(player);

        final Limit limit = new Limit(LimitationType.BET_AMOUNT, TimeUnit.DAY, new Money(1001L));
        final List<Limit> limits = new ArrayList<>();
        limits.add(limit);

        when(passwordEncoder.matches(password, player.getPassword())).thenReturn(true);

        playerServiceImpl.updatePlayerLimitation(player.getId(), limits, null, password);
    }

    @Test
    public void updatePlayerLimitation_fromMonthToDay() {
        final Player player = createPlayerWithWalletAndLevel();
        final String password = "test";
        player.setPassword(password);
        final Wallet wallet = new Wallet(player);
        player.setWallet(wallet);
        final PlayerLimitation playerLimitation = beforeTest_updatePlayerLimitation(player);
        playerLimitation.setBetTimeUnit(TimeUnit.MONTH);
        playerLimitation.setBetLimit(new Money(300L));

        final Limit limit = new Limit(LimitationType.BET_AMOUNT, TimeUnit.DAY, new Money(9L));
        final List<Limit> limits = new ArrayList<>();
        limits.add(limit);

        when(passwordEncoder.matches(password, player.getPassword())).thenReturn(true);
        playerServiceImpl.updatePlayerLimitation(player.getId(), limits, null, password);

        assertThat(playerLimitation.getBetLimit().getCents(), is(new Money(9L).getCents()));
    }

    @Test(expected = PlayerLimitationException.class)
    public void updatePlayerLimitation_fromMonthToDayHarsher() {
        final Player player = createPlayerWithWalletAndLevel();
        final String password = "test";
        player.setPassword(password);
        final PlayerLimitation playerLimitation = beforeTest_updatePlayerLimitation(player);
        playerLimitation.setBetTimeUnit(TimeUnit.MONTH);
        playerLimitation.setBetLimit(new Money(300L));

        final Limit limit = new Limit(LimitationType.BET_AMOUNT, TimeUnit.DAY, new Money(11L));
        final List<Limit> limits = new ArrayList<>();
        limits.add(limit);

        when(passwordEncoder.matches(password, player.getPassword())).thenReturn(true);

        playerServiceImpl.updatePlayerLimitation(player.getId(), limits, null, password);
    }

    @Test
    public void updatePlayerLimitation_fromDayToMonth() {
        final Player player = createPlayerWithWalletAndLevel();
        final String password = "test";
        player.setPassword(password);
        final Wallet wallet = new Wallet(player);
        player.setWallet(wallet);

        final PlayerLimitation playerLimitation = beforeTest_updatePlayerLimitation(player);
        playerLimitation.setBetLimit(new Money(50L));

        final Limit limit = new Limit(LimitationType.BET_AMOUNT, TimeUnit.MONTH, new Money(1500L));
        final List<Limit> limits = new ArrayList<>();
        limits.add(limit);

        when(passwordEncoder.matches(password, player.getPassword())).thenReturn(true);

        playerServiceImpl.updatePlayerLimitation(player.getId(), limits, null, password);

        assertThat(playerLimitation.getBetLimit().getCents(), is(new Money(1500L).getCents()));
    }

    @Test(expected = PlayerLimitationException.class)
    public void updatePlayerLimitation_fromDayToMonthHarsher() {
        final Player player = createPlayerWithWalletAndLevel();
        final String password = "test";
        player.setPassword(password);
        final PlayerLimitation playerLimitation = beforeTest_updatePlayerLimitation(player);
        playerLimitation.setBetLimit(new Money(50L));

        final Limit limit = new Limit(LimitationType.BET_AMOUNT, TimeUnit.MONTH, new Money(1501L));
        final List<Limit> limits = new ArrayList<>();
        limits.add(limit);

        when(passwordEncoder.matches(password, player.getPassword())).thenReturn(true);

        playerServiceImpl.updatePlayerLimitation(player.getId(), limits, null, password);
    }

    @Test(expected = PlayerLimitationException.class)
    public void setSessionLengthLimitation_throwExceptionIfCurrentValueIsHarsher() {
        final Player player = createPlayerWithWalletAndLevel();
        player.setBlockType(BlockType.UNBLOCKED);

        final PlayerLimitation playerLimitation = createPlayerLimitation(player);
        playerLimitation.setSessionLength(10);
        when(playerLimitationRepository.findByPlayer(player)).thenReturn(playerLimitation);

        playerServiceImpl.setSessionLengthLimitation(playerLimitation, 20);
    }

    @Test
    public void setSessionLengthLimitation_setItIfNewValueIsHarsher() {
        final Player player = createPlayerWithWalletAndLevel();
        player.setBlockType(BlockType.UNBLOCKED);

        final PlayerLimitation playerLimitation = createPlayerLimitation(player);
        playerLimitation.setSessionLength(60);
        when(playerLimitationRepository.findByPlayer(player)).thenReturn(playerLimitation);

        playerServiceImpl.setSessionLengthLimitation(playerLimitation, 30);
        assertThat(playerLimitation.getSessionLength(), is(30));

        // same value also can be set
        playerServiceImpl.setSessionLengthLimitation(playerLimitation, 30);
        assertThat(playerLimitation.getSessionLength(), is(30));
    }

    @Test
    public void checkPlayedTime_returnTrueIfPlayerTimeIsOver() {
        final Player player = createPlayerWithWalletAndLevel();
        player.setId(100L);

        when(playerRepository.findOne(100L)).thenReturn(player);
        when(gameService.isSessionTimeOver(player)).thenReturn(true);
        ReflectionTestUtils.setField(playerServiceImpl, "lockTimeForExceedingSessionLengthInMinutes", 5);

        assertThat(playerServiceImpl.checkSessionTime(100L), is(true));
    }

    @Test
    @Ignore
    public void checkPlayedTime_IfPlayerTimeIsOverBlockPlayer() {
        final Player player = createPlayerWithWalletAndLevel();
        player.setId(100L);

        when(playerRepository.findOne(100L)).thenReturn(player);
        when(gameService.isSessionTimeOver(player)).thenReturn(true);
        ReflectionTestUtils.setField(playerServiceImpl, "lockTimeForExceedingSessionLengthInMinutes", 5);

        try {
            playerServiceImpl.checkSessionTime(100L);
        } catch (final BlockedPlayerException ignore) {
        }

        assertThat(player.getBlockType(), is(BlockType.SESSION_LENGTH));
    }

    @Test
    public void getFullPlayerLimitation_() {
        final Player player = createPlayerWithWalletAndLevel();
        final Wallet wallet = player.getWallet();
        wallet.setAccumulatedLossAmountByTimeUnit(TimeUnit.DAY, new Money(1000L));
        wallet.setAccumulatedBetAmountByTimeUnit(TimeUnit.DAY, new Money(1000L));

        final PlayerLimitation playerLimitation = createPlayerLimitation(player);
        playerLimitation.setBetLimit(new Money(2000L));
        playerLimitation.setLossLimit(new Money(4000L));
        playerLimitation.setBetTimeUnit(TimeUnit.DAY);
        playerLimitation.setLossTimeUnit(TimeUnit.DAY);

        final Integer expectedBetPercentage = 50;
        final Integer expectedLossPercentage = 25;

        when(playerLimitationRepository.findByPlayer(player)).thenReturn(playerLimitation);

        playerServiceImpl.getFullPlayerLimitation(player);

        assertThat(playerLimitation.getBetPercentage(), is(expectedBetPercentage));
        assertThat(playerLimitation.getLossPercentage(), is(expectedLossPercentage));
    }
}
