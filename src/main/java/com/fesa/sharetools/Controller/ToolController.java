package com.fesa.sharetools.Controller;

import com.fesa.sharetools.Model.Tool;
import com.fesa.sharetools.Repository.ToolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/tools")
public class ToolController {

    @Autowired
    private ToolRepository toolRepository;

    @PostMapping("/save")
    public String saveTool(@ModelAttribute Tool tool) {
        toolRepository.save(tool);
        return "redirect:/";
    }
}
