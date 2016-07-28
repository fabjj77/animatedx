package com.cs.report;

import com.cs.audit.PlayerActivity;

import com.google.common.base.Joiner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

/**
 * @author Hadi Movaghar
 */
public abstract class AuditReport implements Serializable {
    private static final long serialVersionUID = 1L;
    private final Logger logger = LoggerFactory.getLogger(AuditReport.class);

    static final String HEADER_DELIMITER = ",";
    static final String ROW_DELIMITER = HEADER_DELIMITER;
    private static final long secondsInMillisecond = 1000;
    private static final long minutesInMillisecond = secondsInMillisecond * 60;

    private final Collection<String> csvHeaders;
    private final Collection<PlayerActivity> activities;
    private final AuditReportType auditReportType;

    protected AuditReport(final Collection<PlayerActivity> activities, final Collection<String> csvHeaders,
                          final AuditReportType auditReportType) {
        this.activities = activities;
        this.csvHeaders = csvHeaders;
        this.auditReportType = auditReportType;
    }

    public Collection<PlayerActivity> getActivities() {
        return activities;
    }

    public abstract String getSummary();

    public StringBuilder generateHeaders() {
        return new StringBuilder(Joiner.on(HEADER_DELIMITER).join(csvHeaders) + '\n');
    }

    public String getSummaryFile() {
            final Date startTime = new Date();
            logger.info("Generating {} report started for {} number of rows in {}", auditReportType,  activities.size(), startTime);
//            final ByteBuffer byteBuffer = ByteBuffer.wrap(getSummary().getBytes());
            final String report = getSummary();
            final Date endTime = new Date();
            logger.info("Generating {} report finished for {} number of rows in {}", auditReportType, activities.size(), endTime);
            final long elapsedTime = endTime.getTime() - startTime.getTime();
            logger.info("AuditReport generation of {} for {} rows took {} minutes and {} seconds ({} milliseconds).", auditReportType, activities.size(),
                        elapsedTime / minutesInMillisecond, elapsedTime % minutesInMillisecond / secondsInMillisecond, elapsedTime);
            return report;
    }
}
