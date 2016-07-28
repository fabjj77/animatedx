package com.cs.administration.email;

import com.cs.administration.security.BackOfficeUser;
import com.cs.administration.security.CurrentUser;
import com.cs.messaging.bronto.BrontoService;
import com.cs.messaging.bronto.InternalBrontoContact;
import com.cs.messaging.email.BrontoContact;
import com.cs.messaging.email.BrontoContactFilter;
import com.cs.user.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

/**
 * @author Omid Alaepour
 */
@RestController
@RequestMapping(value = "/api/email", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
public class EmailController {
    private final BrontoService brontoService;

    @Autowired
    public EmailController(final BrontoService brontoService) {
        this.brontoService = brontoService;
    }

    @RequestMapping(method = PUT)
    @ResponseStatus(OK)
    public List<BrontoContactDto> updateContacts(@CurrentUser final BackOfficeUser user) {
        return convertEmailContacts(brontoService.updateContactsInDatabase(new User(user.getId())));
    }

    @RequestMapping(method = PUT, value = "/fields")
    @ResponseStatus(OK)
    public void updateFields(@CurrentUser final BackOfficeUser user) {
        brontoService.updateFieldsInDatabase();
    }

    @RequestMapping(method = PUT, value = "/workflows")
    @ResponseStatus(OK)
    public void updateWorkflows(@CurrentUser final BackOfficeUser user) {
        brontoService.updateWorkflowInDatabase();
    }

    @RequestMapping(method = GET)
    @ResponseStatus(OK)
    public List<BrontoContactDto> getContacts(@RequestParam(value = "filter", required = false) BrontoContactFilter brontoContactFilter) {
        if (brontoContactFilter == null) {
            brontoContactFilter = BrontoContactFilter.NONE;
        }
       return convert(brontoService.readContactsFromBronto(brontoContactFilter));
    }

    private List<BrontoContactDto> convert(final List<InternalBrontoContact> internalBrontoContactList) {
        final List<BrontoContactDto> brontoContactDtos = new ArrayList<>();
        for (final InternalBrontoContact internalBrontoContact : internalBrontoContactList) {
            brontoContactDtos.add(new BrontoContactDto(internalBrontoContact));
        }
        return brontoContactDtos;
    }

    private List<BrontoContactDto> convertEmailContacts(final List<BrontoContact> brontoContacts) {
        final List<BrontoContactDto> brontoContactDtos = new ArrayList<>();
        for (final BrontoContact brontoContact : brontoContacts) {
            brontoContactDtos.add(new BrontoContactDto(brontoContact));
        }
        return brontoContactDtos;
    }
}
