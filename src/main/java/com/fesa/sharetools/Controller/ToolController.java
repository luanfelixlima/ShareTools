package com.fesa.sharetools.Controller;

import com.fesa.sharetools.Model.Tool;
import com.fesa.sharetools.Model.User;
import com.fesa.sharetools.Service.ToolService;
import com.fesa.sharetools.Service.UserService;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        return "tools_list";  // template HTML para listar ferramentas
    }

    // Endpoint para criar ferramenta em outro path, se quiser
    @PostMapping("/create")
    public String createTool(@ModelAttribute Tool tool, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByEmail(userDetails.getUsername()).orElseThrow();
        tool.setOwner(user);
        toolService.save(tool);
        return "redirect:/tools/list";

    }
}
