package com.cs.game;

import com.cs.util.DateFormatPatterns;

public final class CasinoModuleConstants {

    public static final String PLAYER_ID_PREFIX = "BlingCity_";
    static final String PLAYER_PASSWORD = "BlingCityPass";

    static final String WIDTH = "width";
    static final String HEIGHT = "height";
    @SuppressWarnings("SpellCheckingInspection")
    static final String HELP_FILE = "helpfile";
    static final String CLIENT = "client";
    @SuppressWarnings("SpellCheckingInspection")
    static final String STATIC_URL = "staticurl";
    @SuppressWarnings("SpellCheckingInspection")
    static final String GAME_SERVER_URL = "gameserverurl";
    @SuppressWarnings("SpellCheckingInspection")
    static final String MOBILE_GAME_URL = "mobilegameurl";
    static final String BASE = "base";
    static final String VARS = "vars";
    static final String ALLOW_SCRIPT_ACCESS = "AllowScriptAccess";
    static final String FLASH_VERSION = "flashVersion";
    @SuppressWarnings("SpellCheckingInspection")
    static final String WINDOW_MODE = "wmode";

    static final String BIRTH_DATE_FORMAT = DateFormatPatterns.COMPRESSED_DATE_ONLY;

    static final String PHONE = "Phone";
    static final String FIRST_NAME = "FName";
    static final String LAST_NAME = "LName";
    static final String EMAIL = "Email";
    @SuppressWarnings("SpellCheckingInspection")
    static final String BIRTH_DATE = "Birthdate";
    static final String CITY = "City";
    static final String STREET = "Street";
    static final String ZIP = "Zip";
    static final String STATE = "State";
    static final String COUNTRY = "Country";
    static final String DISPLAY_NAME = "DisplayName";
    static final String CHANNEL = "Channel";
    static final String AFFILIATE_CODE = "AffiliateCode";

    static final long SESSION_LENGTH = 1140000; // 19 min

    private CasinoModuleConstants() {}
}
