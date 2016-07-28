package com.cs.administration.player;

import com.cs.payment.Money;
import com.cs.player.BasicWalletDto;
import com.cs.player.Wallet;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;

import static javax.xml.bind.annotation.XmlAccessType.FIELD;

/**
 * @author Joakim Gottzén
 */
@XmlRootElement
@XmlAccessorType(FIELD)
public class BackOfficeWalletDto extends BasicWalletDto {

    @XmlElement
    @Nonnull
    private BigDecimal reservedBonusBalance;

    @XmlElement
    @Nonnull
    private BigDecimal reservedBonusProgressionGoal;

    @XmlElement
    @Nonnull
    private BigDecimal accumulatedBonusTurnover;

    @XmlElement
    @Nonnull
    private BigDecimal accumulatedMoneyTurnover;

    @XmlElement
    @Nullable
    private BigDecimal accumulatedDeposit;

    @XmlElement
    @Nullable
    private BigDecimal accumulatedWithdrawal;

    @SuppressWarnings("UnusedDeclaration")
    public BackOfficeWalletDto() {
    }

    public BackOfficeWalletDto(final Wallet wallet) {
        super(wallet);
        accumulatedMoneyTurnover = wallet.getAccumulatedMoneyTurnover().getEuroValueInBigDecimal();
        accumulatedBonusTurnover = wallet.getAccumulatedBonusTurnover().getEuroValueInBigDecimal();
        accumulatedDeposit = wallet.getAccumulatedDeposit().getEuroValueInBigDecimal();
        accumulatedWithdrawal = wallet.getAccumulatedWithdrawal().getEuroValueInBigDecimal();
    }

    public Wallet asWallet() {
        final Wallet wallet = new Wallet();

        if (getMoneyBalance() != null) {
            wallet.setMoneyBalance(new Money(getMoneyBalance()));
        }

        if (getCreditsBalance() != null) {
            wallet.setCreditsBalance(getCreditsBalance());
        }

        return wallet;
    }
}
