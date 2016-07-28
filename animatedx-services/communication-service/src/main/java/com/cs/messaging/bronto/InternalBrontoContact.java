package com.cs.messaging.bronto;

import com.cs.messaging.email.BrontoContactStatus;
import com.cs.messaging.email.BrontoWorkflowName;
import com.cs.payment.Currency;
import com.cs.persistence.Country;
import com.cs.persistence.Language;
import com.cs.persistence.Status;
import com.cs.player.Player;
import com.cs.player.ReceivePromotion;
import com.cs.player.Verification;

import com.bronto.api.v4.ContactObject;
import com.google.common.base.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import static com.cs.messaging.email.BrontoContactStatus.ONBOARDING;
import static com.cs.messaging.email.BrontoContactStatus.UNCONFIRMED;
import static javax.xml.bind.annotation.XmlAccessType.FIELD;

/**
 * @author Omid Alaepour
 */
@XmlRootElement
@XmlAccessorType(FIELD)
public class InternalBrontoContact implements Serializable {
    private static final long serialVersionUID = 1L;

    @XmlElement
    @Nonnull
    private String brontoId;

    @XmlElement
    @Nonnull
    private BrontoContactStatus status;

    @XmlElement
    @Nonnull
    private String uuid;

    @XmlElement
    @Nonnull
    private String firstName;

    @XmlElement
    @Nonnull
    private String lastName;

    @XmlElement
    @Nonnull
    private String nickname;

    @XmlElement
    @Nonnull
    private String emailAddress;

    @XmlElement
    @Nonnull
    private String phoneNumber;

    @XmlElement
    @Nonnull
    private Long playerId;

    @XmlElement
    @Nonnull
    private String street;

    @XmlElement
    @Nonnull
    private String street2;

    @XmlElement
    @Nonnull
    private String zipCode;

    @XmlElement
    @Nonnull
    private String city;

    @XmlElement
    @Nonnull
    private String state;

    @XmlElement
    @Nonnull
    private Country country;

    @XmlElement
    @Nonnull
    private Long level;

    @XmlElement
    @Nonnull
    private Long avatarId;

    @XmlElement
    @Nonnull
    private Date birthday;

    @XmlElement
    @Nonnull
    private Date createdDate;

    @XmlElement
    @Nonnull
    private Verification emailVerification;

    @XmlElement
    @Nonnull
    private BigDecimal moneyBalance;

    @XmlElement
    @Nonnull
    private BigDecimal bonusBalance;

    @XmlElement
    @Nonnull
    private Language language;

    @XmlElement
    @Nonnull
    private Currency currency;

    @XmlElement
    @Nonnull
    private Status playerStatus;

    @XmlElement
    @Nullable
    private BrontoWorkflowName brontoWorkflowName;

    public InternalBrontoContact() {
    }

    @SuppressWarnings("ConstantConditions")
    public InternalBrontoContact(final Player player) {
        playerId = player.getId();
        firstName = player.getFirstName();
        lastName = player.getLastName();
        nickname = player.getNickname();
        emailVerification = player.getEmailVerification();
        emailAddress = player.getEmailAddress();
        phoneNumber = player.getPhoneNumber();
        level = player.getLevel().getLevel();
        birthday = player.getBirthday();
        avatarId = player.getAvatar().getId();
        moneyBalance = player.getWallet().getMoneyBalance().getEuroValueInBigDecimal();
        bonusBalance = player.getWallet().getBonusBalance().getEuroValueInBigDecimal();
        language = player.getLanguage();
        currency = player.getCurrency();
        playerStatus = player.getStatus();
        if (player.getAddress() != null) {
            street = player.getAddress().getStreet();
            street2 = player.getAddress().getStreet2();
            zipCode = player.getAddress().getZipCode();
            city = player.getAddress().getCity();
            state = player.getAddress().getState();
            country = player.getAddress().getCountry();
        }
        if (player.getReceivePromotion() == ReceivePromotion.UNSUBSCRIBED) {
            status = UNCONFIRMED;
        } else {
            status = ONBOARDING;
        }
    }

    @SuppressWarnings("ConstantConditions")
    public InternalBrontoContact(final Player player, final BrontoWorkflowName brontoWorkflowName) {
        this(player);
        this.brontoWorkflowName = brontoWorkflowName;
    }

    public InternalBrontoContact(final Player player, final BrontoWorkflowName brontoWorkflowName, @Nonnull final String uuid) {
        this(player, brontoWorkflowName);
        this.uuid = uuid;
    }

    public ContactObject asContactObject() {
        final ContactObject contactObject = new ContactObject();
        contactObject.setId(brontoId);
        return contactObject;
    }

    @Nonnull
    public String getBrontoId() {
        return brontoId;
    }

    public void setBrontoId(@Nonnull final String brontoId) {
        this.brontoId = brontoId;
    }

    @Nonnull
    public BrontoContactStatus getStatus() {
        return status;
    }

    public void setStatus(@Nonnull final BrontoContactStatus status) {
        this.status = status;
    }

    @Nonnull
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(@Nonnull final String firstName) {
        this.firstName = firstName;
    }

    @Nonnull
    public String getLastName() {
        return lastName;
    }

    public void setLastName(@Nonnull final String lastName) {
        this.lastName = lastName;
    }

    @Nonnull
    public String getNickname() {
        return nickname;
    }

    public void setNickname(@Nonnull final String nickname) {
        this.nickname = nickname;
    }

    @Nonnull
    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(@Nonnull final String emailAddress) {
        this.emailAddress = emailAddress;
    }

    @Nonnull
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(@Nonnull final String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Nonnull
    public Long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(@Nonnull final Long playerId) {
        this.playerId = playerId;
    }

    @Nonnull
    public String getStreet() {
        return street;
    }

    public void setStreet(@Nonnull final String street) {
        this.street = street;
    }

    public String getStreet2() {
        return street2;
    }

    public void setStreet2(@Nonnull final String street2) {
        this.street2 = street2;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(@Nonnull final String zipCode) {
        this.zipCode = zipCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(@Nonnull final String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(@Nonnull final String state) {
        this.state = state;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(@Nonnull final Country country) {
        this.country = country;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(@Nonnull final Date createdDate) {
        this.createdDate = createdDate;
    }

    @Nonnull
    public String getUuid() {
        return uuid;
    }

    public void setUuid(@Nonnull final String uuid) {
        this.uuid = uuid;
    }

    @Nonnull
    public Verification getEmailVerification() {
        return emailVerification;
    }

    public void setEmailVerification(@Nonnull final Verification emailVerification) {
        this.emailVerification = emailVerification;
    }

    @Nonnull
    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(@Nonnull final Date birthday) {
        this.birthday = birthday;
    }

    @Nullable
    public BrontoWorkflowName getBrontoWorkflowName() {
        return brontoWorkflowName;
    }

    public void setBrontoWorkflowName(@Nonnull final BrontoWorkflowName brontoWorkflowName) {
        this.brontoWorkflowName = brontoWorkflowName;
    }

    @Nonnull
    public Long getLevel() {
        return level;
    }

    public void setLevel(@Nonnull final Long level) {
        this.level = level;
    }

    @Nonnull
    public Long getAvatarId() {
        return avatarId;
    }

    public void setAvatarId(@Nonnull final Long avatarId) {
        this.avatarId = avatarId;
    }

    @Nonnull
    public BigDecimal getMoneyBalance() {
        return moneyBalance;
    }

    public void setMoneyBalance(@Nonnull final BigDecimal moneyBalance) {
        this.moneyBalance = moneyBalance;
    }

    @Nonnull
    public BigDecimal getBonusBalance() {
        return bonusBalance;
    }

    public void setBonusBalance(@Nonnull final BigDecimal bonusBalance) {
        this.bonusBalance = bonusBalance;
    }

    @Nonnull
    public Language getLanguage() {
        return language;
    }

    public void setLanguage(@Nonnull final Language language) {
        this.language = language;
    }

    @Nonnull
    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(@Nonnull final Currency currency) {
        this.currency = currency;
    }

    @Nonnull
    public Status getPlayerStatus() {
        return playerStatus;
    }

    public void setPlayerStatus(@Nonnull final Status playerStatus) {
        this.playerStatus = playerStatus;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final InternalBrontoContact that = (InternalBrontoContact) o;

        return Objects.equal(brontoId, that.brontoId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(brontoId);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("brontoId", brontoId)
                .add("status", status)
                .add("uuid", uuid)
                .add("firstName", firstName)
                .add("lastName", lastName)
                .add("nickname", nickname)
                .add("emailAddress", emailAddress)
                .add("phoneNumber", phoneNumber)
                .add("playerId", playerId)
                .add("street", street)
                .add("street2", street2)
                .add("zipCode", zipCode)
                .add("city", city)
                .add("state", state)
                .add("country", country)
                .add("level", level)
                .add("avatarId", avatarId)
                .add("birthday", birthday)
                .add("createdDate", createdDate)
                .add("emailVerification", emailVerification)
                .add("moneyBalance", moneyBalance)
                .add("bonusBalance", bonusBalance)
                .add("language", language)
                .add("currency", currency)
                .add("brontoWorkflowName", brontoWorkflowName)
                .add("playerStatus", playerStatus)
                .toString();
    }
}
