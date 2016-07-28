package com.cs.messaging.bronto;

import com.cs.messaging.email.BrontoFieldName;
import com.cs.messaging.email.BrontoFieldType;
import com.cs.messaging.email.BrontoFieldVisibility;

import com.google.common.base.Objects;

/**
 * @author Omid Alaepour
 */
public class InternalBrontoField {
    private String id;
    private BrontoFieldName name;
    private String label;
    private BrontoFieldType type;
    private BrontoFieldVisibility visibility;
//    private String[] fieldOptions; //Todo change to list
    private String content;

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public BrontoFieldName getName() {
        return name;
    }

    public void setName(final BrontoFieldName name) {
        this.name = name;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(final String label) {
        this.label = label;
    }

    public BrontoFieldType getType() {
        return type;
    }

    public void setType(final BrontoFieldType type) {
        this.type = type;
    }

    public BrontoFieldVisibility getVisibility() {
        return visibility;
    }

    public void setVisibility(final BrontoFieldVisibility visibility) {
        this.visibility = visibility;
    }

//    public String[] getFieldOptions() {
//        return fieldOptions;
//    }
//
//    public void setFieldOptions(final String[] fieldOptions) {
//        this.fieldOptions = fieldOptions;
//    }

    public String getContent() {
        return content;
    }

    public void setContent(final String content) {
        this.content = content;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final InternalBrontoField that = (InternalBrontoField) o;

        return Objects.equal(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
