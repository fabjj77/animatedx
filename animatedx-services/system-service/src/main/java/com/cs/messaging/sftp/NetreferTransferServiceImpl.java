package com.cs.messaging.sftp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.springframework.transaction.annotation.Isolation.READ_COMMITTED;

/**
 * @author Joakim Gottzén
 */
@Service
@Transactional(isolation = READ_COMMITTED)
public class NetreferTransferServiceImpl implements NetreferTransferService {

    private final Logger logger = LoggerFactory.getLogger(NetreferTransferServiceImpl.class);

    private final MessageChannel sftpNetreferChannel;

    @Value("${netrefer.path.header-name}")
    private String pathHeaderName;
    @Value("${netrefer.activity.path}")
    private String activityPath;
    @Value("${netrefer.btag.registration.path}")
    private String registrationPath;

    @Autowired
    public NetreferTransferServiceImpl(final MessageChannel sftpNetreferChannel) {
        this.sftpNetreferChannel = sftpNetreferChannel;
    }

    @Override
    public void sendMessage(final NetreferActivityMessage activityMessage) {
        logger.info("Sending activity message '{}' to NetRefer", activityMessage);

        activityMessage.addHeader(pathHeaderName, activityPath);
        sftpNetreferChannel.send(activityMessage);
    }

    @Override
    public void sendMessage(final NetreferRegistrationMessage registrationMessage) {
        logger.info("Sending registration message '{}' to NetRefer", registrationMessage);

        registrationMessage.addHeader(pathHeaderName, registrationPath);
        sftpNetreferChannel.send(registrationMessage);
    }
}
