package com.cs.administration.user;

import com.cs.administration.security.BackOfficeUser;
import com.cs.administration.security.CurrentUser;
import com.cs.audit.AuditService;
import com.cs.audit.UserActivityType;
import com.cs.payment.PaymentService;
import com.cs.security.AccessDeniedException;
import com.cs.security.InvalidSessionException;
import com.cs.security.SessionExpiredException;
import com.cs.user.Role;
import com.cs.user.RoleService;
import com.cs.user.SecurityRole;
import com.cs.user.User;
import com.cs.user.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.cs.persistence.Constants.BO_DEFAULT_SIZE;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.MediaType.ALL_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

/**
 * @author Hadi Movaghar
 */
@RestController
@RequestMapping(value = "/api/users", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
public class UserController {

    final AuditService auditService;
    final PaymentService paymentService;
    final RoleService roleService;
    final UserService userService;

    @Autowired
    public UserController(final AuditService auditService, final PaymentService paymentService, final RoleService roleService, final UserService userService) {
        this.auditService = auditService;
        this.paymentService = paymentService;
        this.roleService = roleService;
        this.userService = userService;
    }

    @RequestMapping(method = GET, value = "/login", consumes = ALL_VALUE)
    @ResponseStatus(OK)
    public void login() {
    }

    @RequestMapping(method = GET, value = "/login/success", consumes = ALL_VALUE)
    @ResponseStatus(OK)
    public UserDto loginSuccess(@CurrentUser final BackOfficeUser currentUser) {
        final User user = userService.getUser(currentUser.getId());
        auditService.trackUserActivity(user, UserActivityType.LOGIN);
        return new UserDto(user);
    }

    @RequestMapping(method = GET, value = "/login/failure", consumes = ALL_VALUE)
    @ResponseStatus(UNAUTHORIZED)
    public void loginFailure() {
    }

    @RequestMapping(method = POST, value = "/logout", consumes = ALL_VALUE)
    @ResponseStatus(OK)
    public void logout() {
    }

    @RequestMapping(method = GET, value = "/logout/success", consumes = ALL_VALUE)
    @ResponseStatus(OK)
    public void logoutSuccess() {
    }

    @RequestMapping(method = GET, value = "/session/expired", consumes = ALL_VALUE)
    @ResponseStatus(UNAUTHORIZED)
    public String sessionExpired() {
        throw new SessionExpiredException("Your session has expired");
    }

    @RequestMapping(method = GET, value = "/session/invalid", consumes = ALL_VALUE)
    @ResponseStatus(UNAUTHORIZED)
    public String invalidSession() {
        throw new InvalidSessionException("Your session is invalid");
    }

    @RequestMapping(method = POST)
    @ResponseStatus(CREATED)
    public UserDto createUser(@RequestBody(required = true) @Valid final UserDto user, @CurrentUser final BackOfficeUser currentUser) {
        final User u = userService.getUser(currentUser.getId());
        if (u.getRoles().get(0).getPk().getRole().getName() != SecurityRole.SUPER_USER) {
            throw new AccessDeniedException("User not allowed");
        }
        return new UserDto(userService.createUser(user.asUser(), user.getRole(), currentUser.getId()));
    }

    @RequestMapping(method = GET)
    @ResponseStatus(OK)
    public UserPageableDto searchUsers(@RequestParam(value = "userId", required = false) final Long userId,
                                       @RequestParam(value = "emailAddress", required = false) final String emailAddress,
                                       @RequestParam(value = "nickname", required = false) final String nickname,
                                       @RequestParam(value = "firstName", required = false) final String firstName,
                                       @RequestParam(value = "lastName", required = false) final String lastName,
                                       @RequestParam(value = "page", required = false, defaultValue = "0") final Integer page,
                                       @RequestParam(value = "size", required = false, defaultValue = BO_DEFAULT_SIZE) final Integer size) {

        if (userId != null) {
            return new UserPageableDto(new PageImpl<>(Collections.singletonList(userService.getUser(userId))));
        }

        final Page<User> users = userService.searchUsers(emailAddress, nickname, firstName, lastName, page, size);
        return new UserPageableDto(users);
    }

    @RequestMapping(method = GET, value = "/{userId}")
    @ResponseStatus(OK)
    public UserDto getUser(@PathVariable("userId") final Long userId) {
        final User user = userService.getUser(userId);
        return new UserDto(user);
    }

    @RequestMapping(method = PUT, value = "/{userId}")
    @ResponseStatus(OK)
    public UserDto updateUser(@PathVariable("userId") final Long userId, @RequestBody(required = true) @Valid final UserUpdateDto user,
                              @CurrentUser final BackOfficeUser currentUser) {
        final User u = userService.getUser(currentUser.getId());
        if (u.getRoles().get(0).getPk().getRole().getName() != SecurityRole.SUPER_USER) {
            throw new AccessDeniedException("User not allowed");
        }
        return new UserDto(userService.updateUser(userId, user.asUser(), user.getPassword(), user.getNewPassword(), user.getRole(), currentUser.getId()));
    }

    @RequestMapping(method = DELETE, value = "/{userId}")
    @ResponseStatus(OK)
    public UserDto deleteUser(@PathVariable("userId") final Long userId, @CurrentUser final BackOfficeUser currentUser) {
        final User u = userService.getUser(currentUser.getId());
        if (u.getRoles().get(0).getPk().getRole().getName() != SecurityRole.SUPER_USER) {
            throw new AccessDeniedException("User not allowed");
        }
        return new UserDto(userService.deleteUser(userId, currentUser.getId()));
    }

    @RequestMapping(method = GET, value = "/roles")
    @ResponseStatus(OK)
    public List<RoleDto> getAllRoles() {
        final List<Role> roles = roleService.getRoles();
        return convert(roles);
    }

    private List<RoleDto> convert(final List<Role> roles) {
        final List<RoleDto> roleDtos = new ArrayList<>(roles.size());
        for (final Role role : roles) {
            roleDtos.add(new RoleDto(role.getName()));
        }
        return roleDtos;
    }
}
