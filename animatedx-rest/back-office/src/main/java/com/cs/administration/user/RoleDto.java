package com.cs.administration.user;

import com.cs.user.SecurityRole;

import org.hibernate.validator.constraints.NotEmpty;

import javax.annotation.Nonnull;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import static javax.xml.bind.annotation.XmlAccessType.FIELD;

/**
 * @author Joakim Gottzén
 */
@XmlRootElement
@XmlAccessorType(FIELD)
public class RoleDto {
    @XmlElement
    @Nonnull
    @NotEmpty(message = "role.name.notEmpty")
    private String role;

    @SuppressWarnings("UnusedDeclaration")
    public RoleDto() {
    }

    public RoleDto(final SecurityRole role) {
        this.role = role.name();
    }
}
