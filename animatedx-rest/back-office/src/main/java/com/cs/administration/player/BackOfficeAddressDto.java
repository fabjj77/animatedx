package com.cs.administration.player;

import com.cs.persistence.Country;
import com.cs.player.Address;
import com.cs.player.BasicAddressDto;

import org.hibernate.validator.constraints.NotEmpty;

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
public class BackOfficeAddressDto extends BasicAddressDto {

    @XmlElement
    @Nonnull
    @NotEmpty
    private Country country;

    @SuppressWarnings("UnusedDeclaration")
    public BackOfficeAddressDto() {
    }

    public BackOfficeAddressDto(final Address address) {
        super(address);
        country = address.getCountry();
    }

    @Override
    protected Address asAddress() {
        final Address address = super.asAddress();
        address.setCountry(country);
        return address;
    }
}
