package bachtx.myapp.sso_service.security;

import bachtx.myapp.sso_service.entity.Oauth2AuthorizationConsent;
import bachtx.myapp.sso_service.repository.Oauth2AuthorizationConsentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsent;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class JpaOAuth2AuthorizationConsentService implements OAuth2AuthorizationConsentService {

    private final Oauth2AuthorizationConsentRepository consentRepository;

    @Override
    public void save(OAuth2AuthorizationConsent authorizationConsent) {
        // UPSERT: Tìm consent cũ hoặc tạo mới — KHÔNG tạo entity mới vô điều kiện
        // Tạo mới luôn sẽ gây lỗi duplicate PK khi user re-consent
        Oauth2AuthorizationConsent entity = consentRepository
                .findByRegisteredClientIdAndPrincipalName(
                        authorizationConsent.getRegisteredClientId(),
                        authorizationConsent.getPrincipalName())
                .orElseGet(Oauth2AuthorizationConsent::new);

        entity.setRegisteredClientId(authorizationConsent.getRegisteredClientId());
        entity.setPrincipalName(authorizationConsent.getPrincipalName());

        Set<String> authorities = new HashSet<>();
        for (GrantedAuthority authority : authorizationConsent.getAuthorities()) {
            authorities.add(authority.getAuthority());
        }
        entity.setAuthorities(StringUtils.collectionToCommaDelimitedString(authorities));

        consentRepository.save(entity);
    }

    @Override
    public void remove(OAuth2AuthorizationConsent authorizationConsent) {
        consentRepository.deleteByRegisteredClientIdAndPrincipalName(
                authorizationConsent.getRegisteredClientId(), 
                authorizationConsent.getPrincipalName()
        );
    }

    @Override
    public OAuth2AuthorizationConsent findById(String registeredClientId, String principalName) {
        return consentRepository.findByRegisteredClientIdAndPrincipalName(registeredClientId, principalName)
                .map(this::toObject)
                .orElse(null);
    }

    private OAuth2AuthorizationConsent toObject(Oauth2AuthorizationConsent entity) {
        OAuth2AuthorizationConsent.Builder builder = OAuth2AuthorizationConsent.withId(
                entity.getRegisteredClientId(), entity.getPrincipalName());
        
        if (entity.getAuthorities() != null) {
            for (String authority : StringUtils.commaDelimitedListToSet(entity.getAuthorities())) {
                builder.authority(new SimpleGrantedAuthority(authority));
            }
        }
        return builder.build();
    }
}
