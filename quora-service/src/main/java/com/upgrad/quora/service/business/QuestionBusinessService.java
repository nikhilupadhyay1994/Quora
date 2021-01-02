package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserAuthDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;

@Service
public class QuestionBusinessService {
    @Autowired
    private QuestionDao questionDao;
    @Autowired
    private UserAuthDao userAuthDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteQuestion(String questionId, final String authorizationToken) throws AuthorizationFailedException, InvalidQuestionException {
        UserAuthEntity userAuthTokenEntity = userAuthDao.getUserAuthToken(authorizationToken);

        if(userAuthTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }else if(userAuthTokenEntity.getLogoutAt() != null)
        {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to delete an answer");
        }else {
            QuestionEntity question = questionDao.getQuestionById(questionId);
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



}
