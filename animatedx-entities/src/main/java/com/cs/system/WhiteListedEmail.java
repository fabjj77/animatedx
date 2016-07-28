package com.cs.system;

/**
 * @author Omid Alaepour
 */
public enum WhiteListedEmail {
    ANIMATED_GAMES("animatedgames.se"),
    @SuppressWarnings("SpellCheckingInspection")BLING_CITY("blingcity.com"),
    @SuppressWarnings("SpellCheckingInspection")LESTER("lesterhein.com");

    private final String email;

    WhiteListedEmail(final String email) {
        this.email = email;
    }

    public static boolean isWhiteListed(final String email){
       for (final WhiteListedEmail whiteListedEmail : values()) {
           if (email.endsWith(whiteListedEmail.email)) {
               return true;
           }
       }
        return false;
    }
}
