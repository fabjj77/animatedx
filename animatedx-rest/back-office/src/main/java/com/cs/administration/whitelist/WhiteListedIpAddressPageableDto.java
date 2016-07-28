package com.cs.administration.whitelist;

import com.cs.whitelist.WhiteListedIpAddress;

import org.springframework.data.domain.Page;

import javax.annotation.Nonnull;
import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Hadi Movaghar
 */
public class WhiteListedIpAddressPageableDto {
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    @XmlElement
    @Nonnull
    private List<WhiteListedIpAddressDto> ipAddresses;

    @XmlElement
    @Nonnull
    private Long count;

    @SuppressWarnings("UnusedDeclaration")
    public WhiteListedIpAddressPageableDto() {
    }

    public WhiteListedIpAddressPageableDto(final Page<WhiteListedIpAddress> ipAddresses) {
        this.ipAddresses = new ArrayList<>(ipAddresses.getSize());
        for (final WhiteListedIpAddress ipAddress : ipAddresses) {
            this.ipAddresses.add(new WhiteListedIpAddressDto(ipAddress));
        }
        count = ipAddresses.getTotalElements();
    }
}
