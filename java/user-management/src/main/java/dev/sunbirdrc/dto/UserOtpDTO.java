package dev.sunbirdrc.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class UserOtpDTO {
    private String username;
    private Integer otp;
    private String password;
}
