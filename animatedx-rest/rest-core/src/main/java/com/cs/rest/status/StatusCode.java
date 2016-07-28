package com.cs.rest.status;

import java.io.Serializable;

/**
 * @author Joakim Gottzén
 */
public enum StatusCode implements Serializable {
    /**
     * A resource was not found
     */
    NOT_FOUND(1),

    /**
     * Password change incorrect
     */
    INCORRECT_PASSWORD_CHANGE(2),

    /**
     * Supplied argument is invalid
     */
    INVALID_ARGUMENT(3),

    /**
     * A resource does not exist
     */
    DOES_NOT_EXIST(4),

    /**
     * A resource does exist
     */
    EXISTS(5),

    /**
     * Expected state is illegal
     */
    ILLEGAL_STATE(6),

    /**
     * Player is blocked
     */
    BLOCKED_PLAYER(7),

    /**
     * Limitation is not settable
     */
    INVALID_LIMITATION(8),

    /**
     * Communication error
     */
    COMMUNICATION_EXCEPTION(9),

    /**
     * Invalid original payment
     */
    INVALID_ORIGINAL_PAYMENT(10),

    /**
     * Invalid amount
     */
    INVALID_AMOUNT(11),

    /**
     * Invalid player
     */
    INVALID_PLAYER(12),

    /**
     * Unlocked player
     */
    UNBLOCKED_PLAYER(13),

    /**
     * The session expired
     */
    SESSION_EXPIRED(14),

    /**
     * The player is unverified and have to be verified before payment operations can continue.
     */
    PLAYER_UNVERIFIED(15),

    /**
     * The entity is expired, entities such as bonus and promotion
     */
    EXPIRED_ENTITY(16),

    /**
     * The entity already assigned to the player, entities such as bonus and promotion
     */
    ALREADY_ASSIGNED(17),

    /**
     * Bank account information is not correct.
     */
    INCORRECT_BANK_ACCOUNT(18),

    /**
     * Bank Name or Bank Location not valid or missing.
     */
    INVALID_OR_MISSING_BANK_NAME_OR_BANK_LOCATION(19),

    /**
     * Invalid iban.
     */
    INVALID_IBAN(20),

    /**
     * Inconsistent iban.
     */
    INCONSISTENT_IBAN(21),

    /**
     * Invalid bic.
     */
    INVALID_BIC(22),

    /**
     * Operation is not permitted.
     */
    ILLEGAL_OPERATION(23),

    /**
     * The avatar is not allowed to be changed
     */
    AVATAR_CHANGE_NOT_ALLOWED(24),

    /**
     * User not allowed access to the resource.
     */
    ACCESS_DENIED(25),

    /**
     * Withdrawal failed.
     */
    WITHDRAWAL_FAILED(26),

    /**
     * Credit amount is not valid.
     */
    INVALID_CREDIT_AMOUNT(27),

    /**
     * IP address of a blocked country.
     */
    BLOCKED_IP_ADDRESS(28),

    /**
     * Invalid session
     */
    INVALID_SESSION(29),

    /**
     * Player is not valid for a devcode request.
     */
    DEVCODE_INVALID_PLAYER(100),

    /**
     * Session id is not valid for verify user devcode request.
     */
    DEVCODE_INVALID_SESSION(101),

    /**
     * Invalid player detail (trust level, blocked) for doing a devcode transaction.
     */
    DEVCODE_INVALID_PLAYER_DETAIL(102),

    /**
     * Invalid amonut for a devcode transaction.
     */
    DEVCODE_INVALID_AMOUNT(103),

    /**
     * Invalid authorization code devcode transaction.
     */
    DEVCODE_INVALID_AUTHORIZATION_CODE(104);

    private final Integer code;

    private StatusCode(final Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }
}
