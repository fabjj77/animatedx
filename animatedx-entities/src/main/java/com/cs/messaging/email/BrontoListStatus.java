package com.cs.messaging.email;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Omid Alaepour
 */
@XmlRootElement
@XmlEnum
public enum BrontoListStatus {
    active, deleted, tmp
}
