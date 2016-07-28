package com.cs.payment;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Omid Alaepour
 */
@XmlRootElement
@XmlEnum
public enum CreditTransactionType {
    CONVERSION,
    BACK_OFFICE_CREDIT_DEPOSIT,
    BACK_OFFICE_CREDIT_WITHDRAW,
}
