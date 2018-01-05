package kwasilewski.marketplace.dao;

import kwasilewski.marketplace.dto.TokenData;
import kwasilewski.marketplace.errors.MKTError;
import kwasilewski.marketplace.errors.MKTException;
import kwasilewski.marketplace.util.JwtTokenUtil;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
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

    public boolean check(String token) throws DataAccessException, MKTException {
        TypedQuery<Long> query = this.em.createQuery("SELECT COUNT(tkn) FROM TokenData tkn WHERE tkn.token = :token AND tkn.date >= :date", Long.class);
        query.setParameter("token", token);
        query.setParameter("date", JwtTokenUtil.minimumTokenDate());
        if (query.getSingleResult() != 0) {
            return true;
        } else {
            throw new MKTException(MKTError.WRONG_TOKEN);
        }
    }

}
