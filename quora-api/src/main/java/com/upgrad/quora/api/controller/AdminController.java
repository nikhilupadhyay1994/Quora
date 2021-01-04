package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.UserDeleteResponse;
import com.upgrad.quora.service.business.AdminService;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping

public class AdminController {

    @Autowired
    AdminService adminService;

    @RequestMapping(method = RequestMethod.DELETE, path = "/admin/user/{userId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UserDeleteResponse> deleteUser(@RequestHeader("authorization") final String authorization, @PathVariable("userId") final String userUuid) throws AuthorizationFailedException, UserNotFoundException {
        UserDeleteResponse userDeleteResponse = null;

             UserEntity userEntity = adminService.deleteUser(userUuid,authorization);
             userDeleteResponse= new UserDeleteResponse().id(userEntity.getUuid()).status("USER SUCCESSFULLY DELETED");
           

        return new ResponseEntity<UserDeleteResponse>(userDeleteResponse, HttpStatus.OK);
    }

}
