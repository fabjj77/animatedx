package com.cs.administration.email;

import com.cs.messaging.bronto.InternalBrontoContact;
import com.cs.messaging.email.BrontoContactStatus;
import com.cs.messaging.email.BrontoContact;

import javax.annotation.Nonnull;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

import static javax.xml.bind.annotation.XmlAccessType.FIELD;

/**
 * @author Omid Alaepour
 */
@XmlRootElement
@XmlAccessorType(FIELD)
public class BrontoContactDto {

    @XmlElement
    @Nonnull
    private String brontoId;

    @XmlElement
    @Nonnull
    private BrontoContactStatus status;

    @XmlElement
    @Nonnull
    private String emailAddress;

    @XmlElement
    @Nonnull
    private Date createdDate;

    public BrontoContactDto() {
    }

    public BrontoContactDto(final InternalBrontoContact internalBrontoContact) {
        brontoId = internalBrontoContact.getBrontoId();
        status = internalBrontoContact.getStatus();
        emailAddress = internalBrontoContact.getEmailAddress();
        createdDate = internalBrontoContact.getCreatedDate();
    }

    public BrontoContactDto(final BrontoContact brontoContact) {
        brontoId = brontoContact.getBrontoId();
        status = brontoContact.getStatus();
        emailAddress = null;
        createdDate = brontoContact.getCreatedDate();
    }


}
