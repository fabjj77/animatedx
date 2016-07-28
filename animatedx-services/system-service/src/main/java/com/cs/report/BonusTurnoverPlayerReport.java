package com.cs.report;

import com.cs.player.Player;

import com.google.common.base.Joiner;

import java.util.Arrays;
import java.util.List;

import static com.cs.report.PlayerReportType.MONTHLY_BONUS_TURNOVER;

/**
 * @author Hadi Movaghar
 */
public class BonusTurnoverPlayerReport extends PlayerReport {
    private static final long serialVersionUID = 1L;
    private static final String FILE_PREFIX = "Monthly-Bonus-Turnover-Report-";
    private static final List<String> CSV_HEADERS = Arrays.asList("Player Id", "Email", "Full Name", "Monthly Money Turnover", "Monthly Bonus Turnover");

    public BonusTurnoverPlayerReport(final List<Player> players) {
        super(players, FILE_PREFIX, CSV_HEADERS, MONTHLY_BONUS_TURNOVER);
    }

    @Override
    public String getSummary() {
        final StringBuilder builder = generateHeaders();
        final Joiner joiner = Joiner.on(ROW_DELIMITER).useForNull("");
        for (final Player player : getPlayers()) {
            builder.append(joiner.join(player.getId(), player.getEmailAddress(), player.getFirstName() + " " + player.getLastName(),
                                       player.getWallet().getAccumulatedMonthlyTurnover().getEuroValueInBigDecimal(),
                                       player.getWallet().getAccumulatedMonthlyBonusTurnover().getEuroValueInBigDecimal()));
            builder.append('\n');
        }
        return builder.toString();
    }
}
