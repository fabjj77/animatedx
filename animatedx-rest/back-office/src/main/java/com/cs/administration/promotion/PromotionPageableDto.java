package com.cs.administration.promotion;

import com.cs.promotion.Promotion;

import org.springframework.data.domain.Page;

import javax.annotation.Nonnull;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

import static javax.xml.bind.annotation.XmlAccessType.FIELD;

/**
 * @author Omid Alaepour
 */
@XmlRootElement
@XmlAccessorType(FIELD)
public class PromotionPageableDto {
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    @XmlElement
    @Nonnull
    private List<PromotionDto> promotions;

    @XmlElement
    @Nonnull
    private Long count;

    @SuppressWarnings("UnusedDeclaration")
    public PromotionPageableDto() {
    }

    public PromotionPageableDto(@Nonnull final Page<Promotion> promotions) {
        this.promotions = new ArrayList<>(promotions.getSize());
        for (final Promotion promotion : promotions) {
            this.promotions.add(new PromotionDto(promotion));
        }
        count = promotions.getTotalElements();
    }
}
