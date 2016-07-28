package com.cs.administration.audit;

import com.cs.audit.UserActivity;
import com.cs.audit.UserActivityType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

import static javax.xml.bind.annotation.XmlAccessType.FIELD;

/**
 * @author Hadi Movaghar
 */
@XmlRootElement
@XmlAccessorType(FIELD)
public class UserActivityDto {
    @XmlElement(nillable = true, required = true)
    @Nonnull
    private Long id;

    @XmlElement
    @Nonnull
    private Long userId;

    @XmlElement
    @Nonnull
    private UserActivityType activityType;

    @XmlElement
    @Nonnull
    private Date activityDate;

    @XmlElement
    @Nullable
    private String description;

    @SuppressWarnings("UnusedDeclaration")
    public UserActivityDto() {
    }

    public UserActivityDto(final UserActivity userActivity) {
        id = userActivity.getId();
        userId = userActivity.getUser().getId();
        activityType = userActivity.getUserActivityType();
        activityDate = userActivity.getActivityDate();
        description = userActivity.getDescription();
    }
}
