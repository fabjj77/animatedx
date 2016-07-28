package com.cs.player;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Omid Alaepour.
 */
@XmlRootElement
@XmlEnum
public enum Verification {
    UNVERIFIED, VERIFIED, RE_VERIFY
}
