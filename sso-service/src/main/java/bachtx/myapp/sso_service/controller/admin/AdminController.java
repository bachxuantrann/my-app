package bachtx.myapp.sso_service.controller.admin;

import bachtx.myapp.sso_service.entity.User;
import bachtx.myapp.sso_service.repository.Oauth2RegisteredClientRepository;
import bachtx.myapp.sso_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;
    private final Oauth2RegisteredClientRepository clientRepository;

    @GetMapping({"/", "/dashboard"})
    public String dashboard(Model model, Authentication authentication) {
        List<User> allUsers = userRepository.findAll();

        long activeCount = allUsers.stream()
                .filter(u -> "ACTIVE".equalsIgnoreCase(u.getStatus()))
                .count();
        long lockedCount = allUsers.stream()
                .filter(u -> u.getLockedUntil() != null && u.getLockedUntil().isAfter(LocalDateTime.now()))
                .count();

        model.addAttribute("totalUsers", allUsers.size());
        model.addAttribute("activeUsers", activeCount);
        model.addAttribute("lockedUsers", lockedCount);
        model.addAttribute("totalClients", clientRepository.count());
        // Show last 5 users
        List<User> recentUsers = allUsers.stream()
                .sorted((a, b) -> Long.compare(b.getId(), a.getId()))
                .limit(5)
                .toList();
        model.addAttribute("recentUsers", recentUsers);
        model.addAttribute("username", authentication.getName());

        return "admin/dashboard";
    }
}
