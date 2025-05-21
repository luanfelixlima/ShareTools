package com.fesa.sharetools.Controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request) {
        Object status = request.getAttribute("jakarta.servlet.error.status_code");

        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());

            if (statusCode == 404) {
                return "error/404"; // → src/main/resources/templates/error/404.html
            } else if (statusCode == 403) {
                return "error/403"; // → src/main/resources/templates/error/403.html
            } else if (statusCode == 500) {
                return "error/500"; // → src/main/resources/templates/error/500.html
            }
        }

        return "error/error"; // fallback
    }
}
