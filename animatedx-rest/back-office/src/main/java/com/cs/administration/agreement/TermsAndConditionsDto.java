package com.cs.administration.agreement;

import com.cs.agreement.TermsAndConditionsVersion;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

import static javax.xml.bind.annotation.XmlAccessType.FIELD;

/**
 * @author Hadi Movaghar
 */
@XmlRootElement
@XmlAccessorType(FIELD)
public class TermsAndConditionsDto {

    @XmlElement
    @Nullable
    private Long id;

    @XmlElement
    @Nonnull
    private String version;

    @XmlElement
    @Nullable
    private Date createdDate;

    @XmlElement
    @Nullable
    private Boolean active;

    @XmlElement
    @Nullable
    private Date activatedDate;

    public TermsAndConditionsDto() {
    }

    @Nonnull
    public String getVersion() {
        return version;
    }

    public TermsAndConditionsDto(final TermsAndConditionsVersion termsAndConditionsVersion) {
        id = termsAndConditionsVersion.getId();
        version = termsAndConditionsVersion.getVersion();
        createdDate = termsAndConditionsVersion.getCreatedDate();
        active = termsAndConditionsVersion.isActive();
        activatedDate = termsAndConditionsVersion.getActivatedDate();
    }
}
