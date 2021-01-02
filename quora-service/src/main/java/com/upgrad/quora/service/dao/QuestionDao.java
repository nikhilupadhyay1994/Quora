package com.upgrad.quora.service.dao;


import com.upgrad.quora.service.entity.Question;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class QuestionDao {
    @PersistenceContext
    private EntityManager entityManager;

    public Question getQuestionById( String questionId)   {
        try {
            return entityManager.createNamedQuery("questionById",Question.class).
                    setParameter("questionId", questionId).getSingleResult();
        }catch (NoResultException nre)
        {
            return null;
        }

    }
    @Transactional
    public void deleteQuestion(String questionId) {
        Question question = this.getQuestionById(questionId);
        entityManager.remove(question);
    }

    public List<Question> getAllQuestion()
    {
        try
        {
            return entityManager.createNamedQuery("Allquestion",
                    Question.class).getResultList();
        }catch (NoResultException nre)
        {
            return null;
        }
    }

    public List<Question> getAllQuestionByUserId(final Integer id)
    {
        try
        {
            return entityManager.createNamedQuery("AllquestionByUserId",
                    Question.class).setParameter("id", id).getResultList();
        }catch (NoResultException nre)
        {
            return null;
        }
    }

}
