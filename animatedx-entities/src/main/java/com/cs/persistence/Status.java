package com.cs.persistence;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Joakim Gottzén
 */
@XmlRootElement
@XmlEnum
public enum Status {
    ACTIVE(true), INACTIVE(true), DELETED(false), DORMANT(true), LOCKED(false), BANNED(false), BAD_CREDENTIALS_LOCKED(false);

    private final boolean isAllowedToLogin;

    Status(final boolean isAllowedToLogin) {
        this.isAllowedToLogin = isAllowedToLogin;
    }

    public boolean isAllowedToLogin() {
        return isAllowedToLogin;
    }
}
