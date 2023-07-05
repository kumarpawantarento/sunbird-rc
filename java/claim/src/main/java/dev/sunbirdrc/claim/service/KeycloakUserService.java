package dev.sunbirdrc.claim.service;

import dev.sunbirdrc.claim.config.KeycloakConfig;
import dev.sunbirdrc.claim.config.PropMapping;
import dev.sunbirdrc.claim.dto.KeycloakTokenDetailsDTO;
import dev.sunbirdrc.claim.dto.KeycloakUserDTO;
import dev.sunbirdrc.claim.entity.ClaimUser;
import dev.sunbirdrc.claim.repository.ClaimUserRepository;
import org.apache.commons.lang.StringUtils;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.ClientsResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.admin.client.token.TokenManager;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.ws.rs.core.Response;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class KeycloakUserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(KeycloakUserService.class);

    @Autowired
    private PropMapping propMapping;

    @Autowired
    private KeycloakConfig keycloakConfig;
    
    @Qualifier("systemKeycloak")
    @Autowired
    private Keycloak systemKeycloak;

    @Autowired
    private ClaimUserRepository claimUserRepository;

    /**
     * @return
     */
    public UsersResource getSystemUsersResource(){
        return systemKeycloak.realm(propMapping.getRealm()).users();
    }

    public ClientsResource getSystemClientResource(){
        return systemKeycloak.realm(propMapping.getRealm()).clients();
    }

    /**
     * @param userName
     * @return
     */
    public List<UserRepresentation> getUserDetails(String userName) {
        return getSystemUsersResource().search(userName, true);
    }


    /**
     * @param userDTO
     * @return
     */
    public KeycloakTokenDetailsDTO loginAndGenerateKeycloakToken(KeycloakUserDTO userDTO) {
        TokenManager tokenManager = keycloakConfig.getUserKeycloak(userDTO.getUserName(), userDTO.getPassword()).tokenManager();

        AccessTokenResponse accessTokenResponse = tokenManager.getAccessToken();

        return KeycloakTokenDetailsDTO.builder()
                .accessToken(accessTokenResponse.getToken())
                .expiresIn(accessTokenResponse.getExpiresIn())
                .refreshToken(accessTokenResponse.getRefreshToken())
                .refreshExpiresIn(accessTokenResponse.getRefreshExpiresIn())
                .tokenType(accessTokenResponse.getTokenType())
                .scope(accessTokenResponse.getScope())
                .build();
    }
    /**
     * @param password
     * @return
     */
    public CredentialRepresentation createPasswordCredentials(String password) {
        CredentialRepresentation passwordCredentials = new CredentialRepresentation();
        passwordCredentials.setTemporary(false);
        passwordCredentials.setType(CredentialRepresentation.PASSWORD);
        passwordCredentials.setValue(password);
        return passwordCredentials;
    }

    /**
     * @param userDTO
     */
    public boolean addUser(KeycloakUserDTO userDTO){
        boolean status = false;

        if (userDTO != null && !StringUtils.isEmpty(userDTO.getUserName())
                && !StringUtils.isEmpty(userDTO.getPassword())) {

            CredentialRepresentation credential = createPasswordCredentials(userDTO.getPassword());
            UserRepresentation user = new UserRepresentation();
            user.setUsername(userDTO.getUserName());
            user.setFirstName(userDTO.getFirstName());
            user.setLastName(userDTO.getLastName());
            user.setEmail(userDTO.getEmail());
            user.setCredentials(Collections.singletonList(credential));
            user.setRequiredActions(Arrays.asList("VERIFY_EMAIL", "UPDATE_PASSWORD"));
            user.setEnabled(true);

            try {
                Response response = getSystemUsersResource().create(user);

                if (response.getStatus() == HttpStatus.CREATED.value()) {
                    persistUserDetails(userDTO.getUserName());
                    status = true;
                } else {
                    LOGGER.error("Unable to create user, systemKeycloak response - " + response.getStatusInfo());
                }
            } catch (Exception e) {
                LOGGER.error("Unable to create user in systemKeycloak", e.getMessage());
            }
        }

        return status;
    }

    public void persistUserDetails(String username) {
        if (!StringUtils.isEmpty(username)) {
            List<UserRepresentation> userRepresentationList = getUserDetails(username);

            if (userRepresentationList != null && !userRepresentationList.isEmpty()) {
                Optional<UserRepresentation> userRepresentationOptional = userRepresentationList.stream()
                        .filter(userRepresentation -> username.equalsIgnoreCase(userRepresentation.getUsername()))
//                        .map(userRepresentation -> userRepresentation.getId())
                        .findFirst();

                if (userRepresentationOptional.isPresent()) {
                    UserRepresentation userRepresentation = userRepresentationOptional.get();

                    ClaimUser claimUser = ClaimUser.builder()
                            .userId(userRepresentation.getId())
                            .userName(userRepresentation.getUsername())
                            .firstName(userRepresentation.getUsername())
                            .lastName(userRepresentation.getLastName())
                            .email(userRepresentation.getEmail())
                            .enabled(userRepresentation.isEnabled())
                            .build();

                    claimUserRepository.save(claimUser);
                }
            }
        }
    }

    /**
     * @param userId
     * @param userDTO
     */
    public void updateUser(String userId, KeycloakUserDTO userDTO){
        CredentialRepresentation credential = createPasswordCredentials(userDTO.getPassword());

        UserRepresentation user = new UserRepresentation();
        user.setUsername(userDTO.getUserName());
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setEmail(userDTO.getEmail());
        user.setCredentials(Collections.singletonList(credential));

        UserResource userResource = getSystemUsersResource().get(userId);
        userResource.update(user);
    }

    /**
     * @param userId
     */
    public void sendVerificationLink(String userId){
        UsersResource usersResource = getSystemUsersResource();
        usersResource.get(userId).sendVerifyEmail();


        List<RoleRepresentation> roleRepresentationList = usersResource.get(userId).roles().realmLevel().listEffective();
        System.out.println(usersResource.get(userId).roles().realmLevel().listEffective());
    }

    /**
     * @param userId
     * @param password
     */
    public void resetPassword(String userId, String password) {
        UsersResource usersResource = getSystemUsersResource();
        usersResource.get(userId).resetPassword(createPasswordCredentials(password));
    }

    /**
     * @param userId
     */
    public void deleteUser(String userId){
        // TO_DO disable user
        UsersResource usersResource = getSystemUsersResource();
        usersResource.get(userId).remove();
    }

    /**
     * @return
     */
    public List<String> getRealmRole() {
        Keycloak userKeycloak = keycloakConfig.getUserKeycloak(propMapping.getUserName(), propMapping.getPassword());

        List<String> roleList = userKeycloak.realm(propMapping.getRealm())
                .roles()
                .list()
                .stream()
                .map(roleRepresentation -> roleRepresentation.getName())
                .collect(Collectors.toList());

        return roleList;
    }

    /**
     * @param roleName
     * @param userId
     */
    public void assignRole(String roleName, String userId) {
        if (!StringUtils.isEmpty(roleName)) {
            List<RoleRepresentation> roleToAdd = new LinkedList<>();

            UserResource user = systemKeycloak
                    .realm(propMapping.getRealm())
                    .users()
                    .get(userId);

            roleToAdd.add(systemKeycloak
                    .realm(propMapping.getRealm())
                    .roles()
                    .get(roleName)
                    .toRepresentation()
            );

            user.roles().realmLevel().add(roleToAdd);
        }
    }

    /**
     * @param userId
     * @return
     */
    public List<String> getUserRole(String userId) {
        List<String> roleList = new ArrayList<>();

        if (!StringUtils.isEmpty(userId)) {
            UsersResource usersResource = getSystemUsersResource();
            List<RoleRepresentation> roleRepresentationList = usersResource
                    .get(userId)
                    .roles()
                    .realmLevel()
                    .listEffective();

            roleList = roleRepresentationList.stream()
                    .map(roleRepresentation -> roleRepresentation.getName())
                    .collect(Collectors.toList());
        }

        return roleList;
    }

    /**
     * @param clientId
     * @return
     *
     * Right now not used, kept for future reference
     */
    public List<String> getClientRole(String clientId) {
        Keycloak userKeycloak = keycloakConfig.getUserKeycloak(propMapping.getUserName(), propMapping.getPassword());

        ClientRepresentation clientRepresentation = userKeycloak.realm(propMapping.getRealm())
                .clients()
                .findByClientId(propMapping.getConfidentialClientId())
                .get(0);

        List<String> roleList = userKeycloak.realm(propMapping.getRealm())
                .clients()
                .get(clientRepresentation.getId())
                .roles()
                .list()
                .stream()
                .map(roleRepresentation -> roleRepresentation.getName())
                .collect(Collectors.toList());

        return roleList;
    }
}
