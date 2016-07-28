package com.cs.job;

import com.cs.job.netrefer.SendPlayerActivitiesToNetreferJob;
import com.cs.job.netrefer.SendPlayerRegistrationsToNetreferJob;
import com.cs.job.player.ApplyPlayerLimitationsJob;
import com.cs.job.player.CalculatePlayerCashbackJob;
import com.cs.job.player.ResetDailyAccumulatedLimitsJob;
import com.cs.job.player.ResetMonthlyAccumulatedLimitsJob;
import com.cs.job.player.ResetPlayerLimitationsBlockJob;
import com.cs.job.player.ResetPlayerMonthlyTurnoverJob;
import com.cs.job.player.ResetWeeklyAccumulatedLimitsJob;
import com.cs.report.ReportServiceConfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import org.quartz.Trigger;

/**
 * @author Joakim Gottzén
 */
@Configuration
@EnableScheduling
@ComponentScan(basePackages = {"com.cs.job.netrefer", "com.cs.job.player"})
@Import({ReportServiceConfig.class})
@PropertySource("classpath:quartz-scheduling.properties")
public class SchedulingServiceConfig {

    @Value("${quartz.player.resetLimitationBlocks.cron}")
    private String resetLimitationBlocksCronExpression;

    @Value("${quartz.player.cashbackCalculations.cron}")
    private String cashbackCalculationsCronExpression;

    @Value("${quartz.player.resetMonthlyTurnovers.cron}")
    private String resetPlayerMonthlyTurnoverCronExpression;

    @Value("${quartz.player.applyPendingLimitations.cron}")
    private String applyPendingLimitationsCronExpression;

    @Value("${quartz.player.resetDailyAccumulatedLimits.cron}")
    private String resetDailyAccumulatedLimitsCronExpression;

    @Value("${quartz.player.resetWeeklyAccumulatedLimits.cron}")
    private String resetWeeklyAccumulatedLimitsCronExpression;

    @Value("${quartz.player.resetMonthlyAccumulatedLimits.cron}")
    private String resetMonthlyAccumulatedLimitsCronExpression;

    @Value("${quartz.netrefer.sendPlayerRegistrations.cron}")
    private String SendPlayerRegistrationsToNetreferCronExpression;

    @Value("${quartz.netrefer.sendPlayerActivities.cron}")
    private String sendPlayerActivitiesToNetreferCronExpression;

    @Autowired
    private ApplicationContext applicationContext;

    @Bean
    public SchedulerFactoryBean quartzScheduler() {
        final SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
        schedulerFactoryBean.setJobFactory(jobFactory());
        schedulerFactoryBean.setAutoStartup(true);
        schedulerFactoryBean.setWaitForJobsToCompleteOnShutdown(true);

        final Trigger[] triggers = {
                resetLimitationsBlockTrigger().getObject(),
                calculateCashbackTrigger().getObject(),
                resetMonthlyTurnoverTrigger().getObject(),
                applyPlayersLimitationsTrigger().getObject(),
                resetDailyAccumulatedLimitsTrigger().getObject(),
                resetWeeklyAccumulatedLimitsTrigger().getObject(),
                resetMonthlyAccumulatedLimitsTrigger().getObject(),
                sendPlayerRegistrationsToNetreferTrigger().getObject(),
                sendPlayerActivitiesToNetreferTrigger().getObject(),
        };
        schedulerFactoryBean.setTriggers(triggers);

        return schedulerFactoryBean;
    }

    @Bean
    public AutowiringSpringBeanJobFactory jobFactory() {
        final AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory();
        jobFactory.setApplicationContext(applicationContext);
        return jobFactory;
    }

    @Bean
    public CronTriggerFactoryBean resetLimitationsBlockTrigger() {
        final CronTriggerFactoryBean cronTriggerFactoryBean = new CronTriggerFactoryBean();
        cronTriggerFactoryBean.setCronExpression(resetLimitationBlocksCronExpression);
        cronTriggerFactoryBean.setJobDetail(resetLimitationsBlockJob().getObject());
        return cronTriggerFactoryBean;
    }

    @Bean
    public JobDetailFactoryBean resetLimitationsBlockJob() {
        final JobDetailFactoryBean jobDetailFactoryBean = new JobDetailFactoryBean();
        jobDetailFactoryBean.setJobClass(ResetPlayerLimitationsBlockJob.class);
        jobDetailFactoryBean.setDurability(true);
        jobDetailFactoryBean.setRequestsRecovery(false);
        return jobDetailFactoryBean;
    }

    @Bean
    public CronTriggerFactoryBean calculateCashbackTrigger() {
        final CronTriggerFactoryBean cronTriggerFactoryBean = new CronTriggerFactoryBean();
        cronTriggerFactoryBean.setCronExpression(cashbackCalculationsCronExpression);
        cronTriggerFactoryBean.setJobDetail(calculateCashbackJob().getObject());
        return cronTriggerFactoryBean;
    }

    @Bean
    public JobDetailFactoryBean calculateCashbackJob() {
        final JobDetailFactoryBean jobDetailFactoryBean = new JobDetailFactoryBean();
        jobDetailFactoryBean.setJobClass(CalculatePlayerCashbackJob.class);
        jobDetailFactoryBean.setDurability(true);
        jobDetailFactoryBean.setRequestsRecovery(false);
        return jobDetailFactoryBean;
    }

    @Bean
    public CronTriggerFactoryBean resetMonthlyTurnoverTrigger() {
        final CronTriggerFactoryBean cronTriggerFactoryBean = new CronTriggerFactoryBean();
        cronTriggerFactoryBean.setCronExpression(resetPlayerMonthlyTurnoverCronExpression);
        cronTriggerFactoryBean.setJobDetail(resetMonthlyTurnoverJob().getObject());
        return cronTriggerFactoryBean;
    }

    @Bean
    public JobDetailFactoryBean resetMonthlyTurnoverJob() {
        final JobDetailFactoryBean jobDetailFactoryBean = new JobDetailFactoryBean();
        jobDetailFactoryBean.setJobClass(ResetPlayerMonthlyTurnoverJob.class);
        jobDetailFactoryBean.setDurability(true);
        jobDetailFactoryBean.setRequestsRecovery(false);
        return jobDetailFactoryBean;
    }

    @Bean
    public CronTriggerFactoryBean applyPlayersLimitationsTrigger() {
        final CronTriggerFactoryBean cronTriggerFactoryBean = new CronTriggerFactoryBean();
        cronTriggerFactoryBean.setCronExpression(applyPendingLimitationsCronExpression);
        cronTriggerFactoryBean.setJobDetail(applyPlayersLimitationsJob().getObject());
        return cronTriggerFactoryBean;
    }

    @Bean
    public JobDetailFactoryBean applyPlayersLimitationsJob() {
        final JobDetailFactoryBean jobDetailFactoryBean = new JobDetailFactoryBean();
        jobDetailFactoryBean.setJobClass(ApplyPlayerLimitationsJob.class);
        jobDetailFactoryBean.setDurability(true);
        jobDetailFactoryBean.setRequestsRecovery(false);
        return jobDetailFactoryBean;
    }

    @Bean
    public CronTriggerFactoryBean resetDailyAccumulatedLimitsTrigger() {
        final CronTriggerFactoryBean cronTriggerFactoryBean = new CronTriggerFactoryBean();
        cronTriggerFactoryBean.setCronExpression(resetDailyAccumulatedLimitsCronExpression);
        cronTriggerFactoryBean.setJobDetail(resetDailyLimitsJob().getObject());
        return cronTriggerFactoryBean;
    }

    @Bean
    public JobDetailFactoryBean resetDailyLimitsJob() {
        final JobDetailFactoryBean jobDetailFactoryBean = new JobDetailFactoryBean();
        jobDetailFactoryBean.setJobClass(ResetDailyAccumulatedLimitsJob.class);
        jobDetailFactoryBean.setDurability(true);
        jobDetailFactoryBean.setRequestsRecovery(false);
        return jobDetailFactoryBean;
    }

    @Bean
    public CronTriggerFactoryBean resetWeeklyAccumulatedLimitsTrigger() {
        final CronTriggerFactoryBean cronTriggerFactoryBean = new CronTriggerFactoryBean();
        cronTriggerFactoryBean.setCronExpression(resetWeeklyAccumulatedLimitsCronExpression);
        cronTriggerFactoryBean.setJobDetail(resetAccumulatedLimitsJob().getObject());
        return cronTriggerFactoryBean;
    }

    @Bean
    public JobDetailFactoryBean resetAccumulatedLimitsJob() {
        final JobDetailFactoryBean jobDetailFactoryBean = new JobDetailFactoryBean();
        jobDetailFactoryBean.setJobClass(ResetWeeklyAccumulatedLimitsJob.class);
        jobDetailFactoryBean.setDurability(true);
        jobDetailFactoryBean.setRequestsRecovery(false);
        return jobDetailFactoryBean;
    }

    @Bean
    public CronTriggerFactoryBean resetMonthlyAccumulatedLimitsTrigger() {
        final CronTriggerFactoryBean cronTriggerFactoryBean = new CronTriggerFactoryBean();
        cronTriggerFactoryBean.setCronExpression(resetMonthlyAccumulatedLimitsCronExpression);
        cronTriggerFactoryBean.setJobDetail(resetMonthlyLimitsJob().getObject());
        return cronTriggerFactoryBean;
    }

    @Bean
    public JobDetailFactoryBean resetMonthlyLimitsJob() {
        final JobDetailFactoryBean jobDetailFactoryBean = new JobDetailFactoryBean();
        jobDetailFactoryBean.setJobClass(ResetMonthlyAccumulatedLimitsJob.class);
        jobDetailFactoryBean.setDurability(true);
        jobDetailFactoryBean.setRequestsRecovery(false);
        return jobDetailFactoryBean;
    }

    @Bean
    public CronTriggerFactoryBean sendPlayerRegistrationsToNetreferTrigger() {
        final CronTriggerFactoryBean cronTriggerFactoryBean = new CronTriggerFactoryBean();
        cronTriggerFactoryBean.setCronExpression(SendPlayerRegistrationsToNetreferCronExpression);
        cronTriggerFactoryBean.setJobDetail(sendPlayerRegistrationsJob().getObject());
        return cronTriggerFactoryBean;
    }

    @Bean
    public JobDetailFactoryBean sendPlayerRegistrationsJob() {
        final JobDetailFactoryBean jobDetailFactoryBean = new JobDetailFactoryBean();
        jobDetailFactoryBean.setJobClass(SendPlayerRegistrationsToNetreferJob.class);
        jobDetailFactoryBean.setDurability(true);
        jobDetailFactoryBean.setRequestsRecovery(false);
        return jobDetailFactoryBean;
    }

    @Bean
    public CronTriggerFactoryBean sendPlayerActivitiesToNetreferTrigger() {
        final CronTriggerFactoryBean cronTriggerFactoryBean = new CronTriggerFactoryBean();
        cronTriggerFactoryBean.setCronExpression(sendPlayerActivitiesToNetreferCronExpression);
        cronTriggerFactoryBean.setJobDetail(sendPlayerActivitiesJob().getObject());
        return cronTriggerFactoryBean;
    }

    @Bean
    public JobDetailFactoryBean sendPlayerActivitiesJob() {
        final JobDetailFactoryBean jobDetailFactoryBean = new JobDetailFactoryBean();
        jobDetailFactoryBean.setJobClass(SendPlayerActivitiesToNetreferJob.class);
        jobDetailFactoryBean.setDurability(true);
        jobDetailFactoryBean.setRequestsRecovery(false);
        return jobDetailFactoryBean;
    }
}
