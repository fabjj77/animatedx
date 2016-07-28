package com.cs.casino.payment;

import javax.annotation.Nonnull;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import static javax.xml.bind.annotation.XmlAccessType.FIELD;

/**
 * @author Omid Alaepour
 */
@XmlRootElement
@XmlAccessorType(FIELD)
public class HelloMessage {
    @XmlElement
    @Nonnull
    private String name;

    public HelloMessage() {
    }

    public HelloMessage(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
