package com.cs.game;

import com.cs.payment.Money;

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
public class LeaderboardEntry {

    @XmlElement
    @Nonnull
    private String gameId;

    @XmlElement
    @Nonnull
    private String gameRoundRef;

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
    private String gameSlug;

    @XmlElement
    @Nonnull
    private String gameName;

    @XmlElement
    @Nonnull
    private Money biggestWin;

    @XmlElement
    @Nonnull
    private Integer freeSpins;

    @SuppressWarnings("UnusedDeclaration")
    public LeaderboardEntry() {
    }

    public LeaderboardEntry(final Object... playerDetail) {
        gameId = playerDetail[0].toString();
        gameRoundRef = playerDetail[1].toString();
        avatarId = playerDetail[2].toString();
        avatarBaseTypeId = playerDetail[3].toString();
        level =  playerDetail[4].toString();
        pictureUrl = playerDetail[5].toString();
        username = playerDetail[6].toString();
        gameSlug = String.valueOf(playerDetail[7]);
        gameName = String.valueOf(playerDetail[8]);
        biggestWin = new Money(((BigDecimal)playerDetail[9]).longValue());
        freeSpins = playerDetail[10] == null ? 0 : (Integer) playerDetail[10];
    }

    @Nonnull
    public String getGameId() {
        return gameId;
    }

    @Nonnull
    public String getAvatarId() {
        return avatarId;
    }

    @Nonnull
    public String getAvatarBaseTypeId() {
        return avatarBaseTypeId;
    }

    @Nonnull
    public String getLevel() {
        return level;
    }

    @Nonnull
    public String getPictureUrl() {
        return pictureUrl;
    }

    @Nonnull
    public String getUsername() {
        return username;
    }

    @Nonnull
    public String getGameSlug() {
        return gameSlug;
    }

    @Nonnull
    public String getGameName() {
        return gameName;
    }

    @Nonnull
    public Money getBiggestWin() {
        return biggestWin;
    }

    @Nonnull
    public Integer getFreeSpins() {
        return freeSpins;
    }
}
