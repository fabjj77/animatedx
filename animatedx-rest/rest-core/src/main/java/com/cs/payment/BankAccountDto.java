package com.cs.payment;

import org.hibernate.validator.constraints.NotEmpty;

import javax.annotation.Nonnull;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import static javax.xml.bind.annotation.XmlAccessType.FIELD;

/**
 * @author Hadi Movaghar
 */
@XmlRootElement
@XmlAccessorType(FIELD)
public class BankAccountDto {
    @XmlElement
    @Nonnull
    @NotEmpty(message = "bankAccountDto.iban.notEmpty")
    private String iban;

    @XmlElement
    @Nonnull
    @NotEmpty(message = "bankAccountDto.bic.notEmpty")
    private String bic;

    @XmlElement
    @Nonnull
    @NotEmpty(message = "bankAccountDto.bankName.notEmpty")
    private String bankName;

    @XmlElement
    @Nonnull
    @NotEmpty(message = "bankAccountDto.name.notEmpty")
    private String name;

    public BankAccountDto() {
    }

    @Nonnull
    public String getIban() {
        return iban;
    }

    @Nonnull
    public String getBic() {
        return bic;
    }

    @Nonnull
    public String getBankName() {
        return bankName;
    }

    @Nonnull
    public String getName() {
        return name;
    }
}
