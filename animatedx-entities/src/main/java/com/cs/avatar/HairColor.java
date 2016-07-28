package com.cs.avatar;

/**
 * @author Omid Alaepour
 */
public enum HairColor {
    IGNORE((short) 0), BLOND((short) 1), BROWN((short) 2), DARK((short) 3);

    private final short color;

    private HairColor(final short color) {
        this.color = color;
    }

    public short getColor() {
        return color;
    }

    public static HairColor getFromColor(final Short color) {
        if (color == null) {
            throw new IllegalArgumentException("null color");
        }

        for (final HairColor hairColor : values()) {
            if (color.equals(hairColor.getColor())) {
                return hairColor;
            }
        }

        throw new IllegalArgumentException("No hair color with color " + color);
    }
}
