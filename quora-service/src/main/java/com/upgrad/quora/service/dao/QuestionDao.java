package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.Question;
import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class QuestionDao {

    @PersistenceContext
    private EntityManager entityManager;

    public Question createQuestion(Question questionEntity)
    {
        entityManager.persist(questionEntity);
        return questionEntity;
    }

}
