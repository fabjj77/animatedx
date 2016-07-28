package com.cs.casino.payment;

import com.cs.casino.bonus.CasinoPlayerBonusDto;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

import static javax.xml.bind.annotation.XmlAccessType.FIELD;

/**
 * @author Hadi Movaghar
 */
@XmlRootElement
@XmlAccessorType(FIELD)
public class WithdrawStoreDto {

    @XmlElement
    @Nullable
    private List<WithdrawDetailDto> withdrawDetailList;

    @XmlElement
    @Nullable
    private List<CasinoPlayerBonusDto> playerBonuses;

    @XmlElement
    private boolean storeBankAccount;

    @SuppressWarnings("UnusedDeclaration")
    public WithdrawStoreDto() {
    }

    public WithdrawStoreDto(@Nullable final List<WithdrawDetailDto> withdrawDetailList, final boolean storeBankAccount,
                            @Nullable final List<CasinoPlayerBonusDto> playerBonuses) {
        this.withdrawDetailList = withdrawDetailList;
        this.storeBankAccount = storeBankAccount;
        this.playerBonuses = playerBonuses;
    }
}
