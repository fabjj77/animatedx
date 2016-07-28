package com.cs.player;

import com.cs.avatar.AvatarDto;
import com.cs.avatar.LevelDto;
import com.cs.payment.Currency;
import com.cs.persistence.Language;
import com.cs.validation.ValidationProperties;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

import static javax.xml.bind.annotation.XmlAccessType.FIELD;

/**
 * @author Joakim Gottzén
 */
@XmlRootElement
@XmlAccessorType(FIELD)
public abstract class BasicPlayerDto {

    @XmlElement(nillable = true, required = true)
    @Nullable
    private Long id;

    @XmlElement
    @Nonnull
    @NotEmpty
    private String firstName;

    @XmlElement
    @Nonnull
    @NotEmpty
    private String lastName;

    @XmlElement
    @Nonnull
    @Email(message = "playerDto.email.notValid")
    private String emailAddress;

    @XmlElement(nillable = true, required = true)
    @Nullable
    @Pattern(regexp = ValidationProperties.PASSWORD_PATTERN, message = "playerDto.password.notValid")
    private String password;

    @XmlElement
    @Nonnull
    @Pattern(regexp = ValidationProperties.NICKNAME_PATTERN, message = "playerDto.nickname.notValid")
    private String nickname;

    @XmlElement
    @Nonnull
    @Past(message = "playerDto.birthday.notValid")
    private Date birthday;

    @XmlElement
    @Nonnull
    @Valid
    private AvatarDto avatar;

    @XmlElement
    @Nonnull
    @Valid
    private LevelDto level;

    @XmlElement
    @Nonnull
    @NotNull(message = "playerDto.currency.notNull")
    private Currency currency;

    @XmlElement
    @Nonnull
    @NotNull(message = "playerDto.language.notNull")
    private Language language;

    @XmlElement
    @Nonnull
    @NotEmpty
    private String phoneNumber;

    @XmlElement
    @Nonnull
    private ReceivePromotion receivePromotion;

    @XmlElement
    @Nullable
    @Valid
    private PlayerRegisterTrackDto tracking;


    @SuppressWarnings("UnusedDeclaration")
    protected BasicPlayerDto() {
    }

    protected BasicPlayerDto(final Player player) {
        id = player.getId();
        firstName = player.getFirstName();
        lastName = player.getLastName();
        emailAddress = player.getEmailAddress();
        nickname = player.getNickname();
        birthday = player.getBirthday();
        avatar = new AvatarDto(player.getAvatar());
        level = new LevelDto(player.getLevel());
        currency = player.getCurrency();
        language = player.getLanguage();
        phoneNumber = player.getPhoneNumber();
        receivePromotion = player.getReceivePromotion();
    }


    @SuppressWarnings("ConstantConditions")
    protected Player asPlayer() {
        final Player player = new Player();
        player.setFirstName(firstName.trim());
        player.setLastName(lastName.trim());
        player.setEmailAddress(emailAddress.trim());
        player.setPassword(password.trim());
        player.setBirthday(birthday);
        player.setAvatar(avatar != null ? avatar.asAvatar() : null);
        player.setPhoneNumber(phoneNumber.trim());
        player.setLanguage(language);
        player.setReceivePromotion(receivePromotion);
        return player;
    }

    @Nonnull
    public String getEmailAddress() {
        return emailAddress;
    }

    @Nullable
    public PlayerRegisterTrackDto getTracking() {
        return tracking;
    }
}
