package com.cs.player;

import com.cs.payment.Money;

import javax.annotation.Nullable;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

import static javax.xml.bind.annotation.XmlAccessType.FIELD;

/**
 * @author Hadi Movaghar
 */
@XmlRootElement
@XmlAccessorType(FIELD)
public class PlayerLimitationsDto {

    @XmlElement
    @Nullable
    @Valid
    private final List<LimitDto> limitList = new ArrayList<>();

    @XmlElement
    @Nullable
    @Min(value = 0, message = "limitDto.amount.min")
    private Integer sessionLength;

    @SuppressWarnings("UnusedDeclaration")
    public PlayerLimitationsDto() {
    }

    public PlayerLimitationsDto(final PlayerLimitation limit) {
        limitList.add(new LimitDto(LimitationType.LOSS_AMOUNT, limit.getLossTimeUnit(), limit.getLossLimit().getEuroValueInBigDecimal(), limit.getLossPercentage(),
                                   limit.getModifiedDate()));
        limitList.add(new LimitDto(LimitationType.BET_AMOUNT, limit.getBetTimeUnit(), limit.getBetLimit().getEuroValueInBigDecimal(), limit.getBetPercentage(),
                                   limit.getModifiedDate()));
        sessionLength = limit.getSessionLength();
    }

    public List<Limit> getLimitsAsList() {
        final List<Limit> limits = new ArrayList<>();
        for (final LimitDto limitDto : limitList) {
            limits.add(new Limit(limitDto.getLimitationType(), limitDto.getTimeUnit(), new Money(limitDto.getAmount())));
        }
        return limits;
    }

    @Nullable
    public Integer getSessionLength() {
        return sessionLength;
    }
}
