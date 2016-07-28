package com.cs.rest.status;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.io.Serializable;

import static javax.xml.bind.annotation.XmlAccessType.FIELD;

/**
 * @author Hadi Movaghar
 */
@XmlAccessorType(FIELD)
public class DevcodeErrorMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement
    private final String userId;

    @XmlElement
    private final Boolean success;

    @XmlElement
    private final Integer errCode;

    @XmlElement
    private final String errMsg;

    public DevcodeErrorMessage(final String playerId, final StatusCode code, final String errorMessage) {
        userId = playerId;
        success = false;
        errCode = code.getCode();
        errMsg = errorMessage;
    }
}
