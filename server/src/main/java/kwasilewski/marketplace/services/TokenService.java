package kwasilewski.marketplace.services;

import kwasilewski.marketplace.dao.TokenDAO;
import kwasilewski.marketplace.dto.UserData;
import kwasilewski.marketplace.errors.MKTException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

@Service
public class TokenService {

    private final TokenDAO tokenDAO;

    @Autowired
    public TokenService(TokenDAO tokenDAO) {
        this.tokenDAO = tokenDAO;
    }

    public UserData checkToken(String token) throws DataAccessException, MKTException {
        return tokenDAO.check(token);
    }

}
