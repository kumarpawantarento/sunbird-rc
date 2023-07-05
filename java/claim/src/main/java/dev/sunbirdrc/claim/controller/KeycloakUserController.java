package dev.sunbirdrc.claim.controller;

import dev.sunbirdrc.claim.dto.KeycloakTokenDetailsDTO;
import dev.sunbirdrc.claim.dto.KeycloakUserDTO;
import dev.sunbirdrc.claim.service.KeycloakUserService;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/v1")
public class KeycloakUserController {

    @Autowired
    private KeycloakUserService keycloakUserService;

    /**
     * @param userDTO
     * @return
     */
    @PostMapping("/login")
    public ResponseEntity<KeycloakTokenDetailsDTO> loginUser(@RequestBody KeycloakUserDTO userDTO) {
        // TO_DO - PW should be in encrypted format
        KeycloakTokenDetailsDTO keycloakTokenDetailsDTO = keycloakUserService.loginAndGenerateKeycloakToken(userDTO);

        return new ResponseEntity<>(keycloakTokenDetailsDTO, HttpStatus.OK);
    }

    /**
     * @param userName
     * @return
     */
    @GetMapping(path = "/keycloak/{userName}")
    public ResponseEntity<List<UserRepresentation>> getUser(@PathVariable("userName") String userName){
        List<UserRepresentation> user = keycloakUserService.getUserDetails(userName);

        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    /**
     * @param userDTO
     * @return
     */
    @PostMapping("/keycloak/create")
    public ResponseEntity<String> createUser(@RequestBody KeycloakUserDTO userDTO) {
        boolean status = keycloakUserService.addUser(userDTO);

        if (status) {
            return new ResponseEntity<>("Successfully added user", HttpStatus.CREATED);
        }else {
            return new ResponseEntity<>("Unable to create user", HttpStatus.FAILED_DEPENDENCY);
        }
    }

    @PostMapping("/keycloak/update/{userId}")
    public ResponseEntity<String> updateUser(@PathVariable("userId") String userId, @RequestBody KeycloakUserDTO userDTO) {
        keycloakUserService.updateUser(userId, userDTO);

        return new ResponseEntity<>("Successfully updated user", HttpStatus.OK);
    }

    @GetMapping(path = "/keycloak/sendVerificationLink/{userId}")
    public ResponseEntity<String> sendVerificationLink(@PathVariable("userId") String userId){
        keycloakUserService.sendVerificationLink(userId);

        return new ResponseEntity<>("Verification Link has been sent to user", HttpStatus.OK);
    }

    @GetMapping(path = "/keycloak/delete/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable("userId") String userId){
        keycloakUserService.deleteUser(userId);

        return new ResponseEntity<>("Successfully delete the user", HttpStatus.OK);
    }

    @PostMapping("/keycloak/updatePassword/{userId}")
    public ResponseEntity<String> updatePassword(@PathVariable("userId") String userId, @RequestBody KeycloakUserDTO userDTO) {
        keycloakUserService.resetPassword(userId, userDTO.getPassword());

        return new ResponseEntity<>("Successfully updated user", HttpStatus.OK);
    }
    @GetMapping(path = "keycloak/allRoles")
    public ResponseEntity<List<String>> getRoles(){
        List<String> roleList = keycloakUserService.getRealmRole();

        return new ResponseEntity<>(roleList, HttpStatus.OK);
    }

    @GetMapping(path = "keycloak/user/role/{userId}")
    public ResponseEntity<List<String>> getRoles(@PathVariable("userId") String userId){
        List<String> roleList = keycloakUserService.getUserRole(userId);

        return new ResponseEntity<>(roleList, HttpStatus.OK);
    }

    @GetMapping(path = "keycloak/assignRole/user/{userId}/role/{roleName}")
    public ResponseEntity<String> getRoles(@PathVariable("userId") String userId, @PathVariable("roleName") String roleName){
        keycloakUserService.assignRole(roleName, userId);

        return new ResponseEntity<>("Role assign successfully", HttpStatus.OK);
    }

}
