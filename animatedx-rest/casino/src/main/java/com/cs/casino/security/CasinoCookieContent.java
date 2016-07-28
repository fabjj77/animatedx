package com.cs.casino.security;

import com.google.common.base.Joiner;

/**
 * @author Hadi Movaghar
 */
public class CasinoCookieContent {

    public static final String COOKIE_NAME = "BLINGCITY_CASINO";
    private static final String SESSION_ID = "sessionId";
    private final String sessionId;

    public CasinoCookieContent(final String sessionId) {
        this.sessionId = sessionId;
    }

    public String getSessionIdContent() {
        return SESSION_ID + "=" + sessionId;
    }

    public String getContent() {
        return Joiner.on("&").skipNulls().join(getSessionIdContent(), null);
    }
}
