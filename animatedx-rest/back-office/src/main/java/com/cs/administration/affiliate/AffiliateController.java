package com.cs.administration.affiliate;

import com.cs.report.ReportService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * @author Joakim Gottzén
 */
@RestController
@RequestMapping(value = "/api/affiliates", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
public class AffiliateController {

    private final ReportService reportService;

    @Autowired
    public AffiliateController(final ReportService reportService) {
        this.reportService = reportService;
    }

    @RequestMapping(method = POST, value = "/netrefer/registration")
    @ResponseStatus(OK)
    public void sendRegistrationUpdate(@RequestParam(value = "date", required = true) @DateTimeFormat(iso = ISO.DATE) final Date date) {
        reportService.sendPlayerRegistrationsToNetrefer(date);
    }

    @RequestMapping(method = POST, value = "/netrefer/activity")
    @ResponseStatus(OK)
    public void sendActivityUpdate(@RequestParam(value = "date", required = true) @DateTimeFormat(iso = ISO.DATE) final Date date) {
        reportService.sendPlayerActivityToNetrefer(date);
    }
}
