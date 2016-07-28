package com.cs.administration.game;

import com.cs.game.GameInfo;
import com.cs.persistence.Language;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import static javax.xml.bind.annotation.XmlAccessType.FIELD;

/**
 * @author Joakim Gottzén
 */
@XmlRootElement
@XmlAccessorType(FIELD)
public class GameInfoDto {
    @XmlElement
    @Nullable
    private Long id;

    @XmlElement
    @Nullable
    private Language language;

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
    private String client;

    @XmlElement
    @Nullable
    private String staticUrl;

    @XmlElement
    @Nullable
    private String gameServerUrl;

    @XmlElement
    @Nullable
    private String mobileGameUrl;

    @XmlElement
    @Nullable
    private String base;

    @XmlElement
    @Nullable
    private String vars;

    @XmlElement
    @Nullable
    private String allowScriptAccess;

    @XmlElement
    @Nullable
    private String flashVersion;

    @XmlElement
    @Nullable
    private String windowMode;

    @SuppressWarnings("UnusedDeclaration")
    public GameInfoDto() {
    }

    public GameInfoDto(final GameInfo gameInfo) {
        id = gameInfo.getId();
        language = gameInfo.getLanguage();
        width = gameInfo.getWidth();
        height = gameInfo.getHeight();
        helpFile = gameInfo.getHelpFile();
        client = gameInfo.getClient();
        staticUrl = gameInfo.getStaticUrl();
        gameServerUrl = gameInfo.getGameServerUrl();
        mobileGameUrl = gameInfo.getMobileGameUrl();
        base = gameInfo.getBase();
        vars = gameInfo.getVars();
        allowScriptAccess = gameInfo.getAllowScriptAccess();
        flashVersion = gameInfo.getFlashVersion();
        windowMode = gameInfo.getWindowMode();
    }
}
