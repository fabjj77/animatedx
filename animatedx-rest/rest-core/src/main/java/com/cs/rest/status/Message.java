package com.cs.rest.status;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.io.Serializable;

import static javax.xml.bind.annotation.XmlAccessType.FIELD;

/**
 * @author Omid Alaepour.
 */
@XmlAccessorType(FIELD)
public abstract class Message implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement
    private final Integer code;

    @XmlElement
    private final String value;

    @XmlElement
    private final String message;

    protected Message(final StatusCode code, final String message) {
        this(code, message, null);
    }

    protected Message(final StatusCode code, final String value, @Nullable final String message) {
        this.code = code.getCode();
        this.value = value;
        this.message = message;
    }
}
