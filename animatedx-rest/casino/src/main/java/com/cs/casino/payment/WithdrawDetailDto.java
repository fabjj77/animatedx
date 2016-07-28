package com.cs.casino.payment;

import com.cs.payment.PayoutDetailReference;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static javax.xml.bind.annotation.XmlAccessType.FIELD;

/**
 * @author Hadi Movaghar
 */
@XmlRootElement
@XmlAccessorType(FIELD)
public class WithdrawDetailDto {

    @XmlElement
    @Nonnull
    private String withdrawReference;

    @XmlElement
    @Nonnull
    private String variant;

    @XmlElement
    @Nonnull
    private String holderName;

    @XmlElement
    @Nonnull
    private Date creationDate;

    @XmlElement
    @Nullable
    private String cardNumber;

    @XmlElement
    @Nullable
    private String accountNumber;

    @XmlElement
    @Nullable
    private String iban;

    @SuppressWarnings("UnusedDeclaration")
    WithdrawDetailDto() {
    }

    WithdrawDetailDto(@Nonnull final PayoutDetailReference payoutDetailReference) {
        withdrawReference = payoutDetailReference.getPayoutReference();
        variant = payoutDetailReference.getVariant();
        holderName = payoutDetailReference.getHolderName();
        creationDate = payoutDetailReference.getCreationDate();
        cardNumber = payoutDetailReference.getCardNumber();
        accountNumber = payoutDetailReference.getAccountNumber();
        iban = payoutDetailReference.getIban();
    }

    public static List<WithdrawDetailDto> getWithdrawDetailList(final List<PayoutDetailReference> list) {
        final List<WithdrawDetailDto> withdrawDetailDtoList = new ArrayList<>();
        for (final PayoutDetailReference payoutDetailReference : list) {
            withdrawDetailDtoList.add(new WithdrawDetailDto(payoutDetailReference));
        }
        return withdrawDetailDtoList;
    }
}
