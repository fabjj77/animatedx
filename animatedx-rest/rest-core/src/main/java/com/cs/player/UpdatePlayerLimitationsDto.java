package com.cs.player;

import com.cs.validation.ValidationProperties;

import javax.annotation.Nonnull;
import javax.validation.constraints.NotNull;
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
public class UpdatePlayerLimitationsDto extends PlayerLimitationsDto {

    @XmlElement(nillable = false, required = true)
    @Nonnull
    @NotNull
    @Pattern(regexp = ValidationProperties.PASSWORD_PATTERN, message = "playerLimitation.password.notValid")
    private String password;

    @Nonnull
    public String getPassword() {
        return password;
    }
}
