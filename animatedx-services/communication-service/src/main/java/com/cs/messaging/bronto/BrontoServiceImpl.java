package com.cs.messaging.bronto;

import com.cs.control.SystemControlService;
import com.cs.messaging.email.BrontoContact;
import com.cs.messaging.email.BrontoContactFilter;
import com.cs.messaging.email.BrontoContactStatus;
import com.cs.messaging.email.BrontoField;
import com.cs.messaging.email.BrontoFieldName;
import com.cs.messaging.email.BrontoFieldType;
import com.cs.messaging.email.BrontoFieldVisibility;
import com.cs.messaging.email.BrontoWorkflow;
import com.cs.messaging.email.BrontoWorkflowName;
import com.cs.messaging.email.BrontoWorkflowStatus;
import com.cs.persistence.CommunicationException;
import com.cs.persistence.NotFoundException;
import com.cs.player.Player;
import com.cs.user.User;
import com.cs.util.DateFormatPatterns;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandlingException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bronto.api.v4.AddContacts;
import com.bronto.api.v4.AddContactsToWorkflow;
import com.bronto.api.v4.ApiException_Exception;
import com.bronto.api.v4.BrontoSoapApiImplService;
import com.bronto.api.v4.ContactField;
import com.bronto.api.v4.ContactFilter;
import com.bronto.api.v4.ContactObject;
import com.bronto.api.v4.DeleteContacts;
import com.bronto.api.v4.FieldObject;
import com.bronto.api.v4.FieldsFilter;
import com.bronto.api.v4.FilterOperator;
import com.bronto.api.v4.ReadContacts;
import com.bronto.api.v4.ReadContactsResponse;
import com.bronto.api.v4.ReadFields;
import com.bronto.api.v4.ReadFieldsResponse;
import com.bronto.api.v4.ReadWorkflows;
import com.bronto.api.v4.ResultItem;
import com.bronto.api.v4.SessionHeader;
import com.bronto.api.v4.StringValue;
import com.bronto.api.v4.UpdateContacts;
import com.bronto.api.v4.WorkflowFilter;
import com.bronto.api.v4.WorkflowObject;
import com.bronto.api.v4.WriteResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static com.cs.messaging.email.BrontoContactFilter.NONE;
import static org.springframework.transaction.annotation.Isolation.READ_COMMITTED;
import static org.springframework.transaction.annotation.Propagation.SUPPORTS;

/**
 * @author Omid Alaepour
 */
@Service
@MessageEndpoint
@Transactional(isolation = READ_COMMITTED)
public class BrontoServiceImpl implements BrontoService {
    private static final Logger logger = LoggerFactory.getLogger(BrontoServiceImpl.class);
    private static final String BRONTO_SOAP_WSDL_LOCATION = "https://api.bronto.com/v4?wsdl";

    private final EmailContactRepository emailContactRepository;
    private final EmailFieldRepository emailFieldRepository;
    private final EmailWorkflowRepository emailWorkflowRepository;
    private final SystemControlService systemControlService;

    @Value("${email.token}")
    private String emailToken;

    @Autowired
    public BrontoServiceImpl(final EmailContactRepository emailContactRepository, final EmailFieldRepository emailFieldRepository,
                             final EmailWorkflowRepository emailWorkflowRepository, final SystemControlService systemControlService) {
        this.emailContactRepository = emailContactRepository;
        this.emailFieldRepository = emailFieldRepository;
        this.emailWorkflowRepository = emailWorkflowRepository;
        this.systemControlService = systemControlService;
    }

    private BrontoSoapApiImplService brontoService()
            throws MalformedURLException {
        return new BrontoSoapApiImplService(new URL(BRONTO_SOAP_WSDL_LOCATION));
    }

    private SessionHeader createBrontoSession() {
        try {
            final SessionHeader sessionHeader = new SessionHeader();
            sessionHeader.setSessionId(brontoService().getBrontoSoapApiImplPort().login(emailToken));
            return sessionHeader;
        } catch (final ApiException_Exception | MalformedURLException e) {
            logger.error("Couldn't create a bronto session: {}", e.getMessage());
            throw new CommunicationException(e.getMessage(), e);
        }
    }

    private XMLGregorianCalendar newXmlDate() {
        final GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.setTime(new Date());
        try {
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);
        } catch (final DatatypeConfigurationException e) {
            logger.error("Error creating xml date: {}", e.getMessage());
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    private List<ContactField> setFieldInfo(final List<BrontoField> brontoFields, final InternalBrontoContact internalBrontoContact) {
        final List<ContactField> contactInfo = new ArrayList<>();

        for (final BrontoField brontoField : brontoFields) {
            final ContactField contactField = new ContactField();
            contactField.setFieldId(brontoField.getFieldId());

            switch (brontoField.getName()) {
                case player_id:
                    contactField.setContent(internalBrontoContact.getPlayerId().toString());
                    contactInfo.add(contactField);
                    break;
                case first_name:
                    contactField.setContent(internalBrontoContact.getFirstName());
                    contactInfo.add(contactField);
                    break;
                case last_name:
                    contactField.setContent(internalBrontoContact.getLastName());
                    contactInfo.add(contactField);
                    break;
                case nickname:
                    contactField.setContent(internalBrontoContact.getNickname());
                    contactInfo.add(contactField);
                    break;
                case street:
                    contactField.setContent(internalBrontoContact.getStreet());
                    contactInfo.add(contactField);
                    break;
                case street2:
                    if (internalBrontoContact.getStreet2() != null && !internalBrontoContact.getStreet2().isEmpty()) {
                        contactField.setContent(internalBrontoContact.getStreet2());
                        contactInfo.add(contactField);
                    }
                    break;
                case zipcode:
                    contactField.setContent(internalBrontoContact.getZipCode());
                    contactInfo.add(contactField);
                    break;
                case state:
                    if (internalBrontoContact.getState() != null && !internalBrontoContact.getState().isEmpty()) {
                        contactField.setContent(internalBrontoContact.getState());
                        contactInfo.add(contactField);
                    }
                    break;
                case city:
                    contactField.setContent(internalBrontoContact.getCity());
                    contactInfo.add(contactField);
                    break;
                case country:
                    contactField.setContent(internalBrontoContact.getCountry().toString());
                    contactInfo.add(contactField);
                    break;
                case email_verification:
                    contactField.setContent(internalBrontoContact.getEmailVerification().toString());
                    contactInfo.add(contactField);
                    break;
                case created_date:
                    break;
                case uuid:
                    contactField.setContent(internalBrontoContact.getUuid());
                    contactInfo.add(contactField);
                    break;
                case level:
                    contactField.setContent(internalBrontoContact.getLevel().toString());
                    contactInfo.add(contactField);
                    break;
                case birthday:
                    contactField.setContent(new SimpleDateFormat(DateFormatPatterns.DATE_ONLY).format(internalBrontoContact.getBirthday()));
                    contactInfo.add(contactField);
                    break;
                case avatar_id:
                    contactField.setContent(internalBrontoContact.getAvatarId().toString());
                    contactInfo.add(contactField);
                    break;
                case money_balance:
                    contactField.setContent(internalBrontoContact.getMoneyBalance().toString());
                    contactInfo.add(contactField);
                    break;
                case bonus_balance:
                    contactField.setContent(internalBrontoContact.getBonusBalance().toString());
                    contactInfo.add(contactField);
                    break;
                case language:
                    contactField.setContent(internalBrontoContact.getLanguage().toString());
                    contactInfo.add(contactField);
                    break;
                case currency:
                    contactField.setContent(internalBrontoContact.getCurrency().toString());
                    contactInfo.add(contactField);
                    break;
                case status:
                    contactField.setContent(internalBrontoContact.getPlayerStatus().toString());
                    contactInfo.add(contactField);
                    break;
                default:
                    logger.info("setFieldInfo: Field {} not set in system", brontoField.getName());
                    break;
            }
        }
        return contactInfo;
    }

    @Transactional(propagation = SUPPORTS)
    @ServiceActivator(inputChannel = "errorChannel")
    @Override
    public void onError(final Message<MessageHandlingException> message) {
        logger.error("Error: {}", message);
    }

    @ServiceActivator(inputChannel = "brontoMessages")
    @Override
    public void addContactToBronto(final InternalBrontoContact internalBrontoContact) {
        if (!systemControlService.isBrontoEnabled()) {
            return;
        }

        try {
            final SessionHeader sessionHeader = createBrontoSession();
            logger.info("addContactToBronto: Adding contact with email {}", internalBrontoContact.getEmailAddress());
            final ContactObject contact = new ContactObject();
            contact.setEmail(internalBrontoContact.getEmailAddress());

            //contact.setMobileNumber(brontoContact.getPhoneNumber()); //Todo Fix exception throw by Bronto
            contact.setCreated(newXmlDate());
            contact.setStatus(internalBrontoContact.getStatus().toString());

            final List<BrontoField> brontoFields = getBrontoFieldsFromDatabase();

            if (!brontoFields.isEmpty()) {
                contact.getFields().addAll(setFieldInfo(brontoFields, internalBrontoContact));
                logger.info("addContactToBronto: Settings fields for  contact with email {}", internalBrontoContact.getEmailAddress());
            }

            final AddContacts contacts = new AddContacts();
            contacts.getContacts().add(contact);

            try {
                final WriteResult result = brontoService().getBrontoSoapApiImplPort().addContacts(contacts, sessionHeader).getReturn();

                //Handle error
                if (!result.getErrors().isEmpty()) {
                    for (final ResultItem resultItem : result.getResults()) {
                        if (resultItem.getErrorString() != null) {
                            logger.error("addContactToBronto: Error occurred when trying to add contact to Bronto: {}", resultItem.getErrorString());
                        }
                    }
                    return;
                }

                logger.info("addContactToBronto: Contact with email {} added to bronto", internalBrontoContact.getEmailAddress());

                for (final ResultItem resultItem : result.getResults()) {
                    if (resultItem.isIsNew() != null && resultItem.isIsNew()) {
                        internalBrontoContact.setBrontoId(resultItem.getId());
                        final BrontoContact brontoContact = convertBrontoContact(internalBrontoContact);
                        brontoContact.setBrontoId(resultItem.getId());
                        brontoContact.setCreatedDate(new Date());
                        emailContactRepository.save(brontoContact);
                    }
                }

                if (internalBrontoContact.getBrontoWorkflowName() != null) {
                    logger.info("addContactToBronto: Adding contact with email {} to workflow {}", internalBrontoContact.getEmailAddress(), internalBrontoContact.getBrontoWorkflowName());
                    addContactToWorkFlow(new AddContactWorkflowMessage(internalBrontoContact, internalBrontoContact.getBrontoWorkflowName()));
                }
            } catch (final ApiException_Exception | MalformedURLException e) {
                logger.error("Couldn't create contact in bronto: {}", e.getMessage(), e);
            }
        } catch (final RuntimeException e) {
            logger.error("Couldn't create contact in bronto: {}", e.getMessage(), e);
        }
    }

    private void addContactToWorkFlow(final AddContactWorkflowMessage addContactWorkflowMessage) {
        BrontoWorkflow brontoWorkflow = getBrontoWorkflowFromDatabase(addContactWorkflowMessage.getBrontoWorkflowName());

        //If workflow does not exist, try updating from Bronto and get it again
        if (brontoWorkflow == null) {
            final List<BrontoWorkflow> workflowsInDatabase = updateWorkflowInDatabase();
            if (workflowsInDatabase.isEmpty()) {
                logger.error("addContactToBronto: No workflows available in Bronto to add to database.");
                return;
            }
            brontoWorkflow = getBrontoWorkflowFromDatabase(addContactWorkflowMessage.getBrontoWorkflowName());
        }
        addContactToWorkFlow(addContactWorkflowMessage.getInternalBrontoContact(), new InternalBrontoWorkflow(brontoWorkflow));
    }

    private void addContactToWorkFlow(final InternalBrontoContact internalBrontoContact, final InternalBrontoWorkflow internalBrontoWorkflow) {
        final SessionHeader sessionHeader = createBrontoSession();

        try {
            final WorkflowObject workflowObject = new WorkflowObject();
            workflowObject.setId(internalBrontoWorkflow.getId());

            final ContactObject contactObject = new ContactObject();
            contactObject.setId(internalBrontoContact.getBrontoId());

            final AddContactsToWorkflow addContactsToWorkflow = new AddContactsToWorkflow();
            addContactsToWorkflow.setWorkflow(workflowObject);
            addContactsToWorkflow.getContacts().add(contactObject);

            final WriteResult result = brontoService().getBrontoSoapApiImplPort().addContactsToWorkflow(addContactsToWorkflow, sessionHeader).getReturn();

            //Handle error
            if (!result.getErrors().isEmpty()) {
                for (final ResultItem resultItem : result.getResults()) {
                    if (resultItem.getErrorString() != null) {
                        logger.error("Error occurred when trying to add contact to workflow: {}", resultItem.getErrorString());
                    }
                }
            }

            logger.info("Adding contact with email {} to workflow {}", internalBrontoContact.getEmailAddress(), internalBrontoContact.getBrontoWorkflowName());
        } catch (final ApiException_Exception | MalformedURLException e) {
            logger.error("Couldn't add contact to workflow: {}", e.getMessage(), e);
        }
    }

    // Do not remove, will be used
    private void removeContact(final InternalBrontoContact internalBrontoContact) {
        final SessionHeader sessionHeader = createBrontoSession();

        final DeleteContacts deleteContacts = new DeleteContacts();
        deleteContacts.getContacts().add(internalBrontoContact.asContactObject());

        try {
            brontoService().getBrontoSoapApiImplPort().deleteContacts(deleteContacts, sessionHeader);
        } catch (final ApiException_Exception | MalformedURLException e) {
            logger.error("Cannot delete contact from Bronto: {}", e.getMessage());
            throw new CommunicationException(e.getMessage(), e);
        }
    }

    @Transactional(propagation = SUPPORTS)
    @ServiceActivator(inputChannel = "brontoMessages")
    @Override
    public void updateContactFieldsInBronto(final UpdateContactFieldMessage updateContactFieldMessage) {
        if (!systemControlService.isBrontoEnabled()) {
            return;
        }

        try {
            final InternalBrontoContact internalBrontoContact = updateContactFieldMessage.getInternalBrontoContact();
            final Map<BrontoFieldName, String> fieldNameMap = updateContactFieldMessage.getFieldNameMap();

            final SessionHeader sessionHeader = createBrontoSession();
            try {
                final BrontoContact brontoContact = emailContactRepository.findByPlayer(new Player(internalBrontoContact.getPlayerId()));

                if (brontoContact == null) {
                    logger.warn("updateContactFieldsInBronto: Contact with player id: {} not found in database.", internalBrontoContact.getPlayerId());
                    // TODO add the contact to bronto or try to fetch from Bronto and update table?
                    return;
                }

                internalBrontoContact.setBrontoId(brontoContact.getBrontoId());

                final ContactObject contactObject = internalBrontoContact.asContactObject();
                for (final Entry<BrontoFieldName, String> brontoFieldNameMapEntry : fieldNameMap.entrySet()) {
                    final BrontoField brontoField = emailFieldRepository.findByName(brontoFieldNameMapEntry.getKey());

                    if (brontoField == null) {
                        logger.warn("Field {} not found in database when updating bronto", brontoFieldNameMapEntry.getKey());
                        continue;
                    }

                    final ContactField contactField = new ContactField();
                    contactField.setFieldId(brontoField.getFieldId());
                    contactField.setContent(brontoFieldNameMapEntry.getValue());

                    contactObject.getFields().add(contactField);
                }

                final UpdateContacts updateContacts = new UpdateContacts();
                updateContacts.getContacts().add(contactObject);

                final WriteResult result = brontoService().getBrontoSoapApiImplPort().updateContacts(updateContacts, sessionHeader).getReturn();

                if (!result.getErrors().isEmpty()) {
                    for (final ResultItem resultItem : result.getResults()) {
                        if (resultItem.isIsError()) {
                            logger.error("Updating fields in Bronto failed: {}", resultItem.getErrorString());
                        }
                    }
                }

                if (updateContactFieldMessage.getBrontoWorkflowName() != null) {
                    addContactToWorkFlow(new AddContactWorkflowMessage(internalBrontoContact, updateContactFieldMessage.getBrontoWorkflowName()));
                }
            } catch (final ApiException_Exception | MalformedURLException e) {
                logger.error("Couldn't update contact fields in bronto: {}", e.getMessage(), e);
            }
        } catch (final RuntimeException e) {
            logger.error("Couldn't update contact fields in bronto: {}", e.getMessage(), e);
        }
    }

    private BrontoWorkflow getBrontoWorkflowFromDatabase(final BrontoWorkflowName brontoWorkflowName) {
        return emailWorkflowRepository.findByName(brontoWorkflowName);
    }

    private List<BrontoField> getBrontoFieldsFromDatabase() {
        return emailFieldRepository.findAll();
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public List<BrontoContact> updateContactsInDatabase(final User user) {
        final List<InternalBrontoContact> internalBrontoContacts = readContactsFromBronto(NONE);

        final List<BrontoContact> brontoContacts = new ArrayList<>();
        for (final InternalBrontoContact internalBrontoContact : internalBrontoContacts) {
            if (internalBrontoContact.getPlayerId() != null) {
                final BrontoContact brontoContactInDatabase = emailContactRepository.findByPlayer(new Player(internalBrontoContact.getPlayerId()));

                final BrontoContact brontoContact;
                if (brontoContactInDatabase != null) {
                    brontoContact = updateChangedEmailContact(internalBrontoContact, brontoContactInDatabase);
                } else {
                    brontoContact = convertBrontoContact(internalBrontoContact);
                }

                brontoContact.setModifiedBy(user);
                brontoContact.setModifiedDate(new Date());
                brontoContacts.add(brontoContact);
            } else {
                logger.error("InternalBrontoContact {} has no playerId", internalBrontoContact);
            }
        }
        return emailContactRepository.save(brontoContacts);
    }

    private BrontoContact updateChangedEmailContact(final InternalBrontoContact internalBrontoContact, final BrontoContact brontoContactInDatabase) {
        if (!brontoContactInDatabase.getBrontoId().equals(internalBrontoContact.getBrontoId())) {
            brontoContactInDatabase.setBrontoId(internalBrontoContact.getBrontoId());
        }

        if (brontoContactInDatabase.getStatus() != internalBrontoContact.getStatus()) {
            brontoContactInDatabase.setStatus(internalBrontoContact.getStatus());
        }
        return brontoContactInDatabase;
    }

    private BrontoContact convertBrontoContact(final InternalBrontoContact internalBrontoContact) {
        final BrontoContact brontoContact = new BrontoContact();
        brontoContact.setBrontoId(internalBrontoContact.getBrontoId());
        brontoContact.setStatus(internalBrontoContact.getStatus());
        brontoContact.setPlayer(new Player(internalBrontoContact.getPlayerId()));
        return brontoContact;
    }

    @Override
    public List<BrontoField> updateFieldsInDatabase() {
        final List<InternalBrontoField> internalBrontoFields = readFieldsFromBronto();

        final List<BrontoField> brontoFields = new ArrayList<>();
        for (final InternalBrontoField internalBrontoField : internalBrontoFields) {

            final BrontoField brontoFieldInDatabase = emailFieldRepository.findByName(internalBrontoField.getName());

            final BrontoField brontoField;
            if (brontoFieldInDatabase != null) {
                brontoField = updateChangedFields(internalBrontoField, brontoFieldInDatabase);
            } else {
                brontoField = new BrontoField();
                brontoField.setName(internalBrontoField.getName());
                brontoField.setFieldId(internalBrontoField.getId());
                brontoField.setLabel(internalBrontoField.getLabel());
                brontoField.setType(internalBrontoField.getType());
                brontoField.setVisibility(internalBrontoField.getVisibility());
            }
            brontoFields.add(brontoField);
        }
        return emailFieldRepository.save(brontoFields);
    }

    private BrontoField updateChangedFields(final InternalBrontoField internalBrontoField, final BrontoField brontoFieldInDatabase) {
        if (!brontoFieldInDatabase.getFieldId().equals(internalBrontoField.getId())) {
            brontoFieldInDatabase.setFieldId(internalBrontoField.getId());
        }

        if (!brontoFieldInDatabase.getLabel().equals(internalBrontoField.getLabel())) {
            brontoFieldInDatabase.setLabel(internalBrontoField.getLabel());
        }

        if (brontoFieldInDatabase.getType() != internalBrontoField.getType()) {
            brontoFieldInDatabase.setType(internalBrontoField.getType());
        }

        if (brontoFieldInDatabase.getVisibility() != internalBrontoField.getVisibility()) {
            brontoFieldInDatabase.setVisibility(internalBrontoField.getVisibility());
        }

        return brontoFieldInDatabase;
    }

    @Override
    public List<BrontoWorkflow> updateWorkflowInDatabase() {
        final List<InternalBrontoWorkflow> internalBrontoWorkflows = readWorkflowsFromBronto();

        final List<BrontoWorkflow> brontoWorkflows = new ArrayList<>();
        for (final InternalBrontoWorkflow internalBrontoWorkflow : internalBrontoWorkflows) {

            final BrontoWorkflow brontoWorkflowInDatabase = emailWorkflowRepository.findByName(internalBrontoWorkflow.getName());

            final BrontoWorkflow brontoWorkflow;
            if (brontoWorkflowInDatabase != null) {
                brontoWorkflow = updateChangedWorkflow(internalBrontoWorkflow, brontoWorkflowInDatabase);
            } else {
                brontoWorkflow = new BrontoWorkflow();
                brontoWorkflow.setWorkflowId(internalBrontoWorkflow.getId());
                brontoWorkflow.setName(internalBrontoWorkflow.getName());
                brontoWorkflow.setStatus(internalBrontoWorkflow.getStatus());
            }

            brontoWorkflows.add(brontoWorkflow);
        }
        return emailWorkflowRepository.save(brontoWorkflows);
    }

    private BrontoWorkflow updateChangedWorkflow(final InternalBrontoWorkflow internalBrontoWorkflow, final BrontoWorkflow brontoWorkflowInDatabase) {
        if (!brontoWorkflowInDatabase.getWorkflowId().equals(internalBrontoWorkflow.getId())) {
            brontoWorkflowInDatabase.setWorkflowId(internalBrontoWorkflow.getId());
        }

        if (brontoWorkflowInDatabase.getStatus() != internalBrontoWorkflow.getStatus()) {
            brontoWorkflowInDatabase.setStatus(internalBrontoWorkflow.getStatus());
        }
        return brontoWorkflowInDatabase;
    }

    @Transactional(propagation = SUPPORTS)
    @Override
    public List<InternalBrontoContact> readContactsFromBronto(final BrontoContactFilter brontoContactFilter) {
        final SessionHeader sessionHeader = createBrontoSession();
        try {
            final ContactFilter contactFilter = new ContactFilter();

            if (brontoContactFilter != NONE) {
                final StringValue stringValue = new StringValue();
                stringValue.setOperator(FilterOperator.EQUAL_TO);
                stringValue.setValue(brontoContactFilter.toString());
                contactFilter.getEmail().add(stringValue);
            }

            final ReadContacts readContacts = new ReadContacts();
            readContacts.setFilter(contactFilter);

            List<BrontoField> brontoFields = emailFieldRepository.findAll();

            if (brontoFields.isEmpty()) {
                //If DB is empty, get fields from Bronto and add to DB
                brontoFields = updateFieldsInDatabase();
            }

            for (final BrontoField brontoField : brontoFields) {
                readContacts.getFields().add(brontoField.getFieldId());
            }

            final ReadContactsResponse contactsResponse = brontoService().getBrontoSoapApiImplPort().readContacts(readContacts, sessionHeader);

            final List<ContactObject> foundContacts = contactsResponse.getReturn();

            if (foundContacts.isEmpty()) {
                throw new NotFoundException("Contacts not found");
            }

            final List<InternalBrontoContact> internalBrontoContacts = new ArrayList<>();
            for (final ContactObject foundContact : foundContacts) {
                final InternalBrontoContact internalBrontoContact = new InternalBrontoContact();
                internalBrontoContact.setBrontoId(foundContact.getId());
                internalBrontoContact.setEmailAddress(foundContact.getEmail());
                internalBrontoContact.setPhoneNumber(foundContact.getMobileNumber());
                final BrontoContactStatus status = BrontoContactStatus.getStatus(foundContact.getStatus());
                if (status == null) {
                    logger.warn("Status {} cannot be mapped to internal status enum", foundContact.getStatus());
                }
                internalBrontoContact.setStatus(status);
                internalBrontoContact.setCreatedDate(foundContact.getCreated().toGregorianCalendar().getTime());

                final List<InternalBrontoField> foundFields = new ArrayList<>();
                for (final ContactField contactField : foundContact.getFields()) {
                    for (final BrontoField brontoField : brontoFields) {
                        if (contactField.getFieldId().equals(brontoField.getFieldId()) && !contactField.getContent().isEmpty()) {
                            final InternalBrontoField internalBrontoField = new InternalBrontoField();
                            internalBrontoField.setId(contactField.getFieldId());
                            internalBrontoField.setName(brontoField.getName());
                            internalBrontoField.setContent(contactField.getContent());
                            foundFields.add(internalBrontoField);
                        }
                    }
                }

                extractFields(internalBrontoContact, foundFields);
                internalBrontoContacts.add(internalBrontoContact);
            }
            return internalBrontoContacts;
        } catch (final ApiException_Exception | MalformedURLException e) {
            logger.error("Error reading contact from bronto: {}", e.getMessage(), e);
            throw new CommunicationException(e.getMessage(), e);
        }
    }

    private InternalBrontoContact extractFields(final InternalBrontoContact internalBrontoContact, final List<InternalBrontoField> internalBrontoFields) {
        for (final InternalBrontoField internalBrontoField : internalBrontoFields) {
            switch (internalBrontoField.getName()) {
                case street:
                    break;
                case street2:
                    break;
                case zipcode:
                    break;
                case state:
                    break;
                case city:
                    break;
                case country:
                    break;
                case money_balance:
                    break;
                case bonus_balance:
                    break;
                case player_id:
                    internalBrontoContact.setPlayerId(Long.parseLong(internalBrontoField.getContent()));
                    break;
                case first_name:
                    break;
                case last_name:
                    break;
                case nickname:
                    break;
                case birthday:
                    break;
                case email_verification:
                    break;
                case language:
                    break;
                case avatar_id:
                    break;
                case level:
                    break;
                case currency:
                    break;
                case created_date:
                    break;
                case uuid:
                    break;
                default:
                    logger.warn("No matching field found for brontoContact");
                    break;
            }
        }
        return internalBrontoContact;
    }

    private List<InternalBrontoField> readFieldsFromBronto() {
        final SessionHeader sessionHeader = createBrontoSession();
        try {
            final FieldsFilter fieldsFilter = new FieldsFilter();

            final ReadFields readFields = new ReadFields();
            readFields.setFilter(fieldsFilter);
            final ReadFieldsResponse readFieldsResponse = brontoService().getBrontoSoapApiImplPort().readFields(readFields, sessionHeader);

            final List<FieldObject> fieldObjects = readFieldsResponse.getReturn();

            final List<InternalBrontoField> internalBrontoFields = new ArrayList<>();
            for (final FieldObject fieldObject : fieldObjects) {
                final InternalBrontoField internalBrontoField = new InternalBrontoField();
                internalBrontoField.setId(fieldObject.getId());
                internalBrontoField.setLabel(fieldObject.getLabel());

                final BrontoFieldName fieldName = BrontoFieldName.getFieldName(fieldObject.getName());
                final BrontoFieldType fieldType = BrontoFieldType.getFieldType(fieldObject.getType());
                if (fieldName == null) {
                    logger.info("Field or {} not found", fieldObject.getName());
                    continue;
                } else if (fieldType == null) {
                    logger.info("FieldType {} not found", fieldObject.getType());
                    continue;
                } else {
                    internalBrontoField.setName(fieldName);
                    internalBrontoField.setType(fieldType);
                }
                internalBrontoField.setVisibility(BrontoFieldVisibility.getVisibility(fieldObject.getVisibility()));
                internalBrontoFields.add(internalBrontoField);
            }
            return internalBrontoFields;
        } catch (final ApiException_Exception | MalformedURLException e) {
            logger.error("Cannot read fields from Bronto: {}", e.getMessage());
            throw new CommunicationException(e.getMessage(), e);
        }
    }

    private List<InternalBrontoWorkflow> readWorkflowsFromBronto() {
        final SessionHeader sessionHeader = createBrontoSession();
        try {
            final WorkflowFilter workflowFilter = new WorkflowFilter();

            final ReadWorkflows readWorkflows = new ReadWorkflows();
            readWorkflows.setFilter(workflowFilter);

            final List<WorkflowObject> workflowObjects = brontoService().getBrontoSoapApiImplPort().readWorkflows(readWorkflows, sessionHeader).getReturn();

            if (workflowObjects.isEmpty()) {
                throw new NotFoundException("No workflows found.");
            }

            final List<InternalBrontoWorkflow> internalBrontoWorkflows = new ArrayList<>();
            for (final WorkflowObject workflowObject : workflowObjects) {

                if (BrontoWorkflowName.getWorkflowName(workflowObject.getName()) == null) {
                    logger.info("Workflow {} found in Brontos system but not in our enum.", workflowObject.getName());
                    continue;
                }

                final InternalBrontoWorkflow internalBrontoWorkflow = new InternalBrontoWorkflow();
                internalBrontoWorkflow.setId(workflowObject.getId());
                internalBrontoWorkflow.setName(BrontoWorkflowName.getWorkflowName(workflowObject.getName()));
                internalBrontoWorkflow.setDescription(internalBrontoWorkflow.getDescription());
                internalBrontoWorkflow.setSiteId(workflowObject.getSiteId());
                internalBrontoWorkflow.setStatus(BrontoWorkflowStatus.getWorkflowStatus(workflowObject.getStatus()));
                internalBrontoWorkflows.add(internalBrontoWorkflow);
            }
            return internalBrontoWorkflows;
        } catch (final ApiException_Exception | MalformedURLException e) {
            logger.error("Cannot read workflows from: {}", e.getMessage(), e);
            throw new CommunicationException(e.getMessage(), e);
        }
    }
}
