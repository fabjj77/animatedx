package com.cs.util;

import javax.xml.bind.DatatypeConverter;

/**
 * @author Joakim Gottzén
 */
public final class Base64 {
    private Base64() {
    }

    public static final class Encoder {
        private Encoder() {}

        public static String encodeToString(final byte[] source) {
            return DatatypeConverter.printBase64Binary(source);
        }
    }
}
