package com.fesa.sharetools.Controller;

import com.fesa.sharetools.Dto.UserToolCountDto;
import com.fesa.sharetools.Model.Role;
import com.fesa.sharetools.Model.Tool;
import com.fesa.sharetools.Model.User;
import com.fesa.sharetools.Repository.RoleRepository;
import com.fesa.sharetools.Service.LoanService;
import com.fesa.sharetools.Service.ToolService;
import com.fesa.sharetools.Service.UserService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin") // Mapeamento base para todas as ações administrativas
public class AdminController {

    private final UserService userService;
    private final RoleRepository roleRepository;
    private final ToolService toolService;
    private final LoanService loanService;

    public AdminController(UserService userService, RoleRepository roleRepository, ToolService toolService, LoanService loanService) {
        this.userService = userService;
        this.roleRepository = roleRepository;
        this.toolService = toolService;
        this.loanService = loanService;
    }

    // --- Métodos do AdminDashboardController ---
    @GetMapping("/dashboard")
    public String showAdminDashboard(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        User userLogado = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));
        model.addAttribute("userLogado", userLogado);

        List<User> allUsers = userService.findAll();
        List<Tool> allTools = toolService.findAll();

        List<UserToolCountDto> userToolCounts = allUsers.stream()
                .map(user -> {
                    long toolCount = allTools.stream()
                            .filter(tool -> tool.getOwner() != null && tool.getOwner().getId().equals(user.getId()))
                            .count();
                    return new UserToolCountDto(user.getName(), toolCount);
                })
                .filter(dto -> dto.getToolCount() > 0)
                .sorted(Comparator.comparingLong(UserToolCountDto::getToolCount).reversed())
                .collect(Collectors.toList());

        model.addAttribute("userToolCounts", userToolCounts);
        model.addAttribute("pageTitle", "Dashboard Administrativo");
        return "admin-dashboard";
    }

    // --- Métodos do AdminUserController ---
    @GetMapping("/users/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "register"; // Assumindo que esta tela de registro é para admins criarem usuários
        // Se for para usuários se auto-registrarem, talvez deva estar fora do /admin
    }

    @PostMapping("/users/register")
    public String registerUserForm(@ModelAttribute("user") User user) {
        // No contexto de um admin registrando um usuário, ele poderia definir a role.
        // Se a intenção é que um usuário comum se registre com role padrão,
        // esta lógica pode precisar de ajuste ou o endpoint ser movido.
        // Por ora, mantendo a lógica original de atribuir role ID 2.
        Role role = roleRepository.findById(2L) // Default role (e.g., USER)
                .orElseThrow(() -> new RuntimeException("Role padrão (ID 2) não encontrada"));
        user.setRole(role);
        userService.save(user);
        // Se um admin registra, talvez redirecionar para a lista de usuários
        return "redirect:/admin/users"; // ou "redirect:/login" dependendo do fluxo
    }

    // API para registro de usuário (se necessário no contexto admin)
    @PostMapping(path = "/users/register", consumes = "application/json", produces = "application/json")
    @ResponseBody
    public ResponseEntity<User> registerUserApi(@RequestBody User user) {
        Role role = roleRepository.findById(2L)
                .orElseThrow(() -> new RuntimeException("Role padrão (ID 2) não encontrada"));
        user.setRole(role);
        User created = userService.save(user);
        return ResponseEntity.ok(created);
    }

    @GetMapping("/users")
    public String listUsers(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        User userLogado = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));
        model.addAttribute("userLogado", userLogado); // Para o cabeçalho

        List<User> users = userService.findAll();
        model.addAttribute("users", users);
        return "users"; // HTML para listar usuários
    }

    @GetMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes, @AuthenticationPrincipal UserDetails userDetailsPrincipal) {
        String loggedInEmail = userDetailsPrincipal.getUsername();
        User loggedInUser = userService.findByEmail(loggedInEmail)
                .orElseThrow(() -> new RuntimeException("Usuário logado não encontrado"));

        if (loggedInUser.getId().equals(id)) {
            redirectAttributes.addFlashAttribute("error", "❌ Você não pode excluir a si mesmo.");
            return "redirect:/admin/users";
        }

        User userToDelete = userService.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário a ser deletado não encontrado"));

        if ("ADMIN".equals(userToDelete.getRole().getName())) {
            long totalAdmins = userService.countByRoleName("ADMIN");
            if (totalAdmins <= 1) {
                redirectAttributes.addFlashAttribute("error", "❌ Não é possível excluir o último administrador.");
                return "redirect:/admin/users";
            }
        }

        userService.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "✅ Usuário excluído com sucesso.");
        return "redirect:/admin/users";
    }

    @PostMapping("/users/update")
    public String updateUser(@ModelAttribute("user") User user, RedirectAttributes redirectAttributes) {
        try {
            User existingUser = userService.findById(user.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado com ID: " + user.getId()));

            existingUser.setName(user.getName());
            existingUser.setEmail(user.getEmail());
            existingUser.setPhone(user.getPhone());

            Role role = roleRepository.findById(user.getRole().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Role inválida: " + user.getRole().getId()));
            existingUser.setRole(role);

            userService.save(existingUser);
            redirectAttributes.addFlashAttribute("success", "✅ Usuário atualizado com sucesso.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", "❌ Erro ao atualizar usuário: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/users/edit/{id}")
    public String showEditUserForm(@PathVariable Long id, Model model, @AuthenticationPrincipal UserDetails userDetails) {
        User userLogado = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));
        model.addAttribute("userLogado", userLogado); // Para o cabeçalho

        User user = userService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID inválido: " + id));
        model.addAttribute("user", user);
        model.addAttribute("roles", roleRepository.findAll());
        return "edit-user"; // HTML para editar usuário
    }


    // --- Métodos do AdminToolController ---
    @GetMapping("/tools")
    public String listAllTools(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        User userLogado = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));
        model.addAttribute("userLogado", userLogado);
        model.addAttribute("tools", toolService.findAll());
        return "admin-tools";
    }

    @GetMapping("/tools/edit/{id}")
    public String showEditToolFormAdmin(@PathVariable("id") Long id, Model model, @AuthenticationPrincipal UserDetails userDetails, RedirectAttributes redirectAttributes) {
        try {
            User currentUser = userService.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));
            model.addAttribute("userLogado", currentUser);

            Tool tool = toolService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Ferramenta não encontrada."));
            model.addAttribute("tool", tool);

            boolean isToolCurrentlyLoaned = loanService.isToolCurrentlyLoaned(tool);
            if (isToolCurrentlyLoaned) {
                tool.setCurrentLoan(new com.fesa.sharetools.Model.Loan()); // Placeholder for Thymeleaf check
            } else {
                tool.setCurrentLoan(null);
            }
            return "edit-tool-admin";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/tools";
        }
    }

    @PostMapping("/tools/update")
    public String updateToolAdmin(@ModelAttribute Tool tool, @AuthenticationPrincipal UserDetails userDetails, RedirectAttributes redirectAttributes) {
        try {
            // User currentUser = userService.findByEmail(userDetails.getUsername()) // Não estritamente necessário para lógica de update de admin
            //         .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

            Tool existingTool = toolService.findById(tool.getId())
                    .orElseThrow(() -> new RuntimeException("Ferramenta não encontrada para atualização."));

            boolean isToolCurrentlyLoaned = loanService.isToolCurrentlyLoaned(existingTool);

            if (isToolCurrentlyLoaned && tool.isAvailable()) {
                redirectAttributes.addFlashAttribute("error", "Não é possível marcar a ferramenta '" + existingTool.getName() + "' como disponível, pois ela está emprestada no momento.");
                return "redirect:/admin/tools/edit/" + existingTool.getId();
            }

            existingTool.setName(tool.getName());
            existingTool.setDescription(tool.getDescription());
            existingTool.setAvailable(tool.isAvailable());
            // O admin pode mudar o dono da ferramenta? Se sim, adicionar lógica para `existingTool.setOwner()`.
            // Por ora, o formulário não parece permitir isso, então mantemos como está.

            toolService.save(existingTool);
            redirectAttributes.addFlashAttribute("success", "Ferramenta '" + existingTool.getName() + "' atualizada com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erro ao atualizar ferramenta: " + e.getMessage());
        }
        return "redirect:/admin/tools";
    }

    @GetMapping("/tools/delete/{id}")
    public String deleteToolAdmin(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        // @AuthenticationPrincipal UserDetails userDetails - não usado aqui, pode remover se não houver auditoria ou lógica específica.
        try {
            Tool toolToDelete = toolService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Ferramenta não encontrada para exclusão."));

            boolean isToolCurrentlyLoaned = loanService.isToolCurrentlyLoaned(toolToDelete);
            if (isToolCurrentlyLoaned) {
                redirectAttributes.addFlashAttribute("error", "Não é possível excluir a ferramenta '" + toolToDelete.getName() + "', pois ela está emprestada no momento.");
                return "redirect:/admin/tools";
            }

            toolService.delete(id);
            redirectAttributes.addFlashAttribute("success", "Ferramenta '" + toolToDelete.getName() + "' excluída com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erro ao excluir ferramenta: " + e.getMessage());
        }
        return "redirect:/admin/tools";
    }
}