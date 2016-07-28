package com.cs.bonus;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

/**
 * @author Hadi Movaghar
 */
@XmlRootElement
@XmlEnum
public enum BonusType {
    REAL_MONEY,
    BONUS_MONEY,
    CREDITS,
    FREE_ROUND,
    DEPOSIT_BONUS,
    CREDITS_MULTIPLIER_X2,
    CREDITS_MULTIPLIER_X3;

    private static final Collection<BonusType> currentBonuses = Collections.unmodifiableCollection(Arrays.asList(BONUS_MONEY, DEPOSIT_BONUS));

    public static List<BonusType> getTriggeredBonuses(final TriggerEvent trigger) {
        List<BonusType> bonusTypes = new ArrayList<>();
        switch (trigger) {
            case SIGN_UP:
                bonusTypes = new ArrayList<>(EnumSet.of(BONUS_MONEY, FREE_ROUND));
                break;
            case LEVEL_UP:
                bonusTypes = new ArrayList<>(EnumSet.of(BONUS_MONEY, FREE_ROUND));
                break;
            case DEPOSIT:
                bonusTypes = new ArrayList<>(EnumSet.of(FREE_ROUND));
                break;
        }

        return bonusTypes;
    }

    public static Collection<BonusType> currentBonusTypes() {
        return currentBonuses;
    }
}
