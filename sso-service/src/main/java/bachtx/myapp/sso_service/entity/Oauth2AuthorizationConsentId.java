package bachtx.myapp.sso_service.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Oauth2AuthorizationConsentId implements Serializable {
    private String registeredClientId;
    private String principalName;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Oauth2AuthorizationConsentId that = (Oauth2AuthorizationConsentId) o;
        return registeredClientId.equals(that.registeredClientId) && principalName.equals(that.principalName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(registeredClientId, principalName);
    }
}
