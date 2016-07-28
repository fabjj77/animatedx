package com.cs.job.player;

import com.cs.payment.PaymentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * @author Omid Alaepour
 */
@Component
public class ResetPlayerMonthlyTurnoverJob implements Job {

    private PaymentService paymentService;

    @Autowired
    public void setPaymentService(final PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Override
    public void execute(final JobExecutionContext context)
            throws JobExecutionException {
        paymentService.resetMonthlyTurnover();
    }
}
