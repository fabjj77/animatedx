package com.cs.player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

/**
 * @author Omid Alaepour.
 */
@XmlRootElement
@XmlEnum
public enum BlockType {
    UNBLOCKED, DEFINITE_SELF_EXCLUSION, INDEFINITE_SELF_EXCLUSION, SESSION_LENGTH, BET_LIMIT, LOSS_LIMIT;

    public boolean isLimited(@Nonnull final Date limitedBlockEndDate) {
        return (this == BET_LIMIT || this == LOSS_LIMIT) && limitedBlockEndDate.after(new Date());
    }

    public boolean isBlocked(@Nullable final Date blockEndDate) {
        if (this == INDEFINITE_SELF_EXCLUSION) {
            return true;
        }

        if (this == SESSION_LENGTH || this == DEFINITE_SELF_EXCLUSION) {
            if (blockEndDate == null || blockEndDate.after(new Date())) {
                return true;
            }
        }

        return false;
    }
}
