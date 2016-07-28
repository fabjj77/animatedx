package com.cs.whitelist;

import com.google.common.base.Objects;
import org.hibernate.annotations.Type;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.regex.Pattern;

import static javax.persistence.GenerationType.IDENTITY;
import static javax.persistence.TemporalType.TIMESTAMP;

/**
 * @author Hadi Movaghar
 */
@Entity
@Table(name = "white_listed_ip_addresses")
public class WhiteListedIpAddress implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Pattern IP_ADDRESS_COMPILE_PATTERN = Pattern.compile("\\.");

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    @Nonnull
    private Long id;

    @Column(name = "from_ip")
    @Nonnull
    private Long fromIpAddress;

    @Column(name = "to_ip")
    @Nonnull
    private Long toIpAddress;

    @Column(name = "created_date", nullable = false)
    @Temporal(TIMESTAMP)
    @Nonnull
    @NotNull(message = "player.createdDate.notNull")
    private Date createdDate;

    @Column(name = "modified_date")
    @Temporal(TIMESTAMP)
    @Nullable
    private Date modifiedDate;

    @Column(name = "deleted", nullable = false)
    @Type(type = "org.hibernate.type.NumericBooleanType")
    @Nonnull
    private Boolean deleted;

    public WhiteListedIpAddress() {
    }

    @Nonnull
    public Long getId() {
        return id;
    }

    public void setId(@Nonnull final Long id) {
        this.id = id;
    }

    @Nonnull
    public Long getFromIpAddress() {
        return fromIpAddress;
    }

    public void setFromIpAddress(@Nonnull final Long fromIpAddress) {
        this.fromIpAddress = fromIpAddress;
    }

    @Nonnull
    public Long getToIpAddress() {
        return toIpAddress;
    }

    public void setToIpAddress(@Nonnull final Long toIpAddress) {
        this.toIpAddress = toIpAddress;
    }

    @Nonnull
    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(@Nonnull final Date createdDate) {
        this.createdDate = createdDate;
    }

    @Nullable
    public Date getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(@Nullable final Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    @Nonnull
    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(@Nonnull final Boolean deleted) {
        this.deleted = deleted;
    }

    public boolean isSingleIpAddress() {
        return fromIpAddress.equals(toIpAddress);
    }

    public static Long stringIPToLong(final String stringIpAddress) {
        final String[] addressArray = IP_ADDRESS_COMPILE_PATTERN.split(stringIpAddress);
        long result = 0;

        for (int i = 0; i < addressArray.length; i++) {
            final Integer block = Integer.parseInt(addressArray[i]);
            if (block > 255) {
                throw new NumberFormatException("IP address contains invalid digits : " + block);
            }

            final int power = 3 - i;
            result += block % 256 * Math.pow(256, power);
        }

        return result;
    }

    public static String longIPToString(final long longIp) {
        return (longIp >> 24 & 0xFF) + "." + (longIp >> 16 & 0xFF) + "." + (longIp >> 8 & 0xFF) + "." + (longIp & 0xFF);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final WhiteListedIpAddress that = (WhiteListedIpAddress) o;

        return Objects.equal(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .addValue(id)
                .addValue(fromIpAddress)
                .addValue(toIpAddress)
                .addValue(createdDate)
                .addValue(modifiedDate)
                .addValue(deleted)
                .toString();
    }
}
