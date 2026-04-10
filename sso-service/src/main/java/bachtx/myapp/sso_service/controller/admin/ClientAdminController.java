package bachtx.myapp.sso_service.controller.admin;

import bachtx.myapp.sso_service.entity.Oauth2RegisteredClient;
import bachtx.myapp.sso_service.repository.Oauth2RegisteredClientRepository;
import bachtx.myapp.sso_service.security.JpaRegisteredClientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/clients")
@RequiredArgsConstructor
@Slf4j
public class ClientAdminController {

    private final Oauth2RegisteredClientRepository clientRepository;
    private final JpaRegisteredClientRepository registeredClientRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("clients", clientRepository.findAll());
        return "admin/clients/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("client", new Oauth2RegisteredClient());
        model.addAttribute("isEdit", false);
        return "admin/clients/form";
    }

    @PostMapping("/new")
    public String create(@RequestParam String clientId,
                         @RequestParam String clientSecret,
                         @RequestParam String clientName,
                         @RequestParam String redirectUris,
                         @RequestParam(required = false, defaultValue = "") String postLogoutRedirectUris,
                         @RequestParam(required = false, defaultValue = "openid,profile,email") String scopes,
                         RedirectAttributes redirectAttr) {
        try {
            // Kiểm tra trùng clientId
            if (clientRepository.findByClientId(clientId).isPresent()) {
                redirectAttr.addFlashAttribute("errorMsg", "Client ID '" + clientId + "' đã tồn tại!");
                return "redirect:/admin/clients/new";
            }

            // Chuyển multiline textarea sang comma-delimited
            String redirectUrisClean = Arrays.stream(redirectUris.split("\\n"))
                    .map(String::trim).filter(s -> !s.isEmpty())
                    .collect(Collectors.joining(","));

            String postLogoutClean = Arrays.stream(postLogoutRedirectUris.split("\\n"))
                    .map(String::trim).filter(s -> !s.isEmpty())
                    .collect(Collectors.joining(","));

            RegisteredClient registeredClient = RegisteredClient.withId(UUID.randomUUID().toString())
                    .clientId(clientId)
                    .clientSecret(passwordEncoder.encode(clientSecret))
                    .clientName(clientName)
                    .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                    .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
                    .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                    .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                    .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                    .clientSecretExpiresAt(Instant.now().plus(365, ChronoUnit.DAYS))
                    .redirectUris(uris -> Arrays.stream(redirectUrisClean.split(","))
                            .map(String::trim).filter(s -> !s.isEmpty()).forEach(uris::add))
                    .postLogoutRedirectUris(uris -> Arrays.stream(postLogoutClean.split(","))
                            .map(String::trim).filter(s -> !s.isEmpty()).forEach(uris::add))
                    .scopes(scopeSet -> Arrays.stream(scopes.split(","))
                            .map(String::trim).filter(s -> !s.isEmpty()).forEach(scopeSet::add))
                    .build();

            registeredClientRepository.save(registeredClient);
            redirectAttr.addFlashAttribute("successMsg", "Đã đăng ký Client '" + clientName + "' thành công!");
        } catch (Exception e) {
            log.error("Error creating client", e);
            redirectAttr.addFlashAttribute("errorMsg", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/clients";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Oauth2RegisteredClient client = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client không tồn tại"));
        // Chuyển comma-delimited sang newline cho textarea
        client.setRedirectUris(client.getRedirectUris().replace(",", "\n"));
        client.setPostLogoutRedirectUris(client.getPostLogoutRedirectUris().replace(",", "\n"));
        model.addAttribute("client", client);
        model.addAttribute("isEdit", true);
        return "admin/clients/form";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id,
                         @RequestParam String clientName,
                         @RequestParam String redirectUris,
                         @RequestParam(required = false, defaultValue = "") String postLogoutRedirectUris,
                         @RequestParam(required = false, defaultValue = "openid,profile,email") String scopes,
                         RedirectAttributes redirectAttr) {
        try {
            Oauth2RegisteredClient client = clientRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Client không tồn tại"));

            String redirectUrisClean = Arrays.stream(redirectUris.split("\\n"))
                    .map(String::trim).filter(s -> !s.isEmpty())
                    .collect(Collectors.joining(","));
            String postLogoutClean = Arrays.stream(postLogoutRedirectUris.split("\\n"))
                    .map(String::trim).filter(s -> !s.isEmpty())
                    .collect(Collectors.joining(","));
            String scopesClean = Arrays.stream(scopes.split(","))
                    .map(String::trim).filter(s -> !s.isEmpty())
                    .collect(Collectors.joining(","));

            client.setClientName(clientName);
            client.setRedirectUris(redirectUrisClean);
            client.setPostLogoutRedirectUris(postLogoutClean);
            client.setScopes(scopesClean);
            clientRepository.save(client);
            redirectAttr.addFlashAttribute("successMsg", "Cập nhật Client thành công!");
        } catch (Exception e) {
            log.error("Error updating client id={}", id, e);
            redirectAttr.addFlashAttribute("errorMsg", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/clients";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttr) {
        try {
            clientRepository.deleteById(id);
            redirectAttr.addFlashAttribute("successMsg", "Đã xóa Client!");
        } catch (Exception e) {
            redirectAttr.addFlashAttribute("errorMsg", "Không thể xóa: " + e.getMessage());
        }
        return "redirect:/admin/clients";
    }
}
