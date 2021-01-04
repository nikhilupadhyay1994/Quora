package com.upgrad.quora.service.business;


import com.upgrad.quora.service.dao.UserAuthDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminService {
    @Autowired
    UserDao userDao;
    @Autowired
    UserAuthDao userAuthDao;


    public UserEntity deleteUser(final String userUuid, final String authorization) throws UserNotFoundException ,AuthorizationFailedException {
        UserAuthEntity userAuthEntity=  userAuthDao.getUserAuthEntity(authorization);
        if(userAuthEntity== null)
        {
                throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
        else if(userAuthEntity.getLogoutAt() != null)
        {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.");
        }
        else if(!userAuthEntity.getUser().getRole().equalsIgnoreCase("admin"))
        {
            throw new AuthorizationFailedException("ATHR-003", "Unauthorized Access, Entered user is not an admin");
        }else
        {
            UserEntity userEntity=userDao.getUser(userUuid);
            if (userEntity== null)
            {
                throw new UserNotFoundException("USR-001","User with entered uuid to be deleted does not exist");
            }
            else
            {
                return userDao.deleteUser(userEntity);
            }
        }

    }


}
