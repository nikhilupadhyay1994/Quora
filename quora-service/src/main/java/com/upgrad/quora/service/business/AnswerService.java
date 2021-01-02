package com.upgrad.quora.service.business;

import com.upgrad.quora.service.Dao.AnswerDao;
import com.upgrad.quora.service.Dao.AuthTokenDao;
import com.upgrad.quora.service.entity.userAuthEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AnswerService {
    @Autowired
    AnswerDao answerDao;

    @Autowired
    AuthTokenDao authTokenDao;


    public String editAnswerContent(final String uuid, final String ans, final String authorization) throws AuthorizationFailedException, AnswerNotFoundException {

        userAuthEntity userAuthEntity=  authTokenDao.getUserAuthToken(authorization);
        if(userAuthEntity== null)
        {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
        else if(userAuthEntity.getLogoutAt() != null)
        {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to delete an answer");
        }
        else if(!(userAuthEntity.getUser().getUuid().equals(answerDao.getUserForAnswer(uuid))) && !userAuthEntity.getUser().getRole().equalsIgnoreCase("admin"))
        {
            throw new AuthorizationFailedException("ATHR-003", "Only the answer owner or admin can delete the answer");
        }
        else
        {

            if(answerDao.editAnswerContent(uuid, ans)== null)
            {
                throw new AnswerNotFoundException("ANS-001", "Entered answer uuid does not exist");
            }
        }
        return uuid;
    }
}
