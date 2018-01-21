package kwasilewski.marketplace.service;

import kwasilewski.marketplace.configuration.context.UserContext;
import kwasilewski.marketplace.dao.TokenDAO;
import kwasilewski.marketplace.dao.UserDAO;
import kwasilewski.marketplace.dto.UserData;
import kwasilewski.marketplace.dtoext.user.PasswordDataExt;
import kwasilewski.marketplace.errors.MKTException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserDAO userDAO;
    private final TokenDAO tokenDAO;

    @Autowired
    public UserService(UserDAO userDAO, TokenDAO tokenDAO) {
        this.userDAO = userDAO;
        this.tokenDAO = tokenDAO;
    }

    public void createUser(UserData user) throws DataAccessException, MKTException {
        userDAO.create(user);
    }

    public UserData modifyUser(UserContext ctx, UserData user) throws DataAccessException, MKTException {
        return userDAO.modify(ctx, user);
    }

    public void changeUserPassword(UserContext ctx, Long userId, PasswordDataExt passwordData) throws DataAccessException, MKTException {
        userDAO.changePassword(ctx, userId, passwordData);
    }

    public void promoteUser(UserContext ctx, Long userId) throws DataAccessException, MKTException {
        userDAO.promote(ctx, userId);
    }

    public void removeUser(UserContext ctx, Long userId) throws DataAccessException, MKTException {
        userDAO.remove(ctx, userId);
    }

    public UserData loginUser(String email, String password) throws DataAccessException, MKTException {
        UserData user = userDAO.login(email, password);
        user.setToken(tokenDAO.create(user.getId()));
        return user;
    }

    public UserData checkToken(String token) throws DataAccessException, MKTException {
        return tokenDAO.check(token);
    }

    public List<UserData> getAllUsers(UserContext ctx) throws DataAccessException, MKTException {
        return userDAO.getAll(ctx);
    }

    public UserData getUser(Long id) throws DataAccessException {
        return userDAO.getUser(id);
    }

    public UserData getUser(String email) throws DataAccessException {
        return userDAO.getUser(email);
    }

}
