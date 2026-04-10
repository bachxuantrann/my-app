package bachtx.myapp.sso_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Set;

@Data
public class ClientRequest {

    @NotBlank(message = "Client ID is required")
    private String clientId;

    @NotBlank(message = "Client Secret is required")
    private String clientSecret;

    @NotBlank(message = "Client Name is required")
    private String clientName;

    private Set<String> redirectUris;
    
    private Set<String> scopes;
}
