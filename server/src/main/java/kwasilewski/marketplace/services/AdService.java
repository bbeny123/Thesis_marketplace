package kwasilewski.marketplace.services;

import kwasilewski.marketplace.dao.AdDAO;
import kwasilewski.marketplace.dto.AdData;
import kwasilewski.marketplace.responses.AdSearchRequest;
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

    public List<AdData> findAds(AdSearchRequest adSearchRequest) throws DataAccessException {
        return adDAO.find(adSearchRequest);
    }

}
