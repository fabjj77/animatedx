package com.cs.player;

import com.cs.persistence.Country;

import com.google.common.base.Objects;
import org.hibernate.validator.constraints.NotEmpty;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

import static javax.persistence.EnumType.STRING;

/**
 * @author Joakim Gottzén
 */
@Embeddable
public class Address implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column(name = "street", nullable = false, length = 70)
    @Nonnull
    @NotEmpty(message = "address.street.notEmpty")
    private String street;

    @Column(name = "street2", nullable = true, length = 70)
    @Nullable
    private String street2;

    @Column(name = "zip_code", nullable = false, length = 10)
    @Nonnull
    @NotEmpty(message = "address.zipCode.notEmpty")
    private String zipCode;

    @Column(name = "city", nullable = false, length = 50)
    @Nonnull
    @NotEmpty(message = "address.city.notEmpty")
    private String city;

    @Column(name = "state", nullable = true, length = 50)
    @Nullable
    private String state;

    @Column(name = "country", nullable = false, length = 40)
    @Nonnull
    @NotNull(message = "address.country.notNull")
    @Enumerated(STRING)
    private Country country;

    public Address() {}

    public Address(@Nonnull final String street, @Nullable final String street2, @Nonnull final String zipCode, @Nonnull final String city,
                   @Nullable final String state, @Nonnull final Country country) {
        this.street = street;
        this.street2 = street2;
        this.zipCode = zipCode;
        this.city = city;
        this.state = state;
        this.country = country;
    }

    @Nonnull
    public String getStreet() {
        return street;
    }

    public void setStreet(@Nonnull final String street) {
        this.street = street;
    }

    @Nullable
    public String getStreet2() {
        return street2;
    }

    public void setStreet2(@Nullable final String street2) {
        this.street2 = street2;
    }

    @Nonnull
    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(@Nonnull final String zipCode) {
        this.zipCode = zipCode;
    }

    @Nonnull
    public String getCity() {
        return city;
    }

    public void setCity(@Nonnull final String city) {
        this.city = city;
    }

    @Nullable
    public String getState() {
        return state;
    }

    public void setState(@Nullable final String state) {
        this.state = state;
    }

    @Nonnull
    public Country getCountry() {
        return country;
    }

    public void setCountry(@Nonnull final Country country) {
        this.country = country;
    }

    public Address trim() {
        return new Address(street.trim(), street2 != null ? street2.trim() : null, zipCode.trim(), city.trim(), state != null ? state.trim() : null, country);
    }

    @SuppressWarnings("ConstantConditions")
    public void updateFromAddress(final Address address) {
        if (address.street != null) {
            street = address.street;
        }
        if (address.street2 != null) {
            street2 = address.street2;
        }
        if (address.zipCode != null) {
            zipCode = address.zipCode;
        }
        if (address.city != null) {
            city = address.city;
        }
        if (address.state != null) {
            state = address.state;
        }
        if (address.country != null) {
            country = address.country;
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final Address that = (Address) o;

        return Objects.equal(street, that.street) &&
               Objects.equal(street2, that.street2) &&
               Objects.equal(zipCode, that.zipCode) &&
               Objects.equal(city, that.city) &&
               Objects.equal(state, that.state) &&
               Objects.equal(country, that.country);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(street, street2, zipCode, city, state, country);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .addValue(street)
                .addValue(street2)
                .addValue(zipCode)
                .addValue(city)
                .addValue(state)
                .addValue(country)
                .toString();
    }
}
