package com.cs.player;

import com.cs.avatar.Avatar;
import com.cs.validation.NullOrNotEmpty;
import com.cs.validation.ValidationProperties;

import org.hibernate.validator.constraints.Email;

import javax.annotation.Nullable;
import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import static javax.xml.bind.annotation.XmlAccessType.FIELD;

/**
 * @author Joakim Gottzén
 */
@XmlRootElement
@XmlAccessorType(FIELD)
public abstract class BasicUpdatePlayerDto {

    @XmlElement(nillable = true)
    @NullOrNotEmpty(message = "playerDto.firstName.notValid")
    @Nullable
    private String firstName;

    @XmlElement(nillable = true)
    @NullOrNotEmpty(message = "playerDto.lastName.notValid")
    @Nullable
    private String lastName;

    @XmlElement(nillable = true)
    @Email(message = "playerDto.email.notValid")
    @Nullable
    private String emailAddress;

    @XmlElement(nillable = true)
    @Pattern(regexp = ValidationProperties.NULLABLE_PASSWORD_PATTERN, message = "playerDto.password.notValid")
    @Nullable
    private String password;

    @XmlElement(nillable = true)
    @Pattern(regexp = ValidationProperties.NULLABLE_PASSWORD_PATTERN, message = "playerDto.newPassword.notValid")
    @Nullable
    private String newPassword;

    @XmlElement(nillable = true)
    @NullOrNotEmpty(message = "playerDto.nickname.notValid")
    @Nullable
    private String nickname;

    @XmlElement(nillable = true)
    @NullOrNotEmpty(message = "playerDto.phoneNumber.notValid")
    @Nullable
    private String phoneNumber;

    @XmlElement(nillable = true)
    @Nullable
    private Long avatarId;

    protected BasicUpdatePlayerDto() {
    }

    @Nullable
    public String getPassword() {
        return password;
    }

    @Nullable
    public String getNewPassword() {
        return newPassword;
    }

    @SuppressWarnings("ConstantConditions")
    protected Player asPlayer() {
        final Player player = new Player();
        player.setFirstName(firstName);
        player.setLastName(lastName);
        player.setEmailAddress(emailAddress);
        player.setPassword(password);
        player.setNickname(nickname);
        player.setPhoneNumber(phoneNumber);
        player.setAvatar(new Avatar(avatarId));
        return player;
    }
}
