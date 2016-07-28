package com.cs.casino.player;

import com.cs.validation.ValidationProperties;

import javax.annotation.Nonnull;
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
public class ResetPasswordDto {
    @XmlElement
    @Nonnull
    @Pattern(regexp = ValidationProperties.UUID_PATTERN, message = "resetPasswordDto.uuid.notValid")
    private String uuid;

    @XmlElement
    @Nonnull
    @Pattern(regexp = ValidationProperties.PASSWORD_PATTERN, message = "resetPasswordDto.password.notValid")
    private String password;

    @Nonnull
    public String getUuid() {
        return uuid;
    }

    @Nonnull
    public String getPassword() {
        return password;
    }
}
