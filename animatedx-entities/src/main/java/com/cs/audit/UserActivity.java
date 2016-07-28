package com.cs.audit;

import com.cs.user.User;

import com.google.common.base.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

import static javax.persistence.EnumType.STRING;
import static javax.persistence.GenerationType.IDENTITY;
import static javax.persistence.TemporalType.TIMESTAMP;

/**
 * @author Hadi Movaghar
 */
@Entity
@Table(name = "user_activities")
public class UserActivity implements Serializable {
    private static final long serialVersionUID = 3L;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    @Nonnull
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    @Nonnull
    @Valid
    private User user;

    @Column(name = "activity_type", nullable = false)
    @Enumerated(STRING)
    @Nonnull
    @NotNull(message = "userActivity.userActivityType.notNull")
    private UserActivityType userActivityType;

    @Column(name = "activity_date", nullable = false)
    @Temporal(TIMESTAMP)
    @Nonnull
    @NotNull(message = "userActivity.activityDate..notNull")
    private Date activityDate;

    @Column(name = "description")
    @Nullable
    private String description;

    public UserActivity() {
    }

    public UserActivity(@Nonnull final User user, @Nonnull final UserActivityType userActivityType, @Nonnull final Date activityDate) {
        this.user = user;
        this.userActivityType = userActivityType;
        this.activityDate = activityDate;
    }

    public UserActivity(@Nonnull final User user, @Nonnull final UserActivityType userActivityType, @Nonnull final Date activityDate, @Nullable final String description) {
        this.user = user;
        this.userActivityType = userActivityType;
        this.activityDate = activityDate;
        this.description = description;
    }

    @Nonnull
    public Long getId() {
        return id;
    }

    public void setId(@Nonnull final Long id) {
        this.id = id;
    }

    @Nonnull
    public User getUser() {
        return user;
    }

    public void setUser(@Nonnull final User user) {
        this.user = user;
    }

    @Nonnull
    public UserActivityType getUserActivityType() {
        return userActivityType;
    }

    public void setUserActivityType(@Nonnull final UserActivityType userActivityType) {
        this.userActivityType = userActivityType;
    }

    @Nonnull
    public Date getActivityDate() {
        return activityDate;
    }

    public void setActivityDate(@Nonnull final Date activityDate) {
        this.activityDate = activityDate;
    }

    @Nullable
    public String getDescription() {
        return description;
    }

    public void setDescription(@Nullable final String description) {
        this.description = description;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final UserActivity that = (UserActivity) o;

        return Objects.equal(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .addValue(id)
                .addValue(user)
                .addValue(userActivityType)
                .addValue(activityDate)
                .addValue(description)
                .toString();
    }
}
