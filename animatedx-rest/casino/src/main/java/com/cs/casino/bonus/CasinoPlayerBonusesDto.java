package com.cs.casino.bonus;

import com.cs.bonus.PlayerBonus;
import com.cs.payment.Money;

import javax.annotation.Nonnull;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static javax.xml.bind.annotation.XmlAccessType.FIELD;

/**
 * @author Joakim Gottzén
 */
@XmlRootElement
@XmlAccessorType(FIELD)
public class CasinoPlayerBonusesDto {

    @XmlElement
    @Nonnull
    private BigDecimal totalAmount;

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    @XmlElement
    @Nonnull
    private List<CasinoPlayerBonusDto> bonuses;

    @SuppressWarnings("UnusedDeclaration")
    public CasinoPlayerBonusesDto() {
    }

    public CasinoPlayerBonusesDto(@Nonnull final List<PlayerBonus> bonuses) {
        totalAmount = BigDecimal.ZERO;
        this.bonuses = new ArrayList<>(bonuses.size());
        for (final PlayerBonus bonus : bonuses) {
            this.bonuses.add(new CasinoPlayerBonusDto(bonus));
            final Money currentBalance = bonus.getCurrentBalance();
            if (currentBalance != null) {
                totalAmount = totalAmount.add(currentBalance.getEuroValueInBigDecimal());
            }
        }
    }
}
