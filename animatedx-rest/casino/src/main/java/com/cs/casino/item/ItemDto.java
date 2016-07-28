package com.cs.casino.item;

import com.cs.bonus.Bonus;
import com.cs.bonus.BonusType;
import com.cs.item.ItemState;
import com.cs.item.PlayerItem;
import com.cs.payment.Currency;

import org.hibernate.validator.constraints.NotEmpty;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;
import java.util.Date;

import static javax.xml.bind.annotation.XmlAccessType.FIELD;

/**
 * @author Joakim Gottzén
 */
@XmlRootElement
@XmlAccessorType(FIELD)
public class ItemDto {
    @XmlID
    @XmlElement
    @Nonnull
    private Long id;

    @XmlElement
    @Nonnull
    @NotEmpty(message = "itemDto.url.notEmpty")
    private String url;

    @XmlElement
    @Nonnull
    @Min(value=1, message = "itemDto.level.min")
    private Long level;

    @XmlElement
    @Nonnull
    @NotNull(message="itemDto.state.notNull")
    private ItemState state;

    @XmlElement(nillable = true, required = true)
    @Nullable
    private Reward reward;

    @SuppressWarnings("UnusedDeclaration")
    public ItemDto() {
    }

    public ItemDto(final PlayerItem playerItem) {
        id = playerItem.getItem().getId();
        url = playerItem.getItem().getUrl();
        level = playerItem.getItem().getLevel().getLevel();
        state = playerItem.getItemState();
        reward = new Reward(playerItem.getItem().getBonus());
    }

    @XmlRootElement
    @XmlAccessorType(FIELD)
    private static final class Reward {
        @XmlElement
        @Nullable
        private String text;

        @XmlElement
        @Nullable
        private String title;

        @XmlElement
        @Nonnull
        private Date validFrom;

        @XmlElement
        @Nonnull
        private Date validTo;

        @XmlElement
        @Nonnull
        private BonusType type;

        @XmlElement
        @Nullable
        private BigDecimal amount;

        @XmlElement
        @Nullable
        private BigDecimal maxAmount;

        @XmlElement
        @Nullable
        private Integer percentage;

        @XmlElement
        @Nullable
        private Integer quantity;

        @XmlElement
        @Nullable
        private Currency currency;

        @SuppressWarnings("UnusedDeclaration")
        private Reward() {
        }

        private Reward(final Bonus bonus) {
            text = bonus.getName();
            validFrom = bonus.getValidFrom();
            validTo = bonus.getValidTo();
            type = bonus.getBonusType();
            amount = bonus.getAmount() != null ? bonus.getAmount().getEuroValueInBigDecimal() : null;
            maxAmount = bonus.getMaxAmount() != null ? bonus.getMaxAmount().getEuroValueInBigDecimal() : null;
            percentage = bonus.getPercentage();
            quantity = bonus.getQuantity() != null ? bonus.getQuantity() : null;
            currency = bonus.getCurrency();
        }
    }
}
