package com.cs.player;

import com.cs.user.User;

import org.springframework.data.domain.Page;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Joakim Gottzén
 */
public interface PlayerService {

    Page<Player> searchPlayers(@Nullable final String emailAddress, @Nullable final String nickname, @Nullable final String firstName, @Nullable final String lastName,
                               @Nullable final BlockType blockType, @Nullable final LimitationStatus limitationStatus, @Nonnull final Integer page, final Integer size);

    Iterable<Player> searchPlayers(@Nullable final String emailAddress, @Nullable final String nickname, @Nullable final String firstName,
                                   @Nullable final String lastName, @Nullable final BlockType blockType, @Nullable final LimitationStatus limitationStatus);

    Player getPlayer(final Long id);

    Player getPlayer(@Nonnull final String emailAddress);

    /**
     * This method should only be used when fetching players for authentication, by the Spring Security framework, since it doesn't check if the user is active or not!
     */
    Player getPlayerForAuthentication(@Nonnull final String emailAddress);

    Player getPlayerByCasinoUsername(final String netEntUsername);

    Player createPlayer(final Player player, @Nullable final PlayerRegisterTrack track, final String bTag, final String ipAddress);

    Player updatePlayer(final Long id, final Player player, @Nullable final String oldPassword, @Nullable final String newPassword);

    Player updatePlayerFromBackOffice(final Long id, final Player player);

    Player inactivatePlayer(final Long id);

    Address getPlayerAddress(final Long id);

    Address updatePlayerAddress(final Long id, final Address address);

    Wallet getPlayerWallet(final Long id);

    boolean checkLevelByProgressTurnover(final Player player);

    void sendPlayerRegistrationEmail(final Long playerId);

    void createResetPassword(@Nullable final String emailAddress);

    Player resetPassword(@Nonnull final String uuidString, @Nonnull final String newPassword);

    Player verifyEmail(@Nonnull final String uuidString);

    boolean isEmailAddressUnregistered(@Nonnull final String emailAddress);

    boolean isNicknameUnregistered(@Nonnull final String nickname);

    PlayerLimitation updatePlayerLimitation(@Nonnull final Long playerId, @Nonnull final List<Limit> limits, @Nullable final Integer sessionLength,
                                            @Nonnull final String password);

    PlayerLimitation getPlayerLimitation(@Nonnull final Long playerId);

    void logoffPlayer(@Nonnull final Long playerId);

    PlayerUuid createUuidForDeposit(final Player player);

    boolean checkSessionTime(final Long playerId);

    PlayerLimitation backOfficeGetPlayerLimitation(@Nonnull final Long playerId);

    PlayerLimitation backOfficeUpdatePlayerLimitations(@Nonnull final User user, @Nonnull final Long playerId, @Nonnull final List<Limit> limits,
                                                       @Nullable final Integer sessionLength);

    PlayerLimitation forceUpdatePlayerLimitations(@Nonnull final User user, @Nonnull final Long playerId, @Nonnull final List<Limit> limits,
                                                  @Nullable final Integer sessionLength);

    void selfExcludePlayer(@Nonnull final Long playerId, @Nonnull final BlockType blockType, @Nullable final Integer days);

    void blockPlayerSelfExcluded(@Nonnull final User user, @Nonnull final Long playerId, @Nonnull final BlockType blockType, @Nullable final Integer days);

    void unblockSelfExcludedPlayer(@Nonnull final User user, @Nonnull final Long playerId);

    void forceUnblockSelfExcludedPlayer(@Nonnull final User user, @Nonnull final Long playerId);

    void validatePassword(@Nonnull final Player player, @Nonnull final String password);

    void resetLoginFailureCounter(Long id);

    void recordLoginFailure(@Nonnull Long id);

    Player savePlayer(final Player player);

    void resetExpiredLimitBlocks();

    List<Player> getPlayersRegisteredBefore(final Date date);

    Map<BigInteger, String> getPlayerSignUpIp();

    boolean isIpBlocked(final Player player, final String ipAddress);

    void applyPlayerLimitations();

    void resetDailyAccumulatedLimits();

    void resetWeeklyAccumulatedLimits();

    void resetMonthlyAccumulatedLimits();
}
