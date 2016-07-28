package com.cs.messaging.bronto;

import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;

import java.util.concurrent.Future;

/**
 * @author Omid Alaepour
 */
@MessagingGateway(errorChannel = "errorChannel")
public interface BrontoGateway {

    @Gateway(requestChannel = "brontoMessages")
    Future<Void> addContact(final InternalBrontoContact internalBrontoContact);

    @Gateway(requestChannel = "brontoMessages")
    Future<Void> updateContactFields(final UpdateContactFieldMessage updateContactFieldMessage);
}
