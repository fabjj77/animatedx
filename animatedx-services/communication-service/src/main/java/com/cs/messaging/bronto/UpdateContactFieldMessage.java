package com.cs.messaging.bronto;

import com.cs.messaging.email.BrontoFieldName;
import com.cs.messaging.email.BrontoWorkflowName;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.EnumMap;
import java.util.Map;

import static javax.xml.bind.annotation.XmlAccessType.FIELD;

/**
 * @author Omid Alaepour
 */
@XmlRootElement
@XmlAccessorType(FIELD)
public class UpdateContactFieldMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement
    @Nonnull
    private final InternalBrontoContact internalBrontoContact;

    @XmlElement
    @Nonnull
    private final Map<BrontoFieldName, String> fieldNameMap;

    @XmlElement
    @Nullable
    private BrontoWorkflowName brontoWorkflowName;

    public UpdateContactFieldMessage(@Nonnull final InternalBrontoContact internalBrontoContact, @Nonnull final Map<BrontoFieldName, String> fieldNameMap) {
        this.internalBrontoContact = internalBrontoContact;
        this.fieldNameMap = new EnumMap<BrontoFieldName, String>(fieldNameMap);
    }

    public UpdateContactFieldMessage(@Nonnull final InternalBrontoContact internalBrontoContact, @Nonnull final Map<BrontoFieldName, String> fieldNameMap,
                                     @Nullable final BrontoWorkflowName brontoWorkflowName) {
        this.internalBrontoContact = internalBrontoContact;
        this.fieldNameMap = new EnumMap<BrontoFieldName, String>(fieldNameMap);
        this.brontoWorkflowName = brontoWorkflowName;
    }

    @Nonnull
    public InternalBrontoContact getInternalBrontoContact() {
        return internalBrontoContact;
    }

    @Nonnull
    public Map<BrontoFieldName, String> getFieldNameMap() {
        return fieldNameMap;
    }

    @Nullable
    public BrontoWorkflowName getBrontoWorkflowName() {
        return brontoWorkflowName;
    }
}
