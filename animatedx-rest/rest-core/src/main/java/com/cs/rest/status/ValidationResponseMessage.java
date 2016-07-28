package com.cs.rest.status;

import javax.xml.bind.annotation.XmlElement;

/**
 * @author Omid Alaepour.
 */
public class ValidationResponseMessage extends ResponseMessage {

    private static final long serialVersionUID = 1L;

    @XmlElement
    private final boolean valid;

    public ValidationResponseMessage(final boolean valid) {
        super(valid ? StatusCode.DOES_NOT_EXIST : StatusCode.EXISTS, "");
        this.valid = valid;
    }
}
