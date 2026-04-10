package bachtx.myapp.sso_service.controller.api;

import bachtx.myapp.sso_service.dto.response.ApiResponse;
import bachtx.myapp.sso_service.exception.AppException;
import bachtx.myapp.sso_service.exception.ErrorCode;
import bachtx.myapp.sso_service.dto.request.ClientRequest;
import bachtx.myapp.sso_service.security.JpaRegisteredClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin/clients")
@RequiredArgsConstructor
public class ClientManagementController {

    private final JpaRegisteredClientRepository clientRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping
    public ResponseEntity<ApiResponse<RegisteredClient>> registerClient(@RequestBody ClientRequest request) {
        RegisteredClient registeredClient = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId(request.getClientId())
                .clientSecret(passwordEncoder.encode(request.getClientSecret()))
                .clientName(request.getClientName())
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .redirectUris(uris -> {
                    if (request.getRedirectUris() != null) {
                        uris.addAll(request.getRedirectUris());
                    }
                })
                .scopes(scopes -> {
                    if (request.getScopes() != null) {
                        scopes.addAll(request.getScopes());
                    } else {
                        scopes.add("openid");
                        scopes.add("profile");
                    }
                })
                .build();

        clientRepository.save(registeredClient);

        ApiResponse<RegisteredClient> response = ApiResponse.<RegisteredClient>builder()
                .result(registeredClient)
                .message("Client registered successfully")
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{clientId}")
    public ResponseEntity<ApiResponse<RegisteredClient>> getClient(@PathVariable String clientId) {
        RegisteredClient client = clientRepository.findByClientId(clientId);
        if (client == null) {
            throw new AppException(ErrorCode.CLIENT_NOT_FOUND);
        }
        ApiResponse<RegisteredClient> response = ApiResponse.<RegisteredClient>builder()
                .result(client)
                .build();
        return ResponseEntity.ok(response);
    }
}
