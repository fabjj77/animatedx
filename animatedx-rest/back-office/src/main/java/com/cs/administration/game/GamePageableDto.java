package com.cs.administration.game;

import com.cs.game.Game;

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
public class GamePageableDto {
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    @XmlElement
    @Nullable
    private List<BackOfficeGameDto> games;

    @XmlElement
    @Nullable
    private Long count;

    @SuppressWarnings("UnusedDeclaration")
    public GamePageableDto() {
    }

    public GamePageableDto(final Page<Game> games) {
        this.games = new ArrayList<>(games.getSize());
        for (final Game gameInfo : games) {
            this.games.add(new BackOfficeGameDto(gameInfo));
        }
        count = games.getTotalElements();
    }
}
