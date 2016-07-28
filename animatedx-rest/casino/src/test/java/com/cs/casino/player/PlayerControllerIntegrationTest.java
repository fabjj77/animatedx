package com.cs.casino.player;

import com.cs.avatar.Avatar;
import com.cs.avatar.AvatarBaseType;
import com.cs.avatar.HairColor;
import com.cs.avatar.Level;
import com.cs.avatar.SkinColor;
import com.cs.payment.Money;
import com.cs.persistence.NotFoundException;
import com.cs.persistence.Status;
import com.cs.player.Address;
import com.cs.player.BlockType;
import com.cs.player.Player;
import com.cs.player.PlayerService;
import com.cs.player.TrustLevel;
import com.cs.player.Wallet;
import com.cs.rest.status.NotFoundMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.math.BigDecimal;
import java.util.Date;

import static com.cs.payment.Currency.EUR;
import static com.cs.persistence.Country.SWEDEN;
import static com.cs.persistence.Language.SWEDISH;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * @author Joakim Gottzén
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {TestConfig.class, PlayerController.class})
public class PlayerControllerIntegrationTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;
    @Autowired
    private PlayerService playerService;
    @Autowired
    private ObjectMapper mapper;

    @Before
    public void setup() {
        mockMvc = webAppContextSetup(context).build();
    }

    private Player createPlayerWithAvatarAndWallet() {
        final Player player = new Player();
        player.setId(0L);
        player.setWallet(createWallet(player));
        player.setFirstName("First");
        player.setLastName("Last");
        player.setEmailAddress("email@email.com");
        player.setPassword("$2a$10$9UhOj9mS294sxq0NyIwMb.N5fYvJNRCyGDbI3RD0xF8W1AXs3aRyG");
        player.setNickname("Nick");
        player.setBirthday(new Date(20120101));
        player.setStatus(Status.ACTIVE);
        player.setAddress(createAddress());

        final AvatarBaseType avatarBaseType = new AvatarBaseType();
        avatarBaseType.setId(1);

        final Avatar avatar = new Avatar(1L);
        avatar.setHairColor(HairColor.DARK);
        avatar.setSkinColor(SkinColor.DARK);
        avatar.setPictureUrl("");
        avatar.setAvatarBaseType(avatarBaseType);
        avatar.setLevel(new Level(1L));

        final Level level = new Level();
        level.setLevel(1L);
        level.setDepositBonusPercentage(new BigDecimal("10"));
        level.setCashbackPercentage(new BigDecimal("2"));
        level.setTurnover(new Money(3L));

        player.setAvatar(avatar);
        player.setLevel(level);
        return player;
    }

    private Wallet createWallet(final Player player) {
        return new Wallet(player);
    }

    private Address createAddress() {
        final Address address = new Address();
        address.setStreet("Street");
        address.setZipCode("123456");
        address.setCity("City");
        address.setCountry(SWEDEN);
        return address;
    }

    private Level createLevel() {
        final Level level = new Level();
        level.setLevel(1L);
        level.setCashbackPercentage(new BigDecimal("100"));
        level.setCreditDices((short) 1);
        level.setDepositBonusPercentage(new BigDecimal(10));
        level.setTurnover(new Money(100L));
        return level;
    }

    private Avatar createAvatar() {
        final Avatar avatar = new Avatar(1L);
        avatar.setAvatarBaseType(new AvatarBaseType(1));
        avatar.setLevel(new Level(1L));
        avatar.setSkinColor(SkinColor.DARK);
        avatar.setHairColor(HairColor.DARK);
        return avatar;
    }

    private CasinoPlayerDto createPlayerDto() {
        final Player player = new Player("First", "Last", "email@email.com", "AbaAbaAba123", "Nickname", new Date(), createAvatar(), createLevel(), createAddress(), EUR,
                                         "123-456-789", Status.ACTIVE, TrustLevel.GREEN, BlockType.UNBLOCKED, SWEDISH);
        player.setWallet(new Wallet(player));

        return new CasinoPlayerDto(player);
    }

    @Test
    @Ignore
    public void getPlayer_ShouldReturn404WhenPlayerDoesNotExist()
            throws Exception {
        final long id = 0;
        final NotFoundException notFoundException = new NotFoundException(id);
        final String expectedContent = mapper.writeValueAsString(NotFoundMessage.of(notFoundException));

        when(playerService.getPlayer(anyLong())).thenThrow(notFoundException);

        mockMvc.perform(
                get("/api/players/{id}", id).contentType(APPLICATION_JSON).accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(expectedContent));
    }

    @Test
    @Ignore
    public void getPlayer_ShouldReturnExistingPlayer()
            throws Exception {
        final Player player = createPlayerWithAvatarAndWallet();

        when(playerService.getPlayer(anyLong())).thenReturn(player);

        mockMvc.perform(
                get("/api/players/{id}", player.getId()).contentType(APPLICATION_JSON).accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(player.getId().intValue())))
                .andExpect(jsonPath("$.firstName", is(player.getFirstName())))
                .andExpect(jsonPath("$.lastName", is(player.getLastName())));
    }

    @Test
    @Ignore
    public void createPlayer_ShouldReturnCreatedPlayer()
            throws Exception {
        final CasinoPlayerDto playerDto = createPlayerDto();
        final Player playerDtoAsPlayer = playerDto.asPlayer();
        playerDtoAsPlayer.setLevel(createLevel());
        playerDtoAsPlayer.getAvatar().setLevel(new Level(1L));

        when(playerService.createPlayer(any(Player.class), null, "bTag", "109.228.157.98")).thenReturn(playerDtoAsPlayer);
        when(playerService.getPlayer(anyLong())).thenReturn(playerDtoAsPlayer);

        final String sentContent = mapper.writeValueAsString(playerDto);
        final String expectedContent = mapper.writeValueAsString(new CasinoPlayerDto(playerDtoAsPlayer));

        mockMvc.perform(post("/api/players/").content(sentContent).contentType(APPLICATION_JSON).accept(APPLICATION_JSON).header("X-Forwarded-For", "127.0.0.1"))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().string(expectedContent));
    }
}
