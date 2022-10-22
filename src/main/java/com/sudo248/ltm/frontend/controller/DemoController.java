package com.sudo248.ltm.frontend.controller;

import com.sudo248.ltm.api.model.entity.user.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.List;

@Controller
public class DemoController {
    private static List<User> users = new ArrayList<>();

    static {
        users.add(new User(1L,"Duong", "24/08/2001"));
        users.add(new User(2L, "Oanh", "03/09/2001"));
    }

    @RequestMapping(value = { "/", "/index" }, method = RequestMethod.GET)
    public String index(Model model) {
        String message = "Hello Spring Boot + JSP";
        model.addAttribute("message", message);
        return "index";
    }

    @RequestMapping(value = { "/usersList" }, method = RequestMethod.GET)
    public String viewPersonList(Model model) {

        model.addAttribute("persons", users);

        return "usersList";
    }
}
