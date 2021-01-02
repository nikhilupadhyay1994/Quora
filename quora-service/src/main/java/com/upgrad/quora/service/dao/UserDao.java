package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

@Repository
public class UserDao {
    @PersistenceContext
    private EntityManager entityManager;


    public UserEntity createUser(UserEntity userEntity)
    {
        entityManager.persist(userEntity);
        return userEntity;
    }
    public UserEntity getUser( final String uuid) throws UserNotFoundException {
        try {
            return entityManager.createNamedQuery("userByUuid",UserEntity.class).
                    setParameter("uuid", uuid).getSingleResult();
        }catch (NoResultException nre)
        {
            return null;
        }

    }
    @Transactional
    public UserEntity deleteUser(UserEntity userEntity)
    {
         entityManager.remove(userEntity);
         return userEntity;
    }

}