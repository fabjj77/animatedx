package com.cs.player;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Omid Alaepour.
 */
@XmlRootElement
@XmlEnum
public enum UuidType {
    PAYMENT, RESET_PASSWORD, VERIFY_EMAIL
}
