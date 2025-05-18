package com.fesa.sharetools.Controller;

import com.fesa.sharetools.Model.User;
import com.fesa.sharetools.Model.Role;
import com.fesa.sharetools.Service.UserService;
import com.fesa.sharetools.Repository.RoleRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RequestMapping("/users")
@Controller
public class UserController {

    private final UserService userService;
    private final RoleRepository roleRepository;

    public UserController(UserService userService, RoleRepository roleRepository) {
        this.userService = userService;
        this.roleRepository = roleRepository;
    }

    // Comentado pois não é necessário
 /*   @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUserForm(@ModelAttribute("user") User user) {
        Role role = roleRepository.findById(2L)
                .orElseThrow(() -> new RuntimeException("Role padrão (ID 2) não encontrada"));
        user.setRole(role);
        userService.save(user);
        return "redirect:/login";
    }

    // Se quiser manter API REST separada do formulário HTML
    @PostMapping(path = "/register", consumes = "application/json", produces = "application/json")
    @ResponseBody
    public ResponseEntity<User> registerUserApi(@RequestBody User user) {
        Role role = roleRepository.findById(2L)
                .orElseThrow(() -> new RuntimeException("Role padrão (ID 2) não encontrada"));
        user.setRole(role);
        User created = userService.save(user);
        return ResponseEntity.ok(created);
    }
*/
    @GetMapping
    public String listUsers(Model model) {
        List<User> users = userService.findAll();
        model.addAttribute("users", users);
        return "users";
    }


}
