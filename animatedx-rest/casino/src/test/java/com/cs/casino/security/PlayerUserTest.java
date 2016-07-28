package com.cs.casino.security;

import com.cs.persistence.Status;
import com.cs.player.BlockType;
import com.cs.player.Player;
import com.cs.player.TrustLevel;

import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

import static com.cs.persistence.Status.BAD_CREDENTIALS_LOCKED;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author Joakim Gottzén
 */
public class PlayerUserTest {
    private Player createPlayer() {
        final Player player = new Player();
        player.setBlockType(BlockType.UNBLOCKED);
        player.setTrustLevel(TrustLevel.GREEN);
        player.setStatus(Status.ACTIVE);
        player.setEmailAddress("email");
        player.setPassword("pass");
        player.setFailedLoginAttempts(0);
        return player;
    }

    private Date getNewTime(final Date currentDate, final int timeUnit, final int amount) {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(timeUnit, amount);
        return calendar.getTime();
    }

    @Test
    public void isAccountNonBlocked_returnTrueIfPlayerIsUnblocked() {
        final Player player = createPlayer();
        final PlayerUser playerUser = new PlayerUser(player);

        assertThat(playerUser.isAccountNonLocked(), is(true));
    }

    @Test
    public void isAccountNonBlocked_returnTrueIfPlayerIsLossLimitedBlock() {
        final Player player = createPlayer();
        player.setBlockType(BlockType.LOSS_LIMIT);
        final PlayerUser playerUser = new PlayerUser(player);

        assertThat(playerUser.isAccountNonLocked(), is(true));
    }

    @Test
    public void isAccountNonBlocked_returnTrueIfPlayerIsBetLimitedBlock() {
        final Player player = createPlayer();
        player.setBlockType(BlockType.BET_LIMIT);
        final PlayerUser playerUser = new PlayerUser(player);

        assertThat(playerUser.isAccountNonLocked(), is(true));
    }

    @Test
    public void isAccountNonBlocked_returnTrueIfPlayerIsDefiniteBlockedAndBlockEndTimeIsExpired() {
        final Player player = createPlayer();
        player.setBlockType(BlockType.DEFINITE_SELF_EXCLUSION);
        player.setBlockEndDate(getNewTime(new Date(), Calendar.DATE, -1));
        final PlayerUser playerUser = new PlayerUser(player);

        assertThat(playerUser.isAccountNonLocked(), is(true));
    }

    @Test
    public void isAccountNonBlocked_returnTrueIfPlayerIsSessionLimitBlockedAndBlockEndTimeIsExpired() {
        final Player player = createPlayer();
        player.setBlockType(BlockType.SESSION_LENGTH);
        player.setBlockEndDate(new Date());
        final PlayerUser playerUser = new PlayerUser(player);

        assertThat(playerUser.isAccountNonLocked(), is(true));
    }

    @Test
    public void isAccountNonBlocked_returnFalseIfPlayerIsInDefiniteBlockedRegardlessOfTime() {
        final Player player = createPlayer();
        player.setBlockType(BlockType.INDEFINITE_SELF_EXCLUSION);
        //noinspection ConstantConditions
        player.setBlockEndDate(null);
        final PlayerUser playerUser = new PlayerUser(player);

        assertThat(playerUser.isEnabled(), is(false));
    }

    @Test
    public void isAccountNonBlocked_returnFalseIfPlayerIsDefiniteBlockedAndBlockEndDateHasNotBeenExpired() {
        final Player player = createPlayer();
        player.setBlockType(BlockType.DEFINITE_SELF_EXCLUSION);
        player.setBlockEndDate(getNewTime(new Date(), Calendar.DATE, 1));
        final PlayerUser playerUser = new PlayerUser(player);

        assertThat(playerUser.isEnabled(), is(false));
    }

    @Test
    public void isAccountNonBlocked_returnFalseIfPlayerTrustLevelIsBlackRegardlessOfBlockStatus() {
        final Player player = createPlayer();
        player.setBlockType(BlockType.UNBLOCKED);
        player.setBlockEndDate(getNewTime(new Date(), Calendar.DATE, -1));
        player.setTrustLevel(TrustLevel.BLACK);
        final PlayerUser playerUser = new PlayerUser(player);

        assertThat(playerUser.isEnabled(), is(false));
    }

    @Test
    public void maximumLoginAttemptsShouldLockAccountWhenStatusIsBAD_CREDENTIALS_LOCKED() {
        final Player player = createPlayer();
        player.setStatus(BAD_CREDENTIALS_LOCKED);
        final PlayerUser playerUser = new PlayerUser(player);

        assertThat(playerUser.isAccountNonLocked(), is(false));
    }

    @Test
    public void maximumLoginAttemptsShouldNotLockAccountWhenMoreThanLockoutTime() {
        final Player player = createPlayer();
        final int failedLoginAttempts = 5;
        player.setFailedLoginAttempts(failedLoginAttempts);
        final Calendar calendar = Calendar.getInstance();
        final int minutes = 5;
        calendar.add(Calendar.MINUTE, -minutes);
        calendar.add(Calendar.SECOND, -10);
        final Date time = calendar.getTime();
        player.setLastFailedLoginDate(time);
        final PlayerUser playerUser = new PlayerUser(player);

        assertThat(playerUser.isAccountNonLocked(), is(true));
    }
}
