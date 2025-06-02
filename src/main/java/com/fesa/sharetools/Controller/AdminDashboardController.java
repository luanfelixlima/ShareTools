package com.fesa.sharetools.Controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.fesa.sharetools.Dto.UserToolCountDto; // Precisaremos criar este DTO
import com.fesa.sharetools.Model.Tool;
import com.fesa.sharetools.Model.User;
import com.fesa.sharetools.Service.LoanService;
import com.fesa.sharetools.Service.ToolService;
import com.fesa.sharetools.Service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.Comparator;

// ... (código do AdminToolController existente) ...

@Controller
@RequestMapping("/admin/dashboard")
public class AdminDashboardController { // Ou

    private final ToolService toolService;
    private final UserService userService;
    private final LoanService loanService;

    public AdminDashboardController(ToolService toolService, UserService userService, LoanService loanService) {
        this.toolService = toolService;
        this.userService = userService;
        this.loanService = loanService;
    }

    // ... (métodos existentes como listAllTools, showEditToolFormAdmin, etc.) ...

    @GetMapping("") // Mapeado para /admin/dashboard (devido ao @RequestMapping da classe)
    public String showAdminDashboard(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        User userLogado = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));
        model.addAttribute("userLogado", userLogado);

        List<User> allUsers = userService.findAll();
        List<Tool> allTools = toolService.findAll();

        // Processar dados para o gráfico: Contar ferramentas por usuário
        // Usaremos um DTO para facilitar a passagem para o Thymeleaf e JavaScript
        List<UserToolCountDto> userToolCounts = allUsers.stream()
                .map(user -> {
                    long toolCount = allTools.stream()
                            .filter(tool -> tool.getOwner() != null && tool.getOwner().getId().equals(user.getId()))
                            .count();
                    return new UserToolCountDto(user.getName(), toolCount);
                })
                .filter(dto -> dto.getToolCount() > 0) // Opcional: mostrar apenas usuários com ferramentas
                .sorted(Comparator.comparingLong(UserToolCountDto::getToolCount).reversed()) // Opcional: ordenar
                .collect(Collectors.toList());

        model.addAttribute("userToolCounts", userToolCounts);
        model.addAttribute("pageTitle", "Dashboard Administrativo"); // Para o título da página

        return "admin-dashboard"; // Novo arquivo HTML: admin-dashboard.html
    }
}