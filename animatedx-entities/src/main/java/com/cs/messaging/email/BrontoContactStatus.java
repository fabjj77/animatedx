package com.cs.messaging.email;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Omid Alaepour
 */
@XmlRootElement
@XmlEnum
public enum BrontoContactStatus {
    ACTIVE,
    @SuppressWarnings("SpellCheckingInspection")ONBOARDING,
    TRANSACTIONAL,
    BOUNCE,
    UNCONFIRMED,
    @SuppressWarnings("SpellCheckingInspection")UNSUB,
    FLOAT;

    @Nullable
    public static BrontoContactStatus getStatus(final String status) {
        for (final BrontoContactStatus brontoContactStatus : BrontoContactStatus.values()) {
            if (brontoContactStatus.name().equalsIgnoreCase(status)) {
                return brontoContactStatus;
            }
        }
        return null;
    }
}
