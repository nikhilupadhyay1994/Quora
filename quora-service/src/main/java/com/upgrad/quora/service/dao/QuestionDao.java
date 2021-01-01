package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Repository
public class QuestionDao {
    @PersistenceContext
    private EntityManager em;

    @Transactional
    public void deleteQuestion(String questionId) {
        QuestionEntity question = this.getQuestionById(questionId);
        em.remove(question);
    }

    public QuestionEntity getQuestionById( String questionId)   {
        try {
            return em.createNamedQuery("questionById",QuestionEntity.class).
                    setParameter("questionId", questionId).getSingleResult();
        }catch (NoResultException nre)
        {
            return null;
        }

    }


}
