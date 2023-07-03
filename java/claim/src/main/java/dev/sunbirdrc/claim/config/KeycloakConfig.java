package dev.sunbirdrc.claim.config;

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestTemplate;


@Configuration
public class KeycloakConfig {
   @Autowired
   private PropMapping propMapping;

    @Primary
    @Bean(name = "systemKeycloak")
    public Keycloak systemKeycloak() {
        return KeycloakBuilder.builder()
                .serverUrl(propMapping.getKeycloakServerUrl())
                .realm(propMapping.getRealm())
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .clientId(propMapping.getConfidentialClientId())
                .clientSecret(propMapping.getClientSecret())
                .resteasyClient(new ResteasyClientBuilder()
                        .connectionPoolSize(10)
                        .build()
                )
                .build();
    }

    public Keycloak getUserKeycloak(String username, String password) {
        return KeycloakBuilder.builder()
                .serverUrl(propMapping.getKeycloakServerUrl())
                .realm(propMapping.getRealm())
                .grantType(OAuth2Constants.PASSWORD)
                .username(username)
                .password(password)
                .clientId(propMapping.getPublicClientId())
                .resteasyClient(new ResteasyClientBuilder()
                        .connectionPoolSize(10)
                        .build()
                )
                .build();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
