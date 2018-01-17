package kwasilewski.marketplace.service;

import kwasilewski.marketplace.configuration.context.UserContext;
import kwasilewski.marketplace.dao.AdDAO;
import kwasilewski.marketplace.dao.FavouriteDAO;
import kwasilewski.marketplace.dao.PhotoDAO;
import kwasilewski.marketplace.dto.AdData;
import kwasilewski.marketplace.dto.FavouriteData;
import kwasilewski.marketplace.dtoext.ad.ListSearchDataExt;
import kwasilewski.marketplace.errors.MKTError;
import kwasilewski.marketplace.errors.MKTException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FavouriteService {

    private final FavouriteDAO favouriteDAO;
    private final AdDAO adDAO;
    private final PhotoDAO photoDAO;

    @Autowired
    public FavouriteService(FavouriteDAO favouriteDAO, AdDAO adDAO, PhotoDAO photoDAO) {
        this.favouriteDAO = favouriteDAO;
        this.adDAO = adDAO;
        this.photoDAO = photoDAO;
    }

    public void createFavourite(UserContext ctx, Long adId) throws DataAccessException, MKTException {
        if (adDAO.find(adId, false) == null)
            throw new MKTException(MKTError.AD_NOT_EXISTS);
        favouriteDAO.create(ctx, adId);
    }

    public void removeFavourite(UserContext ctx, Long adId) throws DataAccessException, MKTException {
        favouriteDAO.remove(ctx, adId);
    }

    public List<AdData> findFavourites(UserContext ctx, ListSearchDataExt criteria) throws DataAccessException {
        List<AdData> ads = favouriteDAO.find(ctx, criteria);
        ads.forEach(ad -> ad.setMiniature(photoDAO.findMiniature(ad.getId())));
        return ads;
    }

    public FavouriteData findFavourite(UserContext ctx, Long id) throws DataAccessException {
        return favouriteDAO.find(ctx, id);
    }

}
