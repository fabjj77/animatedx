package com.cs.rest.status;

import javax.xml.bind.annotation.XmlElement;

/**
 * @author Hadi Movaghar
 */
public class BlockedResponseMessage extends ResponseMessage {

    private static final long serialVersionUID = 1L;

    @XmlElement
    private final boolean blocked;

    public BlockedResponseMessage(final boolean blocked) {
        super(blocked ? StatusCode.BLOCKED_PLAYER : StatusCode.UNBLOCKED_PLAYER, "");
        this.blocked = blocked;
    }
}
