package com.cs.game;

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
public class GameDto {
    @XmlElement(nillable = true, required = true)
    @Nullable
    private String gameId;

    @XmlElement
    @Nonnull
    private String fullName;

    @XmlElement
    @Nonnull
    private String name;

    @XmlElement
    @Nonnull
    private GameCategory category;

    @XmlElement
    @Nonnull
    private Boolean featured;

    @XmlElement(nillable = true, required = true)
    @Nonnull
    private String slug;

    @SuppressWarnings("UnusedDeclaration")
    public GameDto() {
    }

    public GameDto(final Game game) {
        gameId = game.getGameId();
        fullName = game.getFullName();
        name = game.getName();
        category = game.getCategory();
        featured = game.getFeatured();
        slug = game.getSlug();
    }

    public GameDto(final GameInfo gameInfo) {
        gameId = gameInfo.getGame().getGameId();
        fullName = gameInfo.getGame().getFullName();
        name = gameInfo.getGame().getName();
        category = gameInfo.getGame().getCategory();
        featured = gameInfo.getGame().getFeatured();
        slug = gameInfo.getGame().getSlug();
    }
}
