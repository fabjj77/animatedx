package com.cs.payment.credit;

import com.cs.payment.CreditTransaction;
import com.cs.payment.CreditTransactionType;
import com.cs.payment.Money;
import com.cs.player.Player;
import com.cs.player.Wallet;

import org.springframework.data.domain.Page;

import java.math.BigInteger;
import java.util.Date;
import java.util.Map;

/**
 * @author Hadi Movaghar
 */
public interface CreditService {

    Wallet convertCreditsToBonusMoney(final Player player, final Integer creditAmount);

    Wallet convertCreditsToRealMoney(final Player player, final Integer creditAmount);

    Page<CreditTransaction> getCreditTransactions(final Long playerId, CreditTransactionType creditTransactionType, final Date startDate, final Date endDate,
                                                  final Integer page, final Integer size);

    Iterable<CreditTransaction> getCreditTransactions(final Long playerId, final Date startDate, final Date endDate);

    Money calculateCredits(final Integer creditAmount, final Double creditExchangeRate);

    Map<BigInteger, CreditSummary> getAffiliatePlayersConvertedCreditsSummary(final Date startDate, final Date endDate);
}
