package com.cs.administration.player;

import com.cs.player.Player;

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
public class PlayerPageableDto {
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    @XmlElement
    @Nonnull
    private List<BackOfficePlayerDto> players;

    @XmlElement
    @Nonnull
    private Long count;

    @SuppressWarnings("UnusedDeclaration")
    public PlayerPageableDto() {
    }

    public PlayerPageableDto(final Page<Player> players) {
        this.players = new ArrayList<>(players.getSize());
        for (final Player player : players) {
            this.players.add(new BackOfficePlayerDto(player));
        }
        count = players.getTotalElements();
    }
}
