package com.cs.payment;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * @author Hadi Movaghar
 */
public enum DCEventType {
    AUTHORIZE, TRANSFER, CANCEL;

    private static final Collection<DCEventType> complementaryEvents = Collections.unmodifiableCollection(Arrays.asList(TRANSFER, CANCEL));

    public static Collection<DCEventType> getComplementaryEvents() {
        return complementaryEvents;
    }
}
