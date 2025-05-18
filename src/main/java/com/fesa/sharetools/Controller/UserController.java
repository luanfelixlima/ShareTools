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


@RequestMapping("/admin/users")
@Controller
public class UserController {

    private final UserService userService;
    private final RoleRepository roleRepository;

    public UserController(UserService userService, RoleRepository roleRepository) {
        this.userService = userService;
        this.roleRepository = roleRepository;
    }

    @GetMapping("/register")
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

    @GetMapping
    public String listUsers(Model model) {
        List<User> users = userService.findAll();
        model.addAttribute("users", users);
        return "users";
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteById(id);
        return "redirect:/admin/users";
    }

    @PostMapping("/update")
    public String updateUser(@ModelAttribute("user") User user) {
        // Busca o usuário original do banco
        User existingUser = userService.findById(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado com ID: " + user.getId()));

        // Atualiza os dados permitidos
        existingUser.setName(user.getName());
        existingUser.setEmail(user.getEmail());
        existingUser.setPhone(user.getPhone());

        // Atualiza o cargo (Role)
        Role role = roleRepository.findById(user.getRole().getId())
                .orElseThrow(() -> new IllegalArgumentException("Role inválido: " + user.getRole().getId()));
        existingUser.setRole(role);

        // Salva o usuário atualizado (sem sobrescrever a senha)
        userService.save(existingUser);

        return "redirect:/admin/users;";
    }


    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        User user = userService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID inválido: " + id));
        model.addAttribute("user", user);
        model.addAttribute("roles", roleRepository.findAll());
        return "edit-user";
    }

}
