package com.cs.payment;

import com.cs.player.Player;

import com.google.common.base.Objects;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.NotEmpty;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Converts;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.Version;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.EnumSet;
import java.util.Set;

import static javax.persistence.EnumType.STRING;
import static javax.persistence.GenerationType.IDENTITY;
import static javax.persistence.TemporalType.TIMESTAMP;

/**
 * @author Omid Alaepour
 */
@Entity
@Table(name = "payment_transactions")
@Converts(value = {@Convert(attributeName = "amount", converter = MoneyConverter.class)})
public class PaymentTransaction implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    @Nonnull
    private Long id;

    @OneToOne
    @JoinColumn(name = "player_id", nullable = false)
    @Nonnull
    @Valid
    private Player player;

    @Column(name = "level")
    @Nonnull
    private Long level;

    @OneToOne
    @JoinColumn(name = "provider_id", nullable = false)
    @Nonnull
    @Valid
    private Provider provider;

    @Column(name = "created_date", nullable = false)
    @Temporal(TIMESTAMP)
    @Nonnull
    @NotNull(message = "paymentTransaction.createdBy.notNull")
    private Date createdDate;

    @Column(name = "event_date")
    @Temporal(TIMESTAMP)
    @Nullable
    private Date eventDate;

    @Column(name = "process_date")
    @Temporal(TIMESTAMP)
    @Nullable
    private Date processDate;

    @Column(name = "withdraw_confirm_date")
    @Temporal(TIMESTAMP)
    @Nullable
    private Date withdrawConfirmDate;

    @Column(name = "currency", nullable = false)
    @Enumerated(STRING)
    @Nonnull
    @NotNull(message = "paymentTransaction.currency.notNull")
    private Currency currency;

    @Column(name = "provider_reference", nullable = false)
    @Nonnull
    @NotEmpty(message = "paymentTransaction.providerReference.notEmpty")
    private String providerReference;

    @Column(name = "original_reference")
    @Nullable
    private String originalReference;

    @Column(name = "status", nullable = false)
    @Enumerated(STRING)
    @Nonnull
    @NotNull(message = "paymentTransaction.paymentStatus.notNull")
    private PaymentStatus paymentStatus;

    @Column(name = "payment_method")
    @Nullable
    private String paymentMethod;

    @Column(name = "authorization_operations")
    @Type(type = "com.cs.payment.OperationType")
    @Nonnull
    private Set<Operation> operations = EnumSet.noneOf(Operation.class);

    @Column(name = "reason")
    @Nullable
    private String reason;

    @Column(name = "amount", nullable = false)
    @Nonnull
    private Money amount;

    @Column(name = "event_code", nullable = false)
    @Enumerated(STRING)
    @Nonnull
    @NotNull(message = "paymentTransaction.eventCode.notNull")
    private EventCode eventCode;

    @Column(name = "uuid")
    @Nullable
    private String uuid;

    @Column(name = "withdraw_reference")
    @Nullable
    private String withdrawReference;

    @Column(name = "version")
    @Version
    private Integer version;

    public PaymentTransaction() {}

    // to deposit
    public PaymentTransaction(@Nonnull final Player player, @Nonnull final Provider provider, @Nullable final Date eventDate, @Nonnull final Currency currency,
                              @Nonnull final String providerReference, @Nullable final String originalReference, @Nonnull final PaymentStatus paymentStatus,
                              @Nullable final String paymentMethod, @Nonnull final Set<Operation> operations, @Nullable final String reason,
                              @Nonnull final Money amount, @Nonnull final EventCode eventCode, @Nullable final String uuid, @Nonnull final Date createdDate) {
        this.player = player;
        level = player.getLevel().getLevel();
        this.provider = provider;
        this.eventDate = eventDate;
        this.currency = currency;
        this.providerReference = providerReference;
        this.originalReference = originalReference;
        this.paymentStatus = paymentStatus;
        this.paymentMethod = paymentMethod;
        this.operations = operations;
        this.reason = reason;
        this.amount = amount;
        this.eventCode = eventCode;
        this.uuid = uuid;
        this.createdDate = createdDate;
    }

    // to withdraw
    public PaymentTransaction(@Nonnull final Player player, @Nonnull final Provider provider, @Nonnull final Date createdDate, @Nonnull final Currency currency,
                              @Nonnull final String providerReference, @Nonnull final PaymentStatus paymentStatus, @Nullable final String paymentMethod,
                              @Nonnull final Money amount, @Nonnull final EventCode eventCode, @Nullable final String withdrawReference) {
        this.player = player;
        level = player.getLevel().getLevel();
        this.provider = provider;
        this.createdDate = createdDate;
        this.currency = currency;
        this.providerReference = providerReference;
        this.paymentStatus = paymentStatus;
        this.paymentMethod = paymentMethod;
        this.amount = amount;
        this.eventCode = eventCode;
        this.withdrawReference = withdrawReference;
    }

    // to refund
    public PaymentTransaction(@Nonnull final Player player, @Nonnull final Provider provider, @Nonnull final Date createdDate,
                              @Nonnull final Currency currency, @Nonnull final String providerReference, @Nullable final String originalReference,
                              @Nonnull final PaymentStatus paymentStatus, @Nullable final String paymentMethod,
                              @Nonnull final Money amount, @Nonnull final EventCode eventCode) {
        this.player = player;
        level = player.getLevel().getLevel();
        this.provider = provider;
        this.createdDate = createdDate;
        this.currency = currency;
        this.providerReference = providerReference;
        this.originalReference = originalReference;
        this.paymentStatus = paymentStatus;
        this.paymentMethod = paymentMethod;
        this.amount = amount;
        this.eventCode = eventCode;
    }

    @Nonnull
    public Long getId() {
        return id;
    }

    public void setId(@Nonnull final Long id) {
        this.id = id;
    }

    @Nonnull
    public Player getPlayer() {
        return player;
    }

    public void setPlayer(@Nonnull final Player player) {
        this.player = player;
    }

    @Nonnull
    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(@Nonnull final Date createdDate) {
        this.createdDate = createdDate;
    }

    @Nullable
    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(@Nullable final Date eventDate) {
        this.eventDate = eventDate;
    }

    @Nonnull
    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(@Nonnull final Currency currency) {
        this.currency = currency;
    }

    @Nonnull
    public String getProviderReference() {
        return providerReference;
    }

    public void setProviderReference(@Nonnull final String providerReference) {
        this.providerReference = providerReference;
    }

    @Nullable
    public String getOriginalReference() {
        return originalReference;
    }

    public void setOriginalReference(@Nullable final String originalReference) {
        this.originalReference = originalReference;
    }

    @Nonnull
    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(@Nonnull final PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    @Nullable
    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(@Nullable final String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    @Nonnull
    public Set<Operation> getOperations() {
        return operations;
    }

    public void setOperations(@Nonnull final Set<Operation> operations) {
        this.operations = operations;
    }

    @Nullable
    public String getReason() {
        return reason;
    }

    public void setReason(@Nullable final String reason) {
        this.reason = reason;
    }

    @Nonnull
    public Money getAmount() {
        return amount;
    }

    public void setAmount(@Nonnull final Money amount) {
        this.amount = amount;
    }

    @Nonnull
    public EventCode getEventCode() {
        return eventCode;
    }

    public void setEventCode(@Nonnull final EventCode eventCode) {
        this.eventCode = eventCode;
    }

    @Nullable
    public String getUuid() {
        return uuid;
    }

    public void setUuid(@Nullable final String uuid) {
        this.uuid = uuid;
    }

    @Nonnull
    public Provider getProvider() {
        return provider;
    }

    public void setProvider(@Nonnull final Provider provider) {
        this.provider = provider;
    }

    @Nullable
    public String getWithdrawReference() {
        return withdrawReference;
    }

    public void setWithdrawReference(@Nullable final String withdrawReference) {
        this.withdrawReference = withdrawReference;
    }

    @Nullable
    public Date getProcessDate() {
        return processDate;
    }

    public void setProcessDate(@Nullable final Date processDate) {
        this.processDate = processDate;
    }

    @Nullable
    public Date getWithdrawConfirmDate() {
        return withdrawConfirmDate;
    }

    public void setWithdrawConfirmDate(@Nullable final Date withdrawConfirmDate) {
        this.withdrawConfirmDate = withdrawConfirmDate;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(final Integer version) {
        this.version = version;
    }

    public Long getLevel() {
        return level;
    }

    public void setLevel(final Long level) {
        this.level = level;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final PaymentTransaction that = (PaymentTransaction) o;

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
                .add("player", player.getId())
                .add("provider", provider.getId())
                .add("createdDate", createdDate)
                .add("eventDate", eventDate)
                .add("processDate", processDate)
                .add("withdrawConfirmDate", withdrawConfirmDate)
                .add("currency", currency)
                .add("providerReference", providerReference)
                .add("originalReference", originalReference)
                .add("paymentStatus", paymentStatus)
                .add("paymentMethod", paymentMethod)
                .add("operations", operations)
                .add("reason", reason)
                .add("amount", amount)
                .add("eventCode", eventCode)
                .add("uuid", uuid)
                .add("withdrawReference", withdrawReference)
                .add("version", version)
                .add("level", level)
                .toString();
    }
}
