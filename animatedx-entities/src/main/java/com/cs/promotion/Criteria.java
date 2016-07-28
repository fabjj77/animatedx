package com.cs.promotion;

import com.cs.avatar.Level;
import com.cs.payment.Money;
import com.cs.payment.MoneyConverter;
import com.cs.persistence.Country;

import com.google.common.base.Objects;
import org.hibernate.annotations.Type;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Converts;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;
import java.util.Set;

/**
 * @author Hadi Movaghar
 */
@Embeddable
@Converts(value = @Convert(attributeName = "amount", converter = MoneyConverter.class))
public class Criteria implements Serializable {

    private static final long serialVersionUID = 1L;

    @ManyToOne
    @JoinColumn(name = "required_level", nullable = false)
    @Nullable
    private Level level;

    @Column(name = "required_amount")
    @Nullable
    private Money amount;

    @Column(name = "required_repetition")
    @Nullable
    private Integer repetition;

    @Column(name = "allowed_countries", nullable = false)
    @Type(type = "com.cs.persistence.CountryType")
    @Nonnull
    private Set<Country> allowedCountries;

    @Embedded
    @Nullable
    private TimeCriteria timeCriteria;

    public Criteria() {
    }

    @Nullable
    public Level getLevel() {
        return level;
    }

    public void setLevel(@Nullable final Level level) {
        this.level = level;
    }

    @Nullable
    public Money getAmount() {
        return amount;
    }

    public void setAmount(@Nullable final Money amount) {
        this.amount = amount;
    }

    @Nullable
    public Integer getRepetition() {
        return repetition;
    }

    public void setRepetition(@Nullable final Integer repetition) {
        this.repetition = repetition;
    }

    @Nonnull
    public Set<Country> getAllowedCountries() {
        return allowedCountries;
    }

    public void setAllowedCountries(@Nonnull final Set<Country> allowedCountries) {
        this.allowedCountries = allowedCountries;
    }

    @Nullable
    public TimeCriteria getTimeCriteria() {
        return timeCriteria;
    }

    public void setTimeCriteria(@Nullable final TimeCriteria timeCriteria) {
        this.timeCriteria = timeCriteria;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final Criteria that = (Criteria) o;

        return Objects.equal(level, that.level) &&
               Objects.equal(amount, that.amount) &&
               Objects.equal(repetition, that.repetition) &&
               Objects.equal(allowedCountries, that.allowedCountries) &&
               Objects.equal(timeCriteria, that.timeCriteria);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(level, amount, repetition, allowedCountries, timeCriteria);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .addValue(level != null ? level.getLevel() : null)
                .addValue(amount)
                .addValue(repetition)
                .addValue(allowedCountries)
                .addValue(timeCriteria)
                .toString();
    }
}
