package com.cs.payment;

import com.cs.audit.AuditService;
import com.cs.avatar.Level;
import com.cs.bonus.BonusService;
import com.cs.comment.PlayerCommentRepository;
import com.cs.payment.adyen.AdyenException;
import com.cs.payment.adyen.AdyenService;
import com.cs.payment.adyen.AdyenTransactionNotification;
import com.cs.payment.credit.CreditTransactionRepository;
import com.cs.payment.transaction.PaymentTransactionFacade;
import com.cs.persistence.CommunicationException;
import com.cs.player.Player;
import com.cs.player.PlayerRepository;
import com.cs.player.PlayerService;
import com.cs.player.PlayerUuid;
import com.cs.player.PlayerUuidRepository;
import com.cs.player.Verification;
import com.cs.player.Wallet;
import com.cs.player.WalletRepository;
import com.cs.promotion.PromotionService;
import com.cs.user.User;

import org.springframework.test.util.ReflectionTestUtils;

import com.adyen.modification.ModificationResult;
import com.adyen.payout.ModifyResponse;
import com.adyen.payout.SubmitResponse;
import com.adyen.recurring.ArrayOfRecurringDetail;
import com.adyen.recurring.RecurringDetail;
import com.adyen.recurring.RecurringDetailsResult;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import java.util.Date;
import java.util.EnumSet;
import java.util.GregorianCalendar;

import static com.cs.payment.Currency.EUR;
import static com.cs.payment.EventCode.AUTHORISATION;
import static com.cs.payment.EventCode.CANCELLATION;
import static com.cs.payment.EventCode.CHARGEBACK;
import static com.cs.payment.EventCode.CHARGEBACK_REVERSED;
import static com.cs.payment.EventCode.NOTIFICATION_OF_CHARGEBACK;
import static com.cs.payment.EventCode.REFUND;
import static com.cs.payment.EventCode.REFUND_FAILED;
import static com.cs.payment.PaymentStatus.AWAITING_APPROVAL;
import static com.cs.payment.PaymentStatus.AWAITING_PAYMENT;
import static com.cs.payment.PaymentStatus.CHARGEBACKED;
import static com.cs.payment.PaymentStatus.FAILURE;
import static com.cs.payment.PaymentStatus.REFUNDED;
import static com.cs.payment.PaymentStatus.SENDING_FAILURE;
import static com.cs.payment.PaymentStatus.SUCCESS;
import static com.cs.persistence.Status.ACTIVE;
import static com.cs.player.BlockType.UNBLOCKED;
import static com.cs.player.TrustLevel.GREEN;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * @author Hadi Movaghar
 */
public class PaymentServiceImplTest {

    @InjectMocks
    private PaymentServiceImpl paymentServiceImpl;

    @Mock
    private AdyenService adyenService;
    @Mock
    private AuditService auditService;
    @Mock
    private BonusService bonusService;
    @Mock
    private CreditTransactionRepository creditTransactionRepository;
    @Mock
    private PaymentMethodRepository paymentMethodRepository;
    @Mock
    private PlayerCommentRepository playerCommentRepository;
    @Mock
    private PlayerRepository playerRepository;
    @Mock
    private PlayerService playerService;
    @Mock
    private PlayerUuidRepository playerUuidRepository;
    @Mock
    private PromotionService promotionService;
    @Mock
    private ProviderRepository providerRepository;
    @Mock
    private PaymentTransactionFacade paymentTransactionFacade;
    @Mock
    private WalletRepository walletRepository;

    @Before
    public void setup() {
        initMocks(this);
        createPlayer();

        ReflectionTestUtils.setField(paymentServiceImpl, "bonusConversionGoalMultiplier", 38.0D);
    }

    private Player createPlayer() {
        final Player player = new Player();
        final Wallet wallet = new Wallet(player);
        player.setWallet(wallet);
        player.setCurrency(EUR);
        player.setId(10L);
        player.setPlayerVerification(Verification.VERIFIED);
        player.setLevel(new Level(1L));
        return player;
    }

    private Provider createProvider() {
        return new Provider("Adyen", ACTIVE, new User(1L));
    }

    private PaymentTransaction createPaymentTransaction(final Player player) {
        return new PaymentTransaction(player, createProvider(), new Date(), EUR, "[providerReference]", "[originalReference]", SUCCESS, "paymentMethod",
                                      EnumSet.noneOf(Operation.class), "reason", Money.ZERO, AUTHORISATION, "uuid", new Date());
    }

    @Test
    public void _isNotificationDuplicated_returnsFalseIfCanNotFindPaymentTransaction() {
        when(paymentTransactionFacade.getPayment("[providerReference]", AUTHORISATION, SUCCESS)).thenReturn(null);

        assertThat(paymentServiceImpl.isNotificationDuplicated("[providerReference]", AUTHORISATION, SUCCESS), is(false));
    }

    @Test
    public void _isNotificationDuplicated_returnsTrueIfPaymentStatusBothAreSUCCESS() {
        final Player player = createPlayer();
        final PaymentTransaction paymentTransaction = createPaymentTransaction(player);

        when(paymentTransactionFacade.getPayment("[providerReference]", AUTHORISATION, SUCCESS))
                .thenReturn(paymentTransaction);

        assertThat(paymentServiceImpl.isNotificationDuplicated("[providerReference]", AUTHORISATION, SUCCESS), is(true));
    }

    @Test
    public void _isNotificationDuplicated_returnsFalseIfPaymentStatusEqualsFAILUREAndPassedPaymentStatusIsSUCCESS() {
        final Player player = createPlayer();
        final PaymentTransaction paymentTransaction = createPaymentTransaction(player);
        paymentTransaction.setPaymentStatus(FAILURE);

        when(paymentTransactionFacade.getPayment("[providerReference]", AUTHORISATION, SUCCESS))
                .thenReturn(paymentTransaction);

        assertThat(paymentServiceImpl.isNotificationDuplicated("[providerReference]", AUTHORISATION, SUCCESS), is(false));
    }

    @Test
    public void _isNotificationDuplicated_returnsTrueIfPaymentStatusBothAreFAILURE() {
        final Player player = createPlayer();
        final PaymentTransaction paymentTransaction = createPaymentTransaction(player);
        paymentTransaction.setPaymentStatus(FAILURE);

        when(paymentTransactionFacade.getPayment("[providerReference]", AUTHORISATION, FAILURE))
                .thenReturn(paymentTransaction);

        assertThat(paymentServiceImpl.isNotificationDuplicated("[providerReference]", AUTHORISATION, FAILURE), is(true));
    }

    private void beforeTest_processPayment(final Player player, final AdyenTransactionNotification transaction, final PaymentTransaction paymentTransaction) {
        player.getWallet().setMoneyBalance(new Money(10L));
        transaction.setProviderReference("[providerReferenceForPayment]");
        transaction.setMerchantReference("067e6162-3b6f-4ae2-a171-2470b63dff00");
        transaction.setEventCode(AUTHORISATION);
        transaction.setCurrency(EUR);
        transaction.setPaymentStatus(SUCCESS);
        transaction.setAmount(new Money(1000L));
        final PlayerUuid playerUuid = new PlayerUuid();
        playerUuid.setPlayer(player);
        player.setTrustLevel(GREEN);

        final Provider provider = createProvider();
        when(providerRepository.findOne(1)).thenReturn(provider);
        when(paymentTransactionFacade.insertPayment(player, transaction, provider)).thenReturn(paymentTransaction);

        when(playerUuidRepository.findOne(transaction.getMerchantReference())).thenReturn(playerUuid);
    }

    @Test
    public void processPayment_transactionAmountAddedToMoneyBalanceAfterSuccessfulPayment() {
        final Player player = createPlayer();
        final AdyenTransactionNotification transaction = new AdyenTransactionNotification();
        final PaymentTransaction paymentTransaction = createPaymentTransaction(player);
        beforeTest_processPayment(player, transaction, paymentTransaction);
        final Wallet wallet = player.getWallet();

        paymentServiceImpl.processDeposit(transaction);

        assertThat(wallet.getMoneyBalance(), is(new Money(1010L)));
        assertThat(player.getTrustLevel(), is(GREEN));
        assertThat(wallet.getAccumulatedDeposit(), is(transaction.getAmount()));
    }

    @Test
    public void processPayment_flagPlayersTrustLevelRedIfDepositAmountExceedsTheMaximum() {
        final Player player = createPlayer();
        final AdyenTransactionNotification transaction = new AdyenTransactionNotification();
        final PaymentTransaction paymentTransaction = createPaymentTransaction(player);
        beforeTest_processPayment(player, transaction, paymentTransaction);
        final Wallet wallet = player.getWallet();
        wallet.setMoneyBalance(new Money(100000L));
        transaction.setAmount(new Money(230100L)); // maximum deposit amount is 230000 (cents with new Money)

        paymentServiceImpl.processDeposit(transaction);

        assertThat(wallet.getMoneyBalance(), is(new Money(330100L)));
        assertThat(wallet.getAccumulatedDeposit(), is(transaction.getAmount()));
    }

    @Test
    public void processPayment_transactionAmountDoesNotAddToMoneyBalanceAfterFailedPayment() {
        final Player player = createPlayer();
        final AdyenTransactionNotification transaction = new AdyenTransactionNotification();
        final PaymentTransaction paymentTransaction = createPaymentTransaction(player);
        beforeTest_processPayment(player, transaction, paymentTransaction);
        final Wallet wallet = player.getWallet();
        transaction.setPaymentStatus(FAILURE);

        paymentServiceImpl.processDeposit(transaction);

        assertThat(wallet.getMoneyBalance(), is(new Money(10L)));
        assertThat(wallet.getAccumulatedDeposit(), is(Money.ZERO));
    }

    @Test
    public void processPayment_transactionAmountDoesNotAddToMoneyBalanceWhenCorrespondingPlayerUuidNotFound() {
        final Player player = createPlayer();
        final AdyenTransactionNotification transaction = new AdyenTransactionNotification();
        final PaymentTransaction paymentTransaction = createPaymentTransaction(player);
        beforeTest_processPayment(player, transaction, paymentTransaction);
        final Wallet wallet = player.getWallet();

        when(playerUuidRepository.findOne(transaction.getMerchantReference())).thenReturn(null);

        paymentServiceImpl.processDeposit(transaction);

        assertThat(wallet.getMoneyBalance(), is(new Money(10L)));
        assertThat(wallet.getAccumulatedDeposit(), is(Money.ZERO));
    }

    @Test
    @Ignore("We don't use multiple currencies anymore")
    public void processPayment_transactionAmountDoesNotAddToMoneyBalanceWhenThereIsCurrencyMismatch() {
        final Player player = createPlayer();
        final AdyenTransactionNotification transaction = new AdyenTransactionNotification();
        final PaymentTransaction paymentTransaction = createPaymentTransaction(player);
        beforeTest_processPayment(player, transaction, paymentTransaction);
        final Wallet wallet = player.getWallet();

        paymentServiceImpl.processDeposit(transaction);

        assertThat(wallet.getMoneyBalance(), is(new Money(10L)));
    }

    private void beforeTest_processRefund(final Player player, final AdyenTransactionNotification transaction, final PaymentTransaction originalPaymentTransaction,
                                          final PaymentTransaction paymentTransaction) {
        final Wallet wallet = player.getWallet();
        wallet.setMoneyBalance(new Money(10L));
        wallet.setReservedBalance(new Money(5000L));
        transaction.setProviderReference("[providerReferenceForRefund]");
        transaction.setOriginalReference("[originalProviderReference]");
        transaction.setEventCode(REFUND);
        transaction.setCurrency(EUR);
        transaction.setPaymentStatus(SUCCESS);
        transaction.setAmount(new Money(5000L));

        when(paymentTransactionFacade.getPayment("[originalProviderReference]", AUTHORISATION, SUCCESS)).thenReturn(originalPaymentTransaction);
        paymentTransaction.setOriginalReference("[originalProviderReference]");
        paymentTransaction.setProviderReference("[providerReferenceForRefund]");
        paymentTransaction.setAmount(new Money(5000L));
        paymentTransaction.setEventCode(REFUND);
        paymentTransaction.setPaymentStatus(AWAITING_PAYMENT);

        when(paymentTransactionFacade.getPayment("[providerReferenceForRefund]")).thenReturn(paymentTransaction);
    }

    private PaymentTransaction createOriginalPaymentTransaction(final Player player) {
        final PaymentTransaction originalPaymentTransaction;
        originalPaymentTransaction = new PaymentTransaction(player, createProvider(), new Date(), EUR, "null", null, SUCCESS, "[paymentMethod]",
                                                            EnumSet.noneOf(Operation.class), "[reason]", new Money(5000L), AUTHORISATION, "[uuid]", new Date());
        originalPaymentTransaction.setProviderReference("[originalProviderReference]");
        return originalPaymentTransaction;
    }

    @Test
    public void processRefund_withdrawFromReservedBalanceWhenPaymentMessageAvailable() {
        final Player player = createPlayer();
        final AdyenTransactionNotification transaction = new AdyenTransactionNotification();
        final PaymentTransaction originalPaymentTransaction = createOriginalPaymentTransaction(player);
        final PaymentTransaction paymentTransaction = createPaymentTransaction(player);
        beforeTest_processRefund(player, transaction, originalPaymentTransaction, paymentTransaction);
        final Wallet wallet = player.getWallet();
        wallet.setAccumulatedDeposit(transaction.getAmount());

        paymentServiceImpl.processRefund(transaction);

        assertThat(wallet.getMoneyBalance(), is(new Money(10L)));
        assertThat(wallet.getReservedBalance(), is(Money.ZERO));
        assertThat(wallet.getAccumulatedDeposit(), is(Money.ZERO));
    }

    @Test
    public void processRefund_withdrawFromMoneyBalanceWhenInsufficientReservedBalanceWhenPaymentMessageAvailable() {
        final Player player = createPlayer();
        final AdyenTransactionNotification transaction = new AdyenTransactionNotification();
        final PaymentTransaction originalPaymentTransaction = createOriginalPaymentTransaction(player);
        final PaymentTransaction paymentTransaction = createPaymentTransaction(player);
        beforeTest_processRefund(player, transaction, originalPaymentTransaction, paymentTransaction);
        final Wallet wallet = player.getWallet();
        wallet.setAccumulatedDeposit(transaction.getAmount());
        wallet.setMoneyBalance(new Money(6000L));
        wallet.setReservedBalance(new Money(1000L));

        paymentServiceImpl.processRefund(transaction);

        assertThat(wallet.getMoneyBalance(), is(new Money(2000L)));
        assertThat(wallet.getReservedBalance(), is(Money.ZERO));
        assertThat(wallet.getAccumulatedDeposit(), is(Money.ZERO));
    }

    @Test
    public void processRefund_negativeMoneyBalanceWhenInsufficientReservedAndMoneyBalanceWhenPaymentMessageAvailable() {
        final Player player = createPlayer();
        final AdyenTransactionNotification transaction = new AdyenTransactionNotification();
        final PaymentTransaction originalPaymentTransaction = createOriginalPaymentTransaction(player);
        final PaymentTransaction paymentTransaction = createPaymentTransaction(player);
        beforeTest_processRefund(player, transaction, originalPaymentTransaction, paymentTransaction);
        final Wallet wallet = player.getWallet();
        wallet.setMoneyBalance(Money.ZERO);
        wallet.setReservedBalance(new Money(1000L));
        wallet.setAccumulatedDeposit(transaction.getAmount());

        paymentServiceImpl.processRefund(transaction);

        assertThat(wallet.getMoneyBalance(), is(new Money(-4000L)));
        assertThat(wallet.getReservedBalance(), is(Money.ZERO));
        assertThat(wallet.getAccumulatedDeposit(), is(Money.ZERO));
    }

    @Test
    public void processRefund_rollbackOnReservedBalanceWhenTransactionStatusIsFailureAndPaymentMessageAvailable() {
        final Player player = createPlayer();
        final AdyenTransactionNotification transaction = new AdyenTransactionNotification();
        final PaymentTransaction originalPaymentTransaction = createOriginalPaymentTransaction(player);
        final PaymentTransaction paymentTransaction = createPaymentTransaction(player);
        beforeTest_processRefund(player, transaction, originalPaymentTransaction, paymentTransaction);
        final Wallet wallet = player.getWallet();
        wallet.setMoneyBalance(Money.ZERO);
        wallet.setReservedBalance(new Money(5000L));
        transaction.setPaymentStatus(FAILURE);

        paymentServiceImpl.processRefund(transaction);

        assertThat(wallet.getMoneyBalance(), is(new Money(5000L)));
        assertThat(wallet.getReservedBalance(), is(Money.ZERO));
    }

    @Test
    public void processRefund_rollbackOnReservedBalanceAndAddOnlyReservedBalanceToMoneyBalanceWhenTransactionStatusIsFailureAndPaymentMessageAvailable() {
        final Player player = createPlayer();
        final AdyenTransactionNotification transaction = new AdyenTransactionNotification();
        final PaymentTransaction originalPaymentTransaction = createOriginalPaymentTransaction(player);
        final PaymentTransaction paymentTransaction = createPaymentTransaction(player);
        beforeTest_processRefund(player, transaction, originalPaymentTransaction, paymentTransaction);
        final Wallet wallet = player.getWallet();
        wallet.setMoneyBalance(Money.ZERO);
        wallet.setReservedBalance(new Money(2000L));
        transaction.setPaymentStatus(FAILURE);

        paymentServiceImpl.processRefund(transaction);

        assertThat(wallet.getMoneyBalance(), is(new Money(2000L)));
        assertThat(wallet.getReservedBalance(), is(new Money(-3000L)));
    }

    @Test
    public void processRefund_withdrawFromMoneyBalanceWhenPaymentMessageNotAvailable() {
        final Player player = createPlayer();
        final AdyenTransactionNotification transaction = new AdyenTransactionNotification();
        final PaymentTransaction originalPaymentTransaction = createOriginalPaymentTransaction(player);
        final PaymentTransaction paymentTransaction = createPaymentTransaction(player);
        beforeTest_processRefund(player, transaction, originalPaymentTransaction, paymentTransaction);
        final Wallet wallet = player.getWallet();
        wallet.setMoneyBalance(new Money(1000L));

        when(paymentTransactionFacade.getPayment("[providerReferenceForRefund]")).thenReturn(null);

        paymentServiceImpl.processRefund(transaction);

        assertThat(wallet.getMoneyBalance(), is(new Money(-4000L)));
        assertThat(wallet.getReservedBalance(), is(new Money(5000L)));
    }

    @Test
    public void processRefund_rollbackOnMoneyBalanceWhenTransactionStatusIsFailureAndPaymentMessageNotAvailable() {
        final Player player = createPlayer();
        final AdyenTransactionNotification transaction = new AdyenTransactionNotification();
        final PaymentTransaction originalPaymentTransaction = createOriginalPaymentTransaction(player);
        final PaymentTransaction paymentTransaction = createPaymentTransaction(player);
        beforeTest_processRefund(player, transaction, originalPaymentTransaction, paymentTransaction);
        final Wallet wallet = player.getWallet();
        wallet.setMoneyBalance(new Money(1000L));
        transaction.setPaymentStatus(FAILURE);

        when(paymentTransactionFacade.getPayment("[providerReferenceForRefund]")).thenReturn(null);

        paymentServiceImpl.processRefund(transaction);

        assertThat(wallet.getMoneyBalance(), is(new Money(6000L)));
        assertThat(wallet.getReservedBalance(), is(new Money(5000L)));
    }

    @Test
    public void processRefund_doNotRefundIfOriginalReferenceNotFound() {
        final Player player = createPlayer();
        final AdyenTransactionNotification transaction = new AdyenTransactionNotification();
        final PaymentTransaction originalPaymentTransaction = createOriginalPaymentTransaction(player);
        final PaymentTransaction paymentTransaction = createPaymentTransaction(player);
        beforeTest_processRefund(player, transaction, originalPaymentTransaction, paymentTransaction);
        final Wallet wallet = player.getWallet();

        when(paymentTransactionFacade.getPayment("[originalProviderReference]", AUTHORISATION, SUCCESS)).thenReturn(null);
        paymentServiceImpl.processRefund(transaction);

        assertThat(wallet.getMoneyBalance(), is(new Money(10L)));
        assertThat(wallet.getReservedBalance(), is(new Money(5000L)));
    }

    @Test
    public void processRefund_doNotRefundWhenOriginalPaymentAmountDoesNotMatchRefundAmount() {
        final Player player = createPlayer();
        final Wallet wallet = player.getWallet();
        final AdyenTransactionNotification transaction = new AdyenTransactionNotification();
        final PaymentTransaction originalPaymentTransaction = createOriginalPaymentTransaction(player);
        final PaymentTransaction paymentTransaction = createPaymentTransaction(player);
        beforeTest_processRefund(player, transaction, originalPaymentTransaction, paymentTransaction);
        originalPaymentTransaction.setAmount(new Money(1000L));
        transaction.setAmount(new Money(2000L));

        paymentServiceImpl.processRefund(transaction);

        assertThat(wallet.getMoneyBalance(), is(new Money(10L)));
        assertThat(wallet.getReservedBalance(), is(new Money(5000L)));
    }

    @Test
    @Ignore("We don't use multiple currencies anymore")
    public void processRefund_doNotRefundWhenMismatchedCurrency() {
        final Player player = createPlayer();
        final AdyenTransactionNotification transaction = new AdyenTransactionNotification();
        final PaymentTransaction originalPaymentTransaction = createOriginalPaymentTransaction(player);
        final PaymentTransaction paymentTransaction = createPaymentTransaction(player);
        beforeTest_processRefund(player, transaction, originalPaymentTransaction, paymentTransaction);
        final Wallet wallet = player.getWallet();

        paymentServiceImpl.processRefund(transaction);

        assertThat(wallet.getMoneyBalance(), is(new Money(10L)));
        assertThat(wallet.getReservedBalance(), is(new Money(5000L)));
    }

    @Test
    public void processRefund_doNotRefundWhenMismatchedAmountWithRefundMessage() {
        final Player player = createPlayer();
        final AdyenTransactionNotification transaction = new AdyenTransactionNotification();
        final PaymentTransaction originalPaymentTransaction = createOriginalPaymentTransaction(player);
        final PaymentTransaction paymentTransaction = createPaymentTransaction(player);
        beforeTest_processRefund(player, transaction, originalPaymentTransaction, paymentTransaction);
        final Wallet wallet = player.getWallet();
        paymentTransaction.setAmount(new Money(888888888L));

        paymentServiceImpl.processRefund(transaction);

        assertThat(wallet.getMoneyBalance(), is(new Money(10L)));
        assertThat(wallet.getReservedBalance(), is(new Money(5000L)));
    }

    @Test
    public void processRefund_doNotRefundWhenRefundMessageWasAlreadyProcessedSuccessfully() {
        final Player player = createPlayer();
        final AdyenTransactionNotification transaction = new AdyenTransactionNotification();
        final PaymentTransaction originalPaymentTransaction = createOriginalPaymentTransaction(player);
        final PaymentTransaction paymentTransaction = createPaymentTransaction(player);
        beforeTest_processRefund(player, transaction, originalPaymentTransaction, paymentTransaction);
        final Wallet wallet = player.getWallet();
        paymentTransaction.setPaymentStatus(SUCCESS);

        paymentServiceImpl.processRefund(transaction);

        assertThat(wallet.getMoneyBalance(), is(new Money(10L)));
        assertThat(wallet.getReservedBalance(), is(new Money(5000L)));
    }

    private void beforeTest_processRefundReversed(final Player player, final AdyenTransactionNotification transaction,
                                                  final PaymentTransaction originalPaymentTransaction, final PaymentTransaction paymentTransaction) {
        final Wallet wallet = player.getWallet();
        wallet.setMoneyBalance(new Money(2000L));
        wallet.setReservedBalance(new Money(2000L));

        transaction.setProviderReference("[providerReferenceForRefundReversed]");
        transaction.setOriginalReference("[originalProviderReference]");
        transaction.setEventCode(REFUND_FAILED);
        transaction.setCurrency(EUR);
        transaction.setPaymentStatus(SUCCESS);
        transaction.setAmount(new Money(2000L));

        when(paymentTransactionFacade.getSucceededOrRefundedPayment(eq(transaction.getOriginalReference()))).thenReturn(originalPaymentTransaction);

        final Provider provider = createProvider();
        when(providerRepository.findOne(1)).thenReturn(provider);

        paymentTransaction.setOriginalReference("[originalProviderReference]");
        paymentTransaction.setProviderReference("[providerReferenceForRefundReversed]");
        paymentTransaction.setAmount(new Money(2000L));
        paymentTransaction.setEventCode(REFUND);
        paymentTransaction.setPaymentStatus(AWAITING_PAYMENT);

        when(paymentTransactionFacade.getPayment("[originalProviderReference]")).thenReturn(paymentTransaction);
    }

    @Test
    public void processRefundReversed_moveReservedBalanceToMoneyBalanceWhenOriginalPaymentStatusIsSuccessAndCorrespondingRefundPaymentMessageStatusIsAwaiting() {
        final Player player = createPlayer();
        final Wallet wallet = player.getWallet();
        final AdyenTransactionNotification transaction = new AdyenTransactionNotification();
        final PaymentTransaction originalPaymentTransaction = createOriginalPaymentTransaction(player);
        originalPaymentTransaction.setAmount(new Money(2000L));
        final PaymentTransaction paymentTransaction = createPaymentTransaction(player);
        beforeTest_processRefundReversed(player, transaction, originalPaymentTransaction, paymentTransaction);
        paymentTransaction.setPaymentStatus(AWAITING_PAYMENT);

        paymentServiceImpl.processRefundReversed(transaction);

        assertThat(wallet.getMoneyBalance(), is(new Money(4000L)));
        assertThat(wallet.getReservedBalance(), is(new Money(0L)));
        assertThat(originalPaymentTransaction.getPaymentStatus(), is(SUCCESS));
        assertThat(paymentTransaction.getPaymentStatus(), is(FAILURE));
    }

    @Test
    public void processRefundReversed_doNotReverseWhenOriginalPaymentStatusIsSuccessAndCorrespondingRefundPaymentMessageStatusIsSuccess() {
        final Player player = createPlayer();
        final Wallet wallet = player.getWallet();
        final AdyenTransactionNotification transaction = new AdyenTransactionNotification();
        final PaymentTransaction originalPaymentTransaction = createOriginalPaymentTransaction(player);
        final PaymentTransaction paymentTransaction = createPaymentTransaction(player);
        beforeTest_processRefundReversed(player, transaction, originalPaymentTransaction, paymentTransaction);
        paymentTransaction.setPaymentStatus(SUCCESS);

        paymentServiceImpl.processRefundReversed(transaction);

        assertThat(wallet.getMoneyBalance(), is(new Money(2000L)));
        assertThat(wallet.getReservedBalance(), is(new Money(2000L)));
        assertThat(originalPaymentTransaction.getPaymentStatus(), is(SUCCESS));
        assertThat(paymentTransaction.getPaymentStatus(), is(SUCCESS));
    }

    @Test
    public void processRefundReversed_moveAmountToMoneyBalanceWhenOriginalPaymentStatusIsRefundedAndCorrespondingRefundPaymentMessageStatusIsSuccess() {
        final Player player = createPlayer();
        final Wallet wallet = player.getWallet();
        final AdyenTransactionNotification transaction = new AdyenTransactionNotification();
        final PaymentTransaction originalPaymentTransaction = createOriginalPaymentTransaction(player);
        final PaymentTransaction paymentTransaction = createPaymentTransaction(player);
        beforeTest_processRefundReversed(player, transaction, originalPaymentTransaction, paymentTransaction);
        originalPaymentTransaction.setPaymentStatus(REFUNDED);
        paymentTransaction.setPaymentStatus(SUCCESS);
        originalPaymentTransaction.setAmount(new Money(2000L));

        paymentServiceImpl.processRefundReversed(transaction);

        assertThat(wallet.getMoneyBalance(), is(new Money(4000L)));
        assertThat(wallet.getReservedBalance(), is(new Money(2000L)));
        assertThat(originalPaymentTransaction.getPaymentStatus(), is(SUCCESS));
        assertThat(paymentTransaction.getPaymentStatus(), is(FAILURE));
    }

    @Test
    public void processRefundReversed_doNotReverseWhenOriginalPaymentStatusIsRefundedAndCorrespondingRefundPaymentMessageStatusIsNotSuccess() {
        final Player player = createPlayer();
        final Wallet wallet = player.getWallet();
        final AdyenTransactionNotification transaction = new AdyenTransactionNotification();
        final PaymentTransaction originalPaymentTransaction = createOriginalPaymentTransaction(player);
        final PaymentTransaction paymentTransaction = createPaymentTransaction(player);
        beforeTest_processRefundReversed(player, transaction, originalPaymentTransaction, paymentTransaction);
        originalPaymentTransaction.setPaymentStatus(REFUNDED);

        paymentServiceImpl.processRefundReversed(transaction);

        assertThat(wallet.getMoneyBalance(), is(new Money(2000L)));
        assertThat(wallet.getReservedBalance(), is(new Money(2000L)));
        assertThat(originalPaymentTransaction.getPaymentStatus(), is(REFUNDED));
    }

    @Test
    public void processRefundReversed_moveAmountToMoneyBalanceWhenOriginalPaymentStatusIsRefundedAndCorrespondingRefundPaymentMessageNotFound() {
        final Player player = createPlayer();
        final Wallet wallet = player.getWallet();
        final AdyenTransactionNotification transaction = new AdyenTransactionNotification();
        final PaymentTransaction originalPaymentTransaction = createOriginalPaymentTransaction(player);
        final PaymentTransaction paymentTransaction = createPaymentTransaction(player);
        beforeTest_processRefundReversed(player, transaction, originalPaymentTransaction, paymentTransaction);
        originalPaymentTransaction.setPaymentStatus(REFUNDED);
        originalPaymentTransaction.setAmount(new Money(2000L));

        when(paymentTransactionFacade.getPayment("[originalProviderReference]")).thenReturn(null);

        paymentServiceImpl.processRefundReversed(transaction);

        assertThat(wallet.getMoneyBalance(), is(new Money(4000L)));
        assertThat(wallet.getReservedBalance(), is(new Money(2000L)));
        assertThat(originalPaymentTransaction.getPaymentStatus(), is(SUCCESS));
    }

    @Test
    public void processRefundReversed_doNotReverseWhenOriginalPaymentStatusIsSuccessAndCorrespondingRefundPaymentMessageNotFound() {
        final Player player = createPlayer();
        final Wallet wallet = player.getWallet();
        final AdyenTransactionNotification transaction = new AdyenTransactionNotification();
        final PaymentTransaction originalPaymentTransaction = createOriginalPaymentTransaction(player);
        final PaymentTransaction paymentTransaction = createPaymentTransaction(player);
        beforeTest_processRefundReversed(player, transaction, originalPaymentTransaction, paymentTransaction);
        originalPaymentTransaction.setPaymentStatus(SUCCESS);

        when(paymentTransactionFacade.getPayment("[originalProviderReference]")).thenReturn(null);

        paymentServiceImpl.processRefundReversed(transaction);

        assertThat(wallet.getMoneyBalance(), is(new Money(2000L)));
        assertThat(wallet.getReservedBalance(), is(new Money(2000L)));
        assertThat(originalPaymentTransaction.getPaymentStatus(), is(SUCCESS));
    }

    @Test
    public void processPaymentCancellation_moveAmountToMoneyBalanceWhenPaymentStatusIsSuccessMarkOriginalPaymentAsFailure() {
        final Player player = createPlayer();
        final Wallet wallet = player.getWallet();
        final AdyenTransactionNotification transaction = new AdyenTransactionNotification();
        final PaymentTransaction originalPaymentTransaction = createOriginalPaymentTransaction(player);
        final PaymentTransaction paymentTransaction = createPaymentTransaction(player);
        beforeTest_processRefundReversed(player, transaction, originalPaymentTransaction, paymentTransaction);
        transaction.setProviderReference("[providerReferenceForCancellation]");
        transaction.setEventCode(CANCELLATION);

        when(paymentTransactionFacade.getPayment(
                transaction.getOriginalReference(), AUTHORISATION, SUCCESS)).thenReturn(originalPaymentTransaction);

        paymentServiceImpl.processPaymentCancellation(transaction);

        assertThat(wallet.getMoneyBalance(), is(new Money(0L)));
        assertThat(wallet.getReservedBalance(), is(new Money(2000L)));
        assertThat(originalPaymentTransaction.getPaymentStatus(), is(FAILURE));
    }

    @Test
    public void defendChargeBack_withdrawFromMoneyBalanceAndMarkOriginalPaymentAsChargeBacked() {
        final Player player = createPlayer();
        final Wallet wallet = player.getWallet();
        final AdyenTransactionNotification transaction = new AdyenTransactionNotification();
        final PaymentTransaction originalPaymentTransaction = createOriginalPaymentTransaction(player);
        final PaymentTransaction paymentTransaction = createPaymentTransaction(player);
        beforeTest_processRefundReversed(player, transaction, originalPaymentTransaction, paymentTransaction);
        transaction.setProviderReference("[providerReferenceForDefendChargeback]");
        transaction.setEventCode(NOTIFICATION_OF_CHARGEBACK);
        transaction.setAmount(new Money(2000L));
        originalPaymentTransaction.setAmount(new Money(2000L));

        when(paymentTransactionFacade.getPayment(
                transaction.getOriginalReference(), AUTHORISATION, SUCCESS)).thenReturn(originalPaymentTransaction);

        paymentServiceImpl.defendChargeBack(transaction);

        assertThat(wallet.getMoneyBalance(), is(new Money(0L)));
        assertThat(wallet.getReservedBalance(), is(new Money(2000L)));
        assertThat(originalPaymentTransaction.getPaymentStatus(), is(CHARGEBACKED));
    }

    @Test
    public void processChargeBack_withdrawFromMoneyBalanceAndMarkOriginalPaymentAsChargeBacked() {
        final Player player = createPlayer();
        final Wallet wallet = player.getWallet();
        final AdyenTransactionNotification transaction = new AdyenTransactionNotification();
        final PaymentTransaction originalPaymentTransaction = createOriginalPaymentTransaction(player);
        final PaymentTransaction paymentTransaction = createPaymentTransaction(player);
        beforeTest_processRefundReversed(player, transaction, originalPaymentTransaction, paymentTransaction);
        transaction.setProviderReference("[providerReferenceForChargeback]");
        transaction.setEventCode(CHARGEBACK);
        originalPaymentTransaction.setAmount(new Money(2000L));

        when(paymentTransactionFacade.getPayment(
                transaction.getOriginalReference(), AUTHORISATION, SUCCESS)).thenReturn(originalPaymentTransaction);

        paymentServiceImpl.defendChargeBack(transaction);

        assertThat(wallet.getMoneyBalance(), is(Money.ZERO));
        assertThat(wallet.getReservedBalance(), is(new Money(2000L)));
        assertThat(originalPaymentTransaction.getPaymentStatus(), is(CHARGEBACKED));
    }

    @Test
    public void processChargeBackReversed_rollbackWithdrawFromMoneyBalanceAndMarkOriginalPaymentAsSuccessWhenAnOriginalChargedBackPaymentIsFound() {
        final Player player = createPlayer();
        final Wallet wallet = player.getWallet();
        final AdyenTransactionNotification transaction = new AdyenTransactionNotification();
        final PaymentTransaction originalPaymentTransaction = createOriginalPaymentTransaction(player);
        final PaymentTransaction paymentTransaction = createPaymentTransaction(player);
        beforeTest_processRefundReversed(player, transaction, originalPaymentTransaction, paymentTransaction);
        originalPaymentTransaction.setAmount(new Money(2000L));
        transaction.setProviderReference("[providerReferenceForChargebackReversed]");
        transaction.setEventCode(CHARGEBACK_REVERSED);

        when(paymentTransactionFacade.getPayment(
                transaction.getOriginalReference(), AUTHORISATION, CHARGEBACKED)).thenReturn(originalPaymentTransaction);

        paymentServiceImpl.processChargeBackReversed(transaction);

        assertThat(wallet.getMoneyBalance(), is(new Money(4000L)));
        assertThat(wallet.getReservedBalance(), is(new Money(2000L)));
        assertThat(originalPaymentTransaction.getPaymentStatus(), is(SUCCESS));
    }

    private Money beforeTest_refundPayment(final Player player, final PaymentTransaction originalPaymentTransaction, final PaymentTransaction paymentTransaction)
            throws AdyenException {
        when(paymentTransactionFacade.getPayment("[originalReference]", AUTHORISATION, SUCCESS)).thenReturn(originalPaymentTransaction);

        final Provider provider = createProvider();
        when(providerRepository.findOne(Provider.ADYEN)).thenReturn(provider);

        paymentTransaction.setId(1L);
        when(paymentTransactionFacade.insertPayment(any(Player.class), anyString(), anyString(), any(Money.class), eq(EUR), anyString(), eq(AWAITING_PAYMENT),
                                                    eq(provider), eq(REFUND))).thenReturn(paymentTransaction);

        final Wallet wallet = player.getWallet();
        wallet.setReservedBalance(Money.ZERO);
        wallet.setMoneyBalance(new Money(10L));

        final Money refundMoney = new Money(10L);

        final ModificationResult modificationResult = new ModificationResult();
        modificationResult.setPspReference(new JAXBElement<>(QName.valueOf(""), String.class, ""));
        when(adyenService.refundDeposit(any(Player.class), anyString(), any(Money.class), any(EventCode.class), anyLong())).thenReturn(modificationResult);

        final User user = new User();
        user.setId(10L);
        when(playerService.getPlayer(player.getId())).thenReturn(player);

        when(paymentTransactionFacade.getPayment(paymentTransaction.getId())).thenReturn(paymentTransaction);

        return refundMoney;
    }

    @Test(expected = InvalidOriginalPaymentException.class)
    public void refundPayment_whenOriginalPaymentNotFound()
            throws AdyenException {
        final Player player = createPlayer();
        final PaymentTransaction originalPaymentTransaction = createOriginalPaymentTransaction(player);
        final PaymentTransaction paymentTransaction = createPaymentTransaction(player);
        beforeTest_refundPayment(player, originalPaymentTransaction, paymentTransaction);
        final User user = new User(10L);

        when(paymentTransactionFacade.getPayment("[originalReference]", AUTHORISATION, SUCCESS)).thenReturn(null);

        paymentServiceImpl.refundDeposit("[originalReference]", player.getId(), user);
    }

    @Test(expected = InvalidOriginalPaymentException.class)
    public void refundPayment_whenRefundIsNotAllowed()
            throws AdyenException {
        final Player player = createPlayer();
        final PaymentTransaction originalPaymentTransaction = createOriginalPaymentTransaction(player);
        final PaymentTransaction paymentTransaction = createPaymentTransaction(player);
        beforeTest_refundPayment(player, originalPaymentTransaction, paymentTransaction);
        final User user = new User(10L);
        originalPaymentTransaction.setOperations(EnumSet.of(Operation.CANCEL));

        paymentServiceImpl.refundDeposit("[originalReference]", player.getId(), user);
    }

    @Test(expected = PaymentAmountException.class)
    @Ignore
    public void refundPayment_whenRefundAmountIsMoreThanOriginalPaymentAmount()
            throws AdyenException {
        final Player player = createPlayer();
        final PaymentTransaction originalPaymentTransaction = createOriginalPaymentTransaction(player);
        final PaymentTransaction paymentTransaction = createPaymentTransaction(player);
        beforeTest_refundPayment(player, originalPaymentTransaction, paymentTransaction);
        final User user = new User(10L);
        originalPaymentTransaction.setAmount(Money.ZERO);

        paymentServiceImpl.refundDeposit("[originalReference]", player.getId(), user);
    }

    @Test(expected = PaymentAmountException.class)
    public void refundPayment_whenRefundAmountIsMoreThanMoneyBalance()
            throws AdyenException {
        final Player player = createPlayer();
        final Wallet wallet = player.getWallet();
        final PaymentTransaction originalPaymentTransaction = createOriginalPaymentTransaction(player);
        originalPaymentTransaction.getOperations().add(Operation.REFUND);
        final PaymentTransaction paymentTransaction = createPaymentTransaction(player);
        beforeTest_refundPayment(player, originalPaymentTransaction, paymentTransaction);
        final User user = new User(10L);
        wallet.setMoneyBalance(Money.ZERO);

        paymentServiceImpl.refundDeposit("[originalReference]", player.getId(), user);
    }

    @Test
    public void refundPayment_refund()
            throws AdyenException {
        final Player player = createPlayer();
        final Wallet wallet = player.getWallet();
        final PaymentTransaction originalPaymentTransaction = createOriginalPaymentTransaction(player);
        originalPaymentTransaction.getOperations().add(Operation.REFUND);
        final PaymentTransaction transaction = createPaymentTransaction(player);
        beforeTest_refundPayment(player, originalPaymentTransaction, transaction);
        wallet.setMoneyBalance(new Money(5000L));
        final User user = new User(10L);

        when(paymentTransactionFacade.insertPayment(any(Player.class), anyString(), anyString(), any(Money.class), eq(EUR), anyString(), any(PaymentStatus.class),
                                                    eq(transaction.getProvider()), any(EventCode.class)))
                .thenReturn(transaction);

        paymentServiceImpl.refundDeposit("[originalReference]", player.getId(), user);

        assertThat(wallet.getMoneyBalance(), is(Money.ZERO));
        assertThat(wallet.getReservedBalance(), is(new Money(5000L)));
    }

    private Money beforeTest_payout(final Player player, final SubmitResponse submitResponse, final PaymentTransaction transaction)
            throws AdyenException {
        final Money payoutAmount = new Money(10L);
        final Provider provider = createProvider();
        when(providerRepository.findOne(Provider.ADYEN)).thenReturn(provider);

        transaction.setId(1l);
        when(paymentTransactionFacade.insertWithdrawal(player, "TEMPORARY_REFERENCE", "payoutTargetReference", payoutAmount, player.getCurrency(), "visa",
                                                       AWAITING_APPROVAL, provider)).thenReturn(transaction);
        submitResponse.setPspReference("payoutReference");
        submitResponse.setResultCode("[payout-submit-received]");
        submitResponse.setRefusalReason("Some reason for sendWithdrawRequest refusal");
        when(adyenService.withdraw(eq(player), any(RecurringContract.class), anyString(), any(Money.class), eq(transaction.getId()))).thenReturn(submitResponse);

        final ModifyResponse modifyResponse = new ModifyResponse();
        modifyResponse.setPspReference("confirmResponse");
        modifyResponse.setResponse("[payout-confirm-received]");
        when(adyenService.confirmWithdrawal(anyString())).thenReturn(modifyResponse);

        player.setBlockType(UNBLOCKED);

        when(playerService.getPlayer(player.getId())).thenReturn(player);
        when(paymentTransactionFacade.getPayment(transaction.getId())).thenReturn(transaction);

        return payoutAmount;
    }

    @Test(expected = PaymentAmountException.class)
    public void payout_whenPayoutAmountIsIsMoreThanMoneyBalance()
            throws AdyenException {
        final Player player = createPlayer();
        final Wallet wallet = player.getWallet();
        final SubmitResponse submitResponse = new SubmitResponse();
        final PaymentTransaction paymentTransaction = createPaymentTransaction(player);
        beforeTest_payout(player, submitResponse, paymentTransaction);
        wallet.setMoneyBalance(Money.ZERO);

        paymentServiceImpl.withdraw(player.getId(), "payoutTargetReference", "visa", new Money(10L), "password");
    }

    @Test
    public void payout_IfSubmitResponseFailsThenRollbackReservedMoneyAndSetProperPayoutMessageFields()
            throws AdyenException {
        final Player player = createPlayer();
        final Wallet wallet = player.getWallet();
        wallet.setMoneyBalance(Money.TEN);
        final SubmitResponse submitResponse = new SubmitResponse();
        final PaymentTransaction paymentTransaction = createPaymentTransaction(player);
        final Money money = beforeTest_payout(player, submitResponse, paymentTransaction);
        submitResponse.setResultCode("An informative message");

        try {
            paymentServiceImpl.withdraw(player.getId(), "payoutTargetReference", "visa", money, "password");
            fail("Withdraw should fail with an exception");
        } catch (final WithdrawalFailedException ignore) {
            assertThat(wallet.getMoneyBalance(), is(Money.TEN));
            assertThat(wallet.getReservedBalance(), is(Money.ZERO));
            assertThat(paymentTransaction.getPaymentStatus(), is(FAILURE));
            assertThat(paymentTransaction.getOriginalReference(), is("payoutReference"));
            assertThat(paymentTransaction.getReason(), is("Some reason for sendWithdrawRequest refusal"));
        }
    }

    @Test
    public void payout_IfRemoteExceptionWhilePayoutSubmissionThenThrowPaymentRequestSendExceptionSetProperPayoutMessageFields()
            throws AdyenException {
        final Player player = createPlayer();
        final SubmitResponse submitResponse = new SubmitResponse();
        final PaymentTransaction paymentTransaction = createPaymentTransaction(player);
        final Money money = beforeTest_payout(player, submitResponse, paymentTransaction);
        player.getWallet().setMoneyBalance(money);

        final AdyenException adyenException = new AdyenException("test", new RuntimeException("Blah"));
        when(adyenService.withdraw(any(Player.class), any(RecurringContract.class), anyString(), any(Money.class), anyLong())).thenThrow(adyenException);

        try {
            paymentServiceImpl.withdraw(player.getId(), "payoutTargetReference", "visa", money, "password");
        } catch (final CommunicationException e) {
            assertThat(paymentTransaction.getPaymentStatus(), is(SENDING_FAILURE));
        }
    }

    @Test
    public void payout_IfRemoteExceptionWhilePayoutConfirmThenThrowPaymentRequestSendExceptionSetProperPayoutMessageFields()
            throws AdyenException {
        final Player player = createPlayer();
        player.getWallet().setMoneyBalance(Money.TEN);
        final SubmitResponse submitResponse = new SubmitResponse();
        final PaymentTransaction paymentTransaction = createPaymentTransaction(player);
        final Money money = beforeTest_payout(player, submitResponse, paymentTransaction);

        when(adyenService.confirmWithdrawal(anyString())).thenThrow(new AdyenException("test", new RuntimeException("Blah")));
        when(paymentTransactionFacade.savePayment(any(PaymentTransaction.class))).thenReturn(paymentTransaction);

        try {
            paymentServiceImpl.withdraw(player.getId(), "payoutTargetReference", "visa", money, "password");
        } catch (final CommunicationException e) {
            assertThat(paymentTransaction.getPaymentStatus(), is(SENDING_FAILURE));
        }
    }

    private void beforeTest_retrievePayoutReferences(final Player player)
            throws AdyenException {
        @SuppressWarnings("ConstantConditions") final RecurringDetail[] recurringDetailArray = {
                new RecurringDetail(null, null, new GregorianCalendar(2014, 1, 2), null, "Customer01", "recurringDetailReference01", "Visa"),
                new RecurringDetail(null, null, new GregorianCalendar(2014, 2, 5), null, "Customer02", "recurringDetailReference02", "MasterCard")
        };
        final ArrayOfRecurringDetail arrayOfRecurringDetail = new ArrayOfRecurringDetail(recurringDetailArray);
        final RecurringDetailsResult recurringDetailsResult = new RecurringDetailsResult();
        recurringDetailsResult.setDetails(arrayOfRecurringDetail);

        when(adyenService.getWithdrawalDetails(eq(player), any(RecurringContract.class))).thenReturn(recurringDetailsResult);
        when(playerService.getPlayer(player.getId())).thenReturn(player);
    }

    @Test
    public void retrievePayoutReferences_ReturnListOfPayoutDetailReference()
            throws AdyenException {
        final Player player = createPlayer();
        beforeTest_retrievePayoutReferences(player);

        assertThat(paymentServiceImpl.sendPayoutInformationRequest(player).size(), is(2));

        final PayoutDetailReference payoutDetailReference = paymentServiceImpl.sendPayoutInformationRequest(player).get(1);

        assertThat(payoutDetailReference.getPayoutReference(), is("recurringDetailReference02"));
        assertThat(payoutDetailReference.getVariant(), is("MasterCard"));
        assertThat(payoutDetailReference.getHolderName(), is("Customer02"));
    }

    private PaymentTransaction createPayoutPayment(final Player player) {
        final PaymentTransaction paymentTransaction =
                new PaymentTransaction(player, createProvider(), new Date(), player.getCurrency(), "[providerReferenceForPayout]", AWAITING_PAYMENT, "visa",
                                       new Money(10L), EventCode.REFUND_WITH_DATA, "payoutTargetReference");
        paymentTransaction.setOriginalReference("payoutReference");
        return paymentTransaction;
    }

    private void beforeTest_adyenDoPayout(final Player player, final AdyenTransactionNotification transaction, final PaymentTransaction paymentTransaction) {
        transaction.setProviderReference("[providerReferenceForPayout]");
        transaction.setOriginalReference("payoutReference");
        transaction.setMerchantReference("" + Long.MAX_VALUE);
        transaction.setEventCode(EventCode.REFUND_WITH_DATA);
        transaction.setCurrency(EUR);
        transaction.setPaymentStatus(SUCCESS);
        transaction.setAmount(new Money(10L));

        final Wallet wallet = player.getWallet();
        wallet.setMoneyBalance(new Money(10L));
        wallet.setReservedBalance(new Money(10L));

        when(paymentTransactionFacade.getPayment(Long.MAX_VALUE)).thenReturn(paymentTransaction);
    }

    @Test
    public void adyenDoPayout_RemoveProperAmountOnlyFromReservedBalanceIfPayoutAmountIsLessThanReservedBalance() {
        final Player player = createPlayer();
        final Wallet wallet = player.getWallet();
        final AdyenTransactionNotification transaction = new AdyenTransactionNotification();
        final PaymentTransaction paymentTransaction = createPayoutPayment(player);
        beforeTest_adyenDoPayout(player, transaction, paymentTransaction);
        wallet.setMoneyBalance(new Money(100L));
        wallet.setReservedBalance(new Money(100L));
        paymentTransaction.setAmount(new Money(10l));
        transaction.setAmount(new Money(10L));

        paymentServiceImpl.processWithdraw(transaction);

        assertThat(wallet.getMoneyBalance(), is(new Money(100L)));
        assertThat(wallet.getReservedBalance(), is(new Money(90L)));
    }

    @Test
    public void adyenDoPayout_WithdrawProperAmountFromMoneyBalanceIfReservedBalanceDropsLessThanZero() {
        final Player player = createPlayer();
        final Wallet wallet = player.getWallet();
        final AdyenTransactionNotification transaction = new AdyenTransactionNotification();
        final PaymentTransaction paymentTransaction = createPayoutPayment(player);
        beforeTest_adyenDoPayout(player, transaction, paymentTransaction);
        paymentTransaction.setAmount(new Money(20L));
        wallet.setMoneyBalance(new Money(50L));
        wallet.setReservedBalance(new Money(10L));
        transaction.setAmount(new Money(20L));

        paymentServiceImpl.processWithdraw(transaction);

        assertThat(wallet.getMoneyBalance(), is(new Money(40L)));
        assertThat(wallet.getReservedBalance(), is(Money.ZERO));
    }

    @Test
    public void adyenDoPayout_DoNotPayoutIfCorrespondingPayoutMessageNotFound() {
        final Player player = createPlayer();
        final Wallet wallet = player.getWallet();
        final AdyenTransactionNotification transaction = new AdyenTransactionNotification();
        final PaymentTransaction paymentTransaction = createPayoutPayment(player);
        beforeTest_adyenDoPayout(player, transaction, paymentTransaction);
        paymentTransaction.setAmount(new Money(20L));
        transaction.setAmount(new Money(20L));

        when(paymentTransactionFacade.getPayment(Long.MAX_VALUE)).thenReturn(null);

        paymentServiceImpl.processWithdraw(transaction);

        assertThat(wallet.getMoneyBalance(), is(new Money(10L)));
        assertThat(wallet.getReservedBalance(), is(new Money(10L)));
    }

    @Test
    public void adyenDoPayout_DoNotPayoutIfPaymentReferenceDoesNotMatch() {
        final Player player = createPlayer();
        final Wallet wallet = player.getWallet();
        final AdyenTransactionNotification transaction = new AdyenTransactionNotification();
        final PaymentTransaction paymentTransaction = createPayoutPayment(player);
        beforeTest_adyenDoPayout(player, transaction, paymentTransaction);
        paymentTransaction.setAmount(new Money(20L));
        paymentTransaction.setOriginalReference("OriginalRef");
        transaction.setAmount(new Money(20L));
        transaction.setOriginalReference("WRONG_PAYOUT_REFERENCE");

        paymentServiceImpl.processWithdraw(transaction);

        assertThat(wallet.getMoneyBalance(), is(new Money(10L)));
        assertThat(wallet.getReservedBalance(), is(new Money(10L)));
    }

    @Test
    public void adyenDoPayout_DoNotPayoutIfPaymentAmountDoesNotMatch() {
        final Player player = createPlayer();
        final Wallet wallet = player.getWallet();
        final AdyenTransactionNotification transaction = new AdyenTransactionNotification();
        final PaymentTransaction paymentTransaction = createPayoutPayment(player);
        beforeTest_adyenDoPayout(player, transaction, paymentTransaction);
        paymentTransaction.setAmount(new Money(20L));
        transaction.setAmount(new Money(8880L));

        paymentServiceImpl.processWithdraw(transaction);

        assertThat(wallet.getMoneyBalance(), is(new Money(10L)));
        assertThat(wallet.getReservedBalance(), is(new Money(10L)));
    }

    @Test
    public void adyenDoPayout_DoNotPayoutIfPaymentAlreadyWasProcessed() {
        final Player player = createPlayer();
        final Wallet wallet = player.getWallet();
        final AdyenTransactionNotification transaction = new AdyenTransactionNotification();
        final PaymentTransaction paymentTransaction = createPayoutPayment(player);
        beforeTest_adyenDoPayout(player, transaction, paymentTransaction);
        paymentTransaction.setAmount(new Money(20L));
        paymentTransaction.setPaymentStatus(SUCCESS);
        transaction.setAmount(new Money(20L));

        paymentServiceImpl.processWithdraw(transaction);

        assertThat(wallet.getMoneyBalance(), is(new Money(10L)));
        assertThat(wallet.getReservedBalance(), is(new Money(10L)));
    }

    @Test
    @Ignore("We don't use multiple currencies anymore")
    public void adyenDoPayout_DoNotPayoutIfCurrencyDoesNotMatch() {
        final Player player = createPlayer();
        final Wallet wallet = player.getWallet();
        final AdyenTransactionNotification transaction = new AdyenTransactionNotification();
        final PaymentTransaction paymentTransaction = createPayoutPayment(player);
        beforeTest_adyenDoPayout(player, transaction, paymentTransaction);
        transaction.setAmount(new Money(20L));

        paymentServiceImpl.processWithdraw(transaction);

        assertThat(wallet.getMoneyBalance(), is(new Money(10L)));
        assertThat(wallet.getReservedBalance(), is(new Money(10L)));
    }

    @Test(expected = PaymentAmountException.class)
    public void prepareDeposit_ShouldThrowPaymentAmountExceptionMasterCard() {
        final Player player = createPlayer();
        final PaymentMethod masterCard = new PaymentMethod();
        masterCard.setMaxDepositAmount(new Money(300000L));
        when(paymentMethodRepository.findByMethodName("mc")).thenReturn(masterCard);
        paymentServiceImpl.prepareDeposit(player.getId(), new Money(300001L), "null", "mc", 1l);
    }

    @Test(expected = PaymentAmountException.class)
    public void prepareDeposit_ShouldThrowPaymentAmountExceptionVisa() {
        final Player player = createPlayer();
        final PaymentMethod visa = new PaymentMethod();
        visa.setMaxDepositAmount(new Money(300000L));
        when(paymentMethodRepository.findByMethodName("visa")).thenReturn(visa);
        paymentServiceImpl.prepareDeposit(player.getId(), new Money(300001L), "null", "visa", 1l);
    }

    @Test(expected = PaymentAmountException.class)
    public void prepareDeposit_ShouldThrowPaymentAmountExceptionVisaPaySafe() {
        final Player player = createPlayer();
        final PaymentMethod paySafeCard = new PaymentMethod();
        paySafeCard.setMaxDepositAmount(new Money(100000L));
        when(paymentMethodRepository.findByMethodName("paysafecard")).thenReturn(paySafeCard);
        paymentServiceImpl.prepareDeposit(player.getId(), new Money(100001L), "null", "paysafecard", 1l);
    }
}
