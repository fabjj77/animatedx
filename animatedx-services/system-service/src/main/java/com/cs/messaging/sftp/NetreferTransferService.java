package com.cs.messaging.sftp;

/**
 * @author Joakim Gottzén
 */
public interface NetreferTransferService {

    void sendMessage(final NetreferActivityMessage activityMessage);

    void sendMessage(final NetreferRegistrationMessage registrationMessage);
}
