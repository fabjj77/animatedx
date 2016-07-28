package com.cs.player;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Hadi Movaghar
 */
@XmlRootElement
@XmlEnum
public enum LimitationStatus {
    APPLIED, AWAITING
}
