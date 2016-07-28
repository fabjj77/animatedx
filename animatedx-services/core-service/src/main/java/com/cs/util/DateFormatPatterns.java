package com.cs.util;

/**
 * @author Joakim Gottzén
 */
public final class DateFormatPatterns {

    @SuppressWarnings("SpellCheckingInspection")
    public static final String ISO_FORMAT_STRING = "yyyy-MM-dd'T'HH:mm:ss.SSSX";

    @SuppressWarnings("SpellCheckingInspection")
    public static final String DATE_ONLY = "yyyy-MM-dd";

    @SuppressWarnings("SpellCheckingInspection")
    public static final String COMPRESSED_DATE_ONLY = "yyyyMMdd";

    private DateFormatPatterns() {
    }
}
