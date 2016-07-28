package com.cs.netent.wallet;

import com.cs.bonus.PlayerBonus;
import com.cs.game.GameTransaction;
import com.cs.payment.Money;

import com.google.common.base.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

import static com.cs.payment.Money.ZERO;

/**
 * @author Joakim Gottzén
 */
class GameRoundTransactions {

    @Nonnull
    private Money winMoney = ZERO;

    @Nonnull
    private Money winBonus = ZERO;

    @Nonnull
    private Money betMoney = ZERO;

    @Nonnull
    private Money betBonus = ZERO;

    @Nonnull
    private Boolean reachedBonusConversionGoal = Boolean.FALSE;

    @Nullable
    private PlayerBonus activePlayerBonus;

    @Nonnull
    private final Boolean newRound;

    GameRoundTransactions(@Nullable final List<GameTransaction> transactions) {
        newRound = transactions != null && transactions.isEmpty();
        if (transactions != null) {
            for (final GameTransaction transaction : transactions) {
                addTransaction(transaction.getMoneyDeposit(), transaction.getMoneyWithdraw(), transaction.getBonusDeposit(), transaction.getBonusWithdraw(),
                               transaction.getActivePlayerBonus());
                if (transaction.getReachedBonusConversionGoal()) {
                    reachedBonusConversionGoal = Boolean.TRUE;
                }
            }
        }
    }

    private void addTransaction(@Nonnull final Money depositMoney, @Nonnull final Money withdrawMoney, @Nonnull final Money depositBonus,
                                @Nonnull final Money withdrawBonus, @Nullable final PlayerBonus activePlayerBonus) {
        winMoney = winMoney.add(depositMoney);
        betMoney = betMoney.add(withdrawMoney);
        winBonus = winBonus.add(depositBonus);
        betBonus = betBonus.add(withdrawBonus);
        if (this.activePlayerBonus == null) {
            this.activePlayerBonus = activePlayerBonus;
        }
    }

    @Nonnull
    public Money getBetMoney() {
        return betMoney;
    }

    @Nonnull
    public Money getBetBonus() {
        return betBonus;
    }

    @Nonnull
    public Boolean reachedBonusConversionGoal() {
        return reachedBonusConversionGoal;
    }

    boolean isRealMoneyOnlyBet() {
        return betMoney.isPositive() && betBonus.isZero();
    }

    @Nullable
    public PlayerBonus getActivePlayerBonus() {
        return activePlayerBonus;
    }

    @Nonnull
    public Boolean isNewRound() {
        return newRound;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("winMoney", winMoney)
                .add("winBonus", winBonus)
                .add("betMoney", betMoney)
                .add("betBonus", betBonus)
                .add("reachedBonusConversionGoal", reachedBonusConversionGoal)
                .add("activePlayerBonus", activePlayerBonus != null ? activePlayerBonus.getId() : null)
                .add("newRound", newRound)
                .toString();
    }
}
