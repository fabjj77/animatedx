package com.cs.administration.credit;

import com.cs.payment.CreditTransaction;
import com.cs.payment.CreditTransactionType;
import com.cs.payment.credit.CreditService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

import static com.cs.persistence.Constants.BO_DEFAULT_SIZE;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * @author Hadi Movaghar
 */
@RestController
@RequestMapping(value = "/api/credits", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
public class CreditController {

    private final CreditService creditService;

    @Autowired
    public CreditController(final CreditService creditService) {
        this.creditService = creditService;
    }

    @RequestMapping(method = GET)
    @ResponseStatus(OK)
    public CreditPageableDto getCreditTransactions(@RequestParam(value = "startDate", required = true) @DateTimeFormat(iso = ISO.DATE) final Date startDate,
                                                   @RequestParam(value = "endDate", required = true) @DateTimeFormat(iso = ISO.DATE) final Date endDate,
                                                   @RequestParam(value = "playerId", required = false) final Long playerId,
                                                   @RequestParam(value = "creditTransactionType", required = false) final CreditTransactionType creditTransactionType,
                                                   @RequestParam(value = "page", required = false, defaultValue = "0") final Integer page,
                                                   @RequestParam(value = "size", required = false, defaultValue = BO_DEFAULT_SIZE) final Integer size) {
        final Page<CreditTransaction> creditTransactions = creditService.getCreditTransactions(playerId, creditTransactionType, startDate, endDate, page, size);
        return new CreditPageableDto(creditTransactions);
    }
}
