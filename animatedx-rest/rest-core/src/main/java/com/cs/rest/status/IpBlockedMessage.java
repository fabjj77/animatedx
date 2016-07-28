package com.cs.rest.status;

import com.cs.persistence.IpBlockedException;

/**
 * @author Hadi Movaghar
 */
public class IpBlockedMessage extends ErrorMessage {
    private static final long serialVersionUID = 1L;

    protected IpBlockedMessage(final String message) {
        super(StatusCode.BLOCKED_IP_ADDRESS, message);
    }

    public static IpBlockedMessage of(final IpBlockedException exception) {
        return new IpBlockedMessage(exception.getMessage());
    }
}
