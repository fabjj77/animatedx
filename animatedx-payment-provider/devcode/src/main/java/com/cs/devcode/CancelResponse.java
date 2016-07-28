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
public class CancelResponse {

    @XmlElement
    @Nonnull
    @NotNull(message = "cancelResponse.userId.notNull")
    private String userId;

    @XmlElement
    @Nonnull
    @NotNull(message = "cancelResponse.success.notNull")
    private Boolean success;

    @XmlElement
    @Nullable
    private Number errCode;

    @XmlElement
    @Nullable
    private String errMsg;

    @SuppressWarnings("UnusedDeclaration")
    public CancelResponse() {
    }

    public CancelResponse(final DCPaymentTransaction dcPaymentTransaction) {
        userId = dcPaymentTransaction.getPlayer().getId().toString();
        success = dcPaymentTransaction.getSuccess();
    }
}
