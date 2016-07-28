package com.cs.administration.player;

import com.cs.persistence.Status;
import com.cs.player.BasicUpdatePlayerDto;
import com.cs.player.Player;
import com.cs.player.TrustLevel;
import com.cs.player.Verification;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

import static javax.xml.bind.annotation.XmlAccessType.FIELD;

/**
 * @author Omid Alaepour
 */
@XmlRootElement
@XmlAccessorType(FIELD)
public class BackOfficeUpdatePlayerDto extends BasicUpdatePlayerDto {

    @XmlElement(nillable = true)
    @Nullable
    private BackOfficeAddressDto address;

    @XmlElement
    @Nullable
    private Date birthday;

    @XmlElement
    @Nullable
    private Status status;

    @XmlElement
    @Nullable
    private Verification playerVerification;

    @XmlElement
    @Nullable
    private Verification emailVerification;

    @XmlElement
    @Nullable
    private TrustLevel trustLevel;

    @XmlElement
    @Nullable
    private Boolean testAccount;

    @SuppressWarnings("ConstantConditions")
    @Override
    public Player asPlayer() {
        final Player player = super.asPlayer();
        if (address != null) {
            player.setAddress(address.asAddress());
        }
        if (birthday != null) {
            player.setBirthday(birthday);
        }
        if (playerVerification != null) {
            player.setPlayerVerification(playerVerification);
        }
        if (emailVerification != null) {
            player.setEmailVerification(emailVerification);
        }
        if (status != null) {
            player.setStatus(status);
        }
        if (trustLevel != null) {
            player.setTrustLevel(trustLevel);
        }
        if (testAccount != null) {
            player.setTestAccount(testAccount);
        }
        return player;
    }
}
