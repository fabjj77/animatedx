package com.cs.devcode;

import com.google.common.base.Joiner;
import com.google.common.base.Objects;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import static javax.xml.bind.annotation.XmlAccessType.FIELD;

/**
 * @author Hadi Movaghar
 */
@XmlRootElement
@XmlAccessorType(FIELD)
public class Attributes {

    @XmlElement
    @Nullable
    private String bonusId;

    @XmlElement
    @Nullable
    private String channelId;

    public Attributes() {
    }

    @Nullable
    public String getBonusId() {
        return bonusId;
    }

    @Nullable
    public String getAllAttributes() {
        return Joiner.on(";").skipNulls().join(bonusId, channelId);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("bonusId", bonusId)
                .add("channelId", channelId)
                .toString();
    }
}
