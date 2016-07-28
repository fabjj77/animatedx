package com.cs.administration.promotion;

import com.cs.promotion.Promotion;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
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
public class UpdatePromotionDto {
    @XmlElement(required = true)
    @Nonnull
    @NotNull(message = "updatePromotionDto.id.notNull")
    private Long id;

    @XmlElement
    @Nullable
    private String name;

    @XmlElement
    @Nullable
    private Date validFrom;

    @XmlElement
    @Nullable
    private Date validTo;

    public UpdatePromotionDto() {
    }

    Promotion asPromotion() {
        final Promotion promotion = new Promotion();
        promotion.setId(id);
        promotion.setName(name);
        promotion.setValidFrom(validFrom);
        promotion.setValidTo(validTo);
        return promotion;
    }
}
