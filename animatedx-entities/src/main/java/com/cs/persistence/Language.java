package com.cs.persistence;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Omid Alaepour.
 */
@XmlRootElement
@XmlEnum
public enum Language {
    ENGLISH("en"),

    SWEDISH("sv");

    private final String isoCode;

    private Language(final String isoCode) {
        this.isoCode = isoCode;
    }

    public String toIso() {
        return isoCode;
    }
}
