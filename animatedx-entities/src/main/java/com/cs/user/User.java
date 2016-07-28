package com.cs.user;

import com.cs.persistence.Status;
import com.cs.validation.ValidationProperties;

import com.google.common.base.Objects;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

import static javax.persistence.EnumType.STRING;
import static javax.persistence.GenerationType.IDENTITY;
import static javax.persistence.TemporalType.TIMESTAMP;

/**
 * @author Joakim Gottzén
 */
@Entity
@Table(name = "users")
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    @Nonnull
    private Long id;

    @Column(name = "first_name", nullable = false, length = 30)
    @Nonnull
    @NotEmpty(message = "user.firstName.notEmpty")
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 30)
    @Nonnull
    @NotEmpty(message = "user.firstName.notEmpty")
    private String lastName;

    @Column(name = "email_address", nullable = false, length = 50, unique = true)
    @Nonnull
    @Email(message = "user.emailAddress.notValid")
    private String emailAddress;

    @Column(name = "password", nullable = false, length = 70)
    @Nonnull
    @Pattern(regexp = ValidationProperties.BCRYPT_PASSWORD_PATTERN, message = "user.password.notValid")
    private String password;

    @Column(name = "nickname", nullable = false, length = 30, unique = true)
    @Nonnull
    @NotEmpty(message = "user.nickname.notEmpty")
    private String nickname;

    @Column(name = "status", nullable = false, length = 50)
    @Enumerated(STRING)
    @Nonnull
    @NotNull(message = "user.status.notNull")
    private Status status;

    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false)
    @Nonnull
    @NotNull(message = "user.createdBy.notNull")
    private User createdBy;

    @Column(name = "created_date", nullable = false)
    @Temporal(TIMESTAMP)
    @Nonnull
    @NotNull(message = "user.createdDate.notNull")
    private Date createdDate;

    @ManyToOne
    @JoinColumn(name = "modified_by")
    @Nullable
    private User modifiedBy;

    @Column(name = "modified_date")
    @Temporal(TIMESTAMP)
    @Nullable
    private Date modifiedDate;

    @Column(name = "failed_login_attempts")
    @Nonnull
    private Integer failedLoginAttempts;

    @Column(name = "last_failed_login_date")
    @Temporal(TIMESTAMP)
    @Nullable
    private Date lastFailedLoginDate;

    @OneToMany(mappedBy = "pk.user")
    @Nonnull
    private List<UserRole> roles;

    public User() {}

    public User(@Nonnull final Long id) {
        this.id = id;
    }

    protected User(@Nonnull final String firstName, @Nonnull final String lastName, @Nonnull final String emailAddress, @Nonnull final String password,
                   @Nonnull final String nickname, @Nonnull final Status status, @Nonnull final User createdBy, @Nonnull final Date createdDate) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.emailAddress = emailAddress;
        this.password = password;
        this.nickname = nickname;
        this.status = status;
        this.createdBy = createdBy;
        this.createdDate = createdDate;
        failedLoginAttempts = 0;
    }

    @Nonnull
    public Long getId() {
        return id;
    }

    public void setId(@Nonnull final Long id) {
        this.id = id;
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
    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(@Nonnull final String emailAddress) {
        this.emailAddress = emailAddress;
    }

    @Nonnull
    public String getPassword() {
        return password;
    }

    public void setPassword(@Nonnull final String password) {
        this.password = password;
    }

    @Nonnull
    public String getNickname() {
        return nickname;
    }

    public void setNickname(@Nonnull final String nickname) {
        this.nickname = nickname;
    }

    @Nonnull
    public Status getStatus() {
        return status;
    }

    public void setStatus(@Nonnull final Status status) {
        this.status = status;
    }

    @Nonnull
    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(@Nonnull final User createdBy) {
        this.createdBy = createdBy;
    }

    @Nonnull
    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(@Nonnull final Date createdDate) {
        this.createdDate = createdDate;
    }

    @Nullable
    public User getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(@Nullable final User modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    @Nullable
    public Date getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(@Nullable final Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    @Nonnull
    public Integer getFailedLoginAttempts() {
        return failedLoginAttempts;
    }

    public void setFailedLoginAttempts(@Nonnull final Integer failedLoginAttempts) {
        this.failedLoginAttempts = failedLoginAttempts;
    }

    @Nullable
    public Date getLastFailedLoginDate() {
        return lastFailedLoginDate;
    }

    public void setLastFailedLoginDate(@Nullable final Date lastFailedLoginDate) {
        this.lastFailedLoginDate = lastFailedLoginDate;
    }

    @Nonnull
    public List<UserRole> getRoles() {
        return roles;
    }

    public void setRoles(@Nonnull final List<UserRole> roles) {
        this.roles = roles;
    }

    @SuppressWarnings("ConstantConditions")
    public void updateFromUser(final User user) {
        if (user.firstName != null) {
            firstName = user.firstName;
        }
        if (user.lastName != null) {
            lastName = user.lastName;
        }
        if (user.emailAddress != null) {
            emailAddress = user.emailAddress;
        }
        if (user.password != null) {
            password = user.password;
        }
        if (user.nickname != null) {
            nickname = user.nickname;
        }
        if (user.status != null) {
            status = user.status;
        }
        if (user.roles != null && !user.roles.isEmpty()) {
            roles = user.roles;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final User that = (User) o;

        return Objects.equal(id, that.id);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .addValue(id)
                .addValue(firstName)
                .addValue(lastName)
                .addValue(emailAddress)
//              Do not EVER print the password anywhere //Joakim
//                .addValue(password)
                .addValue(nickname)
                .addValue(status)
                .addValue(createdBy != null ? createdBy.getId() : null)
                .addValue(createdDate)
                .addValue(modifiedBy != null ? modifiedBy.getId() : null)
                .addValue(modifiedDate)
                .addValue(failedLoginAttempts)
                .addValue(lastFailedLoginDate)
                .addValue(roles)
                .toString();
    }
}
