package com.cs.avatar;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * @author Omid Alaepour
 */
@Converter
public class HairColorConverter implements AttributeConverter<HairColor, Short> {
    @Override
    public Short convertToDatabaseColumn(final HairColor attribute) {
        return attribute.getColor();
    }

    @Override
    public HairColor convertToEntityAttribute(final Short dbData) {
        return HairColor.getFromColor(dbData);
    }
}
