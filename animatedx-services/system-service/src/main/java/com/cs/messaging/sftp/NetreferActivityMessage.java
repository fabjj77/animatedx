package com.cs.messaging.sftp;

import com.google.common.base.Joiner;
import com.google.common.base.Objects;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author Joakim Gottzén
 */
public class NetreferActivityMessage extends NetreferMessage {

    private static final long serialVersionUID = 1L;

    private static final List<String> CSV_HEADERS = Arrays.asList("CustomerID", "ActivityDate", "ProductID", "Gross Revenue", "Bonuses", "Adjustments", "Deposits",
                                                                  "Blings", "Turnover", "Withdrawals", "Transactions", "AdjustmentTypeID");

    private final List<PlayerActivity> playerActivities;

    public NetreferActivityMessage(final List<PlayerActivity> playerActivities, final Date activityDate) {
        super("activity-", activityDate);
        this.playerActivities = new ArrayList<>(playerActivities);
    }

    @Override
    public String getPayload() {
        final StringBuilder builder = new StringBuilder(Joiner.on(HEADER_DELIMITER).join(CSV_HEADERS) + '\n');
        final Joiner joiner = Joiner.on(ROW_DELIMITER).useForNull("");
        final SimpleDateFormat dateFormat = getDateFormat();
        for (final PlayerActivity playerActivity : playerActivities) {
            builder.append(joiner.join(playerActivity.getCustomerId(), dateFormat.format(playerActivity.getActivityDate()), playerActivity.getProductId(),
                                       playerActivity.getGrossRevenue(), playerActivity.getBonuses(), playerActivity.getAdjustments(), playerActivity.getDeposits(),
                                       playerActivity.getBlings(), playerActivity.getTurnover(), playerActivity.getWithdrawals(), playerActivity.getTransactions(),
                                       playerActivity.getAdjustmentTypeId()));
            builder.append('\n');
        }
        return builder.toString();
    }

    @Override
    protected boolean isEmpty() {
        return playerActivities.isEmpty();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final NetreferActivityMessage that = (NetreferActivityMessage) o;

        return Objects.equal(playerActivities, that.playerActivities);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(playerActivities);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("headers", getHeaders())
                .add("size", playerActivities.size())
                .toString();
    }
}
