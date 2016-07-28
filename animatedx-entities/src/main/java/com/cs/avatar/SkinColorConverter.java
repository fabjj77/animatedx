package com.cs.avatar;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * @author Omid Alaepour
 */
@Converter
public class SkinColorConverter implements AttributeConverter<SkinColor, Short> {
    @Override
    public Short convertToDatabaseColumn(final SkinColor attribute) {
        return attribute.getColor();
    }

    @Override
    public SkinColor convertToEntityAttribute(final Short dbData) {
        return SkinColor.getFromColor(dbData);
    }
}
