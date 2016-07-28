package com.cs.rest.status;

import com.cs.player.BlockedPlayerException;

import static com.cs.rest.status.StatusCode.BLOCKED_PLAYER;

/**
 * @author Hadi Movaghar
 */
public class BlockedPlayerMessage extends ErrorMessage {

    private static final long serialVersionUID = 1L;

    public BlockedPlayerMessage(final String message) {
        super(BLOCKED_PLAYER, message);
    }

    public static BlockedPlayerMessage of(final BlockedPlayerException exception) {
        return new BlockedPlayerMessage(exception.getMessage());
    }
}
