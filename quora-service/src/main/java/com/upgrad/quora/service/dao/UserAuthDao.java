package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.UserAuthEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Repository
public class UserAuthDao {

    @PersistenceContext
    private EntityManager entityManager;
    
  /*public UserAuthEntity getUserAuthToken(final String accessToken)  {
        try
        {
            return entityManager.createNamedQuery("userAuthTokenByAccessToken",
                    UserAuthEntity.class).setParameter("accessToken", accessToken).getSingleResult();
        }
        catch (NoResultException nre)
        {
            return null;
        }

  }*/


    public UserAuthEntity getUserAuthEntity(final String accessToken){
        try {
            return entityManager.createNamedQuery("userAuthTokenbyAccessToken", UserAuthEntity.class).setParameter("accessToken", accessToken).getSingleResult();
        }
        catch(NoResultException ex){
            return null;
        }
    }

    public UserAuthEntity createAuthToken(final UserAuthEntity userAuthTokenEntity){
        entityManager.persist(userAuthTokenEntity);
        return userAuthTokenEntity;
    }
}
