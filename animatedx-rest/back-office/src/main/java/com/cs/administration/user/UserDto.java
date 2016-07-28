package com.cs.administration.user;

import com.cs.persistence.Status;
import com.cs.user.SecurityRole;
import com.cs.user.User;
import com.cs.validation.ValidationProperties;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

import static javax.xml.bind.annotation.XmlAccessType.FIELD;

/**
 * @author Omid Alaepour
 */
@XmlRootElement
@XmlAccessorType(FIELD)
public class UserDto {
    @XmlElement(nillable = true, required = true)
    @Nullable
    private Long id;

    @XmlElement
    @Nonnull
    @NotEmpty(message = "user.firstName.notEmpty")
    private String firstName;

    @XmlElement
    @Nonnull
    @NotEmpty(message = "user.lastName.notEmpty")
    private String lastName;

    @XmlElement
    @Nonnull
    @Email(message = "user.emailAddress.notValid")
    private String emailAddress;

    @XmlElement(nillable = true, required = true)
    @Nonnull
    @Pattern(regexp = ValidationProperties.PASSWORD_PATTERN, message = "user.password.notValid")
    private String password;

    @XmlElement
    @Nonnull
    @NotEmpty(message = "user.nickname.notEmpty")
    private String nickname;

    @XmlElement
    @Nullable
    private Status status;

    @XmlElement
    @Nullable
    private String createdBy;

    @XmlElement
    @Nullable
    private Date createdDate;

    @XmlElement
    @Nullable
    private String modifiedBy;

    @XmlElement
    @Nullable
    private Date modifiedDate;

    @XmlElement
    @Nonnull
    @NotNull
    private SecurityRole role;

    @SuppressWarnings("UnusedDeclaration")
    public UserDto() {
    }

    public UserDto(final User user) {
        id = user.getId();
        firstName = user.getFirstName();
        lastName = user.getLastName();
        emailAddress = user.getEmailAddress();
        nickname = user.getNickname();
        status = user.getStatus();
        createdBy = user.getCreatedBy().getEmailAddress();
        createdDate = user.getCreatedDate();
        modifiedBy = user.getModifiedBy() != null ? user.getModifiedBy().getEmailAddress() : null;
        modifiedDate = user.getModifiedDate();
        role = user.getRoles().get(0).getPk().getRole().getName();
    }

    @Nonnull
    public SecurityRole getRole() {
        return role;
    }

    public User asUser() {
        final User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmailAddress(emailAddress);
        user.setPassword(password);
        user.setNickname(nickname);
        return user;
    }
}
