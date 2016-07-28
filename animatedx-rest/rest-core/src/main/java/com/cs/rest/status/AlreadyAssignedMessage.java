package com.cs.rest.status;

import com.cs.persistence.AlreadyAssignedException;

import static com.cs.rest.status.StatusCode.ALREADY_ASSIGNED;

/**
 * @author Hadi Movaghar
 */
public class AlreadyAssignedMessage extends ErrorMessage {

    private static final long serialVersionUID = 1L;

    protected AlreadyAssignedMessage(final String message) {
        super(ALREADY_ASSIGNED, message);
    }

    public static AlreadyAssignedMessage of(final AlreadyAssignedException exception) {
        return new AlreadyAssignedMessage(exception.getMessage());
    }
}
