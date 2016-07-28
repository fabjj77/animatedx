package com.cs.casino.game;

import com.cs.game.LeaderboardEntry;

import javax.annotation.Nonnull;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

import static javax.xml.bind.annotation.XmlAccessType.FIELD;

/**
 * @author Jose Aleman
 */
@XmlRootElement
@XmlAccessorType(FIELD)
public class LeaderboardDto {

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    @XmlElement
    @Nonnull
    private List<LeaderboardEntryDto> entries;

    @SuppressWarnings("UnusedDeclaration")
    public LeaderboardDto() {
    }

    public LeaderboardDto(final List<LeaderboardEntry> leaderboardEntryList) {
        entries = new ArrayList<>();

        for (final LeaderboardEntry leaderboardEntry : leaderboardEntryList) {
            entries.add(new LeaderboardEntryDto(leaderboardEntry));
        }
    }
}
