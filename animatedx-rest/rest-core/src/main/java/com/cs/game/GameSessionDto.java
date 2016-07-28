package com.cs.game;

import org.hibernate.validator.constraints.NotEmpty;

import javax.annotation.Nonnull;
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
public class GameSessionDto {
    @XmlElement
    @Nonnull
    private Long playerId;

    @XmlElement
    @Nonnull
    @NotEmpty(message = "gameSessionDto.gameId.notEmpty")
    private String gameId;

    @XmlElement
    private Date date;

    @XmlElement
    @Nonnull
    private BigDecimal betRealMoney;

    @XmlElement
    @Nonnull
    private BigDecimal winRealMoney;

    @XmlElement
    @Nonnull
    private BigDecimal betBonusMoney;

    @XmlElement
    @Nonnull
    private BigDecimal winBonusMoney;

    @XmlElement
    @Nonnull
    private BigDecimal moneyBalance;

    @XmlElement
    @Nonnull
    private BigDecimal bonusBalance;

    @SuppressWarnings("UnusedDeclaration")
    public GameSessionDto() {
    }

    public GameSessionDto(final GameTransaction gameTransaction) {
        playerId = gameTransaction.getPlayer().getId();
        gameId = gameTransaction.getGameId();
        date = gameTransaction.getCreatedDate();
        betRealMoney = gameTransaction.getMoneyWithdraw().getEuroValueInBigDecimal();
        winRealMoney = gameTransaction.getMoneyDeposit().getEuroValueInBigDecimal();
        betBonusMoney = gameTransaction.getBonusWithdraw().getEuroValueInBigDecimal();
        winBonusMoney = gameTransaction.getBonusDeposit().getEuroValueInBigDecimal();
        moneyBalance = gameTransaction.getMoneyBalance().getEuroValueInBigDecimal();
        bonusBalance = gameTransaction.getBonusBalance().getEuroValueInBigDecimal();
    }
}
