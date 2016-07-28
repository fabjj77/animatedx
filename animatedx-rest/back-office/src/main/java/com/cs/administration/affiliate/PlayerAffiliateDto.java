package com.cs.administration.affiliate;

import com.cs.affiliate.PlayerAffiliate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

import static javax.xml.bind.annotation.XmlAccessType.FIELD;

/**
 * @author Joakim Gottzén
 */
@XmlRootElement
@XmlAccessorType(FIELD)
public class PlayerAffiliateDto {

    @XmlElement
    @Nonnull
    private String bTag;

    @XmlElement
    @Nullable
    private Date reportedDate;

    @SuppressWarnings("UnusedDeclaration")
    public PlayerAffiliateDto() {
    }

    public PlayerAffiliateDto(final PlayerAffiliate playerAffiliate) {
        bTag = playerAffiliate.getBTag();
        reportedDate = playerAffiliate.getReportedDate();
    }
}
