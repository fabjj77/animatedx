package com.cs.administration.bonus;

import com.cs.bonus.PlayerBonus;

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
public class PlayerBonusPageableDto {
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    @XmlElement
    @Nonnull
    private List<BackOfficePlayerBonusDto> playerBonuses;

    @XmlElement
    @Nonnull
    private Long count;

    @SuppressWarnings("UnusedDeclaration")
    public PlayerBonusPageableDto() {
    }

    public PlayerBonusPageableDto(final Page<PlayerBonus> playerBonuses) {
        this.playerBonuses = new ArrayList<>(playerBonuses.getSize());
        for (final PlayerBonus playerBonus : playerBonuses) {
            this.playerBonuses.add(new BackOfficePlayerBonusDto(playerBonus));
        }
        count = playerBonuses.getTotalElements();
    }
}
