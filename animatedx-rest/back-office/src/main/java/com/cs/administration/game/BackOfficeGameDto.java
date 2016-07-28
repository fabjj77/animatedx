package com.cs.administration.game;

import com.cs.game.Game;
import com.cs.game.GameDto;
import com.cs.game.GameInfo;
import com.cs.persistence.Status;

import javax.annotation.Nonnull;
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
public class BackOfficeGameDto extends GameDto {
    @XmlElement
    @Nonnull
    private Status status;

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    @XmlElement
    @Nonnull
    private List<GameInfoDto> gameInfos;

    @SuppressWarnings("UnusedDeclaration")
    public BackOfficeGameDto() {
    }

    public BackOfficeGameDto(final Game game) {
        super(game);
        status = game.getStatus();
        gameInfos = new ArrayList<>();
        for (final GameInfo gameInfo : game.getGameInfos()) {
            gameInfos.add(new GameInfoDto(gameInfo));
        }
    }
}
