package com.cs.item;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Omid Alaepour.
 */
@XmlRootElement
@XmlEnum
public enum ItemState {
    UNUSED, USED
}
