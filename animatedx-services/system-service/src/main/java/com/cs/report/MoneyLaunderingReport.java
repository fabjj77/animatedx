package com.cs.report;

import com.cs.audit.PlayerActivityType;
import com.cs.player.Player;

import com.google.common.base.Joiner;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.cs.report.PlayerReportType.MONEY_LAUNDERING_REPORT;

/**
 * @author Hadi Movaghar
 */
public class MoneyLaunderingReport extends PlayerReport {
    private static final long serialVersionUID = 1L;
    private static final String FILE_PREFIX = "Money-Laundering-Report-";
    private static final List<String> CSV_HEADERS = Arrays.asList("Player Id", "Email", "Full Name", "Money Balance", "Last Login Date", "Reason(s)");
    private final Map<Long, Date> lastLoginDates;
    private final Map<Player, List<PlayerActivityType>> playersAndActivityTypes;

    public MoneyLaunderingReport(final Map<Player, List<PlayerActivityType>> playersAndActivityTypes, final Map<Long, Date> lastLoginDates) {
        super(playersAndActivityTypes.keySet(), FILE_PREFIX, CSV_HEADERS, MONEY_LAUNDERING_REPORT);
        this.lastLoginDates = lastLoginDates;
        this.playersAndActivityTypes = playersAndActivityTypes;
    }

    @Override
    public String getSummary() {
        final StringBuilder builder = generateHeaders();
        final Joiner joiner = Joiner.on(ROW_DELIMITER).useForNull("");
        for (final Player player : getPlayers()) {
            final List<PlayerActivityType> playerActivityTypes = playersAndActivityTypes.get(player);
            final String activities = Joiner.on(';').useForNull("").join(playerActivityTypes);
            builder.append(joiner.join(player.getId(), player.getEmailAddress(), player.getFirstName() + " " + player.getLastName(),
                                       player.getWallet().getMoneyBalance().getEuroValueInBigDecimal(), lastLoginDates.get(player.getId()), activities));
            builder.append('\n');
        }
        return builder.toString();
    }
}
