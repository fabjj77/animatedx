package com.cs.messaging.sftp;

import com.google.common.base.Objects;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.Date;

/**
 * @author Joakim Gottzén
 */
public class PlayerRegistration implements Serializable {

    private static final long serialVersionUID = 1L;

    @Nonnull
    private final Long customerId;
    @Nonnull
    private final String countryId;
    @Nonnull
    private final String registrationIp;
    @Nonnull
    private final String bTag;
    @Nonnull
    private final Date registrationDate;

    public PlayerRegistration(@Nonnull final Long customerId, @Nonnull final String countryId, @Nonnull final String registrationIp, @Nonnull final String bTag,
                              @Nonnull final Date registrationDate) {
        this.customerId = customerId;
        this.countryId = countryId;
        this.registrationIp = registrationIp;
        this.bTag = bTag;
        this.registrationDate = registrationDate;
    }

    @Nonnull
    public Long getCustomerId() {
        return customerId;
    }

    @Nonnull
    public String getCountryId() {
        return countryId;
    }

    @Nonnull
    public String getRegistrationIp() {
        return registrationIp;
    }

    @Nonnull
    public String getBTag() {
        return bTag;
    }

    @Nonnull
    public Date getRegistrationDate() {
        return registrationDate;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final PlayerRegistration that = (PlayerRegistration) o;

        return Objects.equal(customerId, that.customerId) &&
               Objects.equal(countryId, that.countryId) &&
               Objects.equal(registrationIp, that.registrationIp) &&
               Objects.equal(bTag, that.bTag) &&
               Objects.equal(registrationDate, that.registrationDate);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(customerId, countryId, registrationIp, bTag, registrationDate);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("customerId", customerId)
                .add("countryId", countryId)
                .add("registrationIp", registrationIp)
                .add("bTag", bTag)
                .add("registrationDate", registrationDate)
                .toString();
    }
}
