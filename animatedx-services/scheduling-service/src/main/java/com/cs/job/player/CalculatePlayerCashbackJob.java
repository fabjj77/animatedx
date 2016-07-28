package com.cs.job.player;

import com.cs.payment.PaymentService;
import com.cs.player.Wallet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Omid Alaepour
 */
@Component
public class CalculatePlayerCashbackJob implements Job {

    private final Logger logger = LoggerFactory.getLogger(CalculatePlayerCashbackJob.class);

    private static final int CASHBACK_PAGE_SIZE = 250;

    private PaymentService paymentService;

    @Autowired
    public void setPaymentService(final PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Override
    public void execute(final JobExecutionContext context)
            throws JobExecutionException {

        long updatePlayers = 0;
        Integer page = 0;
        Page<Wallet> wallets;

        do {
            wallets = paymentService.getWalletsWeeklyTurnover(page, CASHBACK_PAGE_SIZE);
            for (final Wallet wallet : wallets) {
                try {
                    updatePlayers += paymentService.giveCashback(wallet) ? 1 : 0;
                } catch (final RuntimeException e) {
                    logger.error("Error updating waller for player {}: {}", wallet.getPlayer().getId(), e.getMessage(), e);
                }
            }

            page++;
        } while (wallets.hasNextPage());

        logger.info("Updates {} players with cashback", updatePlayers);
    }
}
