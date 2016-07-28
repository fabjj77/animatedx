package com.cs.messaging.bronto;

import com.cs.messaging.email.BrontoContact;
import com.cs.messaging.email.BrontoContactFilter;
import com.cs.messaging.email.BrontoField;
import com.cs.messaging.email.BrontoWorkflow;
import com.cs.user.User;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandlingException;

import java.util.List;

/**
 * @author Omid Alaepour
 */
public interface BrontoService {
    // Used by Spring Integration messaging
    @SuppressWarnings("UnusedDeclaration")
    void onError(final Message<MessageHandlingException> message);

    // Used by Spring Integration messaging
    @SuppressWarnings("UnusedDeclaration")
    void addContactToBronto(final InternalBrontoContact internalBrontoContact);

    // Used by Spring Integration messaging
    @SuppressWarnings("UnusedDeclaration")
    void updateContactFieldsInBronto(final UpdateContactFieldMessage updateContactFieldMessage);

    List<BrontoContact> updateContactsInDatabase(final User user);

    List<BrontoField> updateFieldsInDatabase();

    List<BrontoWorkflow> updateWorkflowInDatabase();

    List<InternalBrontoContact> readContactsFromBronto(final BrontoContactFilter brontoContactFilter);
}
