package com.cs.administration.credit;

import com.cs.administration.bonus.BackOfficePlayerBonusDto;
import com.cs.payment.CreditTransaction;
import com.cs.payment.Currency;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;
import java.util.Date;

import static javax.xml.bind.annotation.XmlAccessType.FIELD;

/**
 * @author Hadi Movaghar
 */
@XmlRootElement
@XmlAccessorType(FIELD)
public class CreditTransactionDto {

    @XmlElement
    @Nonnull
    private Long id;

    @XmlElement
    @Nonnull
    private Long playerId;

    @XmlElement
    @Nonnull
    private Integer credit;

    @XmlElement
    @Nonnull
    private BigDecimal realMoney;

    @XmlElement
    @Nonnull
    private BigDecimal bonusMoney;

    @XmlElement
    @Nonnull
    private Currency currency;

    @XmlElement
    @Nonnull
    private Date createdDate;

    @XmlElement
    @Nonnull
    private Long level;

    @XmlElement
    @Nullable
    private Double moneyCreditRate;

    @XmlElement
    @Nullable
    private Double bonusCreditRate;

    @XmlElement
    @Nullable
    private BackOfficePlayerBonusDto playerBonus;

    @SuppressWarnings("UnusedDeclaration")
    public CreditTransactionDto() {
    }

    public CreditTransactionDto(final CreditTransaction creditTransaction) {
        id = creditTransaction.getId();
        playerId = creditTransaction.getPlayer().getId();
        credit = creditTransaction.getCredit();
        realMoney = creditTransaction.getRealMoney() != null ? creditTransaction.getRealMoney().getEuroValueInBigDecimal() : null;
        bonusMoney = creditTransaction.getBonusMoney() != null ? creditTransaction.getBonusMoney().getEuroValueInBigDecimal() : null;
        currency = creditTransaction.getCurrency();
        createdDate = creditTransaction.getCreatedDate();
        level = creditTransaction.getLevel();
        moneyCreditRate = creditTransaction.getMoneyCreditRate();
        bonusCreditRate = creditTransaction.getBonusCreditRate();
        playerBonus = creditTransaction.getPlayerBonus() != null ? new BackOfficePlayerBonusDto(creditTransaction.getPlayerBonus()) : null;
    }
}
