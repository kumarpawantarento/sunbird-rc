package dev.sunbirdrc.claim.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KeycloakTokenDetailsDTO {
    private String accessToken;
    private long expiresIn;
    private String refreshToken;
    private long refreshExpiresIn;
    private String tokenType;
    private String scope;
}