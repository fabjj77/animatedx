package com.cs.promotion;

import com.cs.payment.Money;

import javax.annotation.Nullable;
import java.io.Serializable;

/**
 * @author Hadi Movaghar
 */
public class PlayerCriteria implements Serializable {
    private static final long serialVersionUID = 1L;

    @Nullable
    private Money amount;

    @Nullable
    private Integer repetition;

    public PlayerCriteria() {
    }

    @Nullable
    public Money getAmount() {
        return amount;
    }

    public void setAmount(@Nullable final Money amount) {
        this.amount = amount;
    }

    @Nullable
    public Integer getRepetition() {
        return repetition;
    }

    public void setRepetition(@Nullable final Integer repetition) {
        this.repetition = repetition;
    }
}
