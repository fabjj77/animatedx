package com.cs.administration.credit;

import com.cs.payment.CreditTransaction;

import org.springframework.data.domain.Page;

import javax.annotation.Nonnull;
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
public class CreditPageableDto {

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    @XmlElement
    @Nonnull
    private List<CreditTransactionDto> transactions;

    @XmlElement
    @Nonnull
    private Long count;

    @SuppressWarnings("UnusedDeclaration")
    public CreditPageableDto() {
    }

    public CreditPageableDto(final Page<CreditTransaction> creditTransactions) {
        transactions = new ArrayList<>(creditTransactions.getSize());
        for (final CreditTransaction creditTransaction : creditTransactions) {
            transactions.add(new CreditTransactionDto(creditTransaction));
        }
        count = creditTransactions.getTotalElements();
    }
}
