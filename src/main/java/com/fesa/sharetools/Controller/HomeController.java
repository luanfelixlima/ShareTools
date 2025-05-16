package com.fesa.sharetools.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("")

public class HomeController {

    @GetMapping("/")
    public String index() {
        return "index"; // maps to templates/index.html
    }

    @GetMapping("/register")
    public String showToolRegistrationForm() {
        return "register"; // maps to templates/register.html
    }
}
