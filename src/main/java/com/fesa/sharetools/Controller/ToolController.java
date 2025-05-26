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
        List<Tool> tools = toolService.findAllExceptOwner(user); // Corrected: tools variable initialized here
        model.addAttribute("tools", tools);
        return "tools_list";  // template HTML para listar ferramentas
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
        return "redirect:/dashboard"; // Redirect to dashboard after creation
    }

    /**
     * Endpoint para exibir o formulário de edição de uma ferramenta.
     * Garante que apenas o proprietário da ferramenta possa acessá-la para edição.
     * @param id O ID da ferramenta a ser editada.
     * @param model O objeto Model para passar dados para a view.
     * @param userDetails Os detalhes do usuário autenticado.
     * @param redirectAttributes Usado para adicionar mensagens flash em caso de erro ou sucesso.
     * @return O nome do template HTML para edição ou um redirecionamento para o dashboard.
     */
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
            model.addAttribute("user", currentUser); // Adicionado: Passa o objeto 'user' para o modelo

            return "edit-tool"; // Nome do template HTML para edição (edit-tool.html)
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/dashboard"; // Redireciona para o dashboard em caso de erro ou acesso negado
        }
    }

    /**
     * Endpoint para processar a atualização de uma ferramenta.
     * Garante que apenas o proprietário da ferramenta possa atualizá-la.
     * @param tool O objeto Tool com os dados atualizados do formulário.
     * @param userDetails Os detalhes do usuário autenticado.
     * @param redirectAttributes Usado para adicionar mensagens flash em caso de erro ou sucesso.
     * @return Um redirecionamento para o dashboard após a atualização.
     */
    @PostMapping("/update")
    public String updateTool(@ModelAttribute Tool tool, @AuthenticationPrincipal UserDetails userDetails, RedirectAttributes redirectAttributes) {
        try {
            User currentUser = userService.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

            // Fetch the existing tool from the database to ensure we don't overwrite owner or other critical data
            Tool existingTool = toolService.findById(tool.getId())
                    .orElseThrow(() -> new RuntimeException("Ferramenta não encontrada para atualização."));

            // Security check: ensure the current user owns the tool being updated
            if (!existingTool.getOwner().getId().equals(currentUser.getId())) {
                throw new AccessDeniedException("Você não tem permissão para atualizar esta ferramenta.");
            }

            // Update only the allowed fields (name, description, available)
            existingTool.setName(tool.getName());
            existingTool.setDescription(tool.getDescription());
            existingTool.setAvailable(tool.isAvailable()); // Assuming 'available' can also be edited

            toolService.save(existingTool); // Save the updated existing tool
            redirectAttributes.addFlashAttribute("success", "Ferramenta '" + existingTool.getName() + "' atualizada com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erro ao atualizar ferramenta: " + e.getMessage());
        }
        return "redirect:/dashboard"; // Redireciona para o dashboard após a atualização
    }
}
