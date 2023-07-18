package dev.sunbirdrc.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class UserDetailsDTO {
    private String userName;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String rollNo;
    private String instituteId;
    private String instituteName;
    private String phoneNo;
    private String userId;
}
