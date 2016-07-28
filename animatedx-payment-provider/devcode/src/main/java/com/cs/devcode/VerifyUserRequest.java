package com.cs.devcode;

import com.google.common.base.Objects;

import javax.annotation.Nonnull;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import static javax.xml.bind.annotation.XmlAccessType.FIELD;

/**
 * @author Hadi Movaghar
 */
@XmlRootElement
@XmlAccessorType(FIELD)
public class VerifyUserRequest {

    @XmlElement
    @Nonnull
    @NotNull(message = "verifyUserRequest.sessionId.notNull")
    private String sessionId;

    @XmlElement
    @Nonnull
    @NotNull(message = "verifyUserRequest.userId.notNull")
    private String userId;

    public VerifyUserRequest() {
    }

    @Nonnull
    public String getSessionId() {
        return sessionId;
    }

    @Nonnull
    public String getUserId() {
        return userId;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("sessionId", sessionId)
                .add("userId", userId)
                .toString();
    }
}
