package com.cs.messaging.email;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Omid Alaepour
 */
@XmlRootElement
@XmlEnum
public enum BrontoFieldType {
    TEXT, TEXTAREA, PASSWORD, CHECKBOX, RADIO, SELECT, INTEGER, CURRENCY, DATE;

    @Nullable
    public static BrontoFieldType getFieldType(final String type) {
        for (final BrontoFieldType brontoFieldType : BrontoFieldType.values()) {
            if (brontoFieldType.name().equalsIgnoreCase(type)) {
                return brontoFieldType;
            }
        }
        return null;
    }
 }
