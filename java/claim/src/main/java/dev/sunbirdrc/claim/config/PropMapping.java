package dev.sunbirdrc.claim.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class PropMapping {
    @Value("${keycloak.server.url}")
    private String keycloakServerUrl;
    @Value("${keycloak.realm}")
    private String realm;
    @Value("${keycloak.confidential.client.id}")
    private String confidentialClientId;
    @Value("${keycloak.public.client.id}")
    private String publicClientId;
    @Value("${keycloak.client.secret}")
    private String clientSecret;
    @Value("${keycloak.username}")
    private String userName;
    @Value("${keycloak.password}")
    private String password;
    @Value("${keycloak.token.url}")
    private String keyCloakTokenUrl;
}
