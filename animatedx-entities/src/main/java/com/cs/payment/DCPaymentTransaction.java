package com.cs.payment;

import com.cs.player.Player;

import com.google.common.base.Objects;
import org.hibernate.annotations.Type;

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
import java.io.Serializable;
import java.util.Date;

import static javax.persistence.EnumType.STRING;
import static javax.persistence.GenerationType.IDENTITY;
import static javax.persistence.TemporalType.TIMESTAMP;

/**
 * @author Hadi Movaghar
 */
@Entity
@Table(name = "devcode_transactions")
@Converts(value = {@Convert(attributeName = "amount", converter = MoneyConverter.class),
        @Convert(attributeName = "fee", converter = MoneyConverter.class),
        @Convert(attributeName = "pspAmount", converter = MoneyConverter.class)})
public class DCPaymentTransaction implements Serializable {

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
    private Player player; // authorize = M / trans = M /  cancel = M

    @Column(name = "event_type", nullable = false)
    @Enumerated(STRING)
    @Nonnull
    private DCEventType dcEventType;

    @Column(name = "level")
    @Nonnull
    private Long level;

    @Column(name = "authorization_code")
    @Nullable
    private String authorizationCode; // authCode // authorize = R / trans = O /  cancel = M

    @Column(name = "amount", nullable = false)
    @Nonnull
    private Money amount; // authorize = M / trans = M /  cancel = M

    @Column(name = "currency", nullable = false)
    @Enumerated(STRING)
    @Nonnull
    private Currency currency; // authorize = M / trans = M /  cancel = M

    @Column(name = "transaction_id", nullable = false)
    @Nonnull
    private String transactionId; // transId // authorize = M / trans = M /  cancel = M

    @Column(name = "transaction_type", nullable = false)
    @Nonnull
    private Integer transactionType; // txTypeId // authorize = M / trans = M /  cancel = M

    @Column(name = "transaction_name", nullable = false)
    @Nonnull
    private String transactionName; // txName // authorize = M / trans = M /  cancel = M

    @Column(name = "account_id")
    @Nullable
    private String accountId; // authorize = O / trans = O /  cancel = O

    @Column(name = "masked_account")
    @Nullable
    private String maskedAccount; // authorize = O / trans = O /  cancel = O

    @Column(name = "attributes")
    @Nullable
    private String attributes; // authorize = O / trans = O /  cancel = O

    @Column(name = "bonus_code")
    @Nullable
    private String bonusCode; // from attributes

    @Column(name = "original_transaction")
    @Nullable
    private String originalTransaction; // refTxId // authorize = X / trans = O /  cancel = X

    @Column(name = "transaction_provider")
    @Nullable
    private String transactionProvider; // provider // authorize = X / trans = M /  cancel = M

    @Column(name = "status_code")
    @Nullable
    private String statusCode; // authorize = X / trans = X /  cancel = O

    @Column(name = "psp_status_code")
    @Nullable
    private String pspStatusCode;  // authorize = X / trans = X /  cancel = O

    @Column(name = "success", nullable = false)
    @Type(type = "org.hibernate.type.NumericBooleanType")
    @Nonnull
    private Boolean success; // authorize = M / trans = M    /  cancel = M

    @Column(name = "error_code")
    @Nullable
    private Integer errorCode; // authorize = OM / trans = OM /  cancel = OM

    @Column(name = "error_message")
    @Nullable
    private String errorMessage;  // authorize = OM / trans = OM /  cancel = OM

    @OneToOne
    @JoinColumn(name = "provider_id", nullable = false)
    @Nonnull
    @Valid
    private Provider provider;

    @Column(name = "created_date", nullable = false)
    @Temporal(TIMESTAMP)
    @Nonnull
    private Date createdDate;

    @Column(name = "version")
    @Version
    private Integer version;

    @Column(name = "fee")
    @Nullable
    private Money fee;

    @Column(name = "fee_currency")
    @Enumerated(STRING)
    @Nullable
    private Currency feeCurrency;

    @Column(name = "psp_amount")
    @Nullable
    private Money pspAmount;

    @Column(name = "psp_currency")
    @Enumerated(STRING)
    @Nullable
    private Currency pspCurrency;

    @Column(name = "modified_date")
    @Temporal(TIMESTAMP)
    @Nullable
    private Date modifiedDate;

    public DCPaymentTransaction() {
    }

    @Nonnull
    public Long getId() {
        return id;
    }

    @Nonnull
    public Player getPlayer() {
        return player;
    }

    public void setPlayer(@Nonnull final Player player) {
        this.player = player;
    }

    @Nonnull
    public DCEventType getDcEventType() {
        return dcEventType;
    }

    public void setDcEventType(@Nonnull final DCEventType dcEventType) {
        this.dcEventType = dcEventType;
    }

    @Nonnull
    public Long getLevel() {
        return level;
    }

    public void setLevel(@Nonnull final Long level) {
        this.level = level;
    }

    @Nullable
    public String getAuthorizationCode() {
        return authorizationCode;
    }

    public void setAuthorizationCode(@Nullable final String authorizationCode) {
        this.authorizationCode = authorizationCode;
    }

    @Nonnull
    public Money getAmount() {
        return amount;
    }

    public void setAmount(@Nonnull final Money amount) {
        this.amount = amount;
    }

    @Nonnull
    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(@Nonnull final Currency currency) {
        this.currency = currency;
    }

    @Nonnull
    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(@Nonnull final String transactionId) {
        this.transactionId = transactionId;
    }

    @Nonnull
    public Integer getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(@Nonnull final Integer transactionType) {
        this.transactionType = transactionType;
    }

    @Nonnull
    public String getTransactionName() {
        return transactionName;
    }

    public void setTransactionName(@Nonnull final String transactionName) {
        this.transactionName = transactionName;
    }

    @Nullable
    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(@Nullable final String accountId) {
        this.accountId = accountId;
    }

    @Nullable
    public String getMaskedAccount() {
        return maskedAccount;
    }

    public void setMaskedAccount(@Nullable final String maskedAccount) {
        this.maskedAccount = maskedAccount;
    }

    @Nullable
    public String getAttributes() {
        return attributes;
    }

    public void setAttributes(@Nullable final String attributes) {
        this.attributes = attributes;
    }

    @Nullable
    public String getBonusCode() {
        return bonusCode;
    }

    public void setBonusCode(@Nullable final String bonusCode) {
        this.bonusCode = bonusCode;
    }

    @Nullable
    public String getOriginalTransaction() {
        return originalTransaction;
    }

    public void setOriginalTransaction(@Nullable final String originalTransaction) {
        this.originalTransaction = originalTransaction;
    }

    @Nullable
    public String getTransactionProvider() {
        return transactionProvider;
    }

    public void setTransactionProvider(@Nullable final String transactionProvider) {
        this.transactionProvider = transactionProvider;
    }

    @Nullable
    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(@Nullable final String statusCode) {
        this.statusCode = statusCode;
    }

    @Nullable
    public String getPspStatusCode() {
        return pspStatusCode;
    }

    public void setPspStatusCode(@Nullable final String pspStatusCode) {
        this.pspStatusCode = pspStatusCode;
    }

    @Nonnull
    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(@Nonnull final Boolean success) {
        this.success = success;
    }

    @Nullable
    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(@Nullable final Integer errorCode) {
        this.errorCode = errorCode;
    }

    @Nullable
    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(@Nullable final String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Nonnull
    public Provider getProvider() {
        return provider;
    }

    public void setProvider(@Nonnull final Provider provider) {
        this.provider = provider;
    }

    @Nonnull
    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(@Nonnull final Date createdDate) {
        this.createdDate = createdDate;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(final Integer version) {
        this.version = version;
    }

    @Nullable
    public Money getFee() {
        return fee;
    }

    public void setFee(@Nullable final Money fee) {
        this.fee = fee;
    }

    @Nullable
    public Currency getFeeCurrency() {
        return feeCurrency;
    }

    public void setFeeCurrency(@Nullable final Currency feeCurrency) {
        this.feeCurrency = feeCurrency;
    }

    @Nullable
    public Money getPspAmount() {
        return pspAmount;
    }

    public void setPspAmount(@Nullable final Money pspAmount) {
        this.pspAmount = pspAmount;
    }

    @Nullable
    public Currency getPspCurrency() {
        return pspCurrency;
    }

    public void setPspCurrency(@Nullable final Currency pspCurrency) {
        this.pspCurrency = pspCurrency;
    }

    @Nullable
    public Date getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(@Nullable final Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final DCPaymentTransaction that = (DCPaymentTransaction) o;

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
                .add("player", player)
                .add("dcEventType", dcEventType)
                .add("level", level)
                .add("authorizationCode", authorizationCode)
                .add("amount", amount)
                .add("currency", currency)
                .add("transactionId", transactionId)
                .add("transactionType", transactionType)
                .add("transactionName", transactionName)
                .add("accountId", accountId)
                .add("maskedAccount", maskedAccount)
                .add("attributes", attributes)
                .add("bonusCode", bonusCode)
                .add("originalTransaction", originalTransaction)
                .add("transactionProvider", transactionProvider)
                .add("statusCode", statusCode)
                .add("pspStatusCode", pspStatusCode)
                .add("success", success)
                .add("errorCode", errorCode)
                .add("errorMessage", errorMessage)
                .add("provider", provider)
                .add("createdDate", createdDate)
                .add("version", version)
                .add("fee", fee)
                .add("feeCurrency", feeCurrency)
                .add("pspAmount", pspAmount)
                .add("pspCurrency", pspCurrency)
                .add("modifiedDate", modifiedDate)
                .toString();
    }
}
