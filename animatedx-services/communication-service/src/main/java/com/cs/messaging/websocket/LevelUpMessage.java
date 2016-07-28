package com.cs.messaging.websocket;

import com.google.common.base.Objects;

import java.io.Serializable;

/**
 * @author Joakim Gottzén
 */
public class LevelUpMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    private final Long newLevel;
    private final Long oldLevel;

    public LevelUpMessage(final Long newLevel, final Long oldLevel) {
        this.newLevel = newLevel;
        this.oldLevel = oldLevel;
    }

    public Long getNewLevel() {
        return newLevel;
    }

    public Long getOldLevel() {
        return oldLevel;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final LevelUpMessage that = (LevelUpMessage) o;

        return Objects.equal(newLevel, that.newLevel) &&
               Objects.equal(oldLevel, that.oldLevel);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(newLevel, oldLevel);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .addValue(newLevel)
                .addValue(oldLevel)
                .toString();
    }
}
