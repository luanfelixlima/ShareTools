package com.fesa.sharetools.Controller;

import com.fesa.sharetools.Model.Tool;
import com.fesa.sharetools.Model.User;
import com.fesa.sharetools.Service.ToolService;
import com.fesa.sharetools.Service.UserService;
import com.fesa.sharetools.Service.LoanService; // Para verificar se a ferramenta está emprestada

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
@RequestMapping("/admin/tools") // Mapeamento base para todas as ações administrativas de ferramentas
public class AdminToolController {

    private final ToolService toolService;
    private final UserService userService;
    private final LoanService loanService; // Para verificar se a ferramenta está emprestada

    public AdminToolController(ToolService toolService, UserService userService, LoanService loanService) {
        this.toolService = toolService;
        this.userService = userService;
        this.loanService = loanService;
    }

    // Método para listar todas as ferramentas na tela de administração
    @GetMapping
    public String listAllTools(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        User userLogado = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));
        model.addAttribute("userLogado", userLogado); // Para o cabeçalho
        model.addAttribute("tools", toolService.findAll()); // Busca todas as ferramentas
        return "admin-tools"; // Nome do seu arquivo HTML
    }

    // Método para exibir o formulário de edição de ferramenta (admin)
    @GetMapping("/edit/{id}")
    public String showEditToolFormAdmin(@PathVariable("id") Long id, Model model, @AuthenticationPrincipal UserDetails userDetails, RedirectAttributes redirectAttributes) {
        try {
            User currentUser = userService.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

            Tool tool = toolService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Ferramenta não encontrada."));

            // Não precisa verificar se o usuário logado é o owner da ferramenta aqui,
            // pois é uma tela ADMIN, que pode editar qualquer ferramenta.
            // Apenas adicionamos o userLogado para o cabeçalho.
            model.addAttribute("userLogado", currentUser);
            model.addAttribute("tool", tool);

            // Adiciona a informação se a ferramenta está emprestada para desabilitar o checkbox
            boolean isToolCurrentlyLoaned = loanService.isToolCurrentlyLoaned(tool);
            if (isToolCurrentlyLoaned) {
                // Popula o campo transient 'currentLoan' para o Thymeleaf
                tool.setCurrentLoan(new com.fesa.sharetools.Model.Loan()); // Apenas um objeto não nulo
            } else {
                tool.setCurrentLoan(null);
            }

            return "edit-tool-admin"; // Um novo template para edição de admin, se quiser
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/tools";
        }
    }

    // Método para atualizar a ferramenta (admin)
    @PostMapping("/update")
    public String updateToolAdmin(@ModelAttribute Tool tool, @AuthenticationPrincipal UserDetails userDetails, RedirectAttributes redirectAttributes) {
        try {
            User currentUser = userService.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

            Tool existingTool = toolService.findById(tool.getId())
                    .orElseThrow(() -> new RuntimeException("Ferramenta não encontrada para atualização."));

            // A verificação de ownership não é necessária aqui, pois é um admin.
            // Apenas a verificação se a ferramenta está emprestada e não pode ser disponível.
            boolean isToolCurrentlyLoaned = loanService.isToolCurrentlyLoaned(existingTool);

            if (isToolCurrentlyLoaned && tool.isAvailable()) {
                redirectAttributes.addFlashAttribute("error", "Não é possível marcar a ferramenta '" + existingTool.getName() + "' como disponível, pois ela está emprestada no momento.");
                return "redirect:/admin/tools/edit/" + existingTool.getId(); // Redireciona de volta para a edição admin
            }

            existingTool.setName(tool.getName());
            existingTool.setDescription(tool.getDescription());
            existingTool.setAvailable(tool.isAvailable());

            toolService.save(existingTool);
            redirectAttributes.addFlashAttribute("success", "Ferramenta '" + existingTool.getName() + "' atualizada com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erro ao atualizar ferramenta: " + e.getMessage());
        }
        return "redirect:/admin/tools"; // Redireciona para a lista admin de ferramentas
    }

    // Método para excluir ferramenta (admin)
    @GetMapping("/delete/{id}")
    public String deleteToolAdmin(@PathVariable("id") Long id, @AuthenticationPrincipal UserDetails userDetails, RedirectAttributes redirectAttributes) {
        try {
            User currentUser = userService.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

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