package com.fesa.sharetools.Controller;

import com.fesa.sharetools.Model.Tool;
import com.fesa.sharetools.Model.User;
import com.fesa.sharetools.Service.ToolService;
import com.fesa.sharetools.Service.UserService;

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

    public ToolController(ToolService toolService, UserService userService) {
        this.toolService = toolService;
        this.userService = userService;
    }

    // Exemplo de listar todas as ferramentas (exceto do usuário logado)
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

            // Security check: ensure the current user owns the tool
            if (!tool.getOwner().getId().equals(currentUser.getId())) {
                throw new AccessDeniedException("Você não tem permissão para editar esta ferramenta.");
            }

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

            existingTool.setName(tool.getName());
            existingTool.setDescription(tool.getDescription());
            existingTool.setAvailable(tool.isAvailable());

            toolService.save(existingTool);
            redirectAttributes.addFlashAttribute("success", "Ferramenta '" + existingTool.getName() + "' atualizada com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erro ao atualizar ferramenta: " + e.getMessage());
        }
        return "redirect:/dashboard";
    }


    // Changed to @GetMapping to match the <a> tag behavior with onclick confirmation
    @GetMapping("/delete/{id}")
    public String deleteTool(@PathVariable("id") Long id, @AuthenticationPrincipal UserDetails userDetails, RedirectAttributes redirectAttributes) {
        try {
            User currentUser = userService.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

            Tool toolToDelete = toolService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Ferramenta não encontrada para exclusão."));

            // Security check: ensure the current user owns the tool
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