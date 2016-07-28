package com.cs.devcode;

import com.cs.player.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;

import static javax.xml.bind.annotation.XmlAccessType.FIELD;

/**
 * @author Hadi Movaghar
 */
@XmlRootElement
@XmlAccessorType(FIELD)
public class VerifyUserResponse {

    @XmlElement
    @Nonnull
    @NotNull(message = "verifyUserResponse.userId.notNull")
    private String userId;

    @XmlElement
    @Nonnull
    @NotNull(message = "verifyUserResponse.success.notNull")
    private Boolean success;

    @XmlElement
    @Nullable
    private String userCat;

    @XmlElement
    @Nullable
    private String firstName;

    @XmlElement
    @Nullable
    private String lastName;

    @XmlElement
    @Nullable
    private String street;

    @XmlElement
    @Nullable
    private String city;

    @XmlElement
    @Nullable
    private String zip;

    @XmlElement
    @Nullable
    private String country;

    @XmlElement
    @Nullable
    private String email;

    @XmlElement
    @Nullable
    private String dob;

    @XmlElement
    @Nullable
    private String mobile;

    @XmlElement
    @Nullable
    private BigDecimal balance;

    @XmlElement
    @Nullable
    private String balanceCy;

    @XmlElement
    @Nullable
    private String locale;

    @XmlElement
    @Nullable
    private Number errCode;

    @XmlElement
    @Nullable
    private String errMsg;

    @SuppressWarnings("UnusedDeclaration")
    public VerifyUserResponse() {
    }

    public VerifyUserResponse(@Nonnull final Player player) {
        userId = player.getId().toString();
        success = true;
        firstName = player.getFirstName();
        lastName = player.getLastName();
        street = player.getAddress().getStreet();
        city = player.getAddress().getCity();
        zip = player.getAddress().getZipCode();
        country = player.getAddress().getCountry().toIso3();
        email = player.getEmailAddress();
        dob = new SimpleDateFormat("yyyy-MM-dd").format(player.getBirthday());
        mobile = player.getPhoneNumber(); // TODO according format?
        balance = player.getWallet().getMoneyBalance().getEuroValueInBigDecimal();
        balanceCy = player.getCurrency().toString();
        locale = player.getAddress().getCountry().getLocale();
    }
}
