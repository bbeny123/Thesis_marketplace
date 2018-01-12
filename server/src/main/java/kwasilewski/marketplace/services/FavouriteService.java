package kwasilewski.marketplace.services;

import kwasilewski.marketplace.configuration.context.UserContext;
import kwasilewski.marketplace.dao.FavouriteDAO;
import kwasilewski.marketplace.dto.AdData;
import kwasilewski.marketplace.dto.FavouriteData;
import kwasilewski.marketplace.dtoext.ad.ListRequest;
import kwasilewski.marketplace.errors.MKTException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FavouriteService {

    private final FavouriteDAO favouriteDAO;

    @Autowired
    public FavouriteService(FavouriteDAO favouriteDAO) {
        this.favouriteDAO = favouriteDAO;
    }


    public void createFavourite(UserContext ctx, Long adId) throws DataAccessException, MKTException {
        favouriteDAO.create(ctx, adId);
    }

    public void removeFavourite(UserContext ctx, Long adId) throws DataAccessException, MKTException {
        favouriteDAO.remove(ctx, adId);
    }

    public List<AdData> findFavourites(UserContext ctx, ListRequest criteria) throws DataAccessException {
        return favouriteDAO.find(ctx, criteria);
    }

    public FavouriteData findFavourite(UserContext ctx, Long id) throws DataAccessException {
        return favouriteDAO.find(ctx, id);
    }

}
