package com.cs.administration.player;

import com.cs.administration.affiliate.PlayerAffiliateDto;
import com.cs.avatar.AvatarBaseTypeDto;
import com.cs.persistence.Status;
import com.cs.player.BasicPlayerDto;
import com.cs.player.BlockType;
import com.cs.player.Player;
import com.cs.player.TrustLevel;
import com.cs.player.Verification;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.Enumerated;
import javax.validation.Valid;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

import static javax.persistence.EnumType.STRING;
import static javax.xml.bind.annotation.XmlAccessType.FIELD;

/**
 * @author Joakim Gottzén
 */
@XmlRootElement
@XmlAccessorType(FIELD)
public class BackOfficePlayerDto extends BasicPlayerDto {

    @XmlElement
    @Nonnull
    @Valid
    private BackOfficeAddressDto address;

    @XmlElement
    @Nonnull
    private Verification playerVerification;

    @XmlElement
    @Nonnull
    private Verification emailVerification;

    @XmlElement
    @Nonnull
    private BlockType blockType;

    @XmlElement
    @Nonnull
    private Date blockEndDate;

    @XmlElement
    @Nonnull
    private TrustLevel trustLevel;

    @XmlElement
    @Nonnull
    private AvatarBaseTypeDto avatarBaseType;

    @XmlElement
    @Nonnull
    private Date createdDate;

    @XmlElement(nillable = true, required = true)
    @Nullable
    private Date modifiedDate;

    @XmlElement
    @Enumerated(STRING)
    @Nonnull
    private Status status;

    @XmlElement
    @Nonnull
    private BackOfficeWalletDto wallet;

    @XmlElement
    @Nullable
    private PlayerAffiliateDto affiliate;

    @XmlElement
    @Nonnull
    private Boolean testAccount;

    @SuppressWarnings("UnusedDeclaration")
    public BackOfficePlayerDto() {
    }

    public BackOfficePlayerDto(final Player player) {
        super(player);
        address = new BackOfficeAddressDto(player.getAddress());
        playerVerification = player.getPlayerVerification();
        emailVerification = player.getEmailVerification();
        blockType = player.getBlockType();
        blockEndDate = player.getBlockEndDate();
        trustLevel = player.getTrustLevel();
        avatarBaseType = new AvatarBaseTypeDto(player.getAvatar().getAvatarBaseType());
        createdDate = player.getCreatedDate();
        modifiedDate = player.getModifiedDate();
        status = player.getStatus();
        wallet = new BackOfficeWalletDto(player.getWallet());
        if (player.getPlayerAffiliate() != null) {
            affiliate = new PlayerAffiliateDto(player.getPlayerAffiliate());
        }
        testAccount = player.isTestAccount();
    }
}
