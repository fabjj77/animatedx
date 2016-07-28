package com.cs.administration.game;

import com.cs.game.Game;
import com.cs.game.GameCategory;
import com.cs.persistence.Status;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * @author Omid Alaepour
 */
public class GameUpdateDto {
    @XmlTransient
    private String gameId;

    @XmlElement
    private String fullName;

    @XmlElement
    private String name;

    @XmlElement
    private Status status;

    @XmlElement
    private GameCategory category;

    @XmlElement
    private Boolean featured;

    @XmlElement
    private String slug;

    public Game asGame() {
        final Game game = new Game();
        game.setGameId(gameId);
        game.setFullName(fullName);
        game.setName(name);
        game.setStatus(status);
        game.setCategory(category);
        game.setFeatured(featured);
        game.setSlug(slug);
        return game;
    }

    public void setGameId(final String gameId) {
        this.gameId = gameId;
    }
}
