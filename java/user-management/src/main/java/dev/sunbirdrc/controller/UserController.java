package dev.sunbirdrc.controller;


import dev.sunbirdrc.dto.AdminOtpDTO;
import dev.sunbirdrc.dto.UserDetailsDTO;
import dev.sunbirdrc.dto.UserOtpDTO;
import dev.sunbirdrc.dto.UserTokenDetailsDTO;
import dev.sunbirdrc.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/v1")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<UserTokenDetailsDTO> loginUser(@RequestBody UserDetailsDTO userDTO) {
        UserTokenDetailsDTO keycloakTokenDetailsDTO = null;
        try {
            keycloakTokenDetailsDTO = userService.loginAndGenerateKeycloakToken(userDTO);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return new ResponseEntity<>(keycloakTokenDetailsDTO, HttpStatus.OK);
    }

    @PostMapping("/create")
    public ResponseEntity<String> createUser(@RequestBody UserDetailsDTO userDTO) {
        boolean status = userService.addUser(userDTO);

        if (status) {
            return new ResponseEntity<>("Successfully added user", HttpStatus.CREATED);
        }else {
            return new ResponseEntity<>("Unable to create user", HttpStatus.FAILED_DEPENDENCY);
        }
    }

    @PostMapping("/verifyAndUpdate/otp")
    public ResponseEntity<String> verifyUserMailOTP(@RequestBody UserOtpDTO userOtpDTO) {
        boolean verified = false;
        try {
            verified = userService.verifyMailOTP(userOtpDTO);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (verified) {
            return new ResponseEntity<>("Successfully verified user", HttpStatus.CREATED);
        }else {
            return new ResponseEntity<>("Unable to verify", HttpStatus.FAILED_DEPENDENCY);
        }
    }

    @PostMapping("/admin/generateOtp")
    public ResponseEntity<String> generateAdminOtp(@RequestBody UserDetailsDTO userDTO) {
        try {
            userService.generateAdminOtp(userDTO);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return new ResponseEntity<>("Sending OTP to user mail", HttpStatus.OK);
    }

    @PostMapping("/admin/login")
    public ResponseEntity<UserTokenDetailsDTO> loginAdminUser(@RequestBody AdminOtpDTO adminOtpDTO) {
        UserTokenDetailsDTO tokenDetailsDTO = null;
        try {
            tokenDetailsDTO = userService.getAdminTokenByOtp(adminOtpDTO);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return new ResponseEntity<>(tokenDetailsDTO, HttpStatus.OK);
    }

    @GetMapping(path = "/keycloak")
    public ResponseEntity<String> getUser(){


        return new ResponseEntity<>("Role base access", HttpStatus.OK);
    }

}
