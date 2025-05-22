package com.fesa.sharetools.Controller;

import com.fesa.sharetools.Model.Tool;
import com.fesa.sharetools.Model.User;
import com.fesa.sharetools.Service.LoanService;
import com.fesa.sharetools.Service.ToolService;
import com.fesa.sharetools.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/loans")
public class LoanController {

    @Autowired
    private LoanService loanService;

    @Autowired
    private ToolService toolService;

    @Autowired
    private UserService userService;

    @PostMapping("/create")
    public String createLoan(@RequestParam Long toolId,
                             @AuthenticationPrincipal UserDetails userDetails,
                             RedirectAttributes redirectAttributes) {

        Tool tool = toolService.getById(toolId);
        User borrower = userService.getByEmail(userDetails.getUsername());
        User owner = tool.getOwner();

        if (owner.getId().equals(borrower.getId())) {
            redirectAttributes.addFlashAttribute("error", "Você não pode pegar emprestado sua própria ferramenta.");
            return "redirect:/dashboard";
        }

        try {
            loanService.createLoan(tool, owner, borrower);
            redirectAttributes.addFlashAttribute("success", "Ferramenta emprestada com sucesso!");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/dashboard";
    }

}

