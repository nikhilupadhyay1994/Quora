package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Repository
public class QuestionDao {
    @PersistenceContext
    private EntityManager em;


    public void deleteQuestion(Integer questionId) {
        EntityTransaction transaction = em.getTransaction();

        try {
            transaction.begin();
            QuestionEntity question = (QuestionEntity) em.find(QuestionEntity.class, questionId);
            em.remove(question);
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
        }
    }

    public QuestionEntity getQuestionById( Integer questionId) throws InvalidQuestionException {
        try {
            return em.createNamedQuery("questionById",QuestionEntity.class).
                    setParameter("questionId", questionId).getSingleResult();
        }catch (NoResultException nre)
        {
            return null;
        }

    }


}
