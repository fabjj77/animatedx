package com.cs.administration.user;

import com.cs.validation.ValidationProperties;

import javax.annotation.Nonnull;
import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import static javax.xml.bind.annotation.XmlAccessType.FIELD;

/**
 * @author Hadi Movaghar
 */
@XmlRootElement
@XmlAccessorType(FIELD)
public class PasswordDto {
    @XmlElement(nillable = true, required = true)
    @Nonnull
    @Pattern(regexp = ValidationProperties.PASSWORD_PATTERN, message = "user.password.notValid")
    private String updaterPassword;

    @XmlElement(nillable = true, required = true)
    @Nonnull
    @Pattern(regexp = ValidationProperties.PASSWORD_PATTERN, message = "user.password.notValid")
    private String userPassword;

    @Nonnull
    public String getUpdaterPassword() {
        return updaterPassword;
    }

    @Nonnull
    public String getUserPassword() {
        return userPassword;
    }
}
