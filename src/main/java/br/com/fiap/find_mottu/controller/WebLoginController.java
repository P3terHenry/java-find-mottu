package br.com.fiap.find_mottu.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class WebLoginController {

    @GetMapping("/")
    public String redirectToHome() {
        return "redirect:/dashboard";
    }

    @GetMapping("/login")
    public ModelAndView viewLogin() {
        return new ModelAndView("/auth/login");
    }

    @GetMapping("/forbidden")
    public ModelAndView viewForbidden() {
        return new ModelAndView("/error/forbidden");
    }

    @GetMapping("/error")
    public ModelAndView viewError() {
        return new ModelAndView("/error/404");
    }
}
