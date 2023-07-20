package dev.sunbirdrc.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.*;

@AllArgsConstructor
@Data
public class AdminOtpDTO {
    @NotBlank(message = "Email is required field - It can be blank")
    @Email(message = "Invalid mail id")
    private String email;

    @NotBlank(message = "OTP is required field")
    @Size(min = 6, max = 6, message = "OTP length should be six digit")
    private String otp;
}
