package com.cs.messaging.bronto;

import com.cs.messaging.email.BrontoWorkflowName;

import javax.annotation.Nonnull;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

import static javax.xml.bind.annotation.XmlAccessType.FIELD;

/**
 * @author Omid Alaepour
 */
@XmlRootElement
@XmlAccessorType(FIELD)
public class AddContactWorkflowMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    @XmlElement
    @Nonnull
    private InternalBrontoContact internalBrontoContact;

    @XmlElement
    @Nonnull
    private BrontoWorkflowName brontoWorkflowName;

    public AddContactWorkflowMessage(@Nonnull final InternalBrontoContact internalBrontoContact, @Nonnull final BrontoWorkflowName brontoWorkflowName) {
        this.internalBrontoContact = internalBrontoContact;
        this.brontoWorkflowName = brontoWorkflowName;
    }

    @Nonnull
    public InternalBrontoContact getInternalBrontoContact() {
        return internalBrontoContact;
    }

    public void setInternalBrontoContact(@Nonnull final InternalBrontoContact internalBrontoContact) {
        this.internalBrontoContact = internalBrontoContact;
    }

    @Nonnull
    public BrontoWorkflowName getBrontoWorkflowName() {
        return brontoWorkflowName;
    }

    public void setBrontoWorkflowName(@Nonnull final BrontoWorkflowName brontoWorkflowName) {
        this.brontoWorkflowName = brontoWorkflowName;
    }
}
