package com.cs.payment;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Omid Alaepour
 */
@XmlRootElement
@XmlEnum
public enum ConversionType {
    REAL_MONEY,
    BONUS_MONEY
}
