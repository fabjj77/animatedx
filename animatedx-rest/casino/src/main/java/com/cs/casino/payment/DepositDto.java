package com.cs.casino.payment;

import com.cs.payment.DepositDetails;

import javax.annotation.Nonnull;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import static javax.xml.bind.annotation.XmlAccessType.FIELD;

/**
 * @author Joakim Gottzén
 */
@XmlRootElement
@XmlAccessorType(FIELD)
public class DepositDto {
    @XmlElement
    @Nonnull
    private String url;

    @XmlElement
    private boolean popOut;

    @SuppressWarnings("UnusedDeclaration")
    public DepositDto() {
    }

    public DepositDto(final DepositDetails depositDetails) {
        url = depositDetails.getUrl();
        popOut = depositDetails.isPopOut();
    }
}
