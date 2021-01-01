package com.upgrad.quora.service.business;


import com.upgrad.quora.service.dao.UserAuthDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

@Service
public class AuthenticationService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserAuthDao userAuthDao;

    @Autowired
    private PasswordCryptographyProvider cryptographyProvider;

    @Transactional(propagation = Propagation.REQUIRED)
    public UserAuthEntity authenticate(final String username, final String password) throws AuthenticationFailedException {
        UserEntity userEntity = userDao.getUserByUserName(username);
        if(userEntity == null){
            throw new AuthenticationFailedException("ATH-001", "This username does not exist");
        }

        final String encryptedPassword = cryptographyProvider.encrypt(password, userEntity.getSalt());
        if(encryptedPassword.equals(userEntity.getPassword())){
            JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(encryptedPassword);
            UserAuthEntity userAuthToken = new UserAuthEntity();
            userAuthToken.setUser(userEntity);

            final ZonedDateTime now = ZonedDateTime.now();
            final ZonedDateTime expiresAt = now.plusHours(8);
            userAuthToken.setAccessToken(jwtTokenProvider.generateToken(userEntity.getUuid(), now, expiresAt));

            userAuthToken.setLoginAt(now);
            userAuthToken.setExpiresAt(expiresAt);
            userAuthDao.createAuthToken(userAuthToken);
            userDao.updateUser(userEntity);

            return userAuthToken;
        }
        else{
            throw new AuthenticationFailedException("ATH-002","Password failed");
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public UserAuthEntity signOut(final String authenticationToken) throws SignOutRestrictedException {
        UserAuthEntity userAuthEntity = userAuthDao.getUserAuthEntity(authenticationToken);
        if(userAuthEntity == null){
            throw new SignOutRestrictedException("SGR-001", "User is not Signed in");
        }
        else{
            final ZonedDateTime now = ZonedDateTime.now();
            userAuthEntity.setLogoutAt(now);
            userAuthDao.createAuthToken(userAuthEntity);
        }
        return userAuthEntity;
    }
}
