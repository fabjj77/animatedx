package com.cs.rest.status;

import com.cs.avatar.AvatarChangeException;

/**
 * @author Omid Alaepour
 */
public class AvatarChangeMessage extends ErrorMessage {

    private static final long serialVersionUID = 1L;

    protected AvatarChangeMessage(final String message) {
        super(StatusCode.AVATAR_CHANGE_NOT_ALLOWED, message);
    }

    public static AvatarChangeMessage of(final AvatarChangeException exception) {
        return new AvatarChangeMessage(exception.getMessage());
    }
}
