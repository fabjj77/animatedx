package com.cs.player;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import static javax.xml.bind.annotation.XmlAccessType.FIELD;

/**
 * @author Hadi Movaghar
 */
@XmlRootElement
@XmlAccessorType(FIELD)
public class PlayerRegisterTrackDto {
    @XmlElement(nillable = true, required = true)
    @Nullable
    private Long id;

    @XmlElement
    @Nullable
    private String campaign;

    @XmlElement
    @Nullable
    private String source;

    @XmlElement
    @Nullable
    private String medium;

    @XmlElement
    @Nullable
    private String content;

    @XmlElement
    @Nullable
    private String version;

    @SuppressWarnings("UnusedDeclaration")
    public PlayerRegisterTrackDto() {
    }

    public PlayerRegisterTrack asPlayerRegisterTrack() {
        final PlayerRegisterTrack playerRegisterTrack = new PlayerRegisterTrack();
        playerRegisterTrack.setCampaign(campaign);
        playerRegisterTrack.setSource(source);
        playerRegisterTrack.setMedium(medium);
        playerRegisterTrack.setContent(content);
        playerRegisterTrack.setVersion(version);
        return playerRegisterTrack;
    }
}
