package dev.sunbirdrc.service;


import dev.sunbirdrc.config.KeycloakConfig;
import dev.sunbirdrc.config.PropertiesValueMapper;
import dev.sunbirdrc.dto.*;
import dev.sunbirdrc.entity.UserDetails;
import dev.sunbirdrc.exception.*;
import dev.sunbirdrc.repository.UserDetailsRepository;
import dev.sunbirdrc.utils.OtpUtil;
import dev.sunbirdrc.utils.UserConstant;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.ClientsResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.admin.client.token.TokenManager;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.core.Response;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private KeycloakConfig keycloakConfig;

    @Qualifier("systemKeycloak")
    @Autowired
    private Keycloak systemKeycloak;

    @Autowired
    private MailService mailService;

    @Autowired
    private OtpUtil otpUtil;

    @Autowired
    private PropertiesValueMapper valueMapper;

    @Autowired
    private UserDetailsRepository userDetailsRepository;


    public UsersResource getSystemUsersResource(){
        return systemKeycloak.realm(valueMapper.getRealm()).users();
    }

    public CredentialRepresentation createPasswordCredentials(String password) {
        CredentialRepresentation passwordCredentials = new CredentialRepresentation();
        passwordCredentials.setTemporary(false);
        passwordCredentials.setType(CredentialRepresentation.PASSWORD);
        passwordCredentials.setValue(password);
        return passwordCredentials;
    }

    public ClientsResource getSystemClientResource(){
        return systemKeycloak.realm(valueMapper.getRealm()).clients();
    }

    /**
     * It provides all details of user that exist in keycloak server.
     * @param userName
     * @return
     */
    public List<UserRepresentation> getUserDetails(String userName) {
        return getSystemUsersResource().search(userName, true);
    }


    public boolean configureAdmin(UserDetailsDTO userDetailsDTO) {

        return false;
    }

    public UserTokenDetailsDTO loginAndGenerateKeycloakToken(UserLoginDTO userLoginDTO) {
        if (userLoginDTO != null && StringUtils.hasText(userLoginDTO.getUsername())
                && StringUtils.hasText(userLoginDTO.getPassword())) {

            try {
                TokenManager tokenManager = keycloakConfig
                        .getUserKeycloak(userLoginDTO.getUsername(), userLoginDTO.getPassword()).tokenManager();

                AccessTokenResponse accessTokenResponse = tokenManager.getAccessToken();

                return UserTokenDetailsDTO.builder()
                        .accessToken(accessTokenResponse.getToken())
                        .expiresIn(accessTokenResponse.getExpiresIn())
                        .refreshToken(accessTokenResponse.getRefreshToken())
                        .refreshExpiresIn(accessTokenResponse.getRefreshExpiresIn())
                        .tokenType(accessTokenResponse.getTokenType())
                        .scope(accessTokenResponse.getScope())
                        .build();
            } catch (NotAuthorizedException e) {
                throw new AuthorizationException("Credentials have authorization issue");
            } catch (Exception e) {
                throw new KeycloakUserException("Unable to get user detils - Update user");
            }
        } else {
            throw new UserCredentialsException("User credentials are invalid");
        }
    }

    public boolean addUser(UserDetailsDTO userDTO){
        boolean status = false;

        if (userDTO != null && !StringUtils.isEmpty(userDTO.getUserName())) {

            UserRepresentation user = new UserRepresentation();
            user.setUsername(userDTO.getUserName());
            user.setFirstName(userDTO.getFirstName());
            user.setLastName(userDTO.getLastName());
            user.setEmail(userDTO.getEmail());
            user.setRequiredActions(Arrays.asList(UserConstant.VERIFY_MAIL_ACTION, UserConstant.UPDATE_PASSWORD_ACTION));
            user.setEnabled(false);

            Map<String, List<String>> customAttributes = new HashMap<>();
            customAttributes.put(UserConstant.ROLL_NO, Collections.singletonList(userDTO.getRollNo()));
            customAttributes.put(UserConstant.INSTITUTE_ID, Collections.singletonList(userDTO.getInstituteId()));
            customAttributes.put(UserConstant.INSTITUTE_NAME, Collections.singletonList(userDTO.getInstituteName()));
            customAttributes.put(UserConstant.PHONE_NUMBER, Collections.singletonList(userDTO.getPhoneNo()));

            user.setAttributes(customAttributes);

            try {
                Response response = getSystemUsersResource().create(user);

                if (response.getStatus() == HttpStatus.CREATED.value()) {
                    persistUserDetails(userDTO);
                    status = true;
                } else {
                    LOGGER.error("Unable to create user, systemKeycloak response - " + response.getStatusInfo());
                    throw new KeycloakUserException("Unable to create user in keycloak directory: " + response.getStatusInfo());
                }
            } catch (Exception e) {
                LOGGER.error("Unable to create user in systemKeycloak", e.getMessage());
                throw new KeycloakUserException("Unable to create user - error message: " + e.getMessage());
            }
        }
        return status;
    }

    public void persistUserDetails(UserDetailsDTO userDTO) throws Exception {
        if (userDTO != null && !StringUtils.isEmpty(userDTO.getUserName())) {
            List<UserRepresentation> userRepresentationList = getUserDetails(userDTO.getUserName());

            if (userRepresentationList != null && !userRepresentationList.isEmpty()) {
                Optional<UserRepresentation> userRepresentationOptional = userRepresentationList.stream()
                        .filter(userRepresentation -> userDTO.getUserName().equalsIgnoreCase(userRepresentation.getUsername()))
                        .findFirst();

                if (userRepresentationOptional.isPresent()) {
                    UserRepresentation userRepresentation = userRepresentationOptional.get();

                    UserDetails claimUser = UserDetails.builder()
                            .userId(userRepresentation.getId())
                            .userName(userRepresentation.getUsername())
                            .firstName(userRepresentation.getFirstName())
                            .lastName(userRepresentation.getLastName())
                            .email(userRepresentation.getEmail())
                            .enabled(userRepresentation.isEnabled())
                            .rollNo(userDTO.getRollNo())
                            .instituteId(userDTO.getInstituteId())
                            .instituteName(userDTO.getInstituteName())
                            .phoneNo(userDTO.getPhoneNo())
                            .build();

                    userDetailsRepository.save(claimUser);
                    mailService.sendOtpMail(claimUser);
                }
            }
        } else {
            throw new UserNotFoundException("Invalid user details or username, while saving user in claim service");
        }
    }

    public boolean verifyMailOTP(UserOtpDTO userOtpDTO) throws Exception {
        if (userOtpDTO != null && !StringUtils.isEmpty(userOtpDTO.getUsername())) {

            List<UserRepresentation> userRepresentationList = getUserDetails(userOtpDTO.getUsername());

            if (userRepresentationList != null && !userRepresentationList.isEmpty()) {
                Optional<UserRepresentation> userRepresentationOptional = userRepresentationList.stream()
                        .filter(userRepresentation ->
                                userOtpDTO.getUsername().equalsIgnoreCase(userRepresentation.getUsername()))
                        .findFirst();

                if (!userRepresentationOptional.isPresent()) {
                    throw new Exception("Username missing while verifying OTP");
                }


                boolean matched = otpUtil.verifyUserMailOtp(userRepresentationOptional.get().getId(), userOtpDTO.getOtp());

                if (matched) {
                    UserResource userResource = getSystemUsersResource().get(userRepresentationOptional.get().getId());

                    UserRepresentation existingUserRepresentation = userResource.toRepresentation();
                    List<String> requiredActions = existingUserRepresentation.getRequiredActions();

                    if (requiredActions != null && !requiredActions.isEmpty()) {
                        requiredActions = requiredActions.stream()
                                .filter(actionName -> !UserConstant.VERIFY_MAIL_ACTION.equals(actionName)
                                        && !UserConstant.UPDATE_PASSWORD_ACTION.equals(actionName))
                                .collect(Collectors.toList());
                    }

                    existingUserRepresentation.setRequiredActions(requiredActions);

                    CredentialRepresentation credential = createPasswordCredentials(userOtpDTO.getPassword());
                    existingUserRepresentation.setCredentials(Collections.singletonList(credential));
                    existingUserRepresentation.setEnabled(true);

                    userResource.update(existingUserRepresentation);

                    return true;
                }
            }
        }

        return false;
    }

    public void generateAdminOtp(UserDetailsDTO userDTO) throws Exception {
        if (userDTO != null && !StringUtils.isEmpty(userDTO.getUserName())) {
            String username = userDTO.getUserName();

            List<UserRepresentation> userRepresentationList = getUserDetails(username);

            if (userRepresentationList != null && !userRepresentationList.isEmpty()) {
                Optional<UserRepresentation> userRepresentationOptional = userRepresentationList.stream()
                        .filter(userRepresentation -> username.equalsIgnoreCase(userRepresentation.getUsername()))
                        .findFirst();

                if (userRepresentationOptional.isPresent()) {
                    UserRepresentation userRepresentation = userRepresentationOptional.get();

                    UsersResource usersResource = getSystemUsersResource();
                    List<RoleRepresentation> roleRepresentationList = usersResource.get(userRepresentation.getId()).roles().realmLevel().listEffective();

                    Optional<RoleRepresentation> roleRepresentationOptional = roleRepresentationList.stream()
                            .filter(roleRepresentation -> UserConstant.ADMIN_ROLE.equals(roleRepresentation.getName()))
                            .findFirst();

                    if (roleRepresentationOptional.isPresent()) {
                        UserDetails userDetails = UserDetails.builder()
                                .userId(userRepresentation.getId())
                                .userName(userRepresentation.getUsername())
                                .firstName(userRepresentation.getFirstName())
                                .lastName(userRepresentation.getLastName())
                                .email(userRepresentation.getEmail())
                                .enabled(userRepresentation.isEnabled())
                                .build();

                        mailService.sendOtpMail(userDetails);
                    } else {
                        throw new OtpException("User doesn't have role admin");
                    }
                }
            }
        }
    }

    public UserTokenDetailsDTO getAdminTokenByOtp(AdminOtpDTO adminOtpDTO) throws Exception {
        if (adminOtpDTO != null && !StringUtils.isEmpty(adminOtpDTO.getEmail())) {
            String username = adminOtpDTO.getEmail();

            List<UserRepresentation> userRepresentationList = getUserDetails(username);

            if (userRepresentationList != null && !userRepresentationList.isEmpty()) {
                Optional<UserRepresentation> userRepresentationOptional = userRepresentationList.stream()
                        .filter(userRepresentation -> username.equalsIgnoreCase(userRepresentation.getUsername()))
                        .findFirst();

                if (!userRepresentationOptional.isPresent()) {
                    throw new OtpException("Username missing while verifying OTP");
                }
                ///////////////////////////////////////

//                UserRepresentation userRepresentation = userRepresentationOptional.get();
//                List<CredentialRepresentation> credentials = userRepresentation.getCredentials();
//                CredentialRepresentation credentialRepresentation = credentials.get(0);
//                credentialRepresentation.getSecretData();
//                credentialRepresentation.getValue();

                //////////////////////////////////

                if (otpUtil.verifyUserMailOtp(userRepresentationOptional.get().getId(), adminOtpDTO.getOtp())) {
                    TokenManager tokenManager = systemKeycloak.tokenManager();
                    AccessTokenResponse accessTokenResponse = tokenManager.getAccessToken();

                    return UserTokenDetailsDTO.builder()
                            .accessToken(accessTokenResponse.getToken())
                            .expiresIn(accessTokenResponse.getExpiresIn())
                            .tokenType(accessTokenResponse.getTokenType())
                            .scope(accessTokenResponse.getScope())
                            .build();
                } else {
                    throw new OtpException("OTP mismatch");
                }
            } else {
                throw new OtpException("Unable to get user details");
            }
        }else {
            throw new OtpException("OTP details missing");
        }
    }

}
