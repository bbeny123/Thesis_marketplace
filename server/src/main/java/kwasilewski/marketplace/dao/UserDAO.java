package kwasilewski.marketplace.dao;


import kwasilewski.marketplace.configuration.context.UserContext;
import kwasilewski.marketplace.dto.UserData;
import kwasilewski.marketplace.dtoext.user.PasswordDataExt;
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

    @Transactional
    public void create(UserData user) throws DataAccessException, MKTException {
        if (user.getId() != null || getUser(user.getEmail()) != null) throw new MKTException(MKTError.USER_ALREADY_EXISTS);
        this.em.persist(user);
    }

    @Transactional
    public UserData modify(UserContext ctx, UserData user) throws DataAccessException, MKTException {
        UserData usr = user.getId() == null ? null : getUser(user.getId());
        if (usr == null) throw new MKTException(MKTError.USER_NOT_EXISTS);
        else if ((ctx.isUser() && !usr.getId().equals(ctx.getUserId())) || (!usr.getEmail().equals(user.getEmail()) && getUser(user.getEmail()) != null))
            throw new MKTException(MKTError.NOT_AUTHORIZED);
        return this.em.merge(user);
    }
    @Transactional
    public void changePassword(UserContext ctx, Long id, PasswordDataExt passwordData) throws DataAccessException, MKTException {
        UserData user = id == null ? null : getUser(id);
        if (user == null) throw new MKTException(MKTError.USER_NOT_EXISTS);
        else if(ctx.isUser() && (!user.getId().equals(ctx.getUserId()) || !user.getPassword().equals(passwordData.getOldPassword()))) {
            throw new MKTException(MKTError.NOT_AUTHORIZED);
        }
        user.setPassword(passwordData.getNewPassword());
        this.em.merge(user);
    }


    @Transactional
    public void promote(UserContext ctx, Long id) throws DataAccessException, MKTException {
        UserData user = id != null ? getUser(id) : null;
        if (user == null) throw new MKTException(MKTError.USER_NOT_EXISTS);
        else if (ctx.isUser() || user.getId().equals(ctx.getUserId()))
            throw new MKTException(MKTError.NOT_AUTHORIZED);
        user.setAdmin(true);
        this.em.merge(user);
    }

    @Transactional
    public void remove(UserContext ctx, Long id) throws DataAccessException, MKTException {
        if (ctx.isUser() || id == null || id.equals(ctx.getUserId())) throw new MKTException(MKTError.NOT_AUTHORIZED);
        UserData user = getUser(id);
        if (user == null) throw new MKTException(MKTError.USER_NOT_EXISTS);
        this.em.remove(user);
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
            throw new MKTException(MKTError.USER_INVALID_CREDENTIALS);
        }
        user.setToken(tokenDAO.create(user.getId()));
        return user;
    }

    public List<UserData> getAll(UserContext ctx) throws DataAccessException, MKTException {
        if (ctx.isUser()) throw new MKTException(MKTError.NOT_AUTHORIZED);
        TypedQuery<UserData> query = this.em.createQuery("SELECT user FROM UserData user", UserData.class);
        return query.getResultList();
    }

    public UserData getUser(Long id) throws DataAccessException {
        TypedQuery<UserData> query = this.em.createQuery("SELECT user FROM UserData user WHERE user.id = :id", UserData.class);
        query.setParameter("id", id);
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public UserData getUser(String email) throws DataAccessException {
        TypedQuery<UserData> query = this.em.createQuery("SELECT user FROM UserData user WHERE user.email = :email", UserData.class);
        query.setParameter("email", email);
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

}
