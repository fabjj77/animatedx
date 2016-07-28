package com.cs.report;

import com.cs.audit.PlayerActivity;
import com.cs.player.Player;

import com.google.common.base.Joiner;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @author Hadi Movaghar
 */
public class PlayerAuditReport extends AuditReport implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final List<String> CSV_HEADERS = Arrays.asList("Player Id", "Email", "Full Name", "Activity", "Description",
                                                                  "IP Address", "Session ID", "Activity Date");

    public PlayerAuditReport(final Collection<PlayerActivity> activities) {
        super(activities, CSV_HEADERS, AuditReportType.PLAYER_AUDIT);
    }

    @Override
    public String getSummary() {
        final StringBuilder builder = generateHeaders();
        final Joiner joiner = Joiner.on(ROW_DELIMITER).useForNull("");
        Player player;
        for (final PlayerActivity activity : getActivities()) {
            player = activity.getPlayer();
            builder.append(joiner.join(player.getId(), player.getEmailAddress(), player.getFirstName() + " " + player.getLastName(),
                                       activity.getActivity(), activity.getDescription(), activity.getIpAddress(), activity.getSessionId(),
                                       activity.getActivityDate()));
            builder.append('\n');
        }
        return builder.toString();
    }
}
