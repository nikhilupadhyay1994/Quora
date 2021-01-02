package com.upgrad.quora.service.business;


import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserAuthDao;
import com.upgrad.quora.service.entity.Question;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class QuestionBusinessService {

    @Autowired
    private QuestionDao questionDao;

    @Autowired
    private UserAuthDao userAuthDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public Question createQuestion(final Question questionEntity, final String authenticationToken) throws AuthorizationFailedException {
        final Question createdQuestionEntity;
        UserAuthEntity userAuthEntity = userAuthDao.getUserAuthEntity(authenticationToken);
        if(userAuthEntity == null){
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
        else{
            if(userAuthEntity.getLogoutAt() != null){
                throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to post a question");
            }
            else{
                questionEntity.setUser(userAuthEntity.getUser());
                createdQuestionEntity = questionDao.createQuestion(questionEntity);
            }

        }
        return createdQuestionEntity;
    }
}
