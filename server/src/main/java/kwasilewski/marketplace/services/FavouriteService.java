package kwasilewski.marketplace.services;

import kwasilewski.marketplace.dao.FavouriteDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FavouriteService {

    private final FavouriteDAO favouriteDAO;

    @Autowired
    public FavouriteService(FavouriteDAO favouriteDAO) {
        this.favouriteDAO = favouriteDAO;
    }

}
