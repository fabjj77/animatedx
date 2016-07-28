package com.cs.validation;

/**
 * @author Omid Alaepour.
 */
public final class ValidationProperties {
    public static final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,40}$";

    public static final String NULLABLE_PASSWORD_PATTERN = "$^|" + PASSWORD_PATTERN;

    public static final String UUID_PATTERN = "[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}";

    public static final String BCRYPT_PASSWORD_PATTERN = "\\A\\$2a?\\$\\d\\d\\$[./0-9A-Za-z]{53}";

    public static final String NICKNAME_PATTERN = "^[\\p{L}\\d _.'-]+$";

    private ValidationProperties() {
    }
}
