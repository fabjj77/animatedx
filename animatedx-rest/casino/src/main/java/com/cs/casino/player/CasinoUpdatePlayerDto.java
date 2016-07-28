package com.cs.casino.player;

import com.cs.player.BasicUpdatePlayerDto;
import com.cs.player.Player;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import static javax.xml.bind.annotation.XmlAccessType.FIELD;

/**
 * @author Joakim Gottzén
 */
@XmlRootElement
@XmlAccessorType(FIELD)
public class CasinoUpdatePlayerDto extends BasicUpdatePlayerDto {

    @XmlElement(nillable = true)
    @Nullable
    private CasinoAddressDto address;

    @SuppressWarnings("UnusedDeclaration")
    public CasinoUpdatePlayerDto() {
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public Player asPlayer() {
        final Player player = super.asPlayer();
        player.setAddress(address != null ? address.asAddress() : null);
        return player;
    }
}
