package bachtx.myapp.sso_service.security;

import bachtx.myapp.sso_service.entity.Oauth2RegisteredClient;
import bachtx.myapp.sso_service.repository.Oauth2RegisteredClientRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JpaRegisteredClientRepository implements RegisteredClientRepository {

    private final Oauth2RegisteredClientRepository clientRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void save(RegisteredClient registeredClient) {
        Oauth2RegisteredClient entity = clientRepository.findByClientId(registeredClient.getClientId())
                .orElseGet(Oauth2RegisteredClient::new);
        entity.setClientId(registeredClient.getClientId());

        if (registeredClient.getClientIdIssuedAt() != null) {
            entity.setClientIdIssuedAt(registeredClient.getClientIdIssuedAt());
        }
        entity.setClientSecret(registeredClient.getClientSecret());

        if (registeredClient.getClientSecretExpiresAt() != null) {
            entity.setClientSecretExpiresAt(registeredClient.getClientSecretExpiresAt());
        }
        entity.setClientName(registeredClient.getClientName());

        // Nối mảng thành chuỗi cách nhau bởi dấu phẩy
        entity.setClientAuthenticationMethods(StringUtils.collectionToCommaDelimitedString(
                registeredClient.getClientAuthenticationMethods().stream()
                        .map(ClientAuthenticationMethod::getValue)
                        .collect(Collectors.toList())));

        entity.setAuthorizationGrantTypes(StringUtils.collectionToCommaDelimitedString(
                registeredClient.getAuthorizationGrantTypes().stream()
                        .map(AuthorizationGrantType::getValue)
                        .collect(Collectors.toList())));

        entity.setRedirectUris(StringUtils.collectionToCommaDelimitedString(registeredClient.getRedirectUris()));
        entity.setPostLogoutRedirectUris(StringUtils.collectionToCommaDelimitedString(registeredClient.getPostLogoutRedirectUris()));
        entity.setScopes(StringUtils.collectionToCommaDelimitedString(registeredClient.getScopes()));

        // Map Settings ra chuỗi JSON
        try {
            entity.setClientSettings(objectMapper.writeValueAsString(registeredClient.getClientSettings().getSettings()));
            entity.setTokenSettings(objectMapper.writeValueAsString(registeredClient.getTokenSettings().getSettings()));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Lỗi khi parse cấu hình Client Settings", e);
        }

        // Lưu xuống DB
        clientRepository.save(entity);
    }

    @Override
    public RegisteredClient findById(String id) {
        return clientRepository.findById(Long.parseLong(id))
                .map(this::toObject)
                .orElse(null);
    }

    @Override
    public RegisteredClient findByClientId(String clientId) {
        return clientRepository.findByClientId(clientId)
                .map(this::toObject)
                .orElse(null);
    }

    // Hàm ánh xạ từ Entity của bạn sang RegisteredClient của Spring
    private RegisteredClient toObject(Oauth2RegisteredClient entity) {
        Set<String> clientAuthenticationMethods = StringUtils.commaDelimitedListToSet(entity.getClientAuthenticationMethods());
        Set<String> authorizationGrantTypes = StringUtils.commaDelimitedListToSet(entity.getAuthorizationGrantTypes());
        Set<String> redirectUris = StringUtils.commaDelimitedListToSet(entity.getRedirectUris());
        Set<String> clientScopes = StringUtils.commaDelimitedListToSet(entity.getScopes());

        return RegisteredClient.withId(String.valueOf(entity.getId()))
                .clientId(entity.getClientId())
                .clientSecret(entity.getClientSecret())
                .clientName(entity.getClientName())
                .clientAuthenticationMethods(methods ->
                        clientAuthenticationMethods.forEach(method -> methods.add(new ClientAuthenticationMethod(method)))
                )
                .authorizationGrantTypes(grants ->
                        authorizationGrantTypes.forEach(grant -> grants.add(new AuthorizationGrantType(grant)))
                )
                .redirectUris(uris -> uris.addAll(redirectUris))
                .scopes(scopes -> scopes.addAll(clientScopes))
                // Bật PKCE bắt buộc cho các public client (như Mobile App)
                .clientSettings(ClientSettings.builder().requireProofKey(true).build())
                .tokenSettings(TokenSettings.builder().build())
                .build();
    }
}
