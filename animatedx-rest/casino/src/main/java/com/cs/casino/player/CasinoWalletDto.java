package com.cs.casino.player;

import com.cs.player.BasicWalletDto;
import com.cs.player.Wallet;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlElement;
import java.math.BigDecimal;

/**
 * @author Omid Alaepour
 */
public class CasinoWalletDto extends BasicWalletDto {

    @XmlElement
    @Nullable
    private BigDecimal bonusBalance;

    @XmlElement
    @Nonnull
    private BigDecimal bonusConversionGoal;

    @XmlElement
    @Nonnull
    private BigDecimal bonusConversionProgress;

    @XmlElement
    @Nullable
    private BigDecimal bonusConversionProgressPercentage;

    @SuppressWarnings("UnusedDeclaration")
    public CasinoWalletDto() {
    }

    public CasinoWalletDto(final Wallet wallet) {
        super(wallet);
        bonusBalance = wallet.getBonusBalance().getEuroValueInBigDecimal();
        bonusConversionGoal = wallet.getBonusConversionGoal().getEuroValueInBigDecimal();
        bonusConversionProgress = wallet.getBonusConversionProgress().getEuroValueInBigDecimal();
        bonusConversionProgressPercentage = bonusConversionGoal.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO :
                bonusConversionProgress.multiply(new BigDecimal(100)).divide(bonusConversionGoal, 2, BigDecimal.ROUND_DOWN);
    }
}
