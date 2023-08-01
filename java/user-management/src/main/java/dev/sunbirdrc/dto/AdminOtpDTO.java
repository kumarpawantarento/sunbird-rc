package dev.sunbirdrc.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class AdminOtpDTO {
    private String email;
    private Integer otp;
}
