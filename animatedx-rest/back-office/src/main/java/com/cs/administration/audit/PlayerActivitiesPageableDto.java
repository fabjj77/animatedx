package com.cs.administration.audit;

import com.cs.audit.PlayerActivity;

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
public class PlayerActivitiesPageableDto {
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    @XmlElement
    @Nullable
    private List<PlayerActivityDto> activities;

    @XmlElement
    @Nullable
    private Long count;

    @SuppressWarnings("UnusedDeclaration")
    public PlayerActivitiesPageableDto() {
    }

    public PlayerActivitiesPageableDto(final Page<PlayerActivity> playerActivities) {
        activities = new ArrayList<>(playerActivities.getSize());
        for (final PlayerActivity playerActivity : playerActivities) {
            activities.add(new PlayerActivityDto(playerActivity));
        }
        count = playerActivities.getTotalElements();
    }
}
