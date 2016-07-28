package com.cs.messaging.websocket;

/**
 * @author Joakim Gottzén
 */
public interface WebSocketService {
    // Used by Spring Integration messaging
    @SuppressWarnings("UnusedDeclaration")
    void levelUp(final LevelUpMessage levelUpMessage);
}
