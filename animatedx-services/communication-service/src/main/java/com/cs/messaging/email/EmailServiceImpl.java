package com.cs.messaging.email;

import com.cs.persistence.Country;
import com.cs.player.Player;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Date;

import static org.springframework.transaction.annotation.Isolation.READ_COMMITTED;

/**
 * @author Omid Alaepour.
 */
@Service
@Transactional(isolation = READ_COMMITTED)
public class EmailServiceImpl implements EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

    @Value("${email.email-username}")
    private String fromEmail;
    private static final String IP_COUNTRY_MISMATCH_SUBJECT = "IP Country Mismatch - Account ID + Email address";
    private static final String IP_COUNTRY_MISMATCH_BODY = "Date: %s\nPlayer ID: %s\nEmail address: %s\nIP: %s \tResolved country: %s\nCountry: %s";
    private static final String IP_COUNTRY_MISMATCH_RECIPIENT = "luke.gauci@blingcity.com";
    private static final String CC_RECIPIENT = "operations@animatedgames.se";

    private final JavaMailSender javaMailSender;

    @Autowired
    public EmailServiceImpl(final JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Override
    public void sendIpCountryMismatchEmail(final Player player, final String ipAddress, final Country country) {
        final MimeMessage message = javaMailSender.createMimeMessage();
        final MimeMessageHelper helper = new MimeMessageHelper(message);
        final Date date = new Date();

        try {
            helper.setFrom(fromEmail);
            helper.setTo(IP_COUNTRY_MISMATCH_RECIPIENT);
            helper.setCc(CC_RECIPIENT);
            helper.setSubject(IP_COUNTRY_MISMATCH_SUBJECT);
            helper.setSentDate(date);
            helper.setText(String.format(IP_COUNTRY_MISMATCH_BODY, date, player.getId(), player.getEmailAddress(), ipAddress, country, player.getAddress().getCountry()));
        } catch (final MessagingException e) {
            logger.error("Error while sending {} email.({})", IP_COUNTRY_MISMATCH_SUBJECT, e.getMessage());
            return;
        }

        javaMailSender.send(message);
    }
}
