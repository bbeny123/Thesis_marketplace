package kwasilewski.marketplace.services;

import kwasilewski.marketplace.configuration.context.UserContext;
import kwasilewski.marketplace.dao.AdDAO;
import kwasilewski.marketplace.dto.AdData;
import kwasilewski.marketplace.dto.requests.AdSearchRequest;
import kwasilewski.marketplace.dto.requests.UserAdsRequest;
import kwasilewski.marketplace.errors.MKTException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdService {

    private final AdDAO adDAO;

    @Autowired
    public AdService(AdDAO adDAO) {
        this.adDAO = adDAO;
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

    public void removeAd(UserContext ctx, Long id) throws DataAccessException, MKTException {
        adDAO.remove(ctx, id);
    }

    public List<AdData> getAllAds(UserContext ctx) throws DataAccessException {
        return adDAO.getAll(ctx);
    }

    public AdData get(UserContext ctx, Long id) throws DataAccessException {
        return adDAO.get(ctx, id);
    }

    public AdData findAd(Long id) throws DataAccessException {
        return adDAO.find(id);
    }

    public AdData findAd(UserContext ctx, Long id) throws DataAccessException {
        return adDAO.find(ctx, id);
    }

    public List<AdData> findAds(AdSearchRequest criteria) throws DataAccessException {
        return adDAO.find(criteria);
    }

    public List<AdData> findAds(UserContext ctx, UserAdsRequest criteria) throws DataAccessException {
        return adDAO.find(ctx, criteria);
    }

}
