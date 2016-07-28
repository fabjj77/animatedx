package com.cs.payment;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * @author Hadi Movaghar
 */
@Converter
public class MoneyConverter implements AttributeConverter<Money, Long> {
    @Override
    public Long convertToDatabaseColumn(final Money attribute) {
        return attribute.getCents();
    }

    @Override
    public Money convertToEntityAttribute(final Long dbData) {
        return new Money(dbData);
    }
}
