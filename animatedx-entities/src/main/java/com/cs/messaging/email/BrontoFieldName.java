package com.cs.messaging.email;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Omid Alaepour
 */
@XmlRootElement
@XmlEnum
public enum BrontoFieldName {
    street,
    street2,
    zipcode,
    state,
    city,
    country,
    money_balance,
    bonus_balance,
    player_id,
    first_name,
    last_name,
    nickname,
    birthday,
    email_verification,
    language,
    avatar_id,
    level,
    currency,
    created_date,
    uuid,
    status;

    @Nullable
    public static BrontoFieldName getFieldName(final String name) {
        for (final BrontoFieldName brontoFieldName : BrontoFieldName.values()) {
            if (brontoFieldName.name().equalsIgnoreCase(name)) {
                return brontoFieldName;
            }
        }
        return null;
    }
}
