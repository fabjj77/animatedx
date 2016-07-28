package com.cs.netent.wallet;

import com.cs.bonus.PlayerBonus;
import com.cs.payment.Money;

import javax.annotation.Nullable;

/**
 * @author Joakim Gottzén
 */
class Bets {

    private final Money moneyBet;
    private final Money bonusBet;
    private final boolean reachedBonusConversionGoal;
    private final PlayerBonus activePlayerBonus;

    Bets(final Money moneyBet, final Money bonusBet, final boolean reachedBonusConversionGoal, @Nullable final PlayerBonus activePlayerBonus) {
        this.moneyBet = moneyBet;
        this.bonusBet = bonusBet;
        this.reachedBonusConversionGoal = reachedBonusConversionGoal;
        this.activePlayerBonus = activePlayerBonus;
    }

    public Money getMoneyBet() {
        return moneyBet;
    }

    public Money getBonusBet() {
        return bonusBet;
    }

    public boolean reachedBonusConversionGoal() {
        return reachedBonusConversionGoal;
    }

    @Nullable
    public PlayerBonus getActivePlayerBonus() {
        return activePlayerBonus;
    }
}
