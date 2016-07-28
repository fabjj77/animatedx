package com.cs.avatar;

/**
 * @author Omid Alaepour
 */
public enum SkinColor {
    IGNORE((short) 0), CAUCASIAN((short) 1), DARK((short) 2), ASIAN((short) 3);

    private final short color;

    private SkinColor(final short color) {
        this.color = color;
    }

    public short getColor() {
        return color;
    }

    public static SkinColor getFromColor(final Short color) {
        if (color == null) {
            throw new IllegalArgumentException("null color");
        }

        for (final SkinColor skinColor : SkinColor.values()) {
            if (color.equals(skinColor.getColor())) {
                return skinColor;
            }
        }

        throw new IllegalArgumentException("No skin color with color " + color);
    }
}
