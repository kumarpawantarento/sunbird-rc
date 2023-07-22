package dev.sunbirdrc.controller;


import dev.sunbirdrc.dto.*;
import dev.sunbirdrc.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/api/v1")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<UserTokenDetailsDTO> loginUser(@Valid @RequestBody UserLoginDTO userLoginDTO) {
        UserTokenDetailsDTO keycloakTokenDetailsDTO = userService.loginAndGenerateKeycloakToken(userLoginDTO);

        return new ResponseEntity<>(keycloakTokenDetailsDTO, HttpStatus.OK);
    }

    @PostMapping("/create")
    public ResponseEntity<String> createUser(@Valid @RequestBody UserDetailsDTO userDTO) {
        boolean status = userService.addUser(userDTO);

        if (status) {
            return new ResponseEntity<>("Successfully added user", HttpStatus.CREATED);
        }else {
            return new ResponseEntity<>("Unable to create user", HttpStatus.FAILED_DEPENDENCY);
        }
    }

    @PostMapping("/verifyAndUpdate/otp")
    public ResponseEntity<String> verifyUserMailOTP(@Valid @RequestBody UserOtpDTO userOtpDTO) {
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
    public ResponseEntity<String> generateAdminOtp(@Valid @RequestBody AdminDTO adminDTO) {
        try {
            userService.generateAdminOtp(adminDTO);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return new ResponseEntity<>("Sending OTP to user mail", HttpStatus.OK);
    }

    @PostMapping("/admin/login")
    public ResponseEntity<UserTokenDetailsDTO> loginAdminUser(@Valid @RequestBody AdminLoginDTO adminLoginDTO) {
        UserTokenDetailsDTO tokenDetailsDTO = null;
        try {
            tokenDetailsDTO = userService.getAdminTokenByOtp(adminLoginDTO);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return new ResponseEntity<>(tokenDetailsDTO, HttpStatus.OK);
    }

    @GetMapping(path = "/keycloak")
    public ResponseEntity<String> getUser(){


        return new ResponseEntity<>("Role base access", HttpStatus.OK);
    }

    @PostMapping("/createBulkUser")
    public ResponseEntity<String> createBulkUser(@Valid @RequestBody List<CustomUserDTO> customUserDTOList) {
        userService.addBulkUser(customUserDTOList);

        return new ResponseEntity<>("Bulk user creation is being processed", HttpStatus.CREATED);
    }

    @PostMapping("/user/generateOtp")
    public ResponseEntity<String> generateUserOtp(@Valid @RequestBody CustomUsernameDTO customUsernameDTO) {
        try {
            userService.generateCustomUserOtp(customUsernameDTO);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return new ResponseEntity<>("Sending OTP to user mail", HttpStatus.OK);
    }

    @PostMapping("/user/login")
    public ResponseEntity<UserTokenDetailsDTO> loginCustomUser(@Valid @RequestBody CustomUserLoginDTO customUserLoginDTO) {
        UserTokenDetailsDTO tokenDetailsDTO = null;
        try {
            tokenDetailsDTO = userService.getCustomUserTokenByOtp(customUserLoginDTO);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return new ResponseEntity<>(tokenDetailsDTO, HttpStatus.OK);
    }
}
