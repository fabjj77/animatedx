package com.cs.job.netrefer;

import com.cs.messaging.sftp.PlayerActivity;
import com.cs.report.ReportService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author Hadi Movaghar
 */
@Component
public class SendPlayerActivitiesToNetreferJob implements Job {

    private final Logger logger = LoggerFactory.getLogger(SendPlayerActivitiesToNetreferJob.class);

    private ReportService reportService;

    @Autowired
    public void setReportService(final ReportService reportService) {
        this.reportService = reportService;
    }

    @Override
    public void execute(final JobExecutionContext context)
            throws JobExecutionException {
        final Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        final Date date = calendar.getTime();

        final List<PlayerActivity> playerActivities = reportService.sendPlayerActivityToNetrefer(date);
        logger.info("Sent report to Netrefer with {} activities", playerActivities.size());
    }
}

