package com.cs.payment;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * @author Omid Alaepour
 */
@XmlRootElement
@XmlEnum
public enum PaymentStatus {
    SUCCESS,

    FAILURE,

    CANCELED,

    AWAITING_PAYMENT,

    AWAITING_APPROVAL,

    AWAITING_NOTIFICATION,

    SENDING_FAILURE,

    DECLINED,

    /**
     * When a successful payment has been refunded.
     */
    REFUNDED,

    /**
     * When a successful payment has been charge-backed.
     */
    CHARGEBACKED;

    private static final Collection<PaymentStatus> awaitingStatuses = Collections.unmodifiableCollection(Arrays.asList(AWAITING_APPROVAL, AWAITING_NOTIFICATION,
                                                                                                              AWAITING_PAYMENT));

    public static Collection<PaymentStatus> awaitingStatuses() {
        return awaitingStatuses;
    }
}
