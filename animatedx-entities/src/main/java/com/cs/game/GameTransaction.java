package com.cs.game;

import com.cs.bonus.PlayerBonus;
import com.cs.payment.Currency;
import com.cs.payment.Money;
import com.cs.payment.MoneyConverter;
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
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

import static javax.persistence.EnumType.STRING;
import static javax.persistence.GenerationType.IDENTITY;
import static javax.persistence.TemporalType.TIMESTAMP;

/**
 * @author Hadi Movaghar
 */
@Entity
@Table(name = "netent_transactions")
@Converts(value = {
        @Convert(attributeName = "moneyDeposit", converter = MoneyConverter.class),
        @Convert(attributeName = "moneyWithdraw", converter = MoneyConverter.class),
        @Convert(attributeName = "bonusDeposit", converter = MoneyConverter.class),
        @Convert(attributeName = "bonusWithdraw", converter = MoneyConverter.class),
        @Convert(attributeName = "moneyBalance", converter = MoneyConverter.class),
        @Convert(attributeName = "bonusBalance", converter = MoneyConverter.class)
})
public class GameTransaction implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Nonnull
    private Long id;

    @OneToOne
    @JoinColumn(name = "player_id", nullable = false)
    @Nonnull
    @Valid
    private Player player;

    @Column(name = "money_deposit", nullable = false)
    @Nonnull
    private Money moneyDeposit;

    @Column(name = "money_withdraw", nullable = false)
    @Nonnull
    private Money moneyWithdraw;

    @Column(name = "bonus_deposit", nullable = false)
    @Nonnull
    private Money bonusDeposit;

    @Column(name = "bonus_withdraw", nullable = false)
    @Nonnull
    private Money bonusWithdraw;

    @Column(name = "money_balance", nullable = false)
    @Nonnull
    private Money moneyBalance;

    @Column(name = "bonus_balance", nullable = false)
    @Nonnull
    private Money bonusBalance;

    @Column(name = "currency", nullable = false)
    @Enumerated(STRING)
    @Nonnull
    @NotNull(message = "netEntTransaction.currency.notNull")
    private Currency currency;

    @Column(name = "created_date", nullable = false)
    @Temporal(TIMESTAMP)
    @Nonnull
    @NotNull(message = "netEntTransaction.createdBy.notNull")
    private Date createdDate;

    @Column(name = "rollback_date")
    @Temporal(TIMESTAMP)
    @Nullable
    private Date rollbackDate;

    @Column(name = "transaction_ref", nullable = false)
    @Nonnull
    @NotEmpty(message = "netEntTransaction.transactionRef.notEmpty")
    private String transactionRef;

    @Column(name = "game_round_ref")
    @Nullable
    private String gameRoundRef;

    @Column(name = "game_id", nullable = true)
    @Nullable
    private String gameId;

    @Column(name = "reason", nullable = true)
    @Nullable
    private String reason;

    @Column(name = "session_id", nullable = true)
    @Nullable
    private String sessionId;

    @Column(name = "source", nullable = true)
    @Nullable
    private String source;

    @Column(name = "reached_bonus_conversion_goal", nullable = false)
    @Type(type = "org.hibernate.type.NumericBooleanType")
    @Nonnull
    private Boolean reachedBonusConversionGoal;

    @OneToOne
    @JoinColumn(name = "player_bonus_id")
    @Nullable
    private PlayerBonus playerBonus;

    public GameTransaction() {}

    public GameTransaction(@Nonnull final Player player, @Nonnull final Money moneyDeposit, @Nonnull final Money moneyWithdraw, @Nonnull final Money bonusDeposit,
                           @Nonnull final Money bonusWithdraw, @Nonnull final Money moneyBalance, @Nonnull final Money bonusBalance, @Nonnull final Currency currency,
                           @Nonnull final Date createdDate, @Nonnull final String transactionRef, @Nullable final String gameRoundRef, @Nullable final String gameId,
                           @Nullable final String reason, @Nullable final String sessionId, @Nullable final String source,
                           @Nonnull final Boolean reachedBonusConversionGoal, @Nullable final PlayerBonus playerBonus) {
        this.player = player;
        this.moneyDeposit = moneyDeposit;
        this.moneyWithdraw = moneyWithdraw;
        this.bonusDeposit = bonusDeposit;
        this.bonusWithdraw = bonusWithdraw;
        this.moneyBalance = moneyBalance;
        this.bonusBalance = bonusBalance;
        this.currency = currency;
        this.createdDate = createdDate;
        this.transactionRef = transactionRef;
        this.gameRoundRef = gameRoundRef;
        this.gameId = gameId;
        this.reason = reason;
        this.sessionId = sessionId;
        this.source = source;
        this.reachedBonusConversionGoal = reachedBonusConversionGoal;
        this.playerBonus = playerBonus;
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
    public Money getMoneyDeposit() {
        return moneyDeposit;
    }

    public void setMoneyDeposit(@Nonnull final Money moneyDeposit) {
        this.moneyDeposit = moneyDeposit;
    }

    @Nonnull
    public Money getMoneyWithdraw() {
        return moneyWithdraw;
    }

    public void setMoneyWithdraw(@Nonnull final Money moneyWithdraw) {
        this.moneyWithdraw = moneyWithdraw;
    }

    @Nonnull
    public Money getBonusDeposit() {
        return bonusDeposit;
    }

    public void setBonusDeposit(@Nonnull final Money bonusDeposit) {
        this.bonusDeposit = bonusDeposit;
    }

    @Nonnull
    public Money getBonusWithdraw() {
        return bonusWithdraw;
    }

    public void setBonusWithdraw(@Nonnull final Money bonusWithdraw) {
        this.bonusWithdraw = bonusWithdraw;
    }

    @Nonnull
    public Money getMoneyBalance() {
        return moneyBalance;
    }

    public void setMoneyBalance(@Nonnull final Money moneyBalance) {
        this.moneyBalance = moneyBalance;
    }

    @Nonnull
    public Money getBonusBalance() {
        return bonusBalance;
    }

    public void setBonusBalance(@Nonnull final Money bonusBalance) {
        this.bonusBalance = bonusBalance;
    }

    @Nonnull
    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(@Nonnull final Currency currency) {
        this.currency = currency;
    }

    @Nonnull
    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(@Nonnull final Date createdDate) {
        this.createdDate = createdDate;
    }

    @Nullable
    public Date getRollbackDate() {
        return rollbackDate;
    }

    public void setRollbackDate(@Nullable final Date rollbackDate) {
        this.rollbackDate = rollbackDate;
    }

    @Nonnull
    public String getTransactionRef() {
        return transactionRef;
    }

    public void setTransactionRef(@Nonnull final String transactionRef) {
        this.transactionRef = transactionRef;
    }

    @Nullable
    public String getGameRoundRef() {
        return gameRoundRef;
    }

    public void setGameRoundRef(@Nullable final String gameRoundRef) {
        this.gameRoundRef = gameRoundRef;
    }

    @Nullable
    public String getGameId() {
        return gameId;
    }

    public void setGameId(@Nullable final String gameId) {
        this.gameId = gameId;
    }

    @Nullable
    public String getReason() {
        return reason;
    }

    public void setReason(@Nullable final String reason) {
        this.reason = reason;
    }

    @Nullable
    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(@Nullable final String sessionId) {
        this.sessionId = sessionId;
    }

    @Nullable
    public String getSource() {
        return source;
    }

    public void setSource(@Nullable final String source) {
        this.source = source;
    }

    @Nonnull
    public Boolean getReachedBonusConversionGoal() {
        return reachedBonusConversionGoal;
    }

    public void setReachedBonusConversionGoal(@Nonnull final Boolean reachedBonusConversionGoal) {
        this.reachedBonusConversionGoal = reachedBonusConversionGoal;
    }

    @Nullable
    public PlayerBonus getActivePlayerBonus() {
        return playerBonus;
    }

    public void setActivePlayerBonus(@Nullable final PlayerBonus playerBonus) {
        this.playerBonus = playerBonus;
    }

    public Money getTotalWithdraw() {
        return moneyWithdraw.add(bonusWithdraw);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final GameTransaction that = (GameTransaction) o;

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
                .add("moneyDeposit", moneyDeposit)
                .add("moneyWithdraw", moneyWithdraw)
                .add("bonusDeposit", bonusDeposit)
                .add("bonusWithdraw", bonusWithdraw)
                .add("moneyBalance", moneyBalance)
                .add("bonusBalance", bonusBalance)
                .add("currency", currency)
                .add("createdDate", createdDate)
                .add("rollbackDate", rollbackDate)
                .add("transactionRef", transactionRef)
                .add("gameRoundRef", gameRoundRef)
                .add("gameId", gameId)
                .add("reason", reason)
                .add("sessionId", sessionId)
                .add("source", source)
                .add("reachedBonusConversionGoal", reachedBonusConversionGoal)
                .add("playerBonus", playerBonus != null ? playerBonus.getId() : null)
                .toString();
    }
}
