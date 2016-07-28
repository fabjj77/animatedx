package com.cs.util;

import com.google.common.base.Objects;

/**
 * @author Joakim Gottzén
 */
public class Pair<LEFT, RIGHT> {
    private final LEFT left;
    private final RIGHT right;

    public Pair(final LEFT left, final RIGHT right) {
        this.left = left;
        this.right = right;
    }

    public LEFT getLeft() {
        return left;
    }

    public RIGHT getRight() {
        return right;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final Pair that = (Pair) o;

        return Objects.equal(left, that.left) &&
               Objects.equal(right, that.right);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(left, right);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("left", left)
                .add("right", right)
                .toString();
    }
}
