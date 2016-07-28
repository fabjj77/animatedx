package com.cs.common;

import com.google.common.base.Objects;
import org.hibernate.validator.constraints.Email;

import javax.annotation.Nonnull;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import static javax.xml.bind.annotation.XmlAccessType.FIELD;

/**
 * @author Joakim Gottzén
 */
@XmlRootElement
@XmlAccessorType(FIELD)
public class EmailAddressDto {
    @XmlElement(nillable = true, required = true)
    @Nonnull
    @Email(message = "emailAddressDto.emailAddress.notValid")
    private String emailAddress;

    @Nonnull
    public String getEmailAddress() {
        return emailAddress;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final EmailAddressDto that = (EmailAddressDto) o;

        return Objects.equal(emailAddress, that.emailAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(emailAddress);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .addValue(emailAddress)
                .toString();
    }
}
