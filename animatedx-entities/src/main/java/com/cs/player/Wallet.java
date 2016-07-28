package com.cs.player;

import com.cs.bonus.PlayerBonus;
import com.cs.payment.Money;
import com.cs.payment.MoneyConverter;

import com.google.common.base.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Converts;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

import static javax.persistence.GenerationType.IDENTITY;

/**
 * @author Joakim Gottzén
 */
@Entity
@Table(name = "wallets")
@Converts(value = {
        @Convert(attributeName = "moneyBalance", converter = MoneyConverter.class),
        @Convert(attributeName = "reservedBalance", converter = MoneyConverter.class),
        @Convert(attributeName = "bonusBalance", converter = MoneyConverter.class),
        @Convert(attributeName = "turnoverCashback", converter = MoneyConverter.class),
        @Convert(attributeName = "reservedBonusBalance", converter = MoneyConverter.class),
        @Convert(attributeName = "reservedBonusBonus", converter = MoneyConverter.class),
        @Convert(attributeName = "reservedBonusProgressionGoal", converter = MoneyConverter.class),
        @Convert(attributeName = "bonusConversionProgress", converter = MoneyConverter.class),
        @Convert(attributeName = "bonusConversionGoal", converter = MoneyConverter.class),
        @Convert(attributeName = "accumulatedCashback", converter = MoneyConverter.class),
        @Convert(attributeName = "accumulatedMoneyTurnover", converter = MoneyConverter.class),
        @Convert(attributeName = "accumulatedBonusTurnover", converter = MoneyConverter.class),
        @Convert(attributeName = "levelProgress", converter = MoneyConverter.class),
        @Convert(attributeName = "accumulatedWeeklyTurnover", converter = MoneyConverter.class),
        @Convert(attributeName = "accumulatedMonthlyTurnover", converter = MoneyConverter.class),
        @Convert(attributeName = "accumulatedMonthlyBonusTurnover", converter = MoneyConverter.class),
        @Convert(attributeName = "accumulatedDailyLoss", converter = MoneyConverter.class),
        @Convert(attributeName = "accumulatedWeeklyLoss", converter = MoneyConverter.class),
        @Convert(attributeName = "accumulatedMonthlyLoss", converter = MoneyConverter.class),
        @Convert(attributeName = "accumulatedDailyBet", converter = MoneyConverter.class),
        @Convert(attributeName = "accumulatedWeeklyBet", converter = MoneyConverter.class),
        @Convert(attributeName = "accumulatedMonthlyBet", converter = MoneyConverter.class),
        @Convert(attributeName = "accumulatedDeposit", converter = MoneyConverter.class),
        @Convert(attributeName = "accumulatedWithdrawal", converter = MoneyConverter.class)
})
public class Wallet implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    @Nonnull
    private Long id;

    @OneToOne
    @JoinColumn(name = "player_id", referencedColumnName = "id")
    @Nonnull
    @Valid
    private Player player;

    @Column(name = "money_balance", nullable = false)
    @Nonnull
    @NotNull
    private Money moneyBalance;

    @Column(name = "reserved_balance", nullable = false)
    @Nonnull
    @NotNull
    private Money reservedBalance;

    @Column(name = "credits_balance", nullable = false)
    @Nonnull
    @NotNull
    private Integer creditsBalance;

    @Column(name = "accumulated_cashback", nullable = false)
    @Nonnull
    @NotNull
    private Money accumulatedCashback;

    @Column(name = "accumulated_money_turnover", nullable = false)
    @Nonnull
    @NotNull
    private Money accumulatedMoneyTurnover;

    @Column(name = "accumulated_bonus_turnover", nullable = false)
    @Nonnull
    @NotNull
    private Money accumulatedBonusTurnover;

    @Column(name = "level_progress", nullable = false)
    @Nonnull
    @NotNull
    private Money levelProgress;

    @Column(name = "accumulated_weekly_turnover", nullable = false)
    @Nonnull
    @NotNull
    private Money accumulatedWeeklyTurnover;

    @Column(name = "accumulated_monthly_turnover", nullable = false)
    @Nonnull
    @NotNull
    private Money accumulatedMonthlyTurnover;

    @Column(name = "accumulated_monthly_bonus_turnover", nullable = false)
    @Nonnull
    @NotNull
    private Money accumulatedMonthlyBonusTurnover;

    @Column(name = "accumulated_daily_loss", nullable = false)
    @Nonnull
    @NotNull
    private Money accumulatedDailyLoss;

    @Column(name = "accumulated_weekly_loss", nullable = false)
    @Nonnull
    @NotNull
    private Money accumulatedWeeklyLoss;

    @Column(name = "accumulated_monthly_loss", nullable = false)
    @Nonnull
    @NotNull
    private Money accumulatedMonthlyLoss;

    @Column(name = "accumulated_daily_bet", nullable = false)
    @Nonnull
    @NotNull
    private Money accumulatedDailyBet;

    @Column(name = "accumulated_weekly_bet", nullable = false)
    @Nonnull
    @NotNull
    private Money accumulatedWeeklyBet;

    @Column(name = "accumulated_monthly_bet", nullable = false)
    @Nonnull
    @NotNull
    private Money accumulatedMonthlyBet;

    @Column(name = "accumulated_deposits", nullable = false)
    @Nonnull
    @NotNull
    private Money accumulatedDeposit;

    @Column(name = "accumulated_withdrawals", nullable = false)
    @Nonnull
    @NotNull
    private Money accumulatedWithdrawal;

    @Transient
    @Nullable
    private BigDecimal nextLevelPercentage;

    @OneToOne
    @JoinColumn(name = "player_bonus_id")
    @Nullable
    private PlayerBonus activePlayerBonus;

    public Wallet() {}

    public Wallet(@Nonnull final Player player) {
        this.player = player;
        moneyBalance = Money.ZERO;
        reservedBalance = Money.ZERO;
        creditsBalance = 0;
        accumulatedCashback = Money.ZERO;
        accumulatedMoneyTurnover = Money.ZERO;
        accumulatedBonusTurnover = Money.ZERO;
        levelProgress = Money.ZERO;
        accumulatedWeeklyTurnover = Money.ZERO;
        accumulatedMonthlyTurnover = Money.ZERO;
        accumulatedMonthlyBonusTurnover = Money.ZERO;
        accumulatedDailyLoss = Money.ZERO;
        accumulatedWeeklyLoss = Money.ZERO;
        accumulatedMonthlyLoss = Money.ZERO;
        accumulatedDailyBet = Money.ZERO;
        accumulatedWeeklyBet = Money.ZERO;
        accumulatedMonthlyBet = Money.ZERO;
        nextLevelPercentage = BigDecimal.ZERO;
        accumulatedDeposit = Money.ZERO;
        accumulatedWithdrawal = Money.ZERO;
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
    public Money getMoneyBalance() {
        return moneyBalance;
    }

    public void setMoneyBalance(@Nonnull final Money moneyBalance) {
        this.moneyBalance = moneyBalance;
    }

    @Nonnull
    public Money getReservedBalance() {
        return reservedBalance;
    }

    public void setReservedBalance(@Nonnull final Money reservedBalance) {
        this.reservedBalance = reservedBalance;
    }

    @Nonnull
    public Integer getCreditsBalance() {
        return creditsBalance;
    }

    public void setCreditsBalance(@Nonnull final Integer creditsBalance) {
        this.creditsBalance = creditsBalance;
    }

    @Nonnull
    public Money getAccumulatedCashback() {
        return accumulatedCashback;
    }

    public void setAccumulatedCashback(@Nonnull final Money accumulatedCashback) {
        this.accumulatedCashback = accumulatedCashback;
    }

    @Nonnull
    public Money getAccumulatedMoneyTurnover() {
        return accumulatedMoneyTurnover;
    }

    public void setAccumulatedMoneyTurnover(@Nonnull final Money accumulatedMoneyTurnover) {
        this.accumulatedMoneyTurnover = accumulatedMoneyTurnover;
    }

    @Nonnull
    public Money getAccumulatedBonusTurnover() {
        return accumulatedBonusTurnover;
    }

    public void setAccumulatedBonusTurnover(@Nonnull final Money accumulatedBonusTurnover) {
        this.accumulatedBonusTurnover = accumulatedBonusTurnover;
    }

    @Nonnull
    public Money getLevelProgress() {
        return levelProgress;
    }

    public void setLevelProgress(@Nonnull final Money levelProgress) {
        this.levelProgress = levelProgress;
    }

    @Nonnull
    public Money getAccumulatedWeeklyTurnover() {
        return accumulatedWeeklyTurnover;
    }

    public void setAccumulatedWeeklyTurnover(@Nonnull final Money accumulatedWeeklyTurnover) {
        this.accumulatedWeeklyTurnover = accumulatedWeeklyTurnover;
    }

    @Nonnull
    public Money getAccumulatedMonthlyTurnover() {
        return accumulatedMonthlyTurnover;
    }

    public void setAccumulatedMonthlyTurnover(@Nonnull final Money accumulatedMonthlyTurnover) {
        this.accumulatedMonthlyTurnover = accumulatedMonthlyTurnover;
    }

    @Nonnull
    public Money getAccumulatedMonthlyBonusTurnover() {
        return accumulatedMonthlyBonusTurnover;
    }

    public void setAccumulatedMonthlyBonusTurnover(@Nonnull final Money accumulatedMonthlyBonusTurnover) {
        this.accumulatedMonthlyBonusTurnover = accumulatedMonthlyBonusTurnover;
    }

    @Nonnull
    public Money getAccumulatedDailyLoss() {
        return accumulatedDailyLoss;
    }

    @Nonnull
    public Money getAccumulatedWeeklyLoss() {
        return accumulatedWeeklyLoss;
    }

    @Nonnull
    public Money getAccumulatedMonthlyLoss() {
        return accumulatedMonthlyLoss;
    }

    @Nonnull
    public Money getAccumulatedDailyBet() {
        return accumulatedDailyBet;
    }

    @Nonnull
    public Money getAccumulatedWeeklyBet() {
        return accumulatedWeeklyBet;
    }

    @Nonnull
    public Money getAccumulatedMonthlyBet() {
        return accumulatedMonthlyBet;
    }

    @Nullable
    public BigDecimal getNextLevelPercentage() {
        return nextLevelPercentage;
    }

    public void setNextLevelPercentage(@Nullable final BigDecimal nextLevelPercentage) {
        this.nextLevelPercentage = nextLevelPercentage;
    }

    @Nonnull
    public Money getAccumulatedDeposit() {
        return accumulatedDeposit;
    }

    public void setAccumulatedDeposit(@Nonnull final Money accumulatedDeposit) {
        this.accumulatedDeposit = accumulatedDeposit;
    }

    @Nonnull
    public Money getAccumulatedWithdrawal() {
        return accumulatedWithdrawal;
    }

    public void setAccumulatedWithdrawal(@Nonnull final Money accumulatedWithdrawal) {
        this.accumulatedWithdrawal = accumulatedWithdrawal;
    }

    @Nonnull
    public Money getBonusBalance() {
        if (activePlayerBonus != null) {
            return activePlayerBonus.getCurrentBalance() != null ? activePlayerBonus.getCurrentBalance() : Money.ZERO;
        }
        return Money.ZERO;
    }

    @Nonnull
    public Money getBonusConversionProgress() {
        if (activePlayerBonus != null) {
            return activePlayerBonus.getBonusConversionProgress() != null ? activePlayerBonus.getBonusConversionProgress() : Money.ZERO;
        }
        return Money.ZERO;
    }

    @Nonnull
    public Money getBonusConversionGoal() {
        if (activePlayerBonus != null) {
            return activePlayerBonus.getBonusConversionGoal() != null ? activePlayerBonus.getBonusConversionGoal() : Money.ZERO;
        }
        return Money.ZERO;
    }

    @Nullable
    public PlayerBonus getActivePlayerBonus() {
        return activePlayerBonus;
    }

    public void setActivePlayerBonus(@Nullable final PlayerBonus activePlayerBonus) {
        this.activePlayerBonus = activePlayerBonus;
    }

    public Money getAccumulatedLossAmountByTimeUnit(final TimeUnit timeUnit) {
        switch (timeUnit) {
            case DAY:
                return accumulatedDailyLoss;
            case WEEK:
                return accumulatedWeeklyLoss;
            case MONTH:
                return accumulatedMonthlyLoss;
        }
        throw new IllegalArgumentException("Unsupported time unit " + timeUnit);
    }

    public Money setAccumulatedLossAmountByTimeUnit(final TimeUnit timeUnit, final Money amount) {
        switch (timeUnit) {
            case DAY:
                accumulatedDailyLoss = amount;
                return accumulatedDailyLoss;
            case WEEK:
                accumulatedWeeklyLoss = amount;
                return accumulatedWeeklyLoss;
            case MONTH:
                accumulatedMonthlyLoss = amount;
                return accumulatedMonthlyLoss;
        }
        throw new IllegalArgumentException("Unsupported time unit " + timeUnit);
    }

    public Money getAccumulatedBetAmountByTimeUnit(final TimeUnit timeUnit) {
        switch (timeUnit) {
            case DAY:
                return accumulatedDailyBet;
            case WEEK:
                return accumulatedWeeklyBet;
            case MONTH:
                return accumulatedMonthlyBet;
        }
        throw new IllegalArgumentException("Unsupported time unit " + timeUnit);
    }

    public Money setAccumulatedBetAmountByTimeUnit(final TimeUnit timeUnit, final Money amount) {
        switch (timeUnit) {
            case DAY:
                accumulatedDailyBet = amount;
                return accumulatedDailyBet;
            case WEEK:
                accumulatedWeeklyBet = amount;
                return accumulatedWeeklyBet;
            case MONTH:
                accumulatedMonthlyBet = amount;
                return accumulatedMonthlyBet;
        }
        throw new IllegalArgumentException("Unsupported time unit " + timeUnit);
    }

    public Money getTotalBalance() {
        return moneyBalance.add(getBonusBalance());
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final Wallet that = (Wallet) o;

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
                .add("moneyBalance", moneyBalance)
                .add("reservedBalance", reservedBalance)
                .add("creditsBalance", creditsBalance)
                .add("accumulatedCashback", accumulatedCashback)
                .add("accumulatedMoneyTurnover", accumulatedMoneyTurnover)
                .add("accumulatedBonusTurnover", accumulatedBonusTurnover)
                .add("levelProgress", levelProgress)
                .add("accumulatedWeeklyTurnover", accumulatedWeeklyTurnover)
                .add("accumulatedMonthlyTurnover", accumulatedMonthlyTurnover)
                .add("accumulatedMonthlyBonusTurnover", accumulatedMonthlyBonusTurnover)
                .add("accumulatedDailyLoss", accumulatedDailyLoss)
                .add("accumulatedWeeklyLoss", accumulatedWeeklyLoss)
                .add("accumulatedMonthlyLoss", accumulatedMonthlyLoss)
                .add("accumulatedDailyBet", accumulatedDailyBet)
                .add("accumulatedWeeklyBet", accumulatedWeeklyBet)
                .add("accumulatedMonthlyBet", accumulatedMonthlyBet)
                .add("nextLevelTurnoverTarget", nextLevelPercentage)
                .add("accumulatedDeposit", accumulatedDeposit)
                .add("accumulatedWithdrawal", accumulatedWithdrawal)
                .add("activePlayerBonus", activePlayerBonus != null ? activePlayerBonus.getId() : null)
                .toString();
    }
}
