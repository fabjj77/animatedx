package com.cs.administration.bonus;

import javax.annotation.Nonnull;
import javax.validation.Valid;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;

import static javax.xml.bind.annotation.XmlAccessType.FIELD;

/**
 * @author Hadi Movaghar
 */
@XmlRootElement
@XmlAccessorType(FIELD)
public class UpdatePlayerBonusDto {

    @XmlElement
    @Nonnull
    @Valid
    private BigDecimal amount;

    public UpdatePlayerBonusDto() {
    }

    @Nonnull
    public BigDecimal getAmount() {
        return amount;
    }
}
