package com.cs.game;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Hadi Movaghar
 */
@XmlRootElement
@XmlEnum
public enum Channel {
    FLASH("bbg"),

    @SuppressWarnings("SpellCheckingInspection")TOUCH("mobg");

    private final String channel;

    Channel(final String channel) {
        this.channel = channel;
    }

    public String getChannel() {
        return channel;
    }
}
