package com.cs.bonus;

import java.util.List;

/**
 * @author Hadi Movaghar
 */
public enum TriggerEvent {
    SIGN_UP,
    LEVEL_UP,
    DEPOSIT;

    public List<BonusType> getBonusList() {
        return BonusType.getTriggeredBonuses(this);
    }
}
