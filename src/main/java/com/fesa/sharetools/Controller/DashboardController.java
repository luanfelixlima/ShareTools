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
public class DashboardController {

    private final UserService userService;
    private final ToolService toolService;

    public DashboardController(UserService userService, ToolService toolService) {
        this.userService = userService;
        this.toolService = toolService;
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByEmail(userDetails.getUsername()).orElseThrow();

        List<Tool> toolsFromOthers = toolService.getAvailableToolsFromOtherUsers(user);
        List<Tool> userTools = toolService.findByOwner(user);

        model.addAttribute("toolsFromOthers", toolsFromOthers);
        model.addAttribute("userTools", userTools);
        model.addAttribute("user", user);

        return "dashboard";
    }

    @GetMapping("/cadastros")
    public String showCadastroPage(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByEmail(userDetails.getUsername()).orElseThrow();
        model.addAttribute("user", user);
        model.addAttribute("newTool", new Tool());
        return "cadastros";
    }

    /*@PostMapping("/tools")
    public String createTool(@ModelAttribute Tool newTool, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByEmail(userDetails.getUsername()).orElseThrow();
        newTool.setOwner(user);
        toolService.save(newTool);
        return "redirect:/dashboard";
    }*/
}

