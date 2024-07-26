package ru.itmo.wp.web.page;

import ru.itmo.wp.model.domain.User;
import ru.itmo.wp.model.exception.ValidationException;
import ru.itmo.wp.model.service.UserService;
import ru.itmo.wp.web.annotation.Json;
import ru.itmo.wp.web.exception.RedirectException;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/** @noinspection unused*/
public class UsersPage {
    private final UserService userService = new UserService();

    private void action(HttpServletRequest request, Map<String, Object> view) {
        // No operations.
    }

    protected User claimUser(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            request.getSession().setAttribute("message", "You need be authorized!");
            throw new RedirectException("/index");
        }
        return user;
    }

    private void findAll(HttpServletRequest request, Map<String, Object> view) {
        view.put("users", userService.findAll());
    }

    private void findUser(HttpServletRequest request, Map<String, Object> view) {
        view.put("user", userService.find(Long.parseLong(request.getParameter("userId"))));
    }

    @Json
    private void setAdmin(HttpServletRequest request, Map<String, Object> view) throws ValidationException {
        changeStatus(request, true, view);
    }

    @Json
    private void unsetAdmin(HttpServletRequest request, Map<String, Object> view) throws ValidationException {
        changeStatus(request, false, view);
    }

    private void changeStatus(HttpServletRequest request, boolean isAdmin, Map<String, Object> view) throws ValidationException {
        userService.validateUserIsAdmin(claimUser(request));
        User user = userService.validateUserIdAndGetUser(request.getParameter("id"));
        userService.setAdmin(user, isAdmin);
        view.put("id", user.getId());
        view.put("admin", isAdmin);
    }
}
