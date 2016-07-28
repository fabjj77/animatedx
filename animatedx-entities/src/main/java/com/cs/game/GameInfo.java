package com.cs.game;

import com.cs.persistence.Language;

import com.google.common.base.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;

import static javax.persistence.EnumType.STRING;
import static javax.persistence.GenerationType.IDENTITY;

/**
 * @author Omid Alaepour
 */
@Entity
@Table(name = "netent_game_infos")
public class GameInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    @Nonnull
    private Long id;

    @ManyToOne
    @JoinColumn(name = "game_id", nullable = false)
    @Nonnull
    private Game game;

    @Column(name = "language", nullable = false)
    @Nonnull
    @Enumerated(STRING)
    private Language language;

    @Column(name = "width", nullable = false)
    @Nonnull
    private String width;

    @Column(name = "height", nullable = false)
    @Nonnull
    private String height;

    @Column(name = "help_file", nullable = false)
    @Nonnull
    private String helpFile;

    @Column(name = "client", nullable = false)
    @Nonnull
    private String client;

    @Column(name = "static_url", nullable = false)
    @Nullable
    private String staticUrl;

    @Column(name = "game_server_url", nullable = false)
    @Nullable
    private String gameServerUrl;

    @Column(name = "mobile_game_url")
    @Nullable
    private String mobileGameUrl;

    @Column(name = "base")
    @Nullable
    private String base;

    @Column(name = "vars")
    @Nullable
    private String vars;

    @Column(name = "allow_script_access")
    @Nullable
    private String allowScriptAccess;

    @Column(name = "flash_version")
    @Nullable
    private String flashVersion;

    @Column(name = "window_mode")
    @Nullable
    private String windowMode;

    public GameInfo() {
    }

    @Nonnull
    public Long getId() {
        return id;
    }

    public void setId(@Nonnull final Long id) {
        this.id = id;
    }

    @Nonnull
    public Game getGame() {
        return game;
    }

    public void setGame(@Nonnull final Game game) {
        this.game = game;
    }

    @Nonnull
    public Language getLanguage() {
        return language;
    }

    public void setLanguage(@Nonnull final Language language) {
        this.language = language;
    }

    @Nonnull
    public String getWidth() {
        return width;
    }

    public void setWidth(@Nonnull final String width) {
        this.width = width;
    }

    @Nonnull
    public String getHeight() {
        return height;
    }

    public void setHeight(@Nonnull final String height) {
        this.height = height;
    }

    @Nonnull
    public String getHelpFile() {
        return helpFile;
    }

    public void setHelpFile(@Nonnull final String helpFile) {
        this.helpFile = helpFile;
    }

    @Nonnull
    public String getClient() {
        return client;
    }

    public void setClient(@Nonnull final String client) {
        this.client = client;
    }

    @Nullable
    public String getStaticUrl() {
        return staticUrl;
    }

    public void setStaticUrl(@Nullable final String staticUrl) {
        this.staticUrl = staticUrl;
    }

    @Nullable
    public String getGameServerUrl() {
        return gameServerUrl;
    }

    public void setGameServerUrl(@Nullable final String gameServerUrl) {
        this.gameServerUrl = gameServerUrl;
    }

    @Nullable
    public String getMobileGameUrl() {
        return mobileGameUrl;
    }

    public void setMobileGameUrl(@Nullable final String mobileGameUrl) {
        this.mobileGameUrl = mobileGameUrl;
    }

    @Nullable
    public String getBase() {
        return base;
    }

    public void setBase(@Nullable final String base) {
        this.base = base;
    }

    @Nullable
    public String getVars() {
        return vars;
    }

    public void setVars(@Nullable final String vars) {
        this.vars = vars;
    }

    @Nullable
    public String getAllowScriptAccess() {
        return allowScriptAccess;
    }

    public void setAllowScriptAccess(@Nullable final String allowScriptAccess) {
        this.allowScriptAccess = allowScriptAccess;
    }

    @Nullable
    public String getFlashVersion() {
        return flashVersion;
    }

    public void setFlashVersion(@Nullable final String flashVersion) {
        this.flashVersion = flashVersion;
    }

    @Nullable
    public String getWindowMode() {
        return windowMode;
    }

    public void setWindowMode(@Nullable final String windowMode) {
        this.windowMode = windowMode;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final GameInfo that = (GameInfo) o;

        return Objects.equal(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("id", id)
                .add("game", game.getGameId())
                .add("language", language)
                .add("width", width)
                .add("height", height)
                .add("helpFile", helpFile)
                .add("client", client)
                .add("staticUrl", staticUrl)
                .add("gameServerUrl", gameServerUrl)
                .add("mobileGameUrl", mobileGameUrl)
                .add("base", base)
                .add("vars", vars)
                .add("allowScriptAccess", allowScriptAccess)
                .add("flashVersion", flashVersion)
                .add("windowMode", windowMode)
                .toString();
    }
}
