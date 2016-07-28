package com.cs.player;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyIterable;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.Matchers.nullValue;

/**
 * @author Joakim Gottzén
 */
public class PlayerTest {

    @Test
    public void updateFromPlayer_ShouldUpdateCorrectProperties() {
        final Player newPlayer = new Player();
        newPlayer.setFirstName("newFirst");
        newPlayer.setLastName("newLast");
        newPlayer.setEmailAddress("newEmail");
        newPlayer.setNickname("newNickname");
        newPlayer.setPhoneNumber("newPhoneNumber");

        final Player originalPlayer = new Player();

        originalPlayer.updateFromPlayer(newPlayer);

        // Check that the correct properties has been set
        assertThat(originalPlayer.getFirstName(), hasToString(newPlayer.getFirstName()));
        assertThat(originalPlayer.getLastName(), hasToString(newPlayer.getLastName()));
        assertThat(originalPlayer.getEmailAddress(), hasToString(newPlayer.getEmailAddress()));
        assertThat(originalPlayer.getNickname(), hasToString(newPlayer.getNickname()));
        assertThat(originalPlayer.getPhoneNumber(), hasToString(newPlayer.getPhoneNumber()));

        // Check that the no other properties has been set
        assertUpdateFromPlayerFieldsAreNotSet(originalPlayer);
        assertThat(originalPlayer.getPassword(), is(nullValue()));
    }

    private void assertUpdateFromPlayerFieldsAreNotSet(final Player originalPlayer) {
        assertThat(originalPlayer.getId(), is(nullValue()));
        assertThat(originalPlayer.getBirthday(), is(nullValue()));
        assertThat(originalPlayer.getAvatar(), is(nullValue()));
        assertThat(originalPlayer.getLevel(), is(nullValue()));
        assertThat(originalPlayer.getAddress(), is(nullValue()));
        assertThat(originalPlayer.getCurrency(), is(nullValue()));
        assertThat(originalPlayer.getStatus(), is(nullValue()));
        assertThat(originalPlayer.getCreatedDate(), is(nullValue()));
        assertThat(originalPlayer.getModifiedDate(), is(nullValue()));
        assertThat(originalPlayer.getPlayerVerification(), is(nullValue()));
        assertThat(originalPlayer.getEmailVerification(), is(nullValue()));
        assertThat(originalPlayer.getBlockType(), is(nullValue()));
        assertThat(originalPlayer.getBlockEndDate(), is(nullValue()));
        assertThat(originalPlayer.getTrustLevel(), is(nullValue()));
        assertThat(originalPlayer.getLanguage(), is(nullValue()));
        assertThat(originalPlayer.getWallet(), is(nullValue()));
        assertThat(originalPlayer.getPlayerItems(), is(emptyIterable()));
        assertThat(originalPlayer.getPlayerAffiliate(), is(nullValue()));
        assertThat(originalPlayer.getPlayerPromotions(), is(emptyIterable()));
        assertThat(originalPlayer.getPlayerBonuses(), is(emptyIterable()));
    }

    @Test
    public void updateFromPlayer_ShouldNotUpdateNullField() {
        final Player newPlayer = new Player();
        newPlayer.setFirstName("newFirst");
        newPlayer.setLastName("newLast");
        newPlayer.setEmailAddress("newEmail");
        newPlayer.setNickname("newNickname");

        final Player originalPlayer = new Player();

        originalPlayer.updateFromPlayer(newPlayer);

        // Check that the correct properties has been set
        assertThat(originalPlayer.getFirstName(), hasToString(newPlayer.getFirstName()));
        assertThat(originalPlayer.getLastName(), hasToString(newPlayer.getLastName()));
        assertThat(originalPlayer.getEmailAddress(), hasToString(newPlayer.getEmailAddress()));
        assertThat(originalPlayer.getNickname(), hasToString(newPlayer.getNickname()));

        // Check that phone number hasn't been set since it's null
        assertThat(originalPlayer.getPhoneNumber(), is(nullValue()));

        // Check that the no other properties has been set
        assertUpdateFromPlayerFieldsAreNotSet(originalPlayer);
        assertThat(originalPlayer.getPassword(), is(nullValue()));
    }

    @Test
    public void updateFromPlayer_ShouldUpdatePassword() {
        final Player newPlayer = new Player();
        newPlayer.setFirstName("newFirst");
        newPlayer.setLastName("newLast");
        newPlayer.setEmailAddress("newEmail");
        newPlayer.setNickname("newNickname");
        newPlayer.setPhoneNumber("newPhoneNumber");
        newPlayer.setPassword("password");

        final Player originalPlayer = new Player();

        originalPlayer.updateFromPlayer(newPlayer);

        // Check that the correct properties has been set
        assertThat(originalPlayer.getFirstName(), hasToString(newPlayer.getFirstName()));
        assertThat(originalPlayer.getLastName(), hasToString(newPlayer.getLastName()));
        assertThat(originalPlayer.getEmailAddress(), hasToString(newPlayer.getEmailAddress()));
        assertThat(originalPlayer.getNickname(), hasToString(newPlayer.getNickname()));
        assertThat(originalPlayer.getPassword(), hasToString(newPlayer.getPassword()));

        // Check that the no other properties has been set
        assertUpdateFromPlayerFieldsAreNotSet(originalPlayer);
    }
}
