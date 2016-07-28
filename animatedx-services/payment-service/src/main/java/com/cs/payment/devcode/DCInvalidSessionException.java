package com.cs.payment.devcode;

/**
 * @author Hadi Movaghar
 */
public class DCInvalidSessionException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final String playerId;
    private final String sessionId;
    public static final String INVALID_SESSION = "Session %s is invalid.";

    public DCInvalidSessionException(final String playerId, final String sessionId) {
        super(String.format(INVALID_SESSION, sessionId));
        this.sessionId = sessionId;
        this.playerId = playerId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getPlayerId() {
        return playerId;
    }
}
