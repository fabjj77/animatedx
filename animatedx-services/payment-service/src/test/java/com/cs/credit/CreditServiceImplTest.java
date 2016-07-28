package com.cs.credit;

import com.cs.audit.AuditService;
import com.cs.avatar.Level;
import com.cs.bonus.Bonus;
import com.cs.bonus.BonusService;
import com.cs.bonus.PlayerBonus;
import com.cs.payment.Money;
import com.cs.payment.PaymentAmountException;
import com.cs.payment.credit.CreditServiceImpl;
import com.cs.payment.credit.CreditTransactionRepository;
import com.cs.persistence.Country;
import com.cs.player.Address;
import com.cs.player.Player;
import com.cs.player.PlayerService;
import com.cs.player.Verification;
import com.cs.player.Wallet;
import com.cs.player.WalletRepository;
import com.cs.promotion.PlayerCriteria;

import org.springframework.test.util.ReflectionTestUtils;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static com.cs.payment.Currency.EUR;
import static com.cs.persistence.Status.ACTIVE;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * @author Hadi Movaghar
 */
public class CreditServiceImplTest {

    @InjectMocks
    private CreditServiceImpl creditServiceImpl;

    @Mock
    private AuditService auditService;

    @Mock
    private BonusService bonusService;

    @Mock
    private CreditTransactionRepository creditTransactionRepository;

    @Mock
    private PlayerService playerService;

    @Mock
    private WalletRepository walletRepository;

    @Before
    public void setup() {
        initMocks(this);
        createPlayer();

        ReflectionTestUtils.setField(creditServiceImpl, "bonusConversionGoalMultiplier", 38.0D);
    }

    private Player createPlayer() {
        final Player player = new Player();
        final Wallet wallet = new Wallet(player);
        player.setWallet(wallet);
        player.setCurrency(EUR);
        player.setId(10L);
        player.setPlayerVerification(Verification.VERIFIED);
        return player;
    }

    private Address createAddress() {
        final Address address = new Address();
        address.setStreet("Street");
        address.setCity("City");
        address.setZipCode("123456");
        address.setCountry(Country.SWEDEN);
        return address;
    }

    private Level createLevel() {
        final Level newLevel = new Level();
        newLevel.setMoneyCreditRate(0.5D);
        newLevel.setBonusCreditRate(1.5D);
        newLevel.setLevel(5L);
        return newLevel;
    }

    private Wallet createWallet() {
        final Wallet newWallet = new Wallet();
        newWallet.setMoneyBalance(new Money(1000L));
        newWallet.setCreditsBalance(10);
        return newWallet;
    }

    private Player createPlayerWithWalletAndLevel() {
        final Player newPlayer = new Player();
        newPlayer.setEmailAddress("exist@test.com");
        newPlayer.setStatus(ACTIVE);
        newPlayer.setAddress(createAddress());
        newPlayer.setFirstName("First");
        newPlayer.setLastName("Last");
        newPlayer.setNickname("Nick");
        newPlayer.setWallet(createWallet());
        newPlayer.setLevel(createLevel());
        return newPlayer;
    }

    @Test
    public void convertCreditsRealMoney_ShouldReturnCalculatedConvertedAmount() {
        final Player playerWithWallet = createPlayerWithWalletAndLevel();
        final Integer creditAmount = 10;

        final Integer expectedCredit = playerWithWallet.getWallet().getCreditsBalance() - creditAmount;
        final Money expectedRealMoney = playerWithWallet.getWallet().getMoneyBalance().add(
                creditServiceImpl.calculateCredits(creditAmount, playerWithWallet.getLevel().getMoneyCreditRate()));

        when(playerService.getPlayer(anyLong())).thenReturn(playerWithWallet);

        creditServiceImpl.convertCreditsToRealMoney(playerWithWallet, creditAmount);

        assertThat(playerWithWallet.getWallet().getCreditsBalance(), is(expectedCredit));
        assertThat(playerWithWallet.getWallet().getMoneyBalance(), is(expectedRealMoney));
    }

    @Test(expected = PaymentAmountException.class)
    public void convertCreditsRealMoney_ShouldThrowPaymentAmountException() {
        final Player playerWithWallet = createPlayerWithWalletAndLevel();
        final Integer creditAmount = 1000;

        when(playerService.getPlayer(anyLong())).thenReturn(playerWithWallet);

        creditServiceImpl.convertCreditsToRealMoney(playerWithWallet, creditAmount);
    }

    @Test
    public void convertCreditsBonusMoney_ShouldReturnCalculatedConvertedAmount() {
        ReflectionTestUtils.setField(creditServiceImpl, "bonusConversionGoalMultiplier", 38.0D);

        final Player playerWithWallet = createPlayerWithWalletAndLevel();
        final Integer creditAmount = 1;

        final Integer expectedCredit = playerWithWallet.getWallet().getCreditsBalance() - creditAmount;
        final Money expectedBonusMoney = playerWithWallet.getWallet().getBonusBalance().add(
                creditServiceImpl.calculateCredits(creditAmount, playerWithWallet.getLevel().getBonusCreditRate()));

        final Bonus conversionBonus = new Bonus();
        conversionBonus.setAmount(Money.ZERO);
        when(bonusService.getCreditsConversionBonus()).thenReturn(conversionBonus);
        final PlayerBonus playerBonus = new PlayerBonus();
        playerBonus.setUsedAmount(Money.ZERO);

        when(bonusService.useBonus(eq(playerWithWallet), eq(conversionBonus), any(PlayerCriteria.class))).thenReturn(playerBonus);
        when(playerService.getPlayer(anyLong())).thenReturn(playerWithWallet);

        creditServiceImpl.convertCreditsToBonusMoney(playerWithWallet, creditAmount);

        assertThat(playerWithWallet.getWallet().getCreditsBalance(), is(expectedCredit));
        assertThat(playerBonus.getCurrentBalance(), is(expectedBonusMoney));
    }

    @Test(expected = PaymentAmountException.class)
    public void convertCreditsBonusMoney_ShouldThrowPaymentAmountException() {
        final Player playerWithWallet = createPlayerWithWalletAndLevel();
        final Integer creditAmount = 1000;

        when(playerService.getPlayer(anyLong())).thenReturn(playerWithWallet);

        creditServiceImpl.convertCreditsToBonusMoney(playerWithWallet, creditAmount);
    }

    @Test
    public void calculateCredits_ShouldReturnCalculatedCredits() {
        final Player player = createPlayerWithWalletAndLevel();
        final Wallet wallet = player.getWallet();
        final Money expectedAmount = new Money(wallet.getCreditsBalance() * player.getLevel().getMoneyCreditRate());

        final Money calculateCredits = creditServiceImpl.calculateCredits(wallet.getCreditsBalance(), player.getLevel().getMoneyCreditRate());

        assertThat(calculateCredits, is(expectedAmount));
    }
}
