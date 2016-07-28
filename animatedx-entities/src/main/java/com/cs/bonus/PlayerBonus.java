package com.cs.bonus;

import com.cs.payment.DCPaymentTransaction;
import com.cs.payment.Money;
import com.cs.payment.MoneyConverter;
import com.cs.payment.PaymentTransaction;

import com.google.common.base.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Converts;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import java.io.Serializable;
import java.util.Date;

import static javax.persistence.EnumType.STRING;
import static javax.persistence.GenerationType.IDENTITY;
import static javax.persistence.TemporalType.TIMESTAMP;

/**
 * @author Hadi Movaghar
 */
@Entity
@Table(name = "players_bonuses")
@Converts(value = {
        @Convert(attributeName = "usedAmount", converter = MoneyConverter.class),
        @Convert(attributeName = "currentBalance", converter = MoneyConverter.class),
        @Convert(attributeName = "bonusConversionProgress", converter = MoneyConverter.class),
        @Convert(attributeName = "bonusConversionGoal", converter = MoneyConverter.class),
        @Convert(attributeName = "maxRedemptionAmount", converter = MoneyConverter.class)
})
@AssociationOverrides(value = {
        @AssociationOverride(name = "pk.player", joinColumns = @JoinColumn(name = "player_id")),
        @AssociationOverride(name = "pk.bonus", joinColumns = @JoinColumn(name = "bonus_id"))
})
public class PlayerBonus implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    @Nonnull
    private Long id;

    @Embedded
    @Nonnull
    private PlayerBonusPair pk;

    @Column(name = "used_amount")
    @Nullable
    private Money usedAmount;

    @Column(name = "used_quantity")
    @Nullable
    private Integer usedQuantity;

    @Column(name = "created_date")
    @Temporal(TIMESTAMP)
    @Nonnull
    private Date createdDate;

    @Column(name = "activation_date")
    @Temporal(TIMESTAMP)
    @Nullable
    private Date activationDate;

    @Column(name = "used_date")
    @Temporal(TIMESTAMP)
    @Nullable
    private Date usedDate;

    @Column(name = "status", nullable = false)
    @Enumerated(STRING)
    @Nonnull
    private BonusStatus status;

    @Column(name = "current_balance", nullable = false)
    @Nullable
    private Money currentBalance;

    @Column(name = "bonus_conversion_progress", nullable = false)
    @Nullable
    private Money bonusConversionProgress;

    @Column(name = "bonus_conversion_goal", nullable = false)
    @Nullable
    private Money bonusConversionGoal;

    @Column(name = "completion_date")
    @Temporal(TIMESTAMP)
    @Nullable
    private Date completionDate;

    @OneToOne
    @JoinColumn(name = "payment_transaction_id")
    @Nullable
    private PaymentTransaction paymentTransaction;

    @OneToOne
    @JoinColumn(name = "devcode_transaction_id")
    @Nullable
    private DCPaymentTransaction dcPaymentTransaction;

    @Column(name = "max_redemption_amount")
    @Nullable
    private Money maxRedemptionAmount;

    @Column(name = "valid_from")
    @Temporal(TIMESTAMP)
    @Nullable
    private Date validFrom;

    @Column(name = "valid_to")
    @Temporal(TIMESTAMP)
    @Nullable
    private Date validTo;

    @Column(name = "level")
    @Nonnull
    private Long level;

    public PlayerBonus() {
    }

    public PlayerBonus(@Nonnull final PlayerBonusPair pk) {
        this.pk = pk;
        createdDate = new Date();
        level = pk.getPlayer().getLevel().getLevel();
        status = BonusStatus.UNUSED;
    }

    public PlayerBonus(@Nonnull final PlayerBonusPair pk, final Date activationDate) {
        this.pk = pk;
        this.activationDate = activationDate;
        createdDate = new Date();
        level = pk.getPlayer().getLevel().getLevel();
        status = BonusStatus.SCHEDULED;
    }

    @Nonnull
    public Long getId() {
        return id;
    }

    public void setId(@Nonnull final Long id) {
        this.id = id;
    }

    @Nonnull
    public PlayerBonusPair getPk() {
        return pk;
    }

    public void setPk(@Nonnull final PlayerBonusPair pk) {
        this.pk = pk;
    }

    @Nullable
    public Money getUsedAmount() {
        return usedAmount;
    }

    public void setUsedAmount(@Nullable final Money usedAmount) {
        this.usedAmount = usedAmount;
    }

    @Nullable
    public Integer getUsedQuantity() {
        return usedQuantity;
    }

    public void setUsedQuantity(@Nullable final Integer usedQuantity) {
        this.usedQuantity = usedQuantity;
    }

    @Nonnull
    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(@Nonnull final Date createdDate) {
        this.createdDate = createdDate;
    }

    @Nullable
    public Date getActivationDate() {
        return activationDate;
    }

    public void setActivationDate(@Nullable final Date activationDate) {
        this.activationDate = activationDate;
    }

    @Nonnull
    public BonusStatus getStatus() {
        return status;
    }

    public void setStatus(@Nonnull final BonusStatus bonusStatus) {
        this.status = bonusStatus;
    }

    @Nullable
    public Money getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(@Nullable final Money currentBalance) {
        this.currentBalance = currentBalance;
    }

    @Nullable
    public Money getBonusConversionProgress() {
        return bonusConversionProgress;
    }

    public void setBonusConversionProgress(@Nullable final Money bonusConversionProgress) {
        this.bonusConversionProgress = bonusConversionProgress;
    }

    @Nullable
    public Money getBonusConversionGoal() {
        return bonusConversionGoal;
    }

    public void setBonusConversionGoal(@Nullable final Money bonusConversionGoal) {
        this.bonusConversionGoal = bonusConversionGoal;
    }

    @Nullable
    public Date getUsedDate() {
        return usedDate;
    }

    public void setUsedDate(@Nullable final Date usedDate) {
        this.usedDate = usedDate;
    }

    @Nullable
    public Date getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(@Nullable final Date completionDate) {
        this.completionDate = completionDate;
    }

    @Nullable
    public PaymentTransaction getPaymentTransaction() {
        return paymentTransaction;
    }

    public void setPaymentTransaction(@Nullable final PaymentTransaction paymentTransaction) {
        this.paymentTransaction = paymentTransaction;
    }

    @Nullable
    public DCPaymentTransaction getDcPaymentTransaction() {
        return dcPaymentTransaction;
    }

    public void setDcPaymentTransaction(@Nullable final DCPaymentTransaction dcPaymentTransaction) {
        this.dcPaymentTransaction = dcPaymentTransaction;
    }

    @Nullable
    public Money getMaxRedemptionAmount() {
        return maxRedemptionAmount;
    }

    @Nullable
    public Date getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(@Nullable final Date validFrom) {
        this.validFrom = validFrom;
    }

    @Nullable
    public Date getValidTo() {
        return validTo;
    }

    @Nonnull
    public Long getLevel() {
        return level;
    }

    public void setValidTo(@Nullable final Date validTo) {
        this.validTo = validTo;
    }

    public void setMaxRedemptionAmount(@Nullable final Money maxRedemptionAmount) {
        this.maxRedemptionAmount = maxRedemptionAmount;
    }

    public boolean hasValidMonetaryFieldsToUpdate() {
        return currentBalance != null && bonusConversionProgress != null && bonusConversionGoal != null;
    }

    public boolean hasValidStatusToUpdate() {
        return BonusStatus.currentBonusStatuses().contains(status);
    }

    public boolean isExpired() {
        return pk.getBonus().isExpired() || validTo != null && validTo.before(new Date());
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final PlayerBonus that = (PlayerBonus) o;

        return Objects.equal(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("id", id)
                .add("pk", pk)
                .add("usedAmount", usedAmount)
                .add("usedQuantity", usedQuantity)
                .add("createdDate", createdDate)
                .add("activationDate", activationDate)
                .add("usedDate", usedDate)
                .add("status", status)
                .add("currentBalance", currentBalance)
                .add("bonusConversionProgress", bonusConversionProgress)
                .add("bonusConversionGoal", bonusConversionGoal)
                .add("completionDate", completionDate)
                .add("paymentTransaction", paymentTransaction != null ? paymentTransaction.getId() : null)
                .add("dcPaymentTransaction", dcPaymentTransaction != null ? dcPaymentTransaction.getId() : null)
                .add("maxRedemptionAmount", maxRedemptionAmount)
                .add("validFrom", validFrom)
                .add("validTo", validTo)
                .add("level", level)
                .toString();
    }
}
