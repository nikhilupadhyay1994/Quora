package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.Question;
import org.springframework.stereotype.Repository;
import javax.persistence.EntityManager;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Repository
public class QuestionDao {


    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public Question createQuestion(Question questionEntity)
    {
        entityManager.persist(questionEntity);
        return questionEntity;
    }

    @Transactional
    public void deleteQuestion(String questionId) {
        Question question = this.getQuestionById(questionId);
        entityManager.remove(question);
    }

    public Question getQuestionById( String questionId)   {
        try {
            return entityManager.createNamedQuery("questionById",Question.class).
                    setParameter("questionId", questionId).getSingleResult();
        }catch (NoResultException nre)
        {
            return null;
        }

    }

}
