package com.upgrad.quora.service.business;


import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserAuthDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.Question;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class QuestionBusinessService {

    @Autowired
    private QuestionDao questionDao;
    @Autowired
    private UserAuthDao userAuthDao;

    @Autowired
    private UserDao userDao;

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

    public void deleteQuestion(String questionId, final String authorizationToken) throws AuthorizationFailedException, InvalidQuestionException {
        UserAuthEntity userAuthTokenEntity = userAuthDao.getUserAuthEntity(authorizationToken);

        if(userAuthTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }else if(userAuthTokenEntity.getLogoutAt() != null)
        {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to delete an answer");
        }else {
            Question question = questionDao.getQuestionById(questionId);
            if(question==null){
                throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
            }else {
                UserEntity user = question.getUser();
                UserEntity loggedInUser = userAuthTokenEntity.getUser();

                if (user.getRole().equalsIgnoreCase("nonadmin") && user.getId() != loggedInUser.getId()) {
                    throw new AuthorizationFailedException("ATHR-003", "Only the question owner or admin can delete the question");
                }else {
                    questionDao.deleteQuestion(questionId);
                }
            }


        }




    }

    public boolean getQuestionById(final String questionId)
    {
        if(questionDao.getQuestionById(questionId)!=null)
        {
            return true;
        }

        return false;
    }
    public List<Question> getAllQuestion(final String authorization) throws AuthorizationFailedException {
        UserAuthEntity userAuthEntity=  userAuthDao.getUserAuthEntity(authorization);
        if(userAuthEntity== null)
        {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
        else if(userAuthEntity.getLogoutAt() != null)
        {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.");
        }
        else {
            return questionDao.getAllQuestion();
        }
    }

    public List<Question> getAllQuestionByUser(final String authorization , final String userUUId) throws AuthorizationFailedException, UserNotFoundException {
        UserAuthEntity userAuthEntity=  userAuthDao.getUserAuthEntity(authorization);
        if(userAuthEntity== null)
        {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
        else if(userAuthEntity.getLogoutAt() != null)
        {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.");
        }
        else{
            UserEntity userEntity=userDao.getUser(userUUId);
            if (userEntity== null)
            {
                throw new UserNotFoundException("USR-001","User with entered uuid to be deleted does not exist");
            }
            else
            {
                return questionDao.getAllQuestionByUserId(userEntity.getId());
            }
        }

    }

    public String editQuestionContent(final String uuid, final String ques, final String authorization) throws AuthorizationFailedException, InvalidQuestionException {

        UserAuthEntity userAuthEntity=  userAuthDao.getUserAuthEntity(authorization);
        if(userAuthEntity== null)
        {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
        else if(userAuthEntity.getLogoutAt() != null)
        {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to delete an answer");
        }
        else if(!(userAuthEntity.getUser().getUuid().equals(questionDao.getUserForQuestion(uuid))) && !userAuthEntity.getUser().getRole().equalsIgnoreCase("admin"))
        {
            throw new AuthorizationFailedException("ATHR-003", "Only the question owner or admin can delete the question");
        }
        else
        {

            if(questionDao.editQuestionContent(uuid, ques)== null)
            {
                throw new InvalidQuestionException("ANS-001", "Entered question uuid does not exist");
            }
        }
        return uuid;
    }

}
