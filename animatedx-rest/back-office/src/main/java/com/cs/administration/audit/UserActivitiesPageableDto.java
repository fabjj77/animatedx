package com.cs.administration.audit;

import com.cs.audit.UserActivity;

import org.springframework.data.domain.Page;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

import static javax.xml.bind.annotation.XmlAccessType.FIELD;

/**
 * @author Joakim Gottzén
 */
@XmlRootElement
@XmlAccessorType(FIELD)
public class UserActivitiesPageableDto {
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    @XmlElement
    @Nullable
    private List<UserActivityDto> activities;

    @XmlElement
    @Nullable
    private Long count;

    @SuppressWarnings("UnusedDeclaration")
    public UserActivitiesPageableDto() {
    }

    public UserActivitiesPageableDto(final Page<UserActivity> userActivities) {
        activities = new ArrayList<>(userActivities.getSize());
        for (final UserActivity userActivity : userActivities) {
            activities.add(new UserActivityDto(userActivity));
        }
        count = userActivities.getTotalElements();
    }
}
