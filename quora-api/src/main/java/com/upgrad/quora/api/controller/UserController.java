package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.SigninResponse;
import com.upgrad.quora.api.model.SignoutResponse;
import com.upgrad.quora.api.model.SignupUserRequest;
import com.upgrad.quora.api.model.SignupUserResponse;
import com.upgrad.quora.service.business.AuthenticationService;
import com.upgrad.quora.service.business.SignupBusinessService;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class UserController {
    @Autowired
    private SignupBusinessService signupBusinessService;
   
    @Autowired
    private AuthenticationService authenticationService;

        @RequestMapping(method = RequestMethod.POST, path = "/user/signup", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
        public ResponseEntity<SignupUserResponse> signup(final SignupUserRequest signupUserRequest) throws SignUpRestrictedException {
            final UserEntity userEntity = new UserEntity();
            userEntity.setUuid(UUID.randomUUID().toString());
            userEntity.setFirstName(signupUserRequest.getFirstName());
            userEntity.setUserName(signupUserRequest.getUserName());
            userEntity.setDob(signupUserRequest.getDob());
            userEntity.setLastName(signupUserRequest.getLastName());
            userEntity.setEmail(signupUserRequest.getEmailAddress());
            userEntity.setPassword(signupUserRequest.getPassword());
            userEntity.setCountry(signupUserRequest.getCountry());
            userEntity.setAboutMe(signupUserRequest.getAboutMe());
            userEntity.setRole("nonadmin");
            userEntity.setContactNumber(signupUserRequest.getContactNumber());
            userEntity.setSalt("1234abc");

            final UserEntity createdUserEntity;
        createdUserEntity = signupBusinessService.signup(userEntity);
        SignupUserResponse userResponse = new SignupUserResponse().id(createdUserEntity.getUuid()).status("REGISTERED");
            return new ResponseEntity<SignupUserResponse>(userResponse, HttpStatus.CREATED);
        }

        @RequestMapping(method = RequestMethod.POST, path="/user/signin", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
        public ResponseEntity<SigninResponse> signin(@RequestHeader("authorization") final String signinDetails) throws AuthenticationFailedException {
            byte[] decode = Base64.getDecoder().decode(signinDetails.split("Basic ")[1]);
            String decodedText = new String(decode);
            String [] decodedArray = decodedText.split(":");

            UserAuthEntity userAuthToken = authenticationService.authenticate(decodedArray[0], decodedArray[1]);
            UserEntity user = userAuthToken.getUser();

            SigninResponse authorizedUserResponse = new SigninResponse().id(UUID.fromString(user.getUuid()).toString())
                    .message("SIGNED IN SUCCESSFULLY");


            HttpHeaders headers = new HttpHeaders();
            headers.add("access-token", userAuthToken.getAccessToken());

            return new ResponseEntity<SigninResponse>(authorizedUserResponse, headers, HttpStatus.OK);
        }

        @RequestMapping(method = RequestMethod.POST, path="/user/signout", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
        public ResponseEntity<SignoutResponse> signout(@RequestHeader("authorization") final String signoutDetails) throws SignOutRestrictedException {
            UserAuthEntity userAuthToken = authenticationService.signOut(signoutDetails);
            SignoutResponse signoutResponse = new SignoutResponse().id(userAuthToken.getUser().getUuid()).message("SIGNED OUT SUCCESSFULLY");
            return new ResponseEntity<SignoutResponse>(signoutResponse, HttpStatus.OK);
        }

}
