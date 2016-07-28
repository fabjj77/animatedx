package com.cs.payment.devcode;

import com.cs.payment.DCPaymentTransaction;
import com.cs.player.Player;

import org.springframework.data.domain.Page;

import java.math.BigInteger;
import java.util.Date;
import java.util.Map;

/**
 * @author Hadi Movaghar
 */
public interface DevcodePaymentService {

    Player verifyPlayer(final String playerIdString, final String sessionId);

    DCPaymentTransaction authorize(final String playerIdString, final DCPaymentTransaction dcPaymentTransaction);

    DCPaymentTransaction transfer(final String playerIdString, final DCPaymentTransaction dcPaymentTransaction);

    DCPaymentTransaction cancel(final String playerIdString, final DCPaymentTransaction dcPaymentTransaction);

    DCPaymentTransaction getStatus(final String transactionId);

    Map<BigInteger, DCPaymentSummary> getAffiliatePlayersPaymentsSummary(final Date startDateTrimmed, final Date endDateTrimmed);

    Page<DCPaymentTransaction> getPayments(final Long id, final Date startDate, final Date endDate, final Integer page, final Integer size);
}
