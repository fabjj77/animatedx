package com.cs.administration.promotion;

import com.cs.payment.Money;
import com.cs.persistence.Country;
import com.cs.promotion.Criteria;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import static javax.xml.bind.annotation.XmlAccessType.FIELD;

/**
 * @author Hadi Movaghar
 */
@XmlRootElement
@XmlAccessorType(FIELD)
public class CriteriaDto {
    @XmlElement
    @Nullable
    private Long level;

    @XmlElement
    @Nullable
    private BigDecimal amount;

    @XmlElement
    @Nullable
    private Integer repetition;

    @XmlElement
    @Nullable
    private TimeCriteriaDto timeCriteria;

    @XmlElement
    @Nullable
    private List<Country> allowedCountries;

    public CriteriaDto() {
    }

    public CriteriaDto(final Criteria criteria) {
        level = criteria.getLevel() != null ? criteria.getLevel().getLevel() : null;
        amount = criteria.getAmount() != null ? criteria.getAmount().getEuroValueInBigDecimal() : null;
        repetition = criteria.getRepetition();
        timeCriteria = new TimeCriteriaDto(criteria.getTimeCriteria());
        allowedCountries = new ArrayList<>(criteria.getAllowedCountries());
    }

    public Criteria asCriteria() {
        final Criteria criteria = new Criteria();
        final Money moneyAmount = amount != null ? new Money(amount) : null;
        criteria.setAmount(moneyAmount);
        criteria.setRepetition(repetition);
        criteria.setTimeCriteria(timeCriteria != null ? timeCriteria.asTimeCriteria() : null);
        criteria.setAllowedCountries(allowedCountries != null ? EnumSet.copyOf(allowedCountries) : EnumSet.noneOf(Country.class));
        return criteria;
    }

    @Nullable
    public Long getLevel() {
        return level;
    }
}
