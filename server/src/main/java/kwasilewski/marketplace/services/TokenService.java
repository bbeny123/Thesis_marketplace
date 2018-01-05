package kwasilewski.marketplace.services;

import kwasilewski.marketplace.dao.TokenDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TokenService {

    private final TokenDAO tokenDAO;

    @Autowired
    public TokenService(TokenDAO tokenDAO) {
        this.tokenDAO = tokenDAO;
    }

}
