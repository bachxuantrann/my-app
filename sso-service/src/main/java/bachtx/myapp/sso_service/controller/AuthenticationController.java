package bachtx.myapp.sso_service.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import bachtx.myapp.sso_service.service.UserService;

@Controller
@RequiredArgsConstructor
@Slf4j
public class AuthenticationController {

    private final UserService userService;

    @GetMapping("/login")

    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @PostMapping("/register")
    public String processRegister(@ModelAttribute bachtx.myapp.sso_service.dto.request.RegisterRequest request, org.springframework.ui.Model model) {
        try {
            userService.registerUser(request);
            return "redirect:/login?registered";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }

    @GetMapping("/forgot-password")
    public String forgotPassword() {
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@ModelAttribute bachtx.myapp.sso_service.dto.request.ForgotPasswordRequest request, org.springframework.ui.Model model) {
        try {
            userService.processForgotPassword(request.getEmail());
            return "redirect:/login?reset";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "forgot-password";
        }
    }
}
