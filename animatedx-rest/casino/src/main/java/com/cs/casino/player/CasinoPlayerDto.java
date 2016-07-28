package com.cs.casino.player;

import com.cs.player.BasicPlayerDto;
import com.cs.player.Player;

import javax.annotation.Nonnull;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import static javax.xml.bind.annotation.XmlAccessType.FIELD;

/**
 * @author Joakim Gottzén
 */
@XmlRootElement
@XmlAccessorType(FIELD)
public class CasinoPlayerDto extends BasicPlayerDto {

    @XmlElement
    @Nonnull
    @Valid
    private CasinoAddressDto address;

    @XmlElement
    @Nonnull
    private CasinoWalletDto wallet;

    @XmlElement
    @Nonnull
    @NotNull(message = "CasinoPlayerDto.hasAcceptedLatestTermsAndConditions.notNull")
    private boolean hasAcceptedLatestTerms;

    @SuppressWarnings("UnusedDeclaration")
    public CasinoPlayerDto() {
    }

    public CasinoPlayerDto(final Player player) {
        super(player);
        address = new CasinoAddressDto(player.getAddress());
        wallet = new CasinoWalletDto(player.getWallet());
    }

    public CasinoPlayerDto(final Player player, final boolean hasAcceptedLatestTerms) {
        super(player);
        address = new CasinoAddressDto(player.getAddress());
        wallet = new CasinoWalletDto(player.getWallet());
        this.hasAcceptedLatestTerms = hasAcceptedLatestTerms;
    }

    @Override
    public Player asPlayer() {
        final Player player = super.asPlayer();
        player.setAddress(address.asAddress().trim());
        return player;
    }
}
