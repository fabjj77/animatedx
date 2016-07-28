package com.cs.administration.user;

import com.cs.persistence.Status;
import com.cs.user.SecurityRole;
import com.cs.user.User;
import com.cs.validation.NullOrNotEmpty;
import com.cs.validation.ValidationProperties;

import org.hibernate.validator.constraints.Email;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
public class UserUpdateDto {
    @XmlElement(nillable = true, required = true)
    @Nonnull
    private Long id;

    @XmlElement
    @Nullable
    @NullOrNotEmpty(message = "user.firstName.nullOrNotEmpty")
    private String firstName;

    @XmlElement
    @Nullable
    @NullOrNotEmpty(message = "user.lastName.nullOrNotEmpty")
    private String lastName;

    @XmlElement
    @Nullable
    @Email(message = "user.emailAddress.notValid")
    private String emailAddress;

    @XmlElement(nillable = true, required = true)
    @Nullable
    @Pattern(regexp = ValidationProperties.NULLABLE_PASSWORD_PATTERN, message = "user.password.notValid")
    private String password;

    @XmlElement(nillable = true, required = true)
    @Nullable
    @Pattern(regexp = ValidationProperties.NULLABLE_PASSWORD_PATTERN, message = "user.newPassword.notValid")
    private String newPassword;

    @XmlElement
    @Nullable
    @NullOrNotEmpty(message = "user.nickname.nullOrNotEmpty")
    private String nickname;

    @XmlElement
    @Nullable
    private Status status;

    @XmlElement
    @Nonnull
    private SecurityRole role;

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

    @SuppressWarnings("UnusedDeclaration")
    public UserUpdateDto() {
    }

    @Nullable
    public String getPassword() {
        return password;
    }

    @Nullable
    public String getNewPassword() {
        return newPassword;
    }

    @Nonnull
    public SecurityRole getRole() {
        return role;
    }

    @SuppressWarnings("ConstantConditions")
    public User asUser() {
        final User user = new User();
        user.setId(id);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmailAddress(emailAddress);
        user.setNickname(nickname);
        user.setStatus(status);
        // Do not copy the created* and modified* fields

        return user;
    }
}
