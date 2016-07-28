package com.cs.game;

import org.springframework.data.domain.Page;

import javax.annotation.Nonnull;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

import static javax.xml.bind.annotation.XmlAccessType.FIELD;

/**
 * @author Omid Alaepour
 */
@XmlRootElement
@XmlAccessorType(FIELD)
public class GameSessionPageableDto {
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    @XmlElement
    @Nonnull
    private List<GameSessionDto> gameSessions;

    @XmlElement
    @Nonnull
    private Long count;

    @SuppressWarnings("UnusedDeclaration")
    public GameSessionPageableDto() {
    }

    public GameSessionPageableDto(@Nonnull final Page<GameTransaction> netEntTransactions) {
        gameSessions = new ArrayList<>(netEntTransactions.getSize());
        for (final GameTransaction gameTransaction : netEntTransactions) {
            gameSessions.add(new GameSessionDto(gameTransaction));
        }
        count = netEntTransactions.getTotalElements();
    }
}
