package com.cs.whitelist;

import com.cs.audit.AuditService;
import com.cs.audit.UserActivityType;
import com.cs.persistence.InvalidArgumentException;
import com.cs.persistence.NotFoundException;
import com.cs.player.Player;
import com.cs.player.QPlayer;
import com.cs.user.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.querydsl.QSort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mysema.query.types.Order;
import com.mysema.query.types.OrderSpecifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.Date;
import java.util.List;

import static org.springframework.transaction.annotation.Isolation.READ_COMMITTED;

/**
 * @author Hadi Movaghar
 */
@Service
@Transactional(isolation = READ_COMMITTED)
public class WhiteListServiceImpl implements WhiteListService {

    private final Logger logger = LoggerFactory.getLogger(WhiteListServiceImpl.class);

    private final AuditService auditService;
    private final WhiteListedPlayerRepository whiteListedPlayerRepository;
    private final WhiteListedIpAddressRepository whiteListedIpAddressRepository;

    @Autowired
    public WhiteListServiceImpl(final AuditService auditService, final WhiteListedPlayerRepository whiteListedPlayerRepository,
                                final WhiteListedIpAddressRepository whiteListedIpAddressRepository) {
        this.auditService = auditService;
        this.whiteListedPlayerRepository = whiteListedPlayerRepository;
        this.whiteListedIpAddressRepository = whiteListedIpAddressRepository;
    }

    private boolean isPlayerWhiteListed(final Player player) {
        final WhiteListedPlayer whiteListedPlayer = whiteListedPlayerRepository.findByPlayerAndDeletedFalse(player);
        return whiteListedPlayer != null;
    }

    private boolean isIpAddressWhiteListed(final String stringIpAddress) {
        final Long longIpAddress;
        try {
            longIpAddress = WhiteListedIpAddress.stringIPToLong(stringIpAddress);
        } catch (final NumberFormatException e) {
            return false;
        }

        final List<WhiteListedIpAddress> whiteListedIpAddresses = whiteListedIpAddressRepository.findByDeletedFalse();
        for (final WhiteListedIpAddress whiteListedIpAddress : whiteListedIpAddresses) {
            if (longIpAddress >= whiteListedIpAddress.getFromIpAddress() && longIpAddress <= whiteListedIpAddress.getToIpAddress()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isWhiteListedOnLogin(final Player player, final String stringIpAddress) {
        return isPlayerWhiteListed(player) || isIpAddressWhiteListed(stringIpAddress);
    }

    @Override
    public Page<Player> getWhiteListedPlayers(final Integer page, final Integer size) {
        final PageRequest pageRequest = new PageRequest(page, size, new QSort(new OrderSpecifier<>(Order.DESC, QPlayer.player.id)));
        return whiteListedPlayerRepository.findWhiteListedPlayers(pageRequest);
    }

    @Override
    public Player getWhiteListedPlayer(final Long playerId) {
        return whiteListedPlayerRepository.findWhiteListedPlayer(playerId);
    }

    @Override
    public WhiteListedPlayer addPlayerToWhiteList(final User user, final Player player) {
        WhiteListedPlayer whiteListedPlayer = whiteListedPlayerRepository.findByPlayer(player);

        if (whiteListedPlayer != null) {
            whiteListedPlayer.setDeleted(false);
            whiteListedPlayer.setModifiedDate(new Date());
            return whiteListedPlayerRepository.save(whiteListedPlayer);
        }

        whiteListedPlayer = new WhiteListedPlayer();
        whiteListedPlayer.setPlayer(player);
        whiteListedPlayer.setDeleted(false);
        whiteListedPlayer.setCreatedDate(new Date());
        whiteListedPlayerRepository.save(whiteListedPlayer);

        auditService.trackUserActivity(user, UserActivityType.ADD_PLAYER_TO_WHITELIST, String.format("Added player %d to white list.", player.getId()));
        logger.info("User {} added player {} to white list.", user.getId(), player.getId());
        return whiteListedPlayer;
    }

    @Override
    public void removePlayerFromWhiteList(final User user, final Player player) {
        final WhiteListedPlayer whiteListedPlayer = whiteListedPlayerRepository.findByPlayer(player);
        if (whiteListedPlayer != null) {
            whiteListedPlayer.setDeleted(true);
            whiteListedPlayer.setModifiedDate(new Date());
            whiteListedPlayerRepository.save(whiteListedPlayer);
            auditService.trackUserActivity(user, UserActivityType.REMOVE_PLAYER_TO_WHITELIST, String.format("Removed player %d from white list.", player.getId()));
            logger.info("User {} removed player {} from white list.", user.getId(), player.getId());
        }
    }

    @Nullable
    @Override
    public WhiteListedIpAddress getWhiteListedIpAddress(final String stringIpAddress) {
        final Long longIpAddress;
        try {
            longIpAddress = WhiteListedIpAddress.stringIPToLong(stringIpAddress);
        } catch (final NumberFormatException e) {
            throw new InvalidArgumentException("IP Address is not valid.");
        }

        final List<WhiteListedIpAddress> whiteListedIpAddresses = whiteListedIpAddressRepository.findByDeletedFalse();
        for (final WhiteListedIpAddress whiteListedIpAddress : whiteListedIpAddresses) {
            if (longIpAddress >= whiteListedIpAddress.getFromIpAddress() && longIpAddress <= whiteListedIpAddress.getToIpAddress()) {
                return whiteListedIpAddress;
            }
        }

        return null;
    }

    @Override
    public Page<WhiteListedIpAddress> getWhiteListedIpAddresses(final Integer page, final Integer size) {
        final PageRequest pageRequest = new PageRequest(page, size, new QSort(new OrderSpecifier<>(Order.DESC, QWhiteListedIpAddress.whiteListedIpAddress.id)));
        return whiteListedIpAddressRepository.findWhiteListedIpAddresses(pageRequest);
    }

    @Override
    public WhiteListedIpAddress addIpAddressToWhiteList(final User user, final String fromIpAddress, final String toIpAddress) {
        final Long longFromIpAddress;
        final Long longToIpAddress;
        try {
            longFromIpAddress = WhiteListedIpAddress.stringIPToLong(fromIpAddress);
            longToIpAddress = WhiteListedIpAddress.stringIPToLong(toIpAddress);
        } catch (final NumberFormatException e) {
            throw new InvalidArgumentException("Provided IP Address is not valid." + e.getMessage());
        }

        if (longFromIpAddress > longToIpAddress) {
            throw new InvalidArgumentException("Provided IP Addresses Range is not correct.");
        }

        if (checkIpAddressExist(longFromIpAddress, longToIpAddress)) {
            throw new InvalidArgumentException("IP Address (range) is already in white list.");
        }

        auditService.trackUserActivity(user, UserActivityType.ADD_IP_ADDRESS_TO_WHITELIST, String.format("Added %s to %s to white list.", fromIpAddress, toIpAddress));
        logger.info("User {} added {} to {} to white list.", user.getId(), fromIpAddress, toIpAddress);
        return addIpAddresses(longFromIpAddress, longToIpAddress);
    }

    @Override
    public void removeIpAddressToWhiteList(final User user, final Long id) {
        final WhiteListedIpAddress whiteListedIpAddress = whiteListedIpAddressRepository.findOne(id);
        if (whiteListedIpAddress == null) {
            throw new NotFoundException(id);
        }

        whiteListedIpAddress.setDeleted(true);
        whiteListedIpAddress.setModifiedDate(new Date());
        whiteListedIpAddressRepository.save(whiteListedIpAddress);
        auditService.trackUserActivity(user, UserActivityType.REMOVE_IP_ADDRESS_FROM_WHITELIST,
                                       String.format("Removed %s to %s from white list.", WhiteListedIpAddress.longIPToString(whiteListedIpAddress.getFromIpAddress()),
                                                     WhiteListedIpAddress.longIPToString(whiteListedIpAddress.getToIpAddress()))
        );
        logger.info("User {} removed white listed IP address with id {} from white list.", user.getId(), whiteListedIpAddress.getId());
    }

    private WhiteListedIpAddress addIpAddresses(final Long fromIpAddress, final Long toIpAddress) {
        final WhiteListedIpAddress whiteListedIpAddress = new WhiteListedIpAddress();
        whiteListedIpAddress.setCreatedDate(new Date());
        whiteListedIpAddress.setDeleted(false);
        whiteListedIpAddress.setFromIpAddress(fromIpAddress);
        whiteListedIpAddress.setToIpAddress(toIpAddress);
        return whiteListedIpAddressRepository.save(whiteListedIpAddress);
    }

    private boolean checkIpAddressExist(final Long fromIpAddress, final Long toIpAddress) {
        final List<WhiteListedIpAddress> whiteListedIpAddresses = whiteListedIpAddressRepository.findByDeletedFalse();

        if (fromIpAddress.equals(toIpAddress)) {
            return checkSingleIpAddressExist(whiteListedIpAddresses, fromIpAddress);
        }

        return checkIpAddressRangeExist(whiteListedIpAddresses, fromIpAddress, toIpAddress);
    }

    private boolean checkSingleIpAddressExist(final List<WhiteListedIpAddress> whiteListedIpAddresses, final Long ipAddress) {
        for (final WhiteListedIpAddress whiteListedIpAddress : whiteListedIpAddresses) {
            if (ipAddress >= whiteListedIpAddress.getFromIpAddress() && ipAddress <= whiteListedIpAddress.getToIpAddress()) {
                return true;
            }
        }
        return false;
    }

    private boolean checkIpAddressRangeExist(final List<WhiteListedIpAddress> whiteListedIpAddresses, final Long fromIpAddress, final Long toIpAddress) {
        for (final WhiteListedIpAddress whiteListedIpAddress : whiteListedIpAddresses) {
            if (fromIpAddress.equals(whiteListedIpAddress.getFromIpAddress()) && toIpAddress.equals(whiteListedIpAddress.getToIpAddress())) {
                return true;
            }
        }
        return false;
    }
}
