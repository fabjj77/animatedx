package com.cs.payment;

import org.springframework.data.domain.Page;

import javax.annotation.Nonnull;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static javax.xml.bind.annotation.XmlAccessType.FIELD;

/**
 * @author Omid Alaepour
 */
@XmlRootElement
@XmlAccessorType(FIELD)
public class PaymentPageableDto {

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    @XmlElement
    @Nonnull
    private List<PaymentTransactionDto> transactions;

    @XmlElement
    @Nonnull
    private Long count;

    @SuppressWarnings("UnusedDeclaration")
    public PaymentPageableDto() {
    }

    // TODO:Joakim Remove the size parameter when Adyen is removed form the system, the size is necessary for returning the correct number of elements
    public PaymentPageableDto(@Nonnull final Page<PaymentTransaction> paymentTransactions, final Page<DCPaymentTransaction> dcPaymentTransactions,
                              final Integer page, final Integer size) {
        transactions = new ArrayList<>(size);

        final List<PaymentTransactionDto> temp = new ArrayList<>(paymentTransactions.getSize() + dcPaymentTransactions.getSize());
        for (final DCPaymentTransaction dcPaymentTransaction : dcPaymentTransactions) {
            temp.add(new PaymentTransactionDto(dcPaymentTransaction));
        }
        for (final PaymentTransaction paymentTransaction : paymentTransactions) {
            temp.add(new PaymentTransactionDto(paymentTransaction));
        }
        Collections.sort(temp, new Comparator<PaymentTransactionDto>() {
            @Override
            public int compare(final PaymentTransactionDto o1, final PaymentTransactionDto o2) {
                // Sort descending
                return o2.getDate().compareTo(o1.getDate());
            }
        });

        count = paymentTransactions.getTotalElements() + dcPaymentTransactions.getTotalElements();

        final int fromIndex = page * size;
        final int toIndex = fromIndex + size;
        transactions.addAll(temp.subList(fromIndex, (int) Math.min((long) toIndex, count)));
    }
}
