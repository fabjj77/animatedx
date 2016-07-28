package com.cs.payment;

import org.springframework.data.domain.Page;

import javax.annotation.Nonnull;
import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Joakim Gottzén
 */
public class AwaitingWithdrawPageableDto {
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    @XmlElement
    @Nonnull
    private List<AwaitingWithdrawDto> withdrawals;

    @XmlElement
    @Nonnull
    private Long count;

    @SuppressWarnings("UnusedDeclaration")
    public AwaitingWithdrawPageableDto() {
    }

    public AwaitingWithdrawPageableDto(final Page<PaymentTransaction> awaitingWithdraws) {
        withdrawals = new ArrayList<>(awaitingWithdraws.getSize());
        for (final PaymentTransaction transaction : awaitingWithdraws) {
            withdrawals.add(new AwaitingWithdrawDto(transaction));
        }
        count = awaitingWithdraws.getTotalElements();
    }
}
