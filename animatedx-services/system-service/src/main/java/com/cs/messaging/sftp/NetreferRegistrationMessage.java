package com.cs.messaging.sftp;

import com.google.common.base.Joiner;
import com.google.common.base.Objects;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author Joakim Gottzén
 */
public class NetreferRegistrationMessage extends NetreferMessage {

    private static final long serialVersionUID = 1L;

    @SuppressWarnings("SpellCheckingInspection")
    private static final List<String> CSV_HEADERS = Arrays.asList("CustomerID", "CountryID", "registrationip", "Btag", "RegDate");

    private final List<PlayerRegistration> playerRegistrations;

    public NetreferRegistrationMessage(final List<PlayerRegistration> playerRegistrations, final Date activityDate) {
        super("registration-", activityDate);
        this.playerRegistrations = playerRegistrations;
    }

    @Override
    public String getPayload() {
        final StringBuilder builder = new StringBuilder(Joiner.on(HEADER_DELIMITER).join(CSV_HEADERS) + '\n');
        final Joiner joiner = Joiner.on(ROW_DELIMITER).useForNull("");
        final SimpleDateFormat dateFormat = getDateFormat();
        for (final PlayerRegistration playerRegistration : playerRegistrations) {
            builder.append(joiner.join(playerRegistration.getCustomerId(), playerRegistration.getCountryId(), playerRegistration.getRegistrationIp(),
                                       playerRegistration.getBTag(), dateFormat.format(playerRegistration.getRegistrationDate())));
            builder.append('\n');
        }
        return builder.toString();
    }

    @Override
    protected boolean isEmpty() {
        return playerRegistrations.isEmpty();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final NetreferRegistrationMessage that = (NetreferRegistrationMessage) o;

        return Objects.equal(playerRegistrations, that.playerRegistrations);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(playerRegistrations);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("headers", getHeaders())
                .add("size", playerRegistrations.size())
                .toString();
    }
}
