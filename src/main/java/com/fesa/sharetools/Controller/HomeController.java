package com.fesa.sharetools.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("")

public class HomeController {

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "redirect:/users/login"; // Redireciona para o novo
    }

    @GetMapping("/register")
    public String redirectToUserRegister() {
        return "redirect:/users/register";
    }

}
