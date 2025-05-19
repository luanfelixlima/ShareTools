package com.fesa.sharetools.Controller;

import com.fesa.sharetools.Model.Role;
import com.fesa.sharetools.Model.User;
import com.fesa.sharetools.Repository.RoleRepository;
import com.fesa.sharetools.Service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/users")
public class PublicUserController {

    private final UserService userService;
    private final RoleRepository roleRepository;

    public PublicUserController(UserService userService, RoleRepository roleRepository) {
        this.userService = userService;
        this.roleRepository = roleRepository;
    }

    @GetMapping("/register")
    public String showPublicRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "register"; // Você pode usar o mesmo "register.html"
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @PostMapping("/register")
    public String processPublicRegister(@ModelAttribute("user") User user, Model model) {
        if (userService.existsByEmail(user.getEmail())) {
            model.addAttribute("emailError", "Este e-mail já está em uso.");
            return "register"; // volta para a tela com erro
        }

        Role role = roleRepository.findById(2L)
                .orElseThrow(() -> new RuntimeException("Role USER (ID 2) não encontrada"));
        user.setRole(role);
        userService.save(user);
        return "redirect:/login";
    }

}
