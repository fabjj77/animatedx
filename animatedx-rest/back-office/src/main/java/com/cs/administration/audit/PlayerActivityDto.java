package com.cs.administration.audit;

import com.cs.audit.PlayerActivity;
import com.cs.audit.PlayerActivityType;

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
public class PlayerActivityDto {
    @XmlElement(nillable = true, required = true)
    @Nonnull
    private Long id;

    @XmlElement
    @Nonnull
    private Long playerId;

    @XmlElement
    @Nonnull
    private Long level;

    @XmlElement
    @Nonnull
    private PlayerActivityType activityType;

    @XmlElement
    @Nonnull
    private Date activityDate;

    @XmlElement
    @Nullable
    private String sessionId;

    @XmlElement
    @Nullable
    private String ipAddress;

    @XmlElement
    @Nullable
    private String description;

    @SuppressWarnings("UnusedDeclaration")
    public PlayerActivityDto() {
    }

    public PlayerActivityDto(final PlayerActivity playerActivity) {
        id = playerActivity.getId();
        playerId = playerActivity.getPlayer().getId();
        activityType = playerActivity.getActivity();
        activityDate = playerActivity.getActivityDate();
        sessionId = playerActivity.getSessionId();
        ipAddress = playerActivity.getIpAddress();
        description = playerActivity.getDescription();
        level = playerActivity.getLevel();
    }
}
