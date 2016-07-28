package com.cs.casino.player;

import com.google.common.base.Objects;
import org.hibernate.validator.constraints.NotEmpty;

import javax.annotation.Nonnull;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import static javax.xml.bind.annotation.XmlAccessType.FIELD;

/**
 * @author Omid Alaepour.
 */
@XmlRootElement
@XmlAccessorType(FIELD)
public class NicknameDto {
    @XmlElement(nillable = true, required = true)
    @Nonnull
    @NotEmpty(message = "nicknameDto.nickname.notEmpty")
    private String nickname;

    @Nonnull
    public String getNickname() {
        return nickname;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final NicknameDto that = (NicknameDto) o;

        return Objects.equal(nickname, that.nickname);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(nickname);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .addValue(nickname)
                .toString();
    }
}
