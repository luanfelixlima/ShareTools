package com.fesa.sharetools.Controller;

import com.fesa.sharetools.Model.Tool;
import com.fesa.sharetools.Model.User;
import com.fesa.sharetools.Model.Loan;
import com.fesa.sharetools.Service.ToolService;
import com.fesa.sharetools.Service.UserService;
import com.fesa.sharetools.Service.LoanService;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/tools")
public class ToolController {

    private final ToolService toolService;
    private final UserService userService;
    private final LoanService loanService;

    public ToolController(ToolService toolService, UserService userService, LoanService loanService) {
        this.toolService = toolService;
        this.userService = userService;
        this.loanService = loanService;
    }

    @GetMapping("/list")
    public String listTools(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByEmail(userDetails.getUsername()).orElseThrow();
        List<Tool> tools = toolService.findAllExceptOwner(user);
        model.addAttribute("tools", tools);
        return "tools_list";
    }

    // Endpoint para criar ferramenta
    @PostMapping
    public String createTool(@ModelAttribute Tool tool, @AuthenticationPrincipal UserDetails userDetails, RedirectAttributes redirectAttributes) {
        try {
            User user = userService.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));
            tool.setOwner(user);
            toolService.save(tool);
            redirectAttributes.addFlashAttribute("success", "Ferramenta '" + tool.getName() + "' cadastrada com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erro ao cadastrar ferramenta: " + e.getMessage());
        }
        return "redirect:/dashboard";
    }

    @GetMapping("/edit/{id}")
    public String showEditToolForm(@PathVariable("id") Long id, Model model, @AuthenticationPrincipal UserDetails userDetails, RedirectAttributes redirectAttributes) {
        try {
            User currentUser = userService.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

            Tool tool = toolService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Ferramenta não encontrada."));

            if (!tool.getOwner().getId().equals(currentUser.getId())) {
                throw new AccessDeniedException("Você não tem permissão para editar esta ferramenta.");
            }

            // Usamos findActiveLoanByTool que agora retorna o empréstimo se ele existe na tabela Loan
            Optional<Loan> activeLoan = loanService.findActiveLoanByTool(tool);
            // Se encontrou um empréstimo (ou seja, está na tabela Loan), preenche currentLoan para o Thymeleaf
            tool.setCurrentLoan(activeLoan.orElse(null));

            model.addAttribute("tool", tool);
            model.addAttribute("user", currentUser);

            return "edit-tool";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/dashboard";
        }
    }


    @PostMapping("/update")
    public String updateTool(@ModelAttribute Tool tool, @AuthenticationPrincipal UserDetails userDetails, RedirectAttributes redirectAttributes) {
        try {
            User currentUser = userService.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

            Tool existingTool = toolService.findById(tool.getId())
                    .orElseThrow(() -> new RuntimeException("Ferramenta não encontrada para atualização."));

            if (!existingTool.getOwner().getId().equals(currentUser.getId())) {
                throw new AccessDeniedException("Você não tem permissão para atualizar esta ferramenta.");
            }

            // --- Verificação de Lógica de Negócio para Ferramentas Emprestadas ---
            // Verifica se a ferramenta está atualmente na tabela Loan (emprestada)
            boolean isToolCurrentlyLoaned = loanService.isToolCurrentlyLoaned(existingTool);

            // Se a ferramenta está emprestada E o usuário tenta marcá-la como disponível=true, impeça.
            if (isToolCurrentlyLoaned && tool.isAvailable()) {
                redirectAttributes.addFlashAttribute("error", "Não é possível editar a ferramenta '" + existingTool.getName() + "'. Pois ela está emprestada no momento.");
                // Redireciona de volta para a página de edição para mostrar o erro no formulário.
                return "redirect:/tools/edit/" + existingTool.getId();
            }
            // --- Fim da Verificação ---

            existingTool.setName(tool.getName());
            existingTool.setDescription(tool.getDescription());
            existingTool.setAvailable(tool.isAvailable()); // Atualiza o status 'available' apenas se permitido

            toolService.save(existingTool);
            redirectAttributes.addFlashAttribute("success", "Ferramenta '" + existingTool.getName() + "' atualizada com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erro ao atualizar ferramenta: " + e.getMessage());
        }
        return "redirect:/dashboard";
    }

    @GetMapping("/delete/{id}")
    public String deleteTool(@PathVariable("id") Long id, @AuthenticationPrincipal UserDetails userDetails, RedirectAttributes redirectAttributes) {
        try {
            User currentUser = userService.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

            Tool toolToDelete = toolService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Ferramenta não encontrada para exclusão."));

            // Verifica se a ferramenta está atualmente na tabela Loan (emprestada) antes de permitir a exclusão
            boolean isToolCurrentlyLoaned = loanService.isToolCurrentlyLoaned(toolToDelete);
            if (isToolCurrentlyLoaned) {
                redirectAttributes.addFlashAttribute("error", "Não é possível excluir a ferramenta '" + toolToDelete.getName() + "', pois ela está emprestada no momento.");
                return "redirect:/dashboard";
            }

            if (!toolToDelete.getOwner().getId().equals(currentUser.getId())) {
                throw new AccessDeniedException("Você não tem permissão para excluir esta ferramenta.");
            }

            toolService.delete(id);
            redirectAttributes.addFlashAttribute("success", "Ferramenta '" + toolToDelete.getName() + "' excluída com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erro ao excluir ferramenta: " + e.getMessage());
        }
        return "redirect:/dashboard";
    }
}
