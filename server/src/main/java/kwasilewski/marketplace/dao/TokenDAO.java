package kwasilewski.marketplace.dao;

import kwasilewski.marketplace.dto.TokenData;
import kwasilewski.marketplace.dto.UserData;
import kwasilewski.marketplace.errors.MKTError;
import kwasilewski.marketplace.errors.MKTException;
import kwasilewski.marketplace.util.JwtTokenUtil;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

@Repository
public class TokenDAO {

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public String create(Long usrId) throws DataAccessException {
        String token = JwtTokenUtil.createToken(usrId);
        TokenData tokenData = new TokenData();
        tokenData.setUsrId(usrId);
        tokenData.setToken(token);
        this.em.persist(tokenData);
        return token;
    }

    public UserData check(String token) throws DataAccessException, MKTException {
        TypedQuery<UserData> query = this.em.createQuery("SELECT tkn.user FROM TokenData tkn WHERE tkn.token = :token AND tkn.date >= :date", UserData.class);
        query.setParameter("token", token);
        query.setParameter("date", JwtTokenUtil.minimumTokenDate());
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            throw new MKTException(MKTError.WRONG_TOKEN);
        }
    }

}