package com.cs.administration.payment;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static javax.xml.bind.annotation.XmlAccessType.FIELD;

/**
 * @author Omid Alaepour
 */
@XmlRootElement
@XmlAccessorType(FIELD)
public class ConfirmDeclineWithdrawDto {
    @XmlElement
    private List<String> withdrawReferences;

    @XmlElement(nillable = false, required = false)
    private Map<String, String> results;

    @SuppressWarnings("UnusedDeclaration")
    public ConfirmDeclineWithdrawDto() {
    }

    public ConfirmDeclineWithdrawDto(final Map<String, String> results) {
        this.results = new HashMap<>(results);
    }

    public List<String> getWithdrawReferences() {
        return withdrawReferences;
    }
}
