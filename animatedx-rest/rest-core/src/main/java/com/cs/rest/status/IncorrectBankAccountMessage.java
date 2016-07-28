package com.cs.rest.status;

import com.cs.payment.IncorrectBankAccountException;

/**
 * @author Hadi Movaghar
 */
public class IncorrectBankAccountMessage extends ErrorMessage {

    private static final long serialVersionUID = 1L;

    private static final String INVALID_OR_MISSING_BANK_NAME_OR_BANK_LOCATION_ERROR = "142";
    private static final String INVALID_IBAN_ERROR = "161";
    private static final String INCONSISTENT_IBAN_ERROR = "162";
    private static final String INVALID_BIC_ERROR = "163";

    protected IncorrectBankAccountMessage(final StatusCode statusCode, final String message) {
        super(statusCode, message);
    }

    public static IncorrectBankAccountMessage of(final IncorrectBankAccountException exception) {
        return new IncorrectBankAccountMessage(extractStatusCodeFromExceptionMessage(exception.getMessage()), exception.getMessage());
    }

    private static StatusCode extractStatusCodeFromExceptionMessage(final String message) {
        if (message.contains(INVALID_OR_MISSING_BANK_NAME_OR_BANK_LOCATION_ERROR)) {
            return StatusCode.INVALID_OR_MISSING_BANK_NAME_OR_BANK_LOCATION;
        } else if (message.contains(INVALID_IBAN_ERROR)) {
            return StatusCode.INVALID_IBAN;
        } else if (message.contains(INCONSISTENT_IBAN_ERROR)) {
            return StatusCode.INCONSISTENT_IBAN;
        } else if (message.contains(INVALID_BIC_ERROR)) {
            return StatusCode.INVALID_BIC;
        } else {
            return StatusCode.INCORRECT_BANK_ACCOUNT;
        }
    }
}
