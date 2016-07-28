package com.cs.administration.bonus;

import com.cs.administration.promotion.CriteriaDto;
import com.cs.bonus.BasicPlayerBonusDto;
import com.cs.bonus.PlayerBonus;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;
import java.util.Date;

import static javax.xml.bind.annotation.XmlAccessType.FIELD;

/**
 * @author Omid Alaepour
 */
@XmlRootElement
@XmlAccessorType(FIELD)
public class BackOfficePlayerBonusDto extends BasicPlayerBonusDto {

    @XmlElement(nillable = true, required = true)
    @Nullable
    private Long bonusId;

    @XmlElement
    @Nonnull
    @NotNull(message = "playerBonusDto.promotionId.notNull")
    private Long promotionId;

    @XmlElement
    @Nonnull
    private Long playerId;

    @XmlElement
    @Nullable
    private BigDecimal usedAmount;

    @XmlElement
    @Nullable
    private Date usedDate;

    @XmlElement
    @Nullable
    private Integer quantity;

    @XmlElement
    @Nullable
    private Long paymentTransactionId;

    @XmlElement
    @Nonnull
    @Valid
    private CriteriaDto criteria;

    @XmlElement
    @Nonnull
    private Long level;

    @SuppressWarnings("UnusedDeclaration")
    public BackOfficePlayerBonusDto() {
    }

    public BackOfficePlayerBonusDto(final PlayerBonus playerBonus) {
        super(playerBonus);
        bonusId = playerBonus.getPk().getBonus().getId();
        playerId = playerBonus.getPk().getPlayer().getId();
        promotionId = playerBonus.getPk().getBonus().getPromotion().getId();
        usedAmount = playerBonus.getUsedAmount() != null ? playerBonus.getUsedAmount().getEuroValueInBigDecimal() : null;
        usedDate = playerBonus.getUsedDate() != null ? playerBonus.getUsedDate() : null;
        paymentTransactionId = playerBonus.getPaymentTransaction() != null ? playerBonus.getPaymentTransaction().getId() : null;
        quantity = playerBonus.getUsedQuantity() != null ? playerBonus.getUsedQuantity() : null;
        criteria = new CriteriaDto(playerBonus.getPk().getBonus().getCriteria());
        level = playerBonus.getLevel();
    }
}
