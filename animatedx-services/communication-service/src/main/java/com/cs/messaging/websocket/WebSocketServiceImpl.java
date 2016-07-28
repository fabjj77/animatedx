package com.cs.messaging.websocket;

import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.springframework.transaction.annotation.Isolation.READ_COMMITTED;
import static org.springframework.transaction.annotation.Propagation.SUPPORTS;

/**
 * @author Joakim Gottzén
 */
@Service
@MessageEndpoint
@Transactional(isolation = READ_COMMITTED)
public class WebSocketServiceImpl implements WebSocketService {
    private final Logger logger = LoggerFactory.getLogger(WebSocketServiceImpl.class);

    @Transactional(propagation = SUPPORTS)
    @ServiceActivator(inputChannel = "webSocketMessages")
    @Override
    public void levelUp(final LevelUpMessage levelUpMessage) {
        logger.info("Levelling up, message: {}", levelUpMessage);
    }
}
