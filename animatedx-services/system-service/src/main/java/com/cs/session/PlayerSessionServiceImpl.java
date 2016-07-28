package com.cs.session;

import com.cs.player.Player;
import com.cs.system.PlayerSessionRegistry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.transaction.annotation.Isolation.READ_COMMITTED;

/**
 * @author Hadi Movaghar
 */
@Service
@Transactional(isolation = READ_COMMITTED)
public class PlayerSessionServiceImpl implements PlayerSessionService {

    private final PlayerSessionRegistryRepository playerSessionRegistryRepository;

    @Autowired
    public PlayerSessionServiceImpl(final PlayerSessionRegistryRepository playerSessionRegistryRepository) {
        this.playerSessionRegistryRepository = playerSessionRegistryRepository;
    }

    @Override
    public Boolean isPlayerSessionValidForPayment(final Player player, final String uuid) {
        final PlayerSessionRegistry playerSessionRegistry = playerSessionRegistryRepository.findByPlayer(player);
        if (playerSessionRegistry.isSessionValidForPayment(uuid)) {
            return true;
        }
        if (playerSessionRegistry.isSessionValidForFirstDeposit(uuid)) {
            playerSessionRegistry.setActive(false);
            playerSessionRegistryRepository.save(playerSessionRegistry);
            return true;
        }
        return false;
    }

    @Override
    public String getPaymentUuid(final Player player) {
        return playerSessionRegistryRepository.findByPlayer(player).getUuid();
    }
}
