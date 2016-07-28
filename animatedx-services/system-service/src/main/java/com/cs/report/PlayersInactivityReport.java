package com.cs.report;

import com.cs.player.Player;

import com.google.common.base.Joiner;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.cs.report.PlayerReportType.PLAYERS_INACTIVITY_REPORT;

/**
 * @author Hadi Movaghar
 */
public class PlayersInactivityReport extends PlayerReport {
    private static final long serialVersionUID = 1L;
    private static final String FILE_PREFIX = "Player-Inactivity-Report-";
    private static final List<String> CSV_HEADERS = Arrays.asList("Player Id", "Email", "Full Name", "Money Balance", "Bonus Balance", "Last Login Date");
    private final Map<Player, Date> inactivePlayers;

    public PlayersInactivityReport(final List<Player> players, final Map<Player, Date> inactivePlayers) {
        super(players, FILE_PREFIX, CSV_HEADERS, PLAYERS_INACTIVITY_REPORT);
        this.inactivePlayers = inactivePlayers;
    }

    @Override
    public String getSummary() {
        final StringBuilder builder = generateHeaders();
        final Joiner joiner = Joiner.on(ROW_DELIMITER).useForNull("");
        for (final Player player : getPlayers()) {
            builder.append(joiner.join(player.getId(), player.getEmailAddress(), player.getFirstName() + " " + player.getLastName(),
                                       player.getWallet().getMoneyBalance().getEuroValueInBigDecimal(),
                                       player.getWallet().getBonusBalance().getEuroValueInBigDecimal(), inactivePlayers.get(player)));
            builder.append('\n');
        }
        return builder.toString();
    }
}
