package com.cs.messaging.websocket;

import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;

import java.util.concurrent.Future;

/**
 * @author Joakim Gottzén
 */
@MessagingGateway(errorChannel = "errorChannel")
public interface WebSocketGateway {

    @Gateway(requestChannel = "webSocketMessages")
    Future<Void> levelUp(final LevelUpMessage levelUpMessage);
}
