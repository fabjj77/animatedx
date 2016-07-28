package com.cs.payment;

import com.cs.persistence.Country;

import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.NotEmpty;

import javax.annotation.Nonnull;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Converts;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;

import static javax.persistence.GenerationType.IDENTITY;

/**
 * @author Hadi Movaghar
 */
@Entity
@Table(name = "payment_methods")
@Converts(value = {@Convert(attributeName = "maxDepositAmount", converter = MoneyConverter.class)})
public class PaymentMethod implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    @Nonnull
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    @Nonnull
    @NotEmpty(message = "paymentMethod.name.notEmpty")
    private String name;

    @Column(name = "method_name", nullable = false, length = 100)
    @Nonnull
    @NotEmpty(message = "paymentMethod.methodName.notEmpty")
    private String methodName;

    @Column(name = "max_deposit_amount", nullable = false)
    @Nonnull
    @NotNull
    private Money maxDepositAmount;

    @Column(name = "allowed_countries")
    @Type(type = "com.cs.persistence.CountryType")
    @Nonnull
    private Set<Country> allowedCountries;

    @Column(name = "recurring_contracts")
    @Type(type = "com.cs.payment.RecurringContractType")
    @Nonnull
    private Set<RecurringContract> recurringContracts;

    @Column(name = "embedded_frame", nullable = false)
    @Type(type = "org.hibernate.type.NumericBooleanType")
    @Nonnull
    private Boolean embeddedFrame;

    @Column(name = "enabled", nullable = false)
    @Type(type = "org.hibernate.type.NumericBooleanType")
    @Nonnull
    private Boolean enabled;

    public PaymentMethod() {
    }

    @Nonnull
    public Long getId() {
        return id;
    }

    public void setId(@Nonnull final Long id) {
        this.id = id;
    }

    @Nonnull
    public String getName() {
        return name;
    }

    public void setName(@Nonnull final String name) {
        this.name = name;
    }

    @Nonnull
    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(@Nonnull final String methodName) {
        this.methodName = methodName;
    }

    @Nonnull
    public Money getMaxDepositAmount() {
        return maxDepositAmount;
    }

    public void setMaxDepositAmount(@Nonnull final Money maxDepositAmount) {
        this.maxDepositAmount = maxDepositAmount;
    }

    @Nonnull
    public Set<Country> getAllowedCountries() {
        return allowedCountries;
    }

    public void setAllowedCountries(@Nonnull final Set<Country> allowedCountries) {
        this.allowedCountries = allowedCountries;
    }

    @Nonnull
    public Set<RecurringContract> getRecurringContracts() {
        return recurringContracts;
    }

    public void setRecurringContracts(@Nonnull final Set<RecurringContract> recurringContracts) {
        this.recurringContracts = recurringContracts;
    }

    @Nonnull
    public Boolean isEmbeddedFrame() {
        return embeddedFrame;
    }

    public void setEmbeddedFrame(@Nonnull final Boolean embeddedFrame) {
        this.embeddedFrame = embeddedFrame;
    }

    @Nonnull
    public Boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(@Nonnull final Boolean enabled) {
        this.enabled = enabled;
    }

    public String getRecurringContractsString() {
        return Joiner.on(",").skipNulls().join(recurringContracts);
    }

    public boolean isDepositAmountValid(final Money amount) {
        return maxDepositAmount.isZero() || amount.isLessOrEqualThan(maxDepositAmount);
    }

    public BigDecimal getMaxLimit() {
        return maxDepositAmount.getEuroValueInBigDecimal();
    }

    public boolean isPaymentMethodAllowedInCountry(final Country country) {
        return allowedCountries.isEmpty() || allowedCountries.contains(country);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final PaymentMethod that = (PaymentMethod) o;

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
                .addValue(name)
                .addValue(methodName)
                .addValue(maxDepositAmount)
                .addValue(allowedCountries)
                .addValue(recurringContracts)
                .addValue(embeddedFrame)
                .addValue(enabled)
                .toString();
    }
}
