package ru.itmo.wp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.itmo.wp.domain.User;

@Controller
public class UserPage extends Page {

    @GetMapping({
            "/user/{id:[A-Za-z0-9]+}",
            "/user/{invalidId}",
            "/user/"
    })
    public String users(Model model, @PathVariable(value = "id", required = false) String id) {
        User user = idToUser(id);
        model.addAttribute("user", user);
        return "UserPage";
    }
}