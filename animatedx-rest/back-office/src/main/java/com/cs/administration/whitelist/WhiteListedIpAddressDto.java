package com.cs.administration.whitelist;

import com.cs.whitelist.WhiteListedIpAddress;

import org.hibernate.validator.constraints.NotEmpty;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import static javax.xml.bind.annotation.XmlAccessType.FIELD;

/**
 * @author Hadi Movaghar
 */
@XmlRootElement
@XmlAccessorType(FIELD)
public class WhiteListedIpAddressDto {

    @XmlElement(nillable = true, required = true)
    @Nullable
    private Long id;

    @XmlElement
    @Nonnull
    @NotEmpty
    private String fromIpAddress;

    @XmlElement
    @Nullable
    private String toIpAddress;

    @SuppressWarnings("UnusedDeclaration")
    public WhiteListedIpAddressDto() {
    }

    public WhiteListedIpAddressDto(final WhiteListedIpAddress whiteListedIpAddress) {
        id = whiteListedIpAddress.getId();
        fromIpAddress = WhiteListedIpAddress.longIPToString(whiteListedIpAddress.getFromIpAddress());
        if (!whiteListedIpAddress.isSingleIpAddress()) {
            toIpAddress = WhiteListedIpAddress.longIPToString(whiteListedIpAddress.getToIpAddress());
        }
    }

    @Nonnull
    public String getFromIpAddress() {
        return fromIpAddress;
    }

    @Nonnull
    public String getToIpAddress() {
        return toIpAddress != null ? toIpAddress : fromIpAddress;
    }
}
