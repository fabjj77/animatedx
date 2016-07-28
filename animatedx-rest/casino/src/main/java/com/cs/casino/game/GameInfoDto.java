package com.cs.casino.game;

import com.cs.game.GameClient;
import com.cs.game.GameInfo;

import org.hibernate.validator.constraints.NotEmpty;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import static javax.xml.bind.annotation.XmlAccessType.FIELD;

/**
 * @author Omid Alaepour
 */
@XmlRootElement
@XmlAccessorType(FIELD)
public class GameInfoDto {
    @XmlElement
    @Nonnull
    private Long playerId;

    @XmlElement
    @Nonnull
    @NotEmpty(message = "gameDto.gameId.notEmpty")
    private String gameId;

    @XmlElement
    @Nonnull
    private Boolean playForFun;

    @XmlElement
    @Nullable
    private String sessionId;

    @XmlElement
    @Nullable
    private String width;

    @XmlElement
    @Nullable
    private String height;

    @XmlElement
    @Nullable
    private String helpFile;

    @XmlElement
    @Nullable
    private String staticUrl;

    @XmlElement
    @Nullable
    private String language;

    @XmlElement
    @Nullable
    private String flashVersion;

    @XmlElement
    @Nullable
    private String flashVars;

    @XmlElement
    @Nonnull
    private String fullName;

    @XmlElement
    @Nonnull
    private String operatorId;

    @SuppressWarnings("UnusedDeclaration")
    public GameInfoDto() {
    }

    public GameInfoDto(@Nonnull final GameInfo gameInfo, @SuppressWarnings("NullableProblems") final String sessionId, @Nonnull final String operatorId) {
        gameId = gameInfo.getGame().getGameId();
        width = gameInfo.getWidth();
        height = gameInfo.getHeight();
        helpFile = gameInfo.getHelpFile();
        staticUrl = gameInfo.getClient().equals(GameClient.FLASH.getClient()) ? gameInfo.getStaticUrl() : gameInfo.getMobileGameUrl();
        language = gameInfo.getLanguage().toIso();
        flashVersion = gameInfo.getFlashVersion();
        flashVars = gameInfo.getVars();
        fullName = gameInfo.getGame().getFullName();
        this.sessionId = sessionId;
        this.operatorId = operatorId;
    }

    @Nonnull
    public String getGameId() {
        return gameId;
    }
}
