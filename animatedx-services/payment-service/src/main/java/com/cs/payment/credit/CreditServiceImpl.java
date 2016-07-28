package com.cs.payment.credit;

import com.cs.audit.AuditService;
import com.cs.audit.PlayerActivityType;
import com.cs.bonus.Bonus;
import com.cs.bonus.BonusService;
import com.cs.bonus.BonusStatus;
import com.cs.bonus.PlayerBonus;
import com.cs.payment.CreditTransaction;
import com.cs.payment.CreditTransactionType;
import com.cs.payment.InvalidCreditAmountException;
import com.cs.payment.Money;
import com.cs.payment.PaymentAmountException;
import com.cs.payment.QCreditTransaction;
import com.cs.persistence.BonusException;
import com.cs.player.Player;
import com.cs.player.Wallet;
import com.cs.player.WalletRepository;
import com.cs.promotion.PlayerCriteria;
import com.cs.util.CalendarUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.querydsl.QSort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.mysema.query.types.Order;
import com.mysema.query.types.OrderSpecifier;
import com.mysema.query.types.expr.BooleanExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.transaction.annotation.Propagation.SUPPORTS;

/**
 * @author Hadi Movaghar
 */
@Service
@Transactional(isolation = Isolation.READ_COMMITTED)
public class CreditServiceImpl implements CreditService {

    final Logger logger = LoggerFactory.getLogger(CreditServiceImpl.class);

    @Value("${bonus.bonus-conversion-goal-multiplier}")
    private Double bonusConversionGoalMultiplier;

    private final AuditService auditService;
    private final BonusService bonusService;
    private final CreditTransactionRepository creditTransactionRepository;
    private final WalletRepository walletRepository;

    @Autowired
    public CreditServiceImpl(final AuditService auditService, final BonusService bonusService, final CreditTransactionRepository creditTransactionRepository,
                             final WalletRepository walletRepository) {
        this.auditService = auditService;
        this.bonusService = bonusService;
        this.creditTransactionRepository = creditTransactionRepository;
        this.walletRepository = walletRepository;
    }

    @Transactional(propagation = SUPPORTS, readOnly = true)
    @Override
    public Page<CreditTransaction> getCreditTransactions(final Long playerId, final CreditTransactionType creditTransactionType, final Date startDate, final Date endDate,
                                                         final Integer page, final Integer size) {
        final Calendar instance = Calendar.getInstance();
        final Date startDateTrimmed = CalendarUtils.startOfDay(instance, startDate);
        final Date endDateTrimmed = CalendarUtils.endOfDay(instance, endDate);

        final QSort sort = new QSort(new OrderSpecifier<>(Order.DESC, QCreditTransaction.creditTransaction.createdDate));
        final PageRequest pageRequest = new PageRequest(page, size, sort);

        final QCreditTransaction qCreditTransaction = QCreditTransaction.creditTransaction;
        BooleanExpression query = qCreditTransaction.createdDate.between(startDateTrimmed, endDateTrimmed);
        if (playerId != null) {
            query = query.and(qCreditTransaction.player.id.eq(playerId));
        }
        if (creditTransactionType != null) {
            query = query.and(qCreditTransaction.creditTransactionType.eq(creditTransactionType));
        }

        return creditTransactionRepository.findAll(query, pageRequest);
    }

    @Transactional(propagation = SUPPORTS, readOnly = true)
    @Override
    public Iterable<CreditTransaction> getCreditTransactions(final Long playerId, final Date startDate, final Date endDate) {
        final Calendar instance = Calendar.getInstance();
        final Date startDateTrimmed = CalendarUtils.startOfDay(instance, startDate);
        final Date endDateTrimmed = CalendarUtils.endOfDay(instance, endDate);

        final QCreditTransaction qCreditTransaction = QCreditTransaction.creditTransaction;
        BooleanExpression query = qCreditTransaction.createdDate.between(startDateTrimmed, endDateTrimmed);
        if (playerId != null) {
            query = query.and(qCreditTransaction.player.id.eq(playerId));
        }

        return creditTransactionRepository.findAll(query);
    }

    @Override
    public Wallet convertCreditsToRealMoney(final Player player, final Integer creditAmount) {
        logger.debug("Received request to convert credits to real money for player {} with amount {}", player.getId(), creditAmount);
        if (creditAmount > player.getWallet().getCreditsBalance()) {
            throw new PaymentAmountException("Credit amount is invalid");
        }

        final Double creditExchangeRate = player.getLevel().getMoneyCreditRate();

        final Money convertedAmount = calculateCredits(creditAmount, creditExchangeRate);

        if (convertedAmount.isZero()) {
            logger.info("Converted amount for: player: {} | credit amount: {} | credit exchange rate: {} was zero", player.getId(), creditAmount, creditExchangeRate);
            throw new InvalidCreditAmountException(String.format("convertCreditsToRealMoney: convertedAmount was 0 for player %d", player.getId()));
        }

        final Wallet wallet = player.getWallet();
        wallet.setCreditsBalance(wallet.getCreditsBalance() - creditAmount);
        wallet.setMoneyBalance(player.getWallet().getMoneyBalance().add(convertedAmount));

        final CreditTransaction creditTransaction = new CreditTransaction();
        creditTransaction.setPlayer(player);
        creditTransaction.setRealMoney(convertedAmount);
        creditTransaction.setCredit(creditAmount);
        creditTransaction.setCurrency(player.getCurrency());
        creditTransaction.setCreatedDate(new Date());
        creditTransaction.setLevel(player.getLevel().getLevel());
        creditTransaction.setBonusCreditRate(player.getLevel().getMoneyCreditRate());
        creditTransaction.setCreditTransactionType(CreditTransactionType.CONVERSION);
        creditTransactionRepository.save(creditTransaction);

        logger.info("Player {} converted {} credits to real money {}", player.getId(), creditAmount, convertedAmount);
        final String message = String.format("Converted %d credits to real money %s", creditAmount, convertedAmount.getEuroValueInBigDecimal());
        auditService.trackPlayerActivityWithDescription(player, PlayerActivityType.CONVERT_CREDITS_TO_MONEY, message);

        return walletRepository.save(wallet);
    }

    @Override
    public Wallet convertCreditsToBonusMoney(final Player player, final Integer creditAmount) {
        logger.debug("Received request to convert credits to bonus money for player {} with amount {}", player.getId(), creditAmount);
        if (creditAmount > player.getWallet().getCreditsBalance()) {
            throw new PaymentAmountException("Credit amount is invalid");
        }

        final Double creditExchangeRate = player.getLevel().getBonusCreditRate();
        final Money convertedAmount = calculateCredits(creditAmount, creditExchangeRate);

        final Wallet wallet = player.getWallet();
        wallet.setCreditsBalance(wallet.getCreditsBalance() - creditAmount);

        final Bonus conversionBonus = bonusService.getCreditsConversionBonus();
        bonusService.grantBonusToPlayer(player, conversionBonus);

        final PlayerBonus playerBonus = bonusService.useBonus(player, conversionBonus, new PlayerCriteria());
        if (playerBonus == null) {
            throw new BonusException("No bonus could be created for bonus id " + conversionBonus.getId());
        }

        playerBonus.setUsedAmount(convertedAmount);
        playerBonus.setCurrentBalance(convertedAmount);
        playerBonus.setBonusConversionProgress(Money.ZERO);
        playerBonus.setBonusConversionGoal(convertedAmount.multiply(bonusConversionGoalMultiplier));
        playerBonus.setStatus(BonusStatus.INACTIVE);
        bonusService.updatePlayerBonus(playerBonus);

        // activate player-bonus if needed
        bonusService.getActivePlayerBonus(player);

        final CreditTransaction creditTransaction = new CreditTransaction();
        creditTransaction.setPlayer(player);
        creditTransaction.setBonusMoney(convertedAmount);
        creditTransaction.setCredit(creditAmount);
        creditTransaction.setCurrency(player.getCurrency());
        creditTransaction.setCreatedDate(new Date());
        creditTransaction.setPlayerBonus(playerBonus);
        creditTransaction.setLevel(player.getLevel().getLevel());
        creditTransaction.setBonusCreditRate(player.getLevel().getBonusCreditRate());
        creditTransaction.setCreditTransactionType(CreditTransactionType.CONVERSION);
        creditTransactionRepository.save(creditTransaction);

        logger.info("Player {} converted {} credits to bonus money, amount {} added as player-bonus {}", player.getId(), creditAmount, convertedAmount,
                    playerBonus.getId());

        final String message = String.format("Converted %d credits to bonus money, amount %s added as player-bonus %d", creditAmount,
                                             convertedAmount.getEuroValueInBigDecimal(), playerBonus.getId());
        auditService.trackPlayerActivityWithDescription(player, PlayerActivityType.CONVERT_CREDITS_TO_BONUS, message);
        return walletRepository.save(wallet);
    }

    @Override
    public Money calculateCredits(final Integer creditAmount, final Double creditExchangeRate) {
        return new Money(creditAmount * creditExchangeRate);
    }

    @Transactional(propagation = SUPPORTS, readOnly = true)
    @Override
    public Map<BigInteger, CreditSummary> getAffiliatePlayersConvertedCreditsSummary(final Date startDate, final Date endDate) {
        final Map<BigInteger, CreditSummary> map = new HashMap<>();
        final List<Object[]> list = creditTransactionRepository.getAffiliatePlayersConvertedCreditsSummary(startDate, endDate);
        for (final Object[] tuple : list) {
            map.put((BigInteger) tuple[0], new CreditSummary(Money.getMoneyFromCents((BigDecimal) tuple[1]), Money.getMoneyFromCents((BigDecimal) tuple[2]), (Integer) tuple[3]));
        }
        return map;
    }
}
