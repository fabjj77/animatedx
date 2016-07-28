package com.cs.devcode;

import com.cs.payment.DCPaymentTransaction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
public class AuthorizeResponse {

    @XmlElement
    @Nonnull
    @NotNull(message = "authorizeResponse.userId.notNull")
    private String userId;

    @XmlElement
    @Nonnull
    @NotNull(message = "authorizeResponse.success.notNull")
    private Boolean success;

    @XmlElement
    @Nonnull
    @NotNull(message = "authorizeResponse.authCode.notNull")
    private String authCode;

    @XmlElement
    @Nullable
    private Number errCode;

    @XmlElement
    @Nullable
    private String errMsg;

    @SuppressWarnings("UnusedDeclaration")
    public AuthorizeResponse() {
    }

    public AuthorizeResponse(final DCPaymentTransaction dcPaymentTransaction) {
        userId = dcPaymentTransaction.getPlayer().getId().toString();
        success = dcPaymentTransaction.getSuccess();
        authCode = dcPaymentTransaction.getAuthorizationCode();
    }
}
