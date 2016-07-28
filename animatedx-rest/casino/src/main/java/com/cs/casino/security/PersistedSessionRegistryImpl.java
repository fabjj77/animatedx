package com.cs.casino.security;

import com.cs.player.Player;
import com.cs.session.PlayerSessionRegistryRepository;
import com.cs.system.PlayerSessionRegistry;

import org.springframework.context.ApplicationListener;
import org.springframework.security.core.session.SessionDestroyedEvent;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.util.Assert;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArraySet;

public class PersistedSessionRegistryImpl implements SessionRegistry, ApplicationListener<SessionDestroyedEvent> {

    private final Logger logger = LoggerFactory.getLogger(PersistedSessionRegistryImpl.class);

    private final ConcurrentMap<Object, Set<String>> principals = new ConcurrentHashMap<>();
    private final Map<String, SessionInformation> sessionIds = new ConcurrentHashMap<>();

    private final PlayerSessionRegistryRepository playerSessionRegistryRepository;

    public PersistedSessionRegistryImpl(final PlayerSessionRegistryRepository playerSessionRegistryRepository) {
        this.playerSessionRegistryRepository = playerSessionRegistryRepository;
    }

    @Override
    public List<Object> getAllPrincipals() {
        return new ArrayList<>(principals.keySet());
    }

    @Override
    public List<SessionInformation> getAllSessions(final Object principal, final boolean includeExpiredSessions) {
        final Set<String> sessionsUsedByPrincipal = principals.get(principal);

        if (sessionsUsedByPrincipal == null) {
            return Collections.emptyList();
        }

        final List<SessionInformation> list = new ArrayList<>(sessionsUsedByPrincipal.size());

        for (final String sessionId : sessionsUsedByPrincipal) {
            final SessionInformation sessionInformation = getSessionInformation(sessionId);

            if (sessionInformation == null) {
                continue;
            }

            if (includeExpiredSessions || !sessionInformation.isExpired()) {
                list.add(sessionInformation);
            }
        }

        return list;
    }

    @Override
    public SessionInformation getSessionInformation(final String sessionId) {
        Assert.hasText(sessionId, "SessionId required as per interface contract");

        return sessionIds.get(sessionId);
    }

    @Override
    public void onApplicationEvent(final SessionDestroyedEvent event) {
        final String sessionId = event.getId();
        removeSessionInformation(sessionId);
    }

    @Override
    public void refreshLastRequest(final String sessionId) {
        Assert.hasText(sessionId, "SessionId required as per interface contract");

        final SessionInformation info = getSessionInformation(sessionId);
        final PlayerSessionRegistry playerSessionRegistry = playerSessionRegistryRepository.findBySessionId(sessionId);

        if (info != null) {
            info.refreshLastRequest();
        }

        if (playerSessionRegistry != null) {
            playerSessionRegistry.setLastRequest(new Date());
            playerSessionRegistryRepository.save(playerSessionRegistry);
        }
    }

    @Override
    public void registerNewSession(final String sessionId, final Object principal) {
        Assert.hasText(sessionId, "SessionId required as per interface contract");
        Assert.notNull(principal, "Principal required as per interface contract");

        if (logger.isDebugEnabled()) {
            logger.debug("Registering session {}, for principal {}", sessionId, principal);
        }

        if (getSessionInformation(sessionId) != null) {
            removeSessionInformation(sessionId);
        }

        sessionIds.put(sessionId, new SessionInformation(principal, sessionId, new Date()));

        final PlayerUser playerUser = (PlayerUser) principal;
        PlayerSessionRegistry playerSessionRegistry = playerSessionRegistryRepository.findByPlayer(new Player(playerUser.getId()));
        // If the player doesn't exist in the session registry, create the entry
        if (playerSessionRegistry == null) {
            playerSessionRegistry = new PlayerSessionRegistry();
            playerSessionRegistry.setPlayer(new Player(playerUser.getId()));
        }

        playerSessionRegistry.setSessionId(sessionId);
        playerSessionRegistry.setUuid(UUID.randomUUID().toString());
        playerSessionRegistry.setLastRequest(new Date());
        playerSessionRegistry.setExpired(false);
        playerSessionRegistry.setActive(true);
        playerSessionRegistryRepository.save(playerSessionRegistry);

        Set<String> sessionsUsedByPrincipal = principals.get(principal);

        if (sessionsUsedByPrincipal == null) {
            sessionsUsedByPrincipal = new CopyOnWriteArraySet<>();
            final Set<String> prevSessionsUsedByPrincipal = principals.putIfAbsent(principal, sessionsUsedByPrincipal);

            if (prevSessionsUsedByPrincipal != null) {
                sessionsUsedByPrincipal = prevSessionsUsedByPrincipal;
            }
        }

        sessionsUsedByPrincipal.add(sessionId);

        if (logger.isTraceEnabled()) {
            logger.trace("Sessions used by '{}' : {}", principal, sessionsUsedByPrincipal);
        }
    }

    @Override
    public void removeSessionInformation(final String sessionId) {
        Assert.hasText(sessionId, "SessionId required as per interface contract");

        final SessionInformation info = getSessionInformation(sessionId);

        if (info == null) {
            return;
        }

        if (logger.isTraceEnabled()) {
            logger.trace("Removing session {} from set of registered sessions", sessionId);
        }

        sessionIds.remove(sessionId);

        final PlayerSessionRegistry playerSessionRegistry = playerSessionRegistryRepository.findBySessionId(sessionId);
        if (playerSessionRegistry != null) {
            playerSessionRegistry.setActive(false);
            playerSessionRegistryRepository.save(playerSessionRegistry);
        }

        final Set<String> sessionsUsedByPrincipal = principals.get(info.getPrincipal());

        if (sessionsUsedByPrincipal == null) {
            return;
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Removing session {} from principal's set of registered sessions", sessionId);
        }

        sessionsUsedByPrincipal.remove(sessionId);

        if (sessionsUsedByPrincipal.isEmpty()) {
            // No need to keep object in principals Map anymore
            if (logger.isDebugEnabled()) {
                logger.debug("Removing principal {} from registry", info.getPrincipal());
            }
            principals.remove(info.getPrincipal());
        }

        if (logger.isTraceEnabled()) {
            logger.trace("Sessions used by '{}' : {}", info.getPrincipal(), sessionsUsedByPrincipal);
        }
    }
}
