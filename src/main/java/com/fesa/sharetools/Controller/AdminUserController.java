package com.fesa.sharetools.Controller;

import com.fesa.sharetools.Model.User;
import com.fesa.sharetools.Model.Role;
import com.fesa.sharetools.Service.UserService;
import com.fesa.sharetools.Repository.RoleRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.util.List;


@RequestMapping("/admin/users")
@Controller
public class  AdminUserController {

    private final UserService userService;
    private final RoleRepository roleRepository;

    public AdminUserController(UserService userService, RoleRepository roleRepository) {
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
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        // 1. Pega email do usuário logado
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String loggedInEmail = (principal instanceof UserDetails)
                ? ((UserDetails) principal).getUsername()
                : principal.toString();

        // 2. Busca usuário logado pelo email
        User loggedInUser = userService.findByEmail(loggedInEmail)
                .orElseThrow(() -> new RuntimeException("Usuário logado não encontrado"));

        // 3. Impede que o próprio usuário se delete
        if (loggedInUser.getId().equals(id)) {
            redirectAttributes.addFlashAttribute("error", "❌ Você não pode excluir a si mesmo.");
            return "redirect:/admin/users";
        }

        // 4. Impede que o último ADMIN seja excluído
        User userToDelete = userService.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário a ser deletado não encontrado"));

        if ("ADMIN".equals(userToDelete.getRole().getName())) {
            long totalAdmins = userService.countByRoleName("ADMIN");
            if (totalAdmins <= 1) {
                redirectAttributes.addFlashAttribute("error", "❌ Não é possível excluir o último administrador.");
                return "redirect:/admin/users";
            }
        }

        // 5. Exclui normalmente
        userService.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "✅ Usuário excluído com sucesso.");
        return "redirect:/admin/users";
    }

    @PostMapping("/update")
    public String updateUser(@ModelAttribute("user") User user) {
        User existingUser = userService.findById(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado com ID: " + user.getId()));

        existingUser.setName(user.getName());
        existingUser.setEmail(user.getEmail());
        existingUser.setPhone(user.getPhone());

        Role role = roleRepository.findById(user.getRole().getId())
                .orElseThrow(() -> new IllegalArgumentException("Role inválido: " + user.getRole().getId()));
        existingUser.setRole(role);

        userService.save(existingUser);

        return "redirect:/admin/users";
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