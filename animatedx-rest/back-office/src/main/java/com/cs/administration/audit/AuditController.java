package com.cs.administration.audit;

import com.cs.audit.AuditService;
import com.cs.audit.PlayerActivity;
import com.cs.audit.PlayerActivityType;
import com.cs.audit.UserActivity;
import com.cs.audit.UserActivityType;
import com.cs.persistence.CommunicationException;
import com.cs.report.PlayerAuditReport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import static com.cs.persistence.Constants.BO_DEFAULT_SIZE;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * @author Hadi Movaghar
 */
@RestController
@RequestMapping(value = "/api/audit", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
public class AuditController {
    private final AuditService auditService;

    @Autowired
    public AuditController(final AuditService auditService) {
        this.auditService = auditService;
    }

    @RequestMapping(method = GET, value = "/player")
    @ResponseStatus(OK)
    public PlayerActivitiesPageableDto getPlayerActivities(@RequestParam(value = "id", required = false) final Long playerId,
                                                           @RequestParam(value = "activityType", required = false) final PlayerActivityType playerActivityType,
                                                           @RequestParam(value = "startDate", required = true) @DateTimeFormat(iso = ISO.DATE) final Date startDate,
                                                           @RequestParam(value = "endDate", required = true) @DateTimeFormat(iso = ISO.DATE) final Date endDate,
                                                           @RequestParam(value = "page", required = false, defaultValue = "0") final Integer page,
                                                           @RequestParam(value = "size", required = false, defaultValue = BO_DEFAULT_SIZE) final Integer size) {
        final Page<PlayerActivity> playerActivities = auditService.getPlayerActivities(playerId, playerActivityType, startDate, endDate, page, size);
        return new PlayerActivitiesPageableDto(playerActivities);
    }

    @RequestMapping(method = GET, value = "/report", produces = "text/csv")
    @ResponseStatus(OK)
    public void getPlayerActivitiesReport(@RequestParam(value = "playerId", required = false) final Long playerId,
                                          @RequestParam(value = "activityType", required = false) final PlayerActivityType playerActivityType,
                                          @RequestParam(value = "startDate", required = true) @DateTimeFormat(iso = ISO.DATE) final Date startDate,
                                          @RequestParam(value = "endDate", required = true) @DateTimeFormat(iso = ISO.DATE) final Date endDate,
                                          final HttpServletResponse response) {
        final List<PlayerActivity> playerActivities = auditService.getPlayerActivities(playerId, playerActivityType, startDate, endDate);
        try {
            response.getOutputStream().write(new PlayerAuditReport(playerActivities).getSummaryFile().getBytes());
        } catch (final IOException e) {
            throw new CommunicationException("Error while generation audit report.", e);
        }
    }

    @RequestMapping(method = GET, value = "/user")
    @ResponseStatus(OK)
    public UserActivitiesPageableDto getUserActivities(@RequestParam(value = "id", required = false) final Long userId,
                                                       @RequestParam(value = "activityType", required = false) final UserActivityType activityType,
                                                       @RequestParam(value = "startDate", required = true) @DateTimeFormat(iso = ISO.DATE) final Date startDate,
                                                       @RequestParam(value = "endDate", required = true) @DateTimeFormat(iso = ISO.DATE) final Date endDate,
                                                       @RequestParam(value = "page", required = false, defaultValue = "0") final Integer page,
                                                       @RequestParam(value = "size", required = false, defaultValue = BO_DEFAULT_SIZE) final Integer size) {
        final Page<UserActivity> userActivities = auditService.getUserActivities(userId, activityType, startDate, endDate, page, size);
        return new UserActivitiesPageableDto(userActivities);
    }
}
