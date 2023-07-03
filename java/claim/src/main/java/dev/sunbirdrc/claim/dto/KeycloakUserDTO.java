package dev.sunbirdrc.claim.dto;

import lombok.AllArgsConstructor;
import lombok.Data;


@AllArgsConstructor
@Data
public class KeycloakUserDTO {
    private String userName;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
}
