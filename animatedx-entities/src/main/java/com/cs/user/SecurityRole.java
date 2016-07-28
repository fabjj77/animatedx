package com.cs.user;

import java.util.Arrays;
import java.util.List;

/**
 * @author Joakim Gottzén
 */
@SuppressWarnings("UnusedDeclaration")
public enum SecurityRole {
    SUPER_USER(Access.PLAYER_PAYMENTS_ADMIN, Access.AFFILIATE_PAYMENTS_ADMIN, Access.PLAYER_SETTINGS_ADMIN, Access.PLAYER_PROMOTIONS_ADMIN,
               Access.PROMOTION_CAMPAIGNS_ADMIN, Access.PLAYER_CREDITS_ADMIN, Access.GAME_ADMIN),

    KEY_OFFICIAL(Access.PLAYER_PAYMENTS_READ, Access.AFFILIATE_PAYMENTS_READ, Access.PLAYER_SETTINGS_READ, Access.PLAYER_PROMOTIONS_READ,
                 Access.PROMOTION_CAMPAIGNS_READ),

    MONEY_LAUNDERING_OFFICIAL(Access.PLAYER_PAYMENTS_READ, Access.AFFILIATE_PAYMENTS_READ, Access.PLAYER_SETTINGS_READ, Access.PLAYER_PROMOTIONS_READ,
                              Access.PROMOTION_CAMPAIGNS_READ),

    MARKETING_MANAGER(Access.PLAYER_PAYMENTS_READ, Access.AFFILIATE_PAYMENTS_WRITE, Access.PLAYER_SETTINGS_READ, Access.PLAYER_PROMOTIONS_WRITE,
                      Access.PROMOTION_CAMPAIGNS_ADMIN),

    CASINO_MANAGER(Access.PLAYER_PAYMENTS_WRITE, Access.AFFILIATE_PAYMENTS_WRITE, Access.PLAYER_SETTINGS_WRITE, Access.PLAYER_PROMOTIONS_ADMIN,
                   Access.PROMOTION_CAMPAIGNS_ADMIN, Access.GAME_ADMIN),

    ONLINE_MARKETING_MANAGER(Access.PLAYER_PAYMENTS_WRITE, Access.AFFILIATE_PAYMENTS_WRITE, Access.PLAYER_SETTINGS_READ, Access.PLAYER_PROMOTIONS_ADMIN,
                             Access.PROMOTION_CAMPAIGNS_ADMIN),

    PAYMENTS_MANAGER(Access.PLAYER_PAYMENTS_ADMIN, Access.AFFILIATE_PAYMENTS_ADMIN, Access.PLAYER_SETTINGS_WRITE, Access.PLAYER_PROMOTIONS_READ,
                     Access.PROMOTION_CAMPAIGNS_READ, Access.PLAYER_CREDITS_ADMIN),

    CUSTOMER_SUPPORT_MANAGER(Access.PLAYER_PAYMENTS_WRITE, Access.AFFILIATE_PAYMENTS_READ, Access.PLAYER_SETTINGS_ADMIN, Access.PLAYER_PROMOTIONS_WRITE,
                             Access.PROMOTION_CAMPAIGNS_READ, Access.PLAYER_CREDITS_WRITE),

    CUSTOMER_SUPPORT_LEADER(Access.PLAYER_PAYMENTS_WRITE, Access.PLAYER_SETTINGS_WRITE, Access.PLAYER_PROMOTIONS_WRITE, Access.PROMOTION_CAMPAIGNS_READ,
                            Access.PLAYER_CREDITS_WRITE),

    CUSTOMER_SUPPORT(Access.PLAYER_PAYMENTS_WRITE, Access.PLAYER_SETTINGS_WRITE, Access.PLAYER_PROMOTIONS_WRITE, Access.PROMOTION_CAMPAIGNS_READ,
                     Access.PLAYER_CREDITS_WRITE);

    private final List<Access> accesses;

    SecurityRole(final Access... accesses) {
        this.accesses = Arrays.asList(accesses);
    }

    public List<Access> getAccesses() {
        return accesses;
    }

    public enum  Access {
        PLAYER_PAYMENTS_ADMIN(AccessLevel.ADMINISTRATOR),
        PLAYER_PAYMENTS_WRITE(AccessLevel.WRITE),
        PLAYER_PAYMENTS_READ(AccessLevel.READ),

        AFFILIATE_PAYMENTS_ADMIN(AccessLevel.ADMINISTRATOR),
        AFFILIATE_PAYMENTS_WRITE(AccessLevel.WRITE),
        AFFILIATE_PAYMENTS_READ(AccessLevel.READ),

        PLAYER_SETTINGS_ADMIN(AccessLevel.ADMINISTRATOR),
        PLAYER_SETTINGS_WRITE(AccessLevel.WRITE),
        PLAYER_SETTINGS_READ(AccessLevel.READ),

        PLAYER_PROMOTIONS_ADMIN(AccessLevel.ADMINISTRATOR),
        PLAYER_PROMOTIONS_WRITE(AccessLevel.WRITE),
        PLAYER_PROMOTIONS_READ(AccessLevel.READ),

        PROMOTION_CAMPAIGNS_ADMIN(AccessLevel.ADMINISTRATOR),
        PROMOTION_CAMPAIGNS_WRITE(AccessLevel.WRITE),
        PROMOTION_CAMPAIGNS_READ(AccessLevel.READ),

        PLAYER_CREDITS_ADMIN(AccessLevel.WRITE),
        PLAYER_CREDITS_WRITE(AccessLevel.WRITE),
        PLAYER_CREDITS_READ(AccessLevel.READ),

        GAME_ADMIN(AccessLevel.ADMINISTRATOR),
        GAME_WRITE(AccessLevel.WRITE),
        GAME_READ(AccessLevel.READ);

        private final AccessLevel accessLevel;

        private Access(final AccessLevel accessLevel) {
            this.accessLevel = accessLevel;
        }

        public AccessLevel getAccessLevel() {
            return accessLevel;
        }
    }

    public enum AccessLevel {
        ADMINISTRATOR, WRITE, READ
    }
}
