package com.cs.game;

/**
 * @author Joakim Gottzén
 */
public enum GameClient {
    FLASH("flash"),

    @SuppressWarnings("SpellCheckingInspection")TOUCH("mobilehtml");

    private final String client;

    GameClient(final String client) {
        this.client = client;
    }

    public String getClient() {
        return client;
    }
}
