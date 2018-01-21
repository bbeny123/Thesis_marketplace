package kwasilewski.marketplace.service;

import kwasilewski.marketplace.configuration.context.UserContext;
import kwasilewski.marketplace.dao.AdDAO;
import kwasilewski.marketplace.dao.FavouriteDAO;
import kwasilewski.marketplace.dao.PhotoDAO;
import kwasilewski.marketplace.dto.AdData;
import kwasilewski.marketplace.dto.PhotoData;
import kwasilewski.marketplace.dtoext.ad.AdSearchDataExt;
import kwasilewski.marketplace.dtoext.ad.ListSearchDataExt;
import kwasilewski.marketplace.errors.MKTError;
import kwasilewski.marketplace.errors.MKTException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdService {

    private final AdDAO adDAO;
    private final PhotoDAO photoDAO;
    private final FavouriteDAO favouriteDAO;

    @Autowired
    public AdService(AdDAO adDAO, PhotoDAO photoDAO, FavouriteDAO favouriteDAO) {
        this.adDAO = adDAO;
        this.photoDAO = photoDAO;
        this.favouriteDAO = favouriteDAO;
    }

    public void createAd(AdData ad) throws DataAccessException, MKTException {
        adDAO.create(ad);
    }

    public void modifyAd(UserContext ctx, AdData ad) throws DataAccessException, MKTException {
        adDAO.modify(ctx, ad);
    }

    public void changeStatus(UserContext ctx, Long id) throws DataAccessException, MKTException {
        adDAO.changeStatus(ctx, id);
    }

    public void refreshAd(UserContext ctx, Long id) throws DataAccessException, MKTException {
        adDAO.refreshAd(ctx, id);
    }

    public void removeAd(UserContext ctx, Long id) throws DataAccessException, MKTException {
        adDAO.remove(ctx, id);
    }

    public List<AdData> getAllAds(UserContext ctx) throws DataAccessException {
        return adDAO.getAll(ctx);
    }

    public AdData get(UserContext ctx, Long id) throws DataAccessException {
        return adDAO.get(ctx, id);
    }

    @Transactional
    public AdData findAd(Long id) throws DataAccessException {
        return adDAO.find(id, true);
    }

    @Transactional
    public AdData findAd(UserContext ctx, Long id) throws DataAccessException, MKTException {
        AdData ad = adDAO.find(ctx, id, true);
        if (ad == null) throw new MKTException(MKTError.AD_NOT_EXISTS);
        ad.setFavourite(favouriteDAO.alreadyFavourite(ctx.getUserId(), ad.getId()));
        return ad;
    }

    public List<AdData> findAds(AdSearchDataExt criteria) throws DataAccessException {
        List<AdData> ads = adDAO.find(criteria);
        ads.forEach(ad -> ad.setMiniature(photoDAO.findMiniature(ad.getId())));
        return ads;
    }

    public List<AdData> findAds(UserContext ctx, AdSearchDataExt criteria) throws DataAccessException {
        List<AdData> ads = adDAO.find(ctx, criteria);
        ads.forEach(ad -> ad.setMiniature(photoDAO.findMiniature(ctx, ad.getId())));
        return ads;
    }

    public void createFavourite(UserContext ctx, Long adId) throws DataAccessException, MKTException {
        AdData ad = adDAO.find(adId, false);
        if (ad == null)
            throw new MKTException(MKTError.AD_NOT_EXISTS);
        favouriteDAO.create(ctx, ad);
    }

    public void removeFavourite(UserContext ctx, Long adId) throws DataAccessException, MKTException {
        favouriteDAO.remove(ctx, adId);
    }

    public List<AdData> findFavourites(UserContext ctx, ListSearchDataExt criteria) throws DataAccessException {
        List<AdData> ads = favouriteDAO.find(ctx, criteria);
        ads.forEach(ad -> ad.setMiniature(photoDAO.findMiniature(ad.getId())));
        return ads;
    }

    public List<PhotoData> getAllPhotos(UserContext ctx) throws DataAccessException {
        return photoDAO.getAll(ctx);
    }

}
