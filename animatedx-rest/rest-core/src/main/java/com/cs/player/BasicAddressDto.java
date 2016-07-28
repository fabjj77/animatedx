package com.cs.player;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import static javax.xml.bind.annotation.XmlAccessType.FIELD;

/**
 * @author Omid Alaepour
 */
@XmlRootElement
@XmlAccessorType(FIELD)
public abstract class BasicAddressDto {

    @XmlElement
    @Nullable
    private String street;

    @XmlElement
    @Nullable
    private String street2;

    @XmlElement
    @Nullable
    private String zipCode;

    @XmlElement
    @Nullable
    private String city;

    @XmlElement
    @Nullable
    private String state;

    @SuppressWarnings("UnusedDeclaration")
    protected BasicAddressDto() {
    }

    protected BasicAddressDto(final Address address) {
        street = address.getStreet();
        street2 = address.getStreet2();
        zipCode = address.getZipCode();
        city = address.getCity();
        state = address.getState();
    }

    protected Address asAddress() {
        final Address address = new Address();
        address.setStreet(street != null ? street : "-");
        address.setStreet2(street2);
        address.setZipCode(zipCode != null ? zipCode : "-");
        address.setCity(city != null ? city : "-");
        address.setState(state);
        return address;
    }
}
