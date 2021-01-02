package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.UserEntity;
//import com.upgrad.quora.service.entity.userAuthEntity;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

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
    public UserEntity deleteUser(UserEntity userEntity)
    {
        entityManager.remove(userEntity);
        return userEntity;
    }


    public UserEntity getUserByEmail(String email) {
        try {
            return entityManager.createNamedQuery("userByEmail",UserEntity.class).
                    setParameter("email", email).getSingleResult();
        }catch (NoResultException nre)
        {
            return null;
        }

    }

    public UserEntity getUserByUserName(String userName) {
        try {
            UserEntity ut;
            ut = entityManager.createNamedQuery("userByUserName",UserEntity.class).setParameter("userName", userName).getSingleResult();
            return ut;
        }catch (NoResultException nre)
        {
            return null;
        }
    }



    public void updateUser(final UserEntity updatedUserEntity){
        entityManager.merge(updatedUserEntity);
    }

}