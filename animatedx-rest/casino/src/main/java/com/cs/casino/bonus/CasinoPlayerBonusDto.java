package com.cs.casino.bonus;

import com.cs.bonus.BasicPlayerBonusDto;
import com.cs.bonus.PlayerBonus;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

import static javax.xml.bind.annotation.XmlAccessType.FIELD;

/**
 * @author Joakim Gottzén
 */
@XmlRootElement
@XmlAccessorType(FIELD)
public class CasinoPlayerBonusDto extends BasicPlayerBonusDto {

    @SuppressWarnings("UnusedDeclaration")
    public CasinoPlayerBonusDto() {
    }

    public CasinoPlayerBonusDto(final PlayerBonus playerBonus) {
        super(playerBonus);
    }

    public static List<CasinoPlayerBonusDto> of(final List<PlayerBonus> playerBonuses) {
        final List<CasinoPlayerBonusDto> bonuses = new ArrayList<>();
        for (final PlayerBonus playerBonus : playerBonuses) {
            bonuses.add(new CasinoPlayerBonusDto(playerBonus));
        }
        return bonuses;
    }
}
