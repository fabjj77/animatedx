package com.cs.administration.payment;

import com.cs.payment.PaymentTransaction;

import javax.annotation.Nonnull;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static javax.xml.bind.annotation.XmlAccessType.FIELD;

/**
 * @author Hadi Movaghar
 */
@XmlRootElement
@XmlAccessorType(FIELD)
public class RefundCancelDetailDto {
    @XmlElement
    @Nonnull
    private String originalReference;

    @XmlElement
    @Nonnull
    private BigDecimal amount;

    @XmlElement
    @Nonnull
    private String paymentMethod;

    @XmlElement
    @Nonnull
    private Date creationDate;

    RefundCancelDetailDto() {
    }

    RefundCancelDetailDto(@Nonnull final PaymentTransaction paymentTransaction) {
        originalReference = paymentTransaction.getProviderReference();
        amount = paymentTransaction.getAmount().getEuroValueInBigDecimal();
        paymentMethod = paymentTransaction.getPaymentMethod();
        creationDate = paymentTransaction.getCreatedDate();
    }

    public static List<RefundCancelDetailDto> getList(final List<PaymentTransaction> list) {
        final List<RefundCancelDetailDto> refundCancelDetailDtoList = new ArrayList<>();
        for (final PaymentTransaction paymentTransaction : list) {
            refundCancelDetailDtoList.add(new RefundCancelDetailDto(paymentTransaction));
        }
        return refundCancelDetailDtoList;
    }
}
