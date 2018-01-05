package kwasilewski.marketplace.services;

import kwasilewski.marketplace.configuration.context.UserContext;
import kwasilewski.marketplace.dao.UserDAO;
import kwasilewski.marketplace.dto.UserData;
import kwasilewski.marketplace.errors.MKTException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserDAO userDAO;

    @Autowired
    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public List<UserData> getAllUsers(UserContext ctx) throws DataAccessException, MKTException {
        return userDAO.getAll(ctx);
    }

    public UserData findUser(Long id) throws DataAccessException {
        return userDAO.find(id);
    }

    public UserData findUser(String email) throws DataAccessException {
        return userDAO.find(email);
    }

    public UserData loginUser(String email, String password) throws DataAccessException, MKTException {
        return userDAO.login(email, password);
    }

    public void createUser(UserData user) throws DataAccessException, MKTException {
        userDAO.create(user);
    }

    public void promoteUser(UserContext ctx, Long userId) throws DataAccessException, MKTException {
        userDAO.promote(ctx, userId);
    }

    public void removeUser(UserContext ctx, Long userId) throws DataAccessException, MKTException {
        userDAO.remove(ctx, userId);
    }

}
