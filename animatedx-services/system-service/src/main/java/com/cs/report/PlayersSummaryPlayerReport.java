package com.cs.report;

import com.cs.player.Player;

import com.google.common.base.Joiner;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.cs.report.PlayerReportType.MONTHLY_SUMMARY_REPORT;

/**
 * @author Hadi Movaghar
 */
public class PlayersSummaryPlayerReport extends PlayerReport {

    private static final long serialVersionUID = 1L;
    private static final String FILE_PREFIX = "Monthly-Summary-Report-";
    private static final List<String> CSV_HEADERS = Arrays.asList("Player Id", "Email", "Full Name", "Money Balance", "Last Login Date");
    private Map<Long, Date> lastLoginDates = new HashMap<>();

    public PlayersSummaryPlayerReport(final List<Player> players, final Map<Long, Date> lastLoginDates) {
        super(players, FILE_PREFIX, CSV_HEADERS, MONTHLY_SUMMARY_REPORT);
        this.lastLoginDates = lastLoginDates;
    }

    @Override
    public String getSummary() {
        final StringBuilder builder = generateHeaders();
        final Joiner joiner = Joiner.on(ROW_DELIMITER).useForNull("");
        for (final Player player : getPlayers()) {
            builder.append(joiner.join(player.getId(), player.getEmailAddress(), player.getFirstName() + " " + player.getLastName(),
                                       player.getWallet().getMoneyBalance().getEuroValueInBigDecimal(), lastLoginDates.get(player.getId())));
            builder.append('\n');
        }
        return builder.toString();
    }
}
