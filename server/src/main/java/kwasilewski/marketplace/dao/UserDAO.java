package kwasilewski.marketplace.dao;


import kwasilewski.marketplace.configuration.context.UserContext;
import kwasilewski.marketplace.dto.UserData;
import kwasilewski.marketplace.errors.MKTError;
import kwasilewski.marketplace.errors.MKTException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;


@Repository
public class UserDAO {

    @PersistenceContext
    private EntityManager em;

    private final TokenDAO tokenDAO;

    @Autowired
    public UserDAO(TokenDAO tokenDAO) {
        this.tokenDAO = tokenDAO;
    }

    public List<UserData> getAll(UserContext ctx) throws DataAccessException, MKTException {
        if (!ctx.isAdmin()) throw new MKTException(MKTError.NOT_AUTHORIZED);
        TypedQuery<UserData> query = this.em.createQuery("SELECT user FROM UserData user", UserData.class);
        return query.getResultList();
    }

    public UserData find(Long id) throws DataAccessException {
        TypedQuery<UserData> query = this.em.createQuery("SELECT user FROM UserData user WHERE user.id = :id", UserData.class);
        query.setParameter("id", id);
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public UserData find(String email) throws DataAccessException {
        TypedQuery<UserData> query = this.em.createQuery("SELECT user FROM UserData user WHERE user.email = :email", UserData.class);
        query.setParameter("email", email);
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Transactional
    public UserData login(String email, String password) throws DataAccessException, MKTException {
        TypedQuery<UserData> query = this.em.createQuery("SELECT user FROM UserData user WHERE user.email = :email AND user.password = :password", UserData.class);
        query.setParameter("email", email);
        query.setParameter("password", password);
        UserData user;
        try {
            user = query.getSingleResult();
        } catch (NoResultException e) {
            throw new MKTException(MKTError.USER_INVALID_LOGIN_OR_PASSWORD);
        }
        user.setToken(tokenDAO.create(user.getId()));
        return user;
    }

    @Transactional
    public void create(UserData user) throws DataAccessException, MKTException {
        if (find(user.getEmail()) != null) throw new MKTException(MKTError.USER_ALREADY_EXISTS);
        this.em.persist(user);
    }

    @Transactional
    public void modify(UserContext ctx, UserData user) throws DataAccessException, MKTException {
        if (user.getId() == null) throw new MKTException(MKTError.AD_NOT_EXISTS);
        else if (!ctx.isAdmin() || !user.getId().equals(ctx.getUserId()) || find(user.getId()) == null)
            throw new MKTException(MKTError.NOT_AUTHORIZED);
        this.em.merge(user);
    }

    @Transactional
    public void promote(UserContext ctx, Long id) throws DataAccessException, MKTException {
        UserData user = id == null ? null : find(id);
        if (user == null) throw new MKTException(MKTError.USER_NOT_EXISTS);
        else if (!ctx.isAdmin() || user.getId().equals(ctx.getUserId()))
            throw new MKTException(MKTError.NOT_AUTHORIZED);
        user.setAdmin(true);
        this.em.merge(user);
    }

    @Transactional
    public void remove(UserContext ctx, Long id) throws DataAccessException, MKTException {
        UserData user = id == null ? null : find(id);
        if (user == null) throw new MKTException(MKTError.USER_NOT_EXISTS);
        else if (!ctx.isAdmin() || user.getId().equals(ctx.getUserId()))
            throw new MKTException(MKTError.NOT_AUTHORIZED);
        this.em.remove(user);
    }

}
