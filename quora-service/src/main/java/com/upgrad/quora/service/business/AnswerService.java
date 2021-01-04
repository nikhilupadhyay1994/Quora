package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.AnswerDao;
import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserAuthDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.Answer;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AnswerService {
    @Autowired
    private AnswerDao answerDao;

    @Autowired
    private QuestionDao questionDao;

    @Autowired
    private UserAuthDao authTokenDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private QuestionBusinessService questionBusinessService;

    @Transactional(propagation = Propagation.REQUIRED)
    public Answer createAnswer(final Answer answerEntity, final String authenticationToken, final String questionId) throws AuthorizationFailedException, InvalidQuestionException {
        final Answer createdAnswerEntity;
        if (!questionBusinessService.getQuestionById(questionId)) {
            throw new InvalidQuestionException("QUES-001", "The question entered is invalid");
        }
        else{
            UserAuthEntity userAuthEntity = authTokenDao.getUserAuthEntity(authenticationToken);
            if (userAuthEntity == null) {
                throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
            } else {
                if (userAuthEntity.getLogoutAt() != null) {
                    throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to post a answer");
                } else {
                    answerEntity.setUser(userAuthEntity.getUser());
                    createdAnswerEntity = answerDao.createAnswer(answerEntity);
                    return createdAnswerEntity;
                }
            }
        }

    }

    public String editAnswerContent(final String uuid, final String ans, final String authorization) throws AuthorizationFailedException, AnswerNotFoundException {

        UserAuthEntity userAuthEntity=  authTokenDao.getUserAuthEntity(authorization);
        if(userAuthEntity== null)
        {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
        else if(userAuthEntity.getLogoutAt() != null)
        {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to edit an answer");
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

    public List<Answer> getAnswersForQuestionId(String questionId, String authorization) throws AuthorizationFailedException, InvalidQuestionException {

        UserAuthEntity userAuthTokenEntity = authTokenDao.getUserAuthEntity(authorization);

        if(userAuthTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }else if(userAuthTokenEntity.getLogoutAt() != null)
        {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to delete an answer");
        }else {
            List answers=  answerDao.getAnswersForQuestionId(questionId);
            if(answers.isEmpty()){
                throw new InvalidQuestionException("QUES-001", "The question with entered uuid whose details are to be seen does not exist");
            }
            return answers;
        }


    }

    public Answer deleteAnswer(final String answerId, final String authorization) throws AuthorizationFailedException, UserNotFoundException, AnswerNotFoundException {

        UserAuthEntity userAuthEntity =  authTokenDao.getUserAuthEntity(authorization);
        Answer deletedAnswer = answerDao.getAnswerById(answerId);
        UserEntity loggedInUser;

        if(deletedAnswer != null){
            if(userAuthEntity != null){
                loggedInUser = userAuthEntity.getUser();
                if(userAuthEntity.getLogoutAt() != null){
                    throw new AuthorizationFailedException("ATHR-002","User is signed out.Sign in first to delete an answer");
                }
                else{
                    if(deletedAnswer.getUser().getUuid().equals(loggedInUser.getUuid()) || loggedInUser.getRole().equals("admin")){
                        answerDao.deleteAnswer(answerId);
                    }
                    else{
                        throw new AuthorizationFailedException("ATHR-003","Only the answer owner or admin can delete the answer");
                    }
                }
            }
            else{
                throw new AuthorizationFailedException("ATHR-001","User has not signed in");
            }
        }
        else{
            throw new AnswerNotFoundException("ANS-001","Entered answer uuid does not exist");
        }

        return deletedAnswer;

    }
}
