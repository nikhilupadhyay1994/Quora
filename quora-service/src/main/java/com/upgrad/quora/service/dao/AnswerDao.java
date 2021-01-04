package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.Answer;
import org.springframework.stereotype.Repository;

import javax.persistence.*;
import javax.transaction.Transactional;
import java.util.List;

@Repository
public class AnswerDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public Answer createAnswer(Answer answerEntity)
    {
        entityManager.persist(answerEntity);
        return answerEntity;
    }

    @Transactional
    public String editAnswerContent(final String uuid, final String ans)
    {
        try
        {

            Query query= entityManager.createQuery("update Answer a set a.ans=:ans where a.uuid=:uuid");
            query.setParameter("ans", ans);
            query.setParameter("uuid", uuid);
            query.executeUpdate();

        }catch (NoResultException nre)
        {
            return null;
        }
        return uuid;
    }

    public String getUserForAnswer(final String uuid)
    {
        try
        {
            return entityManager.createNamedQuery("UserForAnswer",
                    Answer.class).setParameter("uuid", uuid).getSingleResult().getUser().getUuid();
        }catch (NoResultException nre)
        {
            return null;
        }

    }

    public List<Answer> getAnswersForQuestionId(String questionId) {
        try
        {

            // List answers= entityManager.createQuery("SELECT a from Answer a JOIN QuestionEntity q where q.uuid =:questionId", Answer.class).setParameter("questionId", questionId).getResultList();
            List answers= entityManager.createQuery("SELECT a from Answer a where a.question.uuid =:questionId", Answer.class).setParameter("questionId", questionId).getResultList();


            return answers;
        }
        catch (NoResultException nre)
        {
            return null;
        }
    }

    public Answer getAnswerById(String answerId){
        try{
            return entityManager.createQuery("select a from Answer a where a.uuid=:uuid", Answer.class).setParameter("uuid", answerId).getSingleResult();
        }
        catch(NoResultException ex){
            return null;
        }
    }

    @Transactional
    public void deleteAnswer(String answerId){
        Answer answerEntity = this.getAnswerById(answerId);
        entityManager.remove(answerEntity);
    }
}




