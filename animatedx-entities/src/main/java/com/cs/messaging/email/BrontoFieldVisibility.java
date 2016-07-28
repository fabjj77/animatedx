package com.cs.messaging.email;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Omid Alaepour
 */
@XmlRootElement
@XmlEnum
public enum BrontoFieldVisibility {
    PUBLIC, PRIVATE;

    public static BrontoFieldVisibility getVisibility(final String visibility) {
        for (final BrontoFieldVisibility brontoFieldVisibility : BrontoFieldVisibility.values()) {
            if (brontoFieldVisibility.name().equalsIgnoreCase(visibility)) {
                return brontoFieldVisibility;
            }
        }
        return null;
    }
}
