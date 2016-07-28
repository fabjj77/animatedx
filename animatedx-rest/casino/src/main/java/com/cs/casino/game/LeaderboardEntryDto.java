package com.cs.casino.game;

import com.cs.game.LeaderboardEntry;

import javax.annotation.Nonnull;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;

import static javax.xml.bind.annotation.XmlAccessType.FIELD;

/**
 * @author Jose Aleman
 */
@XmlRootElement
@XmlAccessorType(FIELD)
public class LeaderboardEntryDto {

    @XmlElement
    @Nonnull
    private String avatarId;

    @XmlElement
    @Nonnull
    private String avatarBaseTypeId;

    @XmlElement
    @Nonnull
    private String level;

    @XmlElement
    @Nonnull
    private String pictureUrl;

    @XmlElement
    @Nonnull
    private String username;

    @XmlElement
    @Nonnull
    private GameNameDto game;

    @XmlElement
    @Nonnull
    private BigDecimal biggestWin;

    @XmlElement
    @Nonnull
    private Integer freeSpins;

    @SuppressWarnings("UnusedDeclaration")
    public LeaderboardEntryDto() {
    }

    public LeaderboardEntryDto(final LeaderboardEntry leaderboardEntry) {
        avatarId = leaderboardEntry.getAvatarId();
        avatarBaseTypeId = leaderboardEntry.getAvatarBaseTypeId();
        level =  leaderboardEntry.getLevel();
        pictureUrl = leaderboardEntry.getPictureUrl();
        username = leaderboardEntry.getUsername();
        game = new GameNameDto(leaderboardEntry.getGameSlug(), leaderboardEntry.getGameName());
        biggestWin = leaderboardEntry.getBiggestWin().getEuroValueInBigDecimal();
        freeSpins = leaderboardEntry.getFreeSpins();
    }

    @XmlRootElement
    @XmlAccessorType(FIELD)
    private static final class GameNameDto {

        @XmlElement
        @Nonnull
        private final String name;

        @XmlElement
        @Nonnull
        private final String slug;


        private GameNameDto(@Nonnull final String slug, @Nonnull final String name) {
            this.name = name;
            this.slug = slug;
        }
    }
}
