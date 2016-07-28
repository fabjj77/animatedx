package com.cs.administration.agreement;

import com.cs.administration.security.BackOfficeUser;
import com.cs.administration.security.CurrentUser;
import com.cs.agreement.TermsAndConditionsVersionService;
import com.cs.user.User;
import com.cs.user.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

/**
 * @author Hadi Movaghar
 */
@RestController
@RequestMapping(value = "/api/agreement", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
public class AgreementController {

    private final TermsAndConditionsVersionService termsAndConditionsVersionService;
    private final UserService userService;

    @Autowired
    public AgreementController(final TermsAndConditionsVersionService termsAndConditionsVersionService, final UserService userService) {
        this.termsAndConditionsVersionService = termsAndConditionsVersionService;
        this.userService = userService;
    }

    @RequestMapping(method = PUT, value = "/{id}")
    @ResponseStatus(OK)
    public TermsAndConditionsDto activateTermsAndConditions(@PathVariable("id") final Long id, @CurrentUser final BackOfficeUser currentUser) {
        final User user = userService.getUser(currentUser.getId());
        return new TermsAndConditionsDto(termsAndConditionsVersionService.activateTermsAndConditions(id, user));
    }

    @RequestMapping(method = POST)
    @ResponseStatus(CREATED)
    public TermsAndConditionsDto createTermsAndConditions(@RequestBody(required = true) @Valid final TermsAndConditionsDto termsAndConditionsDto,
                                                     @CurrentUser final BackOfficeUser currentUser) {
        final User user = userService.getUser(currentUser.getId());
        return new TermsAndConditionsDto(termsAndConditionsVersionService.createTermsAndConditions(termsAndConditionsDto.getVersion(), user));
    }
}
