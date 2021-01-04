package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.Question;
import org.springframework.stereotype.Repository;
import javax.persistence.EntityManager;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

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

    @Transactional
    public String editQuestionContent(final String uuid, final String ques)
    {
        try
        {

            Query query= entityManager.createQuery("update Question q set q.content=:ques where q.uuid=:uuid");
            query.setParameter("ques", ques);
            query.setParameter("uuid", uuid);
            query.executeUpdate();

        }catch (NoResultException nre)
        {
            return null;
        }
        return uuid;
    }

    public String getUserForQuestion(final String uuid) {
        try {
            return entityManager.createNamedQuery("UserForQuestion",
                    Question.class).setParameter("uuid", uuid).getSingleResult().getUser().getUuid();
        } catch (NoResultException nre) {
            return null;
        }
    }
}
