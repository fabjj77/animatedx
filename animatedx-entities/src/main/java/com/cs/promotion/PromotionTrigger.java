package com.cs.promotion;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Hadi Movaghar
 */
@XmlRootElement
@XmlEnum
public enum PromotionTrigger {
//    DEPOSIT,
//    DATE,
//    REFERRAL,
//    WIN_LOSS,
//    WITHDRAWAL,
    SIGN_UP,
    LOGIN,
    LEVEL_UP,
    SCHEDULE,
    DEPOSIT,
    LINK,
    MANUAL,
    SYSTEM
}
