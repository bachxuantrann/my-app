package bachtx.myapp.sso_service.controller;

import bachtx.myapp.sso_service.security.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProfileController {

    @GetMapping("/profile")
    public String profile(Model model, Authentication authentication) {
        model.addAttribute("username", authentication.getName());
        // Lấy email nếu principal là CustomUserDetails
        if (authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            model.addAttribute("email", userDetails.getEmail());
        }
        return "profile";
    }
}
