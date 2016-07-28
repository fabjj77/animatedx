package com.cs.report;

import com.cs.persistence.CommunicationException;
import com.cs.player.Player;
import com.cs.util.DateFormatPatterns;

import com.google.common.base.Joiner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

/**
 * @author Hadi Movaghar
 */
public abstract class PlayerReport implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Logger logger = LoggerFactory.getLogger(PlayerReport.class);

    static final String HEADER_DELIMITER = ",";
    static final String ROW_DELIMITER = HEADER_DELIMITER;
    public static final String FILE_ENDING = ".csv";
    private final SimpleDateFormat dateFormat = new SimpleDateFormat(DateFormatPatterns.DATE_ONLY);
    private static final long secondsInMillisecond = 1000;
    private static final long minutesInMillisecond = secondsInMillisecond * 60;

    private final String filePrefix;
    private final Collection<String> csvHeaders;
    private final Collection<Player> players;
    private final PlayerReportType playerReportType;

    protected PlayerReport(final Collection<Player> players, final String filePrefix, final Collection<String> csvHeaders, final PlayerReportType playerReportType) {
        this.players = players;
        this.filePrefix = filePrefix;
        this.csvHeaders = csvHeaders;
        this.playerReportType = playerReportType;
    }

    public Collection<Player> getPlayers() {
        return players;
    }

    public abstract String getSummary();

    public StringBuilder generateHeaders() {
        return new StringBuilder(Joiner.on(HEADER_DELIMITER).join(csvHeaders) + '\n');
    }

    public File getSummaryFile() {
        try {
            final Date startTime = new Date();
            logger.info("Generating {} report started for {} number of rows in {}", playerReportType, players.size(), startTime);
            final Path path = Paths.get(filePrefix + dateFormat.format(new Date()) + FILE_ENDING);
            final File reportFile = Files.write(path, getSummary().getBytes(), StandardOpenOption.CREATE).toFile();
            final Date endTime = new Date();
            logger.info("Generating {} report finished for {} number of rows in {}", playerReportType, players.size(), endTime);
            final long elapsedTime = endTime.getTime() - startTime.getTime();
            logger.info("PlayerReport generation of {} for {} rows took {} minutes and {} seconds.", playerReportType, players.size(),
                        elapsedTime / minutesInMillisecond, elapsedTime % minutesInMillisecond / secondsInMillisecond);
            return reportFile;
        } catch (final IOException e) {
            throw new CommunicationException("Failed to write " + playerReportType + " report.", e);
        }
    }
}
