package com.cs.payment;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Omid Alaepour
 */
@XmlRootElement
@XmlEnum
public enum EventCode {
    AUTHORISATION,
    CANCELLATION,
    REFUND,
    CANCEL_OR_REFUND,
    CAPTURE,
    REFUNDED_REVERSED,
    CAPTURE_FAILED,
    PAYOUT_DECLINE,
    REFUND_FAILED,
    REQUEST_FOR_INFORMATION,
    NOTIFICATION_OF_CHARGEBACK,
    ADVICE_OF_DEBIT,
    CHARGEBACK,
    CHARGEBACK_REVERSED,
    REFUND_WITH_DATA,

    REPORT_AVAILABLE,

    BACK_OFFICE_MONEY_DEPOSIT,
    BACK_OFFICE_BONUS_DEPOSIT,
    BACK_OFFICE_MONEY_WITHDRAW,
    BACK_OFFICE_BONUS_WITHDRAW,

    FREE_SPIN_BONUS,

    BONUS_CONVERSION,
    CASHBACK
}
