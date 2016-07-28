package com.cs.casino.player;

import com.cs.player.PlayerUuid;
import com.cs.validation.ValidationProperties;

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
 * @author Omid Alaepour.
 */
@XmlRootElement
@XmlAccessorType(FIELD)
public class PlayerUuidDto {
    @XmlElement(nillable = true, required = true)
    @Nullable
    @Pattern(regexp = ValidationProperties.UUID_PATTERN, message = "playerUuidDto.uuid.notValid")
    private String uuid;

    @XmlElement
    @Nonnull
    @NotNull
    private Date createdDate;

    @SuppressWarnings("UnusedDeclaration")
    public PlayerUuidDto() {
    }

    public PlayerUuidDto(final PlayerUuid playerUuid) {
        uuid = playerUuid.getUuid().toString();
        createdDate = playerUuid.getCreatedDate();
    }
}
